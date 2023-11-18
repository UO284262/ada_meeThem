package com.ada.ada_meethem.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {
    @NonNull
    @PrimaryKey
    private String contactNumber;

    @NonNull
    private String photoUrl;

    private String contactName;

    public void setContactNumber(@NonNull String contactNumber) {
        this.contactNumber = contactNumber;
    }



    @NonNull
    public String getContactNumber() {
        return contactNumber;
    }



    public Contact(@NonNull String contactNumber, @NonNull String photoUrl, String contactName) {
        this.contactNumber = contactNumber;
        this.photoUrl = photoUrl;
        this.contactName = contactName;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactNumber='" + contactNumber + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", contactName='" + contactName + '\'' +
                '}';
    }

    public void setPhotoUrl(@NonNull String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @NonNull
    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getContactName() {
        return contactName;
    }
}
