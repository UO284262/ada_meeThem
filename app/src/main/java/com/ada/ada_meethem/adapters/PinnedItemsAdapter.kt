package com.ada.ada_meethem.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.isVisible
import com.ada.ada_meethem.R
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.modelo.DateSurveyVotes
import com.ada.ada_meethem.modelo.Plan
import com.ada.ada_meethem.modelo.pinnable.ChatMessage
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.ada.ada_meethem.modelo.pinnable.Pinnable
import com.ada.ada_meethem.modelo.pinnable.PlanImage
import com.google.firebase.auth.FirebaseAuth

class PinnedItemsAdapter(
    private val context: Context,
    private var listaPinned: List<Pinnable>,
    private var listaVotes: DateSurveyVotes,
    private val plan: Plan
) : BaseAdapter() {
    private val SURVEY: Int = 0
    private val MESSAGE: Int = 1
    private val IMAGE: Int = 2
    override fun getCount(): Int {
        return listaPinned.size
    }

    override fun getItem(position: Int): Any {
        return listaPinned[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getViewTypeCount(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item is DateSurvey) return SURVEY
        else if (item is ChatMessage) return MESSAGE
        else if (item is PlanImage) return IMAGE
        else return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewType = getItemViewType(position)
        var item = getItem(position)
        var layout: Int = 0
        when (viewType) {
            SURVEY -> {
                item = getItem(position) as DateSurvey
                layout = R.layout.date_survey
            }

            MESSAGE -> {
                item = getItem(position) as ChatMessage
                layout = R.layout.pinned_message
            }

            IMAGE -> {
                item = getItem(position) as PlanImage
                layout = R.layout.image_item
            }
        }
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(layout, null)
        }

        when (viewType) {
            SURVEY -> {
                val dateSurvey = (item as DateSurvey)
                val listView: ListView =
                    view!!.findViewById<View>(R.id.date_choices_list) as ListView
                val adapter = DateChoicesAdapter(
                    view.context,
                    listaVotes,
                    dateSurvey,
                    plan.planId,
                )
                listView.adapter = adapter

                val closeSurveyBtn =
                    view.findViewById<View>(R.id.close_survey_btn) as ImageButton

                if (FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.substring(3)
                    == plan.creator.phoneNumber
                ) {
                    closeSurveyBtn.isClickable = true
                    closeSurveyBtn.isVisible = true
                }

                closeSurveyBtn.setOnClickListener(View.OnClickListener {
                    dateSurvey.closeSurvey()
                    PlanDatabase.closeSurvey(dateSurvey.id,plan.planId)
                })
            }

            MESSAGE -> {
                val messageText = view!!.findViewById<View>(R.id.message_text_pinned) as TextView
                val messageTime = view.findViewById<View>(R.id.message_time_pinned) as TextView
                val unpinBtn = view.findViewById<View>(R.id.pinbutton)

                if (FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.substring(3)
                    == plan.creator.phoneNumber
                ) {
                    unpinBtn.isClickable = true
                    unpinBtn.isVisible = true
                }

                unpinBtn.setOnClickListener(View.OnClickListener {
                    PlanDatabase.unpinMessage(item as ChatMessage, plan.planId)
                })

                messageText.text = (item as ChatMessage).messageText
                messageTime.text = DateFormat.format(
                    "EEE HH:mm",
                    (item as ChatMessage).messageTime
                )
            }

            IMAGE -> {
                item = getItem(position) as PlanImage
                layout = R.layout.image_item
            }
        }

        return view!!
    }

    fun update(listaPinned: List<Pinnable>) {
        this.listaPinned = listaPinned
        notifyDataSetChanged()
    }

    fun updateVotes(listaVotes: DateSurveyVotes) {
        this.listaVotes = listaVotes
        notifyDataSetChanged()
    }
}