package com.ada.ada_meethem.util;

import com.ada.ada_meethem.database.entities.Contact;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Util {
    public static Contact getCurrentUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
        String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Contact user = null;
        Task<DataSnapshot> task = databaseReference.child(phone).get();
        while (!task.isComplete()) {
        }
        return task.getResult().getValue(Contact.class);
    }
}
