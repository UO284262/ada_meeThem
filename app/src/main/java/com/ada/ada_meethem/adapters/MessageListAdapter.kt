package com.ada.ada_meethem.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ada.ada_meethem.R
import com.ada.ada_meethem.database.ContactDatabase
import com.ada.ada_meethem.modelo.pinnable.ChatMessage
import com.google.firebase.auth.FirebaseAuth

class MessageListAdapter(

    private val context: Context,
    private var listaMessages: List<ChatMessage>,
    private val colorsArray: IntArray
) : BaseAdapter() {

    private val TYPEOWN : Int = 0
    private val TYPEOTHER : Int = 1
    override fun getCount(): Int {
        return listaMessages.size
    }

    override fun getItem(position: Int): Any {
        return listaMessages[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        val userPhoneNumber = (getItem(position) as ChatMessage).messageUser
        return if (userPhoneNumber == FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            TYPEOWN else TYPEOTHER
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewType = getItemViewType(position)
        val chatMessage = getItem(position) as ChatMessage
        var userPhoneNumber = chatMessage.messageUser
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(if (viewType == TYPEOWN) R.layout.own_message else R.layout.message, null)
        }

        // Obtén el usuario actual


        val color = colorsArray[(userPhoneNumber.substring(1).toLong() % 18).toInt()]
        val messageText = view!!.findViewById<View>(
            if (viewType == TYPEOWN) R.id.message_text_own else R.id.message_text_own) as TextView
        val messageUser = view.findViewById<View>(
            if (viewType == TYPEOWN) R.id.message_user_own else R.id.message_user_own) as TextView
        val messageTime = view.findViewById<View>(
            if (viewType == TYPEOWN) R.id.message_time_own else R.id.message_time_own) as TextView
        // Set their text
        messageText.text = chatMessage.messageText
        messageUser.setTextColor(color)
        val cdb = ContactDatabase.getDatabase(context).contactDAO
        val localContact =  cdb.findByNumber(userPhoneNumber)
        if(localContact != null) {
            userPhoneNumber = localContact.contactName
        }
        messageUser.text = if(viewType == TYPEOWN) "You" else userPhoneNumber
        // Format the date before showing it
        messageTime.text = DateFormat.format(
            "EEE HH:mm",
            chatMessage.messageTime
        )
        return view
    }

    fun update(listaChatMessage : List<ChatMessage>) {
        listaMessages = listaChatMessage
        notifyDataSetChanged()
    }
}