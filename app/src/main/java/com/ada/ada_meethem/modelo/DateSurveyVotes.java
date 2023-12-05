package com.ada.ada_meethem.modelo;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DateSurveyVotes {
    private String id;

    private HashMap<String,String> votes = new HashMap<String,String>();

    public DateSurveyVotes() {}

    public DateSurveyVotes(HashMap<String,String> votes) {
        this.votes = votes;
        id = UUID.randomUUID().toString();
    }

    public void setVotes(HashMap<String, String> votes) {
        this.votes = votes;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, String> getVotes() {
        return votes;
    }

    public void vote(String date) {
        votes.put(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),date);
    }
    public void unvote(String date) {
        votes.remove(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
    }

}
