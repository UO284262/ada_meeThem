package com.ada.ada_meethem.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ada.ada_meethem.R
import com.ada.ada_meethem.adapters.PlanListAdapter
import com.ada.ada_meethem.data.ContactProvider
import com.ada.ada_meethem.database.ContactDatabase
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.database.entities.Contact
import com.ada.ada_meethem.modelo.Plan
import com.ada.ada_meethem.ui.PlanFragment.Companion.newInstance
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private var plans: List<Plan> = ArrayList()
    private var contacts: List<Contact> = ArrayList()
    private lateinit var plAdapter : PlanListAdapter
    private var loadContacts : Boolean = true
    private var setListener : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(setListener) setUsersListener()
        if(loadContacts) loadContacts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        loadPlans()
        val plansRecyclerView = root.findViewById<RecyclerView>(R.id.plansRecyclerView)
        plansRecyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(root.context)
        plansRecyclerView.layoutManager = layoutManager
        plAdapter = PlanListAdapter(
            plans
        ) { plan -> clickonItem(plan) }
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
                    if(plan.enlisted.contains(phoneNumber)) {
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
                            if(localContact != null) {
                                localContact.photoUrl = contact.photoUrl
                                loadContacts = true
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Manejo de errores
                    }
                })

    }

    private fun loadContacts() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        else
            try {
                contacts = ContactProvider(context).getContacts(null, null)
            } catch (e: Exception) {
                contacts = ArrayList()
            }
    }

    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                contacts = ContactProvider(context).getContacts(null, null)
            } catch (e: Exception) {
                contacts = ArrayList()
            }
            safeContactsInLocalDB()
        } else Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.read_contact_permissions_not_acepted,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun safeContactsInLocalDB() {
        val cdao = ContactDatabase.getDatabase(context).contactDAO
        for (contact in contacts) {
            contact.contactNumber = contact.contactNumber.replace("\\s".toRegex(), "")
           // if(!contact.contactNumber.contains("+")) contact.contactNumber = "+34" + contact.contactNumber
            cdao.add(contact)
        }
        loadContacts = false
    }
}