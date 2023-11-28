package com.ada.ada_meethem.database;

import com.ada.ada_meethem.modelo.pinnable.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlanDatabase {
    public static DatabaseReference getReference(String planId) {
        return FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(planId).child("pinnedItems");
    }

    public static void unpinMessage(ChatMessage msg, String planId) {
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(planId).child("pinnedItems");
        // Escribe los datos en la base de datos
        chatReference.child(msg.getId()).removeValue();
    }

    public static void pinMessage(ChatMessage msg, String planId) {
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(planId).child("pinnedItems");
        // Escribe los datos en la base de datos
        chatReference.child(msg.getId()).setValue(msg);
    }
}
