package com.ada.ada_meethem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ada.ada_meethem.R
import com.ada.ada_meethem.adapters.ContactListAdapter
import com.ada.ada_meethem.database.ContactDatabase
import com.ada.ada_meethem.database.entities.Contact
import com.ada.ada_meethem.util.ContactLoaderFromProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactPickerFragment : Fragment() {

    companion object {
        const val SELECTED_CONTACTS = "selected_contacts"
    }

    // Contactos extraídos del móvil
    private var contacts: List<Contact>? = null
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarText: TextView
    private lateinit var clAdapter: ContactListAdapter
    private lateinit var bundle: Bundle // para almacenar los datos del createPlanFragment y los contactos seleccionados
    private lateinit var contactLoader: ContactLoaderFromProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_contact_picker, container, false)
        progressBar = root.findViewById(R.id.progressBar)
        progressBarText = root.findViewById(R.id.progressBarText)
        contactsRecyclerView = root.findViewById(R.id.contactPickerRecyclerView)
        contactsRecyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(root.context)
        contactsRecyclerView.setLayoutManager(layoutManager)

        // Load the contacts
        loadContacts()
        // Establece el adapter al Recycler view
        loadRecyclerContactListAdapter()

        // Listener del fab
        val fab = root.findViewById<FloatingActionButton>(R.id.fabContactsSelected)
        fab.setOnClickListener { view: View -> navigateToCreatePlan(view) }

        // Habilitamos el menú
        setHasOptionsMenu(true)

        // Crea el contactLoader
        contactLoader = ContactLoaderFromProvider(
            requireContext(),
            requestPermissionLauncher,
            requireActivity()
        )
        return root
    }

    // Recibe los datos del createPlanFragment y los guarda
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bundle = arguments ?: Bundle()

        // Se marcan los contactos que habían sido seleccionados previamente
        val selectedContacts: List<Contact>? = requireArguments().getParcelableArrayList(SELECTED_CONTACTS)
        if (selectedContacts != null)
            clAdapter.setSelectedContacts(selectedContacts)
    }

    // Guarda los contactos seleccionados en una lista y los envía al CreatePlanFragment
    private fun navigateToCreatePlan(view: View) {
        val selectedContacts = clAdapter.contactsSelected
        bundle.putParcelableArrayList(SELECTED_CONTACTS, selectedContacts as ArrayList<Contact?>)
        findNavController(view).navigate(R.id.action_contactPickerFragment_to_nav_plan_create, bundle)
    }

    private fun loadRecyclerContactListAdapter() {
        if (contacts == null)
            return
        if (contacts!!.isEmpty())
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                R.string.contact_list_is_empty,
                Snackbar.LENGTH_LONG
            ).show()
        clAdapter = ContactListAdapter(contacts) { plan: Contact? -> }
        contactsRecyclerView.adapter = clAdapter
    }

    // Extrae los contactos cacheados de la base de datos
    private fun loadContacts() {
        val cdb = ContactDatabase.getDatabase(context).contactDAO
        contacts = cdb.all
    }

    // Crea el menú de este fragmento (contiene el botón de buscar y refrescar contactos)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_picker_menu, menu)
        searchContactsViewSetUp(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Configura el searchView usado para buscar contactos
    private fun searchContactsViewSetUp(menu: Menu) {
        val menuItemBuscar = menu.findItem(R.id.menu_search_contacts)
        val searchView = menuItemBuscar.actionView as SearchView?
        searchView!!.queryHint = getString(R.string.contacts_search_view_query_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                clAdapter.filter.filter(query) // filtra contactos
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                clAdapter.filter.filter(newText) // filtra contactos
                return false
            }
        })
    }

    // Añade funcionalidad a los elementos del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search_contacts)
            return true

        if (item.itemId == R.id.menu_refresh_contacts) {
            lifecycleScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.VISIBLE
                    progressBarText.visibility = View.VISIBLE
                }

                contacts = contactLoader.loadContacts()

                withContext(Dispatchers.Main) {
                    // Establece el adapter al Recycler view
                    loadRecyclerContactListAdapter()
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.contacts_refresh_OK,
                        Snackbar.LENGTH_SHORT)
                        .show()

                    progressBar.visibility = View.GONE
                    progressBarText.visibility = View.GONE
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> contactLoader.requestPermissionLauncherCallback(isGranted) }

}