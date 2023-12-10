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
import com.ada.ada_meethem.modelo.Contact
import com.ada.ada_meethem.modelo.Person
import com.ada.ada_meethem.modelo.Plan
import com.ada.ada_meethem.ui.PlanFragment.Companion.newInstance
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private var plans: MutableList<Plan>? = null
    private var contacts: List<Contact>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUsersListener()
        loadContacts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        generatePlans()
        val plansRecyclerView = root.findViewById<RecyclerView>(R.id.plansRecyclerView)
        plansRecyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(root.context)
        plansRecyclerView.layoutManager = layoutManager
        val plAdapter = PlanListAdapter(
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

    private fun generatePlans() {
        val person1 = Person("Maricarmen", "666666666", null)
        val person2 = Person("Alex", "0", null)
        val person3 = Person("Abel", "0", null)
        val person4 = Person("Diego", "0", null)
        val plan1 = Plan(
            "Ruta del Cares",
            person1,
            10,
            "https://www.escapadarural.com/blog/wp-content/uploads/2019/11/ruta-cares.jpg"
        )
        val plan2 = Plan(
            "Padelcito",
            person3,
            3,
            "https://www.covb.cat/wp-content/uploads/2023/04/padel-g9a1278dbc_1280.jpg"
        )
        val plan3 = Plan(
            "Surf en rodiles",
            person4,
            5,
            "https://img.redbull.com/images/q_auto,f_auto/redbullcom/2015/01/22/1331701014293_2/iv%C3%A1n-villalba-en-rodiles-%28asturias%29-semeyadetoral.com.jpg"
        )
        plan3.addToPlan(person1)
        plan3.addToPlan(person3)
        plan2.addToPlan(person1)
        plan2.addToPlan(person2)
        plan2.addToPlan(person4)
        plan1.planId = "1"
        plan2.planId = "2"
        plan3.planId = "3"
        plans = ArrayList()
        (plans as ArrayList<Plan>).add(plan1)
        (plans as ArrayList<Plan>).add(plan2)
        (plans as ArrayList<Plan>).add(plan3)
    }

    private fun setUsersListener() {
        val databaseReference =
            FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val cdb = ContactDatabase.getDatabase(context).contactDAO
                        for (chatSnapshot in dataSnapshot.children) {
                            val contact = chatSnapshot.getValue(Person::class.java) as Person
                            val localContact = cdb.findByNumber(contact.phoneNumber)
                            if(localContact != null) {
                                localContact.photoUrl = contact.profileImage
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Manejo de errores
                    }
                })

    }

    private fun loadContacts() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS) else contacts =
            ContactProvider(
                context
            ).getContacts(null, null)
    }
    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            contacts = ContactProvider(context).getContacts(null, null)
            safeContactsInLocalDB()
        } else Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.read_contact_permissions_not_acepted,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun safeContactsInLocalDB() {
        val cdao = ContactDatabase.getDatabase(context).contactDAO
        for (contact in contacts!!) {
            cdao.add(contact.toROM())
        }
    }

}