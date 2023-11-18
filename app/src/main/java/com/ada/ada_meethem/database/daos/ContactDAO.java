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
    Contact findByNumber(int contactNumber);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void add(Contact pelicula);

    @Delete
    void delete(Contact Pelicula);
}
