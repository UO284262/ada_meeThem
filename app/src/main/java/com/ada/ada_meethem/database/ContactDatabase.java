package com.ada.ada_meethem.database;

import androidx.room.Database;

import com.ada.ada_meethem.database.entities.Contact;

@Database(entities = Contact.class, version = 1, exportSchema = false)
public class ContactDatabase {

}
