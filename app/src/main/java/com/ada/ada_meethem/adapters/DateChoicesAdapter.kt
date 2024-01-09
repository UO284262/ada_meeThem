package com.ada.ada_meethem.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import com.ada.ada_meethem.R
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.modelo.DateSurveyVotes
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.google.firebase.auth.FirebaseAuth

class DateChoicesAdapter(
    private val context: Context,
    private var listaVotes: DateSurveyVotes,
    private var dateSurvey: DateSurvey,
    private var planId: String,
) : BaseAdapter() {
    private val phoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
    private var listaDates = dateSurvey.dates
    override fun getCount(): Int {
        return listaDates.size
    }

    override fun getItem(position: Int): Any {
        return listaDates.keys.toList()[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val date = getItem(position) as String
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.date_survey_choice, null)
        }
        val viewStr: CheckBox = view!!.findViewById<View>(R.id.date_survey_cb) as CheckBox
        viewStr.text = date
        if(listaVotes.votes.keys.contains(phoneNumber)
            && listaVotes.votes.get(phoneNumber) == date) {
            viewStr.isChecked = true
            viewStr.isActivated = true
        }

        if(!dateSurvey.open) {
            viewStr.isEnabled = false
            val colorDeshabilitado = getColor(context,R.color.white) // Reemplaza con tu color
            viewStr.buttonDrawable?.setColorFilter(colorDeshabilitado, PorterDuff.Mode.SRC_ATOP)
        }

        viewStr.setOnClickListener(View.OnClickListener {
            if(viewStr.isActivated) {
                dateSurvey.unvoteDate(date)
                listaVotes.unvote(date)
            }
            else if(!viewStr.isActivated) {
                dateSurvey.voteDate(date)
                unvotePrevius()
                listaVotes.vote(date)
            }
            PlanDatabase.pinDateSurvey(dateSurvey,planId)
            PlanDatabase.voteDate(listaVotes,planId)
        })

        val numVotes = view.findViewById<View>(R.id.num_votes_date) as TextView
        numVotes.text = listaDates[date]!!.toString()

        return view
    }

    fun unvotePrevius() {
        var date : String? = listaVotes.votes.get(phoneNumber)
        if(date != null) dateSurvey.unvoteDate(date)
    }
}