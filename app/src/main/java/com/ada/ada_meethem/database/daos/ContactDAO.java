package com.ada.ada_meethem.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ada.ada_meethem.database.entities.Contact;

import java.util.List;

@Dao
public interface ContactDAO {
    @Query("SELECT * FROM contacts")
    List<Contact> getAll();

    @Query("SELECT * FROM contacts WHERE contactNumber = (:contactNumber)")
    Contact findByNumber(String contactNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("DELETE FROM contacts")
    void deleteAll();
}
