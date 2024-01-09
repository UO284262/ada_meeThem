package com.ada.ada_meethem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ada.ada_meethem.database.entities.Contact;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistroActivity extends AppCompatActivity {

    private static final int COD_SEL_IMAGE = 300;
    private Uri selectedImageUriForUpload;

    CircleImageView profilePhoto;
    EditText newUsername;
    Button registerButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();

        profilePhoto = findViewById(R.id.profilePhoto);
        newUsername = findViewById(R.id.newUsername);
        registerButton = findViewById(R.id.registerButton);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { abrirSelectorImagen(); }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, COD_SEL_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COD_SEL_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Aquí obtienes la URI de la imagen seleccionada
            Uri selectedImageUri = data.getData();

            // Puedes cargar la imagen en tu CircleImageView usando Picasso
            Picasso.get().load(selectedImageUri).into(profilePhoto);

            // Guarda la URI para usarla al registrar el usuario
            selectedImageUriForUpload = selectedImageUri;
        }
    }


    private void registrarUsuario() {
        // Obtener el nombre de usuario ingresado
        String username = newUsername.getText().toString().trim();

        // Obtener el número de teléfono del usuario actual
        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        // Verificar que el nombre de usuario no esté vacío
        if (!username.isEmpty()) {

            if(selectedImageUriForUpload != null) {
                Log.e("AAA", "aaaaaaaaaaaa");
                subirImagen(username, phoneNumber);
            } else {
                realizarRegistro(username, phoneNumber, "null");
            }

            // Mostrar un mensaje de éxito
            Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

            // Cerrar esta actividad y volver a MainActivity
            finish();
        } else {
            // Mostrar un mensaje de error si el nombre de usuario está vacío
            Toast.makeText(RegistroActivity.this, "Ingrese un nombre de usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void subirImagen(final String username, String phoneNumber) {
        // Obtiene una referencia a tu Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String path = "profilePhotos/profile-" + mAuth.getUid() + "-" + System.currentTimeMillis();

        // Crea una referencia para la imagen en Storage
        StorageReference imageReference = storageReference.child(path);

        // Sube la imagen
        imageReference.putFile(selectedImageUriForUpload)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                realizarRegistro(username, phoneNumber, uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Maneja el error de la subida de la imagen
                    }
                });
    }

    private void realizarRegistro(String username, String phoneNumber, String imageUrl) {
        // Crear una referencia a la base de datos
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

        // Crear un nuevo objeto Usuario con el nombre y el número de teléfono
        Contact usuario = new Contact(phoneNumber,  imageUrl, username);

        // Agregar la información del usuario a la base de datos
        databaseReference.child(phoneNumber).setValue(usuario);

        selectedImageUriForUpload = null;
    }
}
