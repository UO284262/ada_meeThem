package com.ada.ada_meethem.ui

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ada.ada_meethem.R
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.modelo.DateSurveyVotes
import com.ada.ada_meethem.modelo.Plan
import com.ada.ada_meethem.modelo.pinnable.ChatMessage
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.ada.ada_meethem.util.DatePickerFragment
import com.google.firebase.auth.FirebaseAuth

class EditPlanFragment : Fragment() {
    private var plan: Plan? = null
    private var pinMsgButton: Button? = null
    private var createSurveyBtn: Button? = null
    private var addDateToSurveyBtn: ImageButton? = null
    private var dateTextView: EditText? = null
    private var dateSurvey: DateSurvey? = null
    private var pinMsgText: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dateSurvey = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_plan, container, false)

        plan = requireArguments().getParcelable<Parcelable>("plan") as Plan?

        val planTitle = root.findViewById<View>(R.id.plan_title) as TextView
        planTitle.text = plan!!.title

        pinMsgText = root.findViewById<View>(R.id.et_pin_msg) as EditText

        pinMsgButton = root.findViewById<View>(R.id.pin_msg_btn) as Button
        pinMsgButton!!.setOnClickListener {
            val text = pinMsgText!!.text.toString()
            if (!text.isEmpty()) anclarMensaje(text)
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(pinMsgText!!.windowToken, 0)
            showPlan()
        }

        createSurveyBtn = root.findViewById<View>(R.id.btn_crear_survey) as Button
        createSurveyBtn!!.setOnClickListener {
            if (dateSurvey != null) {
                PlanDatabase.pinDateSurvey(dateSurvey, plan!!.planId)
                PlanDatabase.voteDate(DateSurveyVotes(HashMap<String,String>()),plan!!.planId)
                showPlan()
            }
        }

        dateTextView = root.findViewById<View>(R.id.editTextDate) as EditText
        dateTextView!!.setOnClickListener { showDatePickerDialog() }

        addDateToSurveyBtn = root.findViewById<View>(R.id.btn_add_date) as ImageButton
        addDateToSurveyBtn!!.setOnClickListener {
            if (!dateTextView!!.text.toString().isEmpty()) {
                if (dateSurvey != null) {
                    dateSurvey!!.addDate(dateTextView!!.text.toString())
                } else {
                    dateSurvey = DateSurvey(ArrayList())
                    dateSurvey!!.addDate(dateTextView!!.text.toString())
                }
            }
        }
        return root
    }

    private fun anclarMensaje(text: String) {
        val phoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val message = ChatMessage(text, phoneNumber)
        PlanDatabase.pinMessage(message, plan!!.planId)
    }

    private fun showPlan() {
        val planFragment = PlanFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable("plan", plan)
        findNavController().navigate(
            R.id.action_editPlanFragment_to_planFragment,
            bundle
        );
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance { datePicker, year, month, day -> // +1 because January is zero
                val selectedDate = day.toString() + "-" + (month + 1) + "-" + year
                dateTextView!!.setText(selectedDate)
            }
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    companion object {
        fun newInstance(): EditPlanFragment {
            return EditPlanFragment()
        }
    }
}