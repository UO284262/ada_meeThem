package com.ada.ada_meethem.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.database.entities.ChatMessage;

import java.util.List;

public class MessageListAdapter extends BaseAdapter {
        private List<ChatMessage> listaMessages;
        private Context context;

        public MessageListAdapter(Context context, List<ChatMessage> listaMessages) {
            this.context = context;
            this.listaMessages = listaMessages;
        }

        @Override
        public int getCount() {
            return listaMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return listaMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.message, null);
            }

            // Obtén el usuario actual
            ChatMessage chatMessage = listaMessages.get(position);

            TextView messageText = (TextView)view.findViewById(R.id.message_text);
            TextView messageUser = (TextView)view.findViewById(R.id.message_user);
            TextView messageTime = (TextView)view.findViewById(R.id.message_time);
            // Set their text
            messageText.setText(chatMessage.getMessageText());
            messageUser.setText(chatMessage.getMessageUser());
            // Format the date before showing it
            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    chatMessage.getMessageTime()));


            return view;
        }
}
