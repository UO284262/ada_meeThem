package com.ada.ada_meethem.ui

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ada.ada_meethem.R
import com.ada.ada_meethem.adapters.PinnedItemsAdapter
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.modelo.DateSurveyVotes
import com.ada.ada_meethem.modelo.Plan
import com.ada.ada_meethem.modelo.pinnable.ChatMessage
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.ada.ada_meethem.modelo.pinnable.Pinnable
import com.ada.ada_meethem.modelo.pinnable.PlanImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PlanFragment : Fragment() {
    private var plan: Plan? = null
    private var adapter: PinnedItemsAdapter? = null
    private var surveyDone: Boolean = false
    private var listView: ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_plan, container, false)
        plan = requireArguments().getParcelable<Parcelable>("plan") as Plan?
        (root.findViewById<View>(R.id.planName) as TextView).text = plan!!.title
        (root.findViewById<View>(R.id.participantes) as TextView).text =
            String.format("%d/%d", plan!!.enlisted.size, plan!!.maxPeople)
        (root.findViewById<View>(R.id.creadorPlan) as TextView).text = plan!!.creator.contactName
        //((ImageView) root.findViewById(R.id.imagenPlan))

        val fab = root.findViewById<View>(R.id.fabChat) as FloatingActionButton
        fab.setOnClickListener { abrirChat(plan) }

        val fab2 = root.findViewById<View>(R.id.fabEditPlan) as FloatingActionButton
        fab2.setOnClickListener { abrirEdit(plan) }

        if (FirebaseAuth.getInstance().currentUser!!.phoneNumber!!
            == plan!!.creator.contactNumber
        ) fab2.visibility = View.VISIBLE

        listView = root.findViewById(R.id.pinnedList)
        adapter = PinnedItemsAdapter(root.context, ArrayList(), DateSurveyVotes() ,plan!!)
        listView!!.adapter = adapter
        displayPinned(root)
        return root
    }

    private fun abrirChat(plan: Plan?) {
        val chatFragment = ChatFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable("plan", plan)
        //chatFragment.arguments = bundle
        //requireFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).commit()
        findNavController().navigate(
            R.id.action_planFragment_to_chatFragment,
            bundle
        );
    }

    private fun abrirEdit(plan: Plan?) {
        val editPlanFragment = EditPlanFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable("plan", plan)
        bundle.putBoolean("surveyDone",surveyDone)
        findNavController().navigate(
            R.id.action_planFragment_to_editPlanFragment,
            bundle
        );
    }

    fun displayPinned(root: View?) {
        PlanDatabase.getReference(plan!!.planId).child("pinnedItems").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                surveyDone = false
                val pins: MutableList<Pinnable> = ArrayList()
                for (chatSnapshot in dataSnapshot.children) {
                    val ident : String = chatSnapshot.key!!.subSequence(0,3).toString()
                    when(ident) {
                        "msg" -> pins.add(chatSnapshot.getValue(ChatMessage::class.java) as ChatMessage)
                        "sdt" -> {
                            pins.add(chatSnapshot.getValue(DateSurvey::class.java) as DateSurvey)
                            surveyDone = true
                        }
                        "pli" -> pins.add(chatSnapshot.getValue(PlanImage::class.java) as PlanImage)
                    }
                }
                adapter!!.update(pins.toList())
                listView!!.setSelection(adapter!!.count - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejo de errores
            }
        })

        PlanDatabase.getReference(plan!!.planId).child("surveyVotes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var votes: DateSurveyVotes = DateSurveyVotes();
                for (chatSnapshot in dataSnapshot.children) {
                    votes = chatSnapshot.getValue(DateSurveyVotes::class.java) as DateSurveyVotes
                }
                adapter!!.updateVotes(votes)
                listView!!.setSelection(adapter!!.count - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(): PlanFragment {
            return PlanFragment()
        }
    }
}