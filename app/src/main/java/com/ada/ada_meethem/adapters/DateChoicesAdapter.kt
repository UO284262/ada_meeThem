package com.ada.ada_meethem.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.ada.ada_meethem.R
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.modelo.pinnable.ChatMessage
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.ada.ada_meethem.modelo.pinnable.Pinnable
import java.util.Date

class DateChoicesAdapter(
    private val context: Context,
    private var listaDates: HashMap<String,Int>,
    private var dateSurvey: DateSurvey,
    private var planId: String,
    private var votedDates: ArrayList<String>,
) : BaseAdapter() {
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
        if(votedDates.contains(date)) viewStr.isActivated = true

        viewStr.setOnClickListener(View.OnClickListener {
            if(viewStr.isActivated) {
                dateSurvey.unvoteDate(date)
                votedDates.remove(date)
            }
            else if(!viewStr.isActivated) {
                dateSurvey.voteDate(date)
                votedDates.add(date)
            }
            PlanDatabase.pinDateSurvey(dateSurvey,planId)
        })

        val numVotes = view.findViewById<View>(R.id.num_votes_date) as TextView
        numVotes.text = listaDates.get(date).toString()

        return view
    }
}