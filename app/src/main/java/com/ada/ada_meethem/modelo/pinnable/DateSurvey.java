package com.ada.ada_meethem.modelo.pinnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DateSurvey implements Pinnable {

    private String id;
    private HashMap<String, Integer> dates;

    private boolean open;

    public DateSurvey() {
    }

    public DateSurvey(List<String> dates) {
        this.dates = new HashMap<String, Integer>();
        for (String date : dates) this.dates.put(date, 0);
        id = "sdt" + UUID.randomUUID().toString();
        open = true;
    }

    public void voteDate(String date) {
        if (open)
            this.dates.put(date, dates.get(date) + 1);
    }

    public void unvoteDate(String date) {
        if (open && dates.containsKey(date)) {
            int currentVotes = dates.get(date);
            this.dates.put(date, currentVotes != 0 ? currentVotes - 1 : 0);
        }
    }

    public String mostVoted() {
        int max = 0;
        String date = "";
        for (String dt : dates.keySet()) {
            if (dates.get(dt) >= max) {
                max = dates.get(dt);
                date = dt;
            }
        }
        return date;
    }

    public void addDate(String date) {
        this.dates.put(date, 0);
    }

    public void removeDate(String date) {
        this.dates.remove(date);
    }

    public void closeSurvey() {
        this.open = false;
    }

    public boolean getOpen() {
        return open;
    }

    public void setDates(HashMap<String, Integer> dates) {
        this.dates = dates;
    }

    public HashMap<String, Integer> getDates() {
        return dates;
    }

    public String getId() {
        return this.id;
    }
}
