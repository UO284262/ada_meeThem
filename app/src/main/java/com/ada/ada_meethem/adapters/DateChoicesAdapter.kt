package com.ada.ada_meethem.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import com.ada.ada_meethem.R
import com.ada.ada_meethem.modelo.pinnable.ChatMessage
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.ada.ada_meethem.modelo.pinnable.Pinnable
import java.util.Date

class DateChoicesAdapter(
    private val context: Context,
    private var listaDates: List<Date>,
    private var dateSurvey: DateSurvey,
) : BaseAdapter() {
    override fun getCount(): Int {
        return listaDates.size
    }

    override fun getItem(position: Int): Any {
        return listaDates[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val date = getItem(position) as Date
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.date_survey_choice, null)
        }
        val viewStr: CheckBox = view!!.findViewById<View>(R.id.date_survey_cb) as CheckBox
        viewStr.text = DateFormat.format(
            "dd/MM/yyyy HH:mm",
            getItem(position) as Date
        )
        return view
    }
}