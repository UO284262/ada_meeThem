package com.ada.ada_meethem.database.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact implements Parcelable {
    @NonNull
    @PrimaryKey
    private String contactNumber;

    @NonNull
    private String photoUrl;

    private String contactName;

    public Contact() {}

    protected Contact(Parcel in) {
        contactNumber = in.readString();
        photoUrl = in.readString();
        contactName = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(contactNumber);
        dest.writeString(photoUrl);
        dest.writeString(contactName);
    }
}
