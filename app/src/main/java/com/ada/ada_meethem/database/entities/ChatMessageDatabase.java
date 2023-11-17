package com.ada.ada_meethem.database.entities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChatMessageDatabase {
    public static void sendMessage(String text) {
        FirebaseDatabase.getInstance()
                .getReference()
                .push()
                .setValue(new ChatMessage(text,
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName())
                );
    }
}
