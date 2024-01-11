package com.ada.ada_meethem.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.ada.ada_meethem.R
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.ada.ada_meethem.ui.EditPlanFragment
import com.google.firebase.auth.FirebaseAuth

class OnCreateDateChoicesAdapter(
    private val context: Context,
    private var dateSurvey: DateSurvey,
    private var planId: String,
    private var parentFragment: EditPlanFragment,
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
            view = inflater.inflate(R.layout.date_choice_oncreate, null)
        }
        val viewStr: TextView = view!!.findViewById<View>(R.id.date_tv) as TextView
        viewStr.text = date

        val deleteBtn = view.findViewById<View>(R.id.delete_btn) as ImageButton
        deleteBtn.setOnClickListener(View.OnClickListener {
            this.listaDates.remove(date)
            this.dateSurvey.removeDate(date)
            if (listaDates.keys.size == 0) this.parentFragment.notifyEmptyDates()
            notifyDataSetChanged()
        })

        return view
    }

    fun addDate(date: String) {
        this.listaDates.put(date, 0)
        notifyDataSetChanged()
    }
}