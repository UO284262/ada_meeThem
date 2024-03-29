package com.ada.ada_meethem.database;

import com.ada.ada_meethem.modelo.DateSurveyVotes;
import com.ada.ada_meethem.modelo.Plan;
import com.ada.ada_meethem.modelo.pinnable.ChatMessage;
import com.ada.ada_meethem.modelo.pinnable.DateSurvey;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlanDatabase {
    public static DatabaseReference getReference(String planId) {
        return FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(planId);
    }

    public static DatabaseReference getReference() {
        return FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans");
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

    public static void pinDateSurvey(DateSurvey survey, String planId) {
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(planId).child("pinnedItems");
        // Escribe los datos en la base de datos
        chatReference.child(survey.getId()).setValue(survey);
    }

    public static void voteDate(DateSurveyVotes votes, String planId) {
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(planId).child("surveyVotes");
        // Escribe los datos en la base de datos
        chatReference.child(votes.getId()).setValue(votes);
    }

    public static void closeSurvey(String surveyId, String planId, String date) {
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(planId);
        // Escribe los datos en la base de datos
        chatReference.child("pinnedItems").child(surveyId).child("open").setValue(false);
        chatReference.child("fecha").setValue(date);
    }

    public static void confirmPlan(Plan plan) {
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(plan.getPlanId()).child("confirmed");
        // Escribe los datos en la base de datos
        chatReference.setValue(plan.getConfirmed());
    }

    public static void exitPlan(Plan plan) {
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(plan.getPlanId()).child("enlisted");
        // Escribe los datos en la base de datos
        chatReference.setValue(plan.getEnlisted());
    }

    public static void setEnlisted(Plan plan) {
        DatabaseReference chatReference = FirebaseDatabase
                .getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("plans").child(plan.getPlanId()).child("enlisted");
        // Escribe los datos en la base de datos
        chatReference.setValue(plan.getEnlisted());
    }
}
