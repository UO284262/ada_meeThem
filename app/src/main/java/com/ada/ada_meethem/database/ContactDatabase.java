package com.ada.ada_meethem.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ada.ada_meethem.database.daos.ContactDAO;
import com.ada.ada_meethem.database.entities.Contact;

@Database(entities = { Contact.class }, version = 1, exportSchema = false)
public abstract class ContactDatabase extends RoomDatabase {
    public static final String DB_NOMBRE = "contacts.db";

    private static ContactDatabase db;

    // Singleton
    public static ContactDatabase getDatabase(Context applicationContext) {
        if (db == null) {
            /*
                allowMainThreadQueries() implica que utilizaremos el hilo principal.
                Esto es un crimen (bloqueamos la interfaz y demás problemas).
                Lo trabajaremos la semana que viene con Kotlin.
             */
            db = Room.databaseBuilder(applicationContext, ContactDatabase.class, DB_NOMBRE)
                    .allowMainThreadQueries()
                    .build();
        }
        return db;
    }

    public abstract ContactDAO getContactDAO();
}
