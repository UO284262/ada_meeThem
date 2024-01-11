package com.ada.ada_meethem.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ada.ada_meethem.R
import com.ada.ada_meethem.adapters.PlanListAdapter
import com.ada.ada_meethem.database.ContactDatabase
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.database.entities.Contact
import com.ada.ada_meethem.modelo.Plan
import com.ada.ada_meethem.ui.PlanFragment.Companion.newInstance
import com.ada.ada_meethem.util.ContactLoaderFromProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var plans: List<Plan> = ArrayList()
    private var contacts: List<Contact> = ArrayList()
    private lateinit var plAdapter: PlanListAdapter
    private lateinit var contactLoader: ContactLoaderFromProvider
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarText: TextView

    private var loadContacts: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUsersListener()

        // Crea el contactLoader
        contactLoader = ContactLoaderFromProvider(
            requireContext(),
            requestPermissionLauncher,
            requireActivity()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        progressBar = root.findViewById(R.id.progressBar)
        progressBarText = root.findViewById(R.id.progressBarText)

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
                progressBarText.visibility = View.VISIBLE
            }

            // Carga los contactos y los planes
            if (loadContacts) {
                contacts = contactLoader.loadContacts()
                loadContacts = false
            }
            loadPlans()

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                progressBarText.visibility = View.GONE

            }
        }

        // Configura el recycler view de planes
        val plansRecyclerView = root.findViewById<RecyclerView>(R.id.plansRecyclerView)
        plansRecyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(root.context)
        plansRecyclerView.layoutManager = layoutManager
        plAdapter = PlanListAdapter(plans) { plan -> clickonItem(plan) }
        plansRecyclerView.adapter = plAdapter
        return root
    }

    fun clickonItem(plan: Plan?) {
        //Paso el modo de apertura
        val planFragment = newInstance()
        val bundle = Bundle()
        bundle.putParcelable("plan", plan)
        //planFragment.setArguments(bundle);
        //getFragmentManager().beginTransaction().replace(R.id.fragment_container, planFragment).commit();
        this.findNavController().navigate(
            R.id.action_homeFragment_to_planFragment,
            bundle
        )
    }

    private fun loadPlans() {
        val phoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        PlanDatabase.getReference().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val plans = ArrayList<Plan>()
                val cdb = ContactDatabase.getDatabase(context).contactDAO
                for (chatSnapshot in dataSnapshot.children) {
                    val plan = chatSnapshot.getValue(Plan::class.java) as Plan
                    if (plan.enlisted.contains(phoneNumber)) {
                        plans.add(plan)
                    }
                }
                plAdapter.update(plans)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    private fun setUsersListener() {
        val databaseReference =
            FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val cdb = ContactDatabase.getDatabase(context).contactDAO
                        for (chatSnapshot in dataSnapshot.children) {
                            val contact = chatSnapshot.getValue(Contact::class.java) as Contact
                            val localContact = cdb.findByNumber(contact.contactNumber)
                            if (localContact != null) {
                                localContact.photoUrl = contact.photoUrl
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Manejo de errores
                    }
                })

    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    )
    { isGranted: Boolean -> contactLoader.requestPermissionLauncherCallback(isGranted) }

}