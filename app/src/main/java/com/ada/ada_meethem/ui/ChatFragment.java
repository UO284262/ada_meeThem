package com.ada.ada_meethem.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.database.entities.ChatMessage;
import com.ada.ada_meethem.database.entities.ChatMessageDatabase;
import com.ada.ada_meethem.modelo.Plan;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private FirebaseListAdapter<ChatMessage> adapter;
    private Plan plan;
    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root= getLayoutInflater().inflate(R.layout.fragment_chat, null, false);

        FloatingActionButton fab =
                (FloatingActionButton)root.findViewById(R.id.fabSendMsg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)root.findViewById(R.id.etMessage);
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                ChatMessageDatabase.sendMessage(input.getText().toString());
                // Clear the input
                input.setText("");
            }
        });

        displayMessages(root);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_chat, container, false);
        plan = (Plan) getArguments().getSerializable("plan");

        ((TextView) root.findViewById(R.id.planNameChat)).setText(plan.getTitle());

        return root;
    }

    public void displayMessages(View root) {
        ListView listOfMessages = (ListView) root.findViewById(R.id.chatList);

        Query query = FirebaseDatabase.getInstance()
                .getReference();

        FirebaseListOptions<ChatMessage> options =
                         new FirebaseListOptions.Builder<ChatMessage>()
                                .setQuery(query, ChatMessage.class)
                                .build();
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }
}