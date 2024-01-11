package com.ada.ada_meethem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ada.ada_meethem.database.entities.Contact;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

    TextView responseText;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();

        profilePhoto = findViewById(R.id.profilePhoto);
        newUsername = findViewById(R.id.newUsername);
        registerButton = findViewById(R.id.registerButton);

        responseText = findViewById(R.id.responseText);

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

        // Verificar que el nombre de usuario tenga la longitud correcta
        if (correctLength(username)) {

            if (!validChars(username)) {
                responseText.setText(getText(R.string.username_illegal_chars));
                responseText.setTextColor(Color.RED);
            } else {
                // Realizar la verificación asincrónica del usuario existente
                usuarioExistente(username, new UserExistenceCallback() {
                    @Override
                    public void onUserExistenceCheck(boolean userExists) {
                        if (userExists) {
                            // El usuario ya existe
                            responseText.setText(getText(R.string.existing_username));
                            responseText.setTextColor(Color.RED);
                        } else {
                            // El usuario no existe, continuar con el registro
                            if (selectedImageUriForUpload != null) {
                                subirImagen(username, phoneNumber);
                            } else {
                                realizarRegistro(username, phoneNumber, "null");
                            }

                            // Mostrar un mensaje de éxito
                            Toast.makeText(RegistroActivity.this, getText(R.string.correct_register), Toast.LENGTH_SHORT).show();
                            // Cerrar esta actividad e ir a MainActivity
                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }

        } else if(!username.isEmpty()) {
            // Mostrar un mensaje de error si el nombre de usuario tiene longitudes ilegales
            responseText.setText(getText(R.string.username_length_error));
            responseText.setTextColor(Color.RED);
        } else {
            // Mostrar un mensaje de error si el nombre de usuario está vacío
            responseText.setText(getText(R.string.username_empty));
            responseText.setTextColor(Color.RED);
        }
    }

    public interface UserExistenceCallback {
        void onUserExistenceCheck(boolean userExists);
    }

    public static void usuarioExistente(String username, UserExistenceCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

        Query query = databaseReference.orderByChild("contactName").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verifica si se encontró algún usuario
                boolean userExists = dataSnapshot.exists();

                // Llama al método callback con el resultado
                callback.onUserExistenceCheck(userExists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de la consulta
            }
        });
    }

    public static boolean correctLength(String username) {
         return (username.length() >= 3 && username.length() <= 16);
    }

    public static boolean validChars(String username) {
        for(int i = 0; i < username.length(); i++) {
            char current = username.charAt(i);

            if(!Character.isLetterOrDigit(current) && current != '-' && current != '_' && current != '.')
                return false;
        }
        return true;
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
