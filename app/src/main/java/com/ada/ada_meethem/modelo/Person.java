package com.ada.ada_meethem.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Person implements Parcelable {
    private String username;

    public Person(String username) {
        this.username = username;
    }

    protected Person(Parcel in) {
        username = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
    }
}
