package com.ada.ada_meethem.data;

import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageStorer {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    public void storeImage() {
        StorageReference storageRef = storage.getReference();
    }
}
