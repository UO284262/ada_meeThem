package com.ada.ada_meethem.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.ada.ada_meethem.database.entities.Contact;

import java.util.ArrayList;
import java.util.List;

public class Plan implements Parcelable {

    private String imageUrl;
    private String title;
    private Contact creator;
    private List<String> confirmed = new ArrayList<>();
    private List<String> enlisted;
    private String fecha;
    private int maxPeople;
    private String planId;

    public Plan() {
    }

    public Plan(String title, Contact creator, int maxPeople, String imageUrl, List<String> enlisted, String uuid, String fecha) {
        this.title = title;
        this.creator = creator;
        this.enlisted = new ArrayList<>(enlisted);
        confirmed.add(creator.getContactNumber());
        this.maxPeople = maxPeople;
        this.imageUrl = imageUrl;
        this.planId = uuid;
        this.fecha = fecha;
    }

    public void addToPlan(String person) {
        if (!enlisted.contains(person))
            enlisted.add(person);
    }

    public void confirmToPlan(String person) {
        if (!confirmed.contains(person) && confirmed.size() <= maxPeople)
            confirmed.add(person);
    }

    public void exitPlan(String person) {
        enlisted.remove(person);
    }

    public void removeFromPlan(Contact person) {
        if (enlisted.contains(person)) enlisted.remove(person);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreator(Contact creator) {
        this.creator = creator;
    }

    public void setEnlisted(List<String> enlisted) {
        this.enlisted = enlisted;
    }

    public void setConfirmed(List<String> confirmed) {
        this.confirmed = confirmed;
    }

    public String getTitle() {
        return title;
    }

    public Contact getCreator() {
        return creator;
    }

    public List<String> getEnlisted() {
        return new ArrayList<String>(enlisted);
    }

    public List<String> _getEnlisted() {
        return enlisted;
    }

    public List<String> getConfirmed() {
        return new ArrayList<String>(confirmed);
    }

    public List<String> _getConfirmed() {
        return confirmed;
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

    public String getFecha() {
        return this.fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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
        dest.writeParcelable(creator, flags);
    }

    protected Plan(Parcel in) {
        title = in.readString();
        imageUrl = in.readString();
        in.readList(enlisted, getClass().getClassLoader());
        maxPeople = in.readInt();
        planId = in.readString();
        creator = in.readParcelable(Contact.class.getClassLoader());
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
