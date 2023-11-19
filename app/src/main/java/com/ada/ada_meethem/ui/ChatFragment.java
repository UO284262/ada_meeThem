package com.ada.ada_meethem.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.adapters.MessageListAdapter;
import com.ada.ada_meethem.database.entities.ChatMessage;
import com.ada.ada_meethem.database.entities.ChatMessageDatabase;
import com.ada.ada_meethem.modelo.Plan;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private View root;
    private ListView listView;
    private MessageListAdapter adapter;
    private Plan plan;

    private int[] colorsArray;
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
        colorsArray = getResources().getIntArray(R.array.colors_array);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_chat, container, false);
        plan = (Plan) getArguments().getParcelable("plan");

        ((TextView) root.findViewById(R.id.planNameChat)).setText(plan.getTitle());
        ImageButton button = (ImageButton) root.findViewById(R.id.buttonSend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) root.findViewById(R.id.etMessage);
                if(!input.getText().toString().trim().isEmpty())
                    ChatMessageDatabase.sendMessage(plan.getPlanId(), input.getText().toString());
                input.setText("");
            }
        });

        listView = root.findViewById(R.id.chatList);
        adapter = new MessageListAdapter(root.getContext(), new ArrayList<ChatMessage>(),colorsArray);
        listView.setAdapter(adapter);
        displayMessages(root);


        return root;
    }

    public void displayMessages(View root) {

        FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("chats").child(plan.getPlanId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatMessage> msgs = new ArrayList<>();
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = chatSnapshot.getValue(ChatMessage.class);
                    if (chatMessage != null) {
                        msgs.add(chatMessage);
                    }
                }
                Comparator<ChatMessage> timeComparator = Comparator.comparingLong(
                                                                ChatMessage::getMessageTime);
                msgs.sort(timeComparator);
                adapter = new MessageListAdapter(root.getContext(), msgs,colorsArray);
                listView.setAdapter(adapter);
                listView.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores
            }
        });
    }
}