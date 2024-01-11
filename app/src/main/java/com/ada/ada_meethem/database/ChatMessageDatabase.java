package com.ada.ada_meethem.database;

import com.ada.ada_meethem.modelo.pinnable.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatMessageDatabase {
    public static void sendMessage(String planId, String text) {
        ChatMessage msg = new ChatMessage(text, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("chats").child(planId);
        // Escribe los datos en la base de datos
        chatReference.child(msg.getId()).setValue(msg);
    }

    public static DatabaseReference getReference(String planId) {
        return FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("chats").child(planId);
    }
}
