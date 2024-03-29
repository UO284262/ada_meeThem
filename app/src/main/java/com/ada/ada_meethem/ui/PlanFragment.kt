package com.ada.ada_meethem.ui

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PlanFragment : Fragment() {
    private var plan: Plan? = null
    private var adapter: PinnedItemsAdapter? = null
    private var surveyDone: Boolean = false
    private var listView: ListView? = null
    private lateinit var textParticipantes: TextView
    private lateinit var tvConfirmedMsg: TextView
    private lateinit var btExit: Button
    private lateinit var btConfirm: Button
    private lateinit var dateTv: TextView
    private lateinit var num: String

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
        (root.findViewById<View>(R.id.creadorPlan) as TextView).text = plan!!.creator.contactName
        //((ImageView) root.findViewById(R.id.imagenPlan))
        num = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!

        val fab = root.findViewById<View>(R.id.fabChat) as FloatingActionButton
        fab.setOnClickListener { abrirChat(plan) }

        val fab2 = root.findViewById<View>(R.id.fabEditPlan) as FloatingActionButton
        fab2.setOnClickListener { abrirEdit(plan) }

        tvConfirmedMsg = root!!.findViewById<View>(R.id.tv_confirmed_msg) as TextView
        if (plan!!.confirmed.contains(num)) {
            tvConfirmedMsg.text = "Estas apuntado a este plan"
            tvConfirmedMsg.setTextColor(resources.getColor(R.color.green))
        }

        textParticipantes = root!!.findViewById<View>(R.id.participantes) as TextView
        textParticipantes.text =
            String.format("%d/%d", plan!!.confirmed.size, plan!!.maxPeople)

        dateTv = root.findViewById<View>(R.id.tv_date) as TextView
        dateTv.text = plan!!.fecha

        btExit = root.findViewById<View>(R.id.bt_exit) as Button
        btExit.setOnClickListener { exitPlan(plan!!) }

        btConfirm = root.findViewById<View>(R.id.bt_agree) as Button
        btConfirm.setOnClickListener {
            confirmPlan(plan!!)
        }

        if (num == plan!!.creator.contactNumber
        ) fab2.visibility = View.VISIBLE else if (!plan!!.confirmed.contains(num)) {
            btExit.visibility = View.VISIBLE
            btConfirm.visibility = View.VISIBLE
        }

        listView = root.findViewById(R.id.pinnedList)
        adapter = PinnedItemsAdapter(root.context, ArrayList(), DateSurveyVotes(), plan!!)
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
        bundle.putBoolean("surveyDone", surveyDone)
        findNavController().navigate(
            R.id.action_planFragment_to_editPlanFragment,
            bundle
        );
    }

    private fun confirmPlan(plan: Plan) {
        val num = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!
        plan.confirmToPlan(num)
        PlanDatabase.confirmPlan(plan)
        btConfirm.visibility = View.INVISIBLE
        btExit.visibility = View.INVISIBLE
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.plan_confirmed, Snackbar.LENGTH_LONG
        ).show()
    }

    private fun exitPlan(plan: Plan) {
        plan.exitPlan(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
        PlanDatabase.exitPlan(plan)
        btConfirm.visibility = View.INVISIBLE
        btExit.visibility = View.INVISIBLE
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.plan_exited, Snackbar.LENGTH_LONG
        ).show()
        findNavController().navigate(
            R.id.action_global_nav_home
        );
    }

    fun displayPinned(root: View?) {
        PlanDatabase.getReference(plan!!.planId).child("pinnedItems")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    surveyDone = false
                    val pins: MutableList<Pinnable> = ArrayList()
                    for (chatSnapshot in dataSnapshot.children) {
                        val ident: String = chatSnapshot.key!!.subSequence(0, 3).toString()
                        when (ident) {
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

        PlanDatabase.getReference(plan!!.planId).child("surveyVotes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var votes: DateSurveyVotes = DateSurveyVotes();
                    for (chatSnapshot in dataSnapshot.children) {
                        votes =
                            chatSnapshot.getValue(DateSurveyVotes::class.java) as DateSurveyVotes
                    }
                    adapter!!.updateVotes(votes)
                    listView!!.setSelection(adapter!!.count - 1)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejo de errores
                }
            })

        PlanDatabase.getReference(plan!!.planId).child("confirmed")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (chatSnapshot in dataSnapshot.children) {
                        plan!!.confirmed.add(chatSnapshot.getValue(String::class.java) as String)
                    }
                    textParticipantes.text =
                        String.format("%d/%d", plan!!.confirmed.size, plan!!.maxPeople)
                    if (plan!!.confirmed.size == plan!!.maxPeople) {
                        plan!!.enlisted = plan!!.confirmed
                        PlanDatabase.setEnlisted(plan!!)
                        btConfirm.visibility = View.INVISIBLE
                        btExit.visibility = View.INVISIBLE
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            R.string.plan_full, Snackbar.LENGTH_LONG
                        ).show()
                    }
                    if (plan!!.confirmed.contains(num)) {
                        tvConfirmedMsg.text = "Estas apuntado a este plan"
                        tvConfirmedMsg.setTextColor(resources.getColor(R.color.green))
                    } else if (plan!!.confirmed.size == plan!!.maxPeople) {
                        tvConfirmedMsg.text = "Plan lleno"
                        tvConfirmedMsg.setTextColor(resources.getColor(R.color.colorRed))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejo de errores
                }
            })

        PlanDatabase.getReference(plan!!.planId).child("fecha")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (chatSnapshot in dataSnapshot.children) {
                        plan!!.fecha = chatSnapshot.getValue(String::class.java) as String
                    }
                    dateTv.text = plan!!.fecha
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