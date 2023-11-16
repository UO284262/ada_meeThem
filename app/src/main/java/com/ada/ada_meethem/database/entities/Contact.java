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

    public void setContactNumber(@NonNull String contactNumber) {
        this.contactNumber = contactNumber;
    }



    @NonNull
    public String getContactNumber() {
        return contactNumber;
    }



    public Contact(@NonNull String contactNumber, @NonNull String photoUrl) {
        this.contactNumber = contactNumber;
        this.photoUrl = photoUrl;
    }
}
