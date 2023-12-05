package com.ada.ada_meethem.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Plan implements Parcelable {

    private String imageUrl;
    private String title;
    private Person creator;
    private List<Person> enlisted;

    private List<Person> invited;
    private boolean open;
    private int maxPeople;
    private String planId;

    public Plan(String title, Person creator, int maxPeople, String imageUrl) {
        this.title = title;
        this.creator = creator;
        this.enlisted = new ArrayList<>();
        this.open = true;
        this.maxPeople = maxPeople;
        this.imageUrl = imageUrl;
        this.planId = UUID.randomUUID().toString();
    }


    public Plan(String title, Person creator, int maxPeople, String imageUrl, List<Person> invited) {
        this(title,creator,maxPeople,imageUrl);
        this.invited = invited;
    }

    public void addToPlan(Person person) {
        if(!enlisted.contains(person) && open && enlisted.size() <= maxPeople)
            enlisted.add(person);
    }

    public void removeFromPlan(Person person) {
        if(enlisted.contains(person)) enlisted.remove(person);
    }

    public void closePlan(Person person) {
        if(person.equals(creator)) this.open = false;
    }

    public void openPlan(Person person) {
        if(person.equals(creator)) this.open = true;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public void setEnlisted(List<Person> enlisted) {
        this.enlisted = enlisted;
    }

    public void setInvited(List<Person> invited) {
        this.invited = invited;
    }

    public String getTitle() {
        return title;
    }

    public Person getCreator() {
        return creator;
    }

    public List<Person> getEnlisted() {
        return new ArrayList<>(enlisted);
    }

    public List<Person> _getEnlisted() {
        return enlisted;
    }

    public List<Person> getInvited() {
        return new ArrayList<>(invited);
    }

    public List<Person> _getInvited() {
        return invited;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPlanId() {
        return this.planId;
    }

    public void setPlanId(String id) {
        this.planId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imageUrl);
        dest.writeList(enlisted);
        dest.writeInt(maxPeople);
        dest.writeString(planId);
        dest.writeByte((byte) (open ? 1 : 0));
        dest.writeParcelable(creator,flags);
    }
    protected Plan(Parcel in) {
        title = in.readString();
        imageUrl = in.readString();
        in.readList(enlisted, getClass().getClassLoader());
        maxPeople = in.readInt();
        planId = in.readString();
        open = in.readByte() != 0;
        creator = in.readParcelable(Person.class.getClassLoader());
    }

    public static final Creator<Plan> CREATOR = new Creator<Plan>() {
        @Override
        public Plan createFromParcel(Parcel in) {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };

}
