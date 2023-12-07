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
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ada.ada_meethem.R
import com.ada.ada_meethem.adapters.OnCreateDateChoicesAdapter
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.modelo.DateSurveyVotes
import com.ada.ada_meethem.modelo.Plan
import com.ada.ada_meethem.modelo.pinnable.ChatMessage
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.ada.ada_meethem.util.DatePickerFragment
import com.google.firebase.auth.FirebaseAuth

class EditPlanFragment : Fragment() {
    private var plan: Plan? = null
    private var surveyDone: Boolean = false
    private var pinMsgButton: Button? = null
    private var createSurveyBtn: Button? = null
    private var addDateToSurveyBtn: ImageButton? = null
    private var dateTextView: EditText? = null
    private var dateSurvey: DateSurvey = DateSurvey(ArrayList())
    private var pinMsgText: EditText? = null
    private var adapterOnCreate : OnCreateDateChoicesAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_plan, container, false)

        plan = requireArguments().getParcelable<Parcelable>("plan") as Plan?
        surveyDone = requireArguments().getBoolean("surveyDone")

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
        if(!surveyDone) {
            createSurveyBtn = root.findViewById<View>(R.id.btn_crear_survey) as Button
            createSurveyBtn!!.setOnClickListener {
                PlanDatabase.pinDateSurvey(dateSurvey, plan!!.planId)
                PlanDatabase.voteDate(DateSurveyVotes(HashMap<String,String>()),plan!!.planId)
                showPlan()
            }
            createSurveyBtn!!.isClickable = false

            dateTextView = root.findViewById<View>(R.id.editTextDate) as EditText
            dateTextView!!.setOnClickListener { showDatePickerDialog() }

            addDateToSurveyBtn = root.findViewById<View>(R.id.btn_add_date) as ImageButton
            addDateToSurveyBtn!!.setOnClickListener {
                if (!dateTextView!!.text.toString().isEmpty()) {
                    dateSurvey.addDate(dateTextView!!.text.toString())
                    adapterOnCreate!!.addDate(dateTextView!!.text.toString())
                    dateTextView!!.setText("")
                    createSurveyBtn!!.isClickable = true
                }
            }

            val listView = root.findViewById<View>(R.id.oncreate_survey_list) as ListView
            adapterOnCreate = OnCreateDateChoicesAdapter(root.context, dateSurvey, plan!!.planId,this)
            listView.adapter = adapterOnCreate
        } else {
            createSurveyBtn = root.findViewById<View>(R.id.btn_crear_survey) as Button
            dateTextView = root.findViewById<View>(R.id.editTextDate) as EditText
            addDateToSurveyBtn = root.findViewById<View>(R.id.btn_add_date) as ImageButton
            val listView = root.findViewById<View>(R.id.oncreate_survey_list) as ListView
            val tv = root.findViewById<View>(R.id.survey_add_title) as TextView

            dateTextView!!.isVisible = false
            tv.isVisible = false
            createSurveyBtn!!.isVisible = false
            addDateToSurveyBtn!!.isVisible = false
            listView.isVisible = false
        }

        return root
    }

    private fun anclarMensaje(text: String) {
        val phoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val message = ChatMessage(text, phoneNumber)
        PlanDatabase.pinMessage(message, plan!!.planId)
    }

    private fun showPlan() {
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

    fun notifyEmptyDates() {
        this.createSurveyBtn!!.isClickable = false
    }

    companion object {
        fun newInstance(): EditPlanFragment {
            return EditPlanFragment()
        }
    }
}