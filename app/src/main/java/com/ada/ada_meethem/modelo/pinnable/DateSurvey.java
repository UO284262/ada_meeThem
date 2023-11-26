package com.ada.ada_meethem.modelo.pinnable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DateSurvey implements Pinnable{
    private HashMap<Date,Integer> dates;

    public DateSurvey(List<Date> dates) {
        this.dates = new HashMap<Date,Integer>();
        for(Date date : dates) this.dates.put(date, 0);
    }

    public void voteDate(Date date) {
        this.dates.put(date, dates.get(date) + 1);
    }

    public void unvoteDate(Date date) {
        int currentVotes = dates.get(date);
        this.dates.put(date, currentVotes != 0 ? currentVotes - 1 : 0);
    }

    public void setDates(HashMap<Date, Integer> dates) {
        this.dates = dates;
    }

    public HashMap<Date, Integer> getDates() {
        return dates;
    }
}
