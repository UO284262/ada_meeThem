package com.ada.ada_meethem.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.RegistroActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final int COD_SEL_IMAGE = 300;

    CircleImageView profilePhoto;
    TextView userName;
    ImageView editUsername;
    TextView phoneNumber;
    Button logout;

    FirebaseAuth mAuth;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño para este fragmento
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Obtener referencias a los elementos del diseño
        profilePhoto = view.findViewById(R.id.profilePhoto);
        userName = view.findViewById(R.id.username);
        editUsername = view.findViewById(R.id.editUsername);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        logout = view.findViewById(R.id.logout);

        mAuth = FirebaseAuth.getInstance();

        cargarUsuarioYNumero(mAuth.getCurrentUser().getPhoneNumber());

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSelectorImagen();
            }
        });

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioNombre();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        return view;
    }

    private void cargarUsuarioYNumero(String phone) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

        databaseReference.orderByChild("phoneNumber").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                            String username = userSnapshot.child("username").getValue(String.class);
                            String url = userSnapshot.child("profileImage").getValue(String.class);

                            if(url != "null")
                                Picasso.get().load(url).into(profilePhoto);
                            userName.setText(username);
                            phoneNumber.setText(phone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar errores si es necesario
                    }
                });
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, COD_SEL_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COD_SEL_IMAGE && resultCode == -1 && data != null && data.getData() != null) {
            // Aquí obtienes la URI de la imagen seleccionada
            Uri selectedImageUri = data.getData();

            // Puedes cargar la imagen en tu CircleImageView usando Picasso
            Picasso.get().load(selectedImageUri).into(profilePhoto);

            subirImagen(selectedImageUri);
        }
    }

    private void subirImagen(Uri selectedImageUri) {
        // Obtiene una referencia a tu Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String path = "profilePhotos/profile-" + mAuth.getUid() + "-" + System.currentTimeMillis();

        // Crea una referencia para la imagen en Storage
        StorageReference imageReference = storageReference.child(path);

        // Sube la imagen
        imageReference.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                actualizarFotoEnBD(mAuth.getCurrentUser().getPhoneNumber(), uri.toString());
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

    private void actualizarFotoEnBD(String phoneNumber, String imageUrl) {
        // Obtiene una referencia a tu base de datos
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

        // Busca el usuario por su número de teléfono
        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Obtén la referencia al primer hijo (debería ser único si phoneNumber es único)
                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                            // Actualiza el campo "profileImage" con la URL de la imagen
                            userSnapshot.getRef().child("profileImage").setValue(imageUrl);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Maneja errores si es necesario
                    }
                });
    }

    private void cambioNombre() {
        // Obtén la referencia al contexto de la actividad
        Activity activity = getActivity();
        if (activity != null) {
            // Infla el layout personalizado
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_dialog_layout, null);

            // Crea el dialogo personalizado
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogView);

            // Configura los botones del AlertDialog
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Obtiene el nuevo nombre de usuario ingresado por el usuario
                    EditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
                    String newUserName = usernameEditText.getText().toString();

                    // Actualiza el nombre de usuario en la interfaz de usuario y en la base de datos
                    userName.setText(newUserName);
                    actualizarNombreDeUsuarioEnBD(mAuth.getCurrentUser().getPhoneNumber(), newUserName);
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Muestra el AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void actualizarNombreDeUsuarioEnBD(String phoneNumber, String newUserName) {
        // Obtiene una referencia a tu base de datos
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

        // Busca el usuario por su número de teléfono
        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Obtén la referencia al primer hijo (debería ser único si phoneNumber es único)
                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                            // Actualiza el campo "profileImage" con la URL de la imagen
                            userSnapshot.getRef().child("username").setValue(newUserName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Maneja errores si es necesario
                    }
                });
    }

}