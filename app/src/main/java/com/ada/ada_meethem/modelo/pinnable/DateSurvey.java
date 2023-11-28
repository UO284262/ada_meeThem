package com.ada.ada_meethem.modelo.pinnable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DateSurvey implements Pinnable{

    private String id;
    private HashMap<Date,Integer> dates;

    private boolean open;

    public DateSurvey(List<Date> dates) {
        this.dates = new HashMap<Date,Integer>();
        for(Date date : dates) this.dates.put(date, 0);
        id = UUID.randomUUID().toString();
        open = true;
    }

    public void voteDate(Date date) {
        if(open)
            this.dates.put(date, dates.get(date) + 1);
    }

    public void unvoteDate(Date date) {
        if(open) {
            int currentVotes = dates.get(date);
            this.dates.put(date, currentVotes != 0 ? currentVotes - 1 : 0);
        }
    }

    public void closeSurvey() {
        this.open = false;
    }

    public boolean getOpen() {
        return open;
    }

    public void setDates(HashMap<Date, Integer> dates) {
        this.dates = dates;
    }

    public HashMap<Date, Integer> getDates() {
        return dates;
    }

    public String getId() {
        return this.id;
    }
}
