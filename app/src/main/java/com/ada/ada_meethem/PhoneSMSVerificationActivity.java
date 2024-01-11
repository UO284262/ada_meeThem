package com.ada.ada_meethem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PhoneSMSVerificationActivity extends AppCompatActivity {

    EditText smsCode;
    TextView responseText;
    Button verifyBtn;

    FirebaseAuth mAuth;
    String intentAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_smsverification);

        smsCode = findViewById(R.id.smsCode);
        responseText = findViewById(R.id.responseText);
        verifyBtn = findViewById(R.id.verifyBtn);

        mAuth = FirebaseAuth.getInstance();
        intentAuth = getIntent().getStringExtra("auth");

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = smsCode.getText().toString();
                if (!code.isEmpty()) {
                    if (code.length() == 6) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(intentAuth, code);
                        iniciarSesion(credential);
                    } else {
                        responseText.setText(getText(R.string.code_length_error));
                        responseText.setTextColor(Color.RED);
                    }
                } else {
                    responseText.setText(getText(R.string.no_code));
                    responseText.setTextColor(Color.RED);
                }
            }

            private void iniciarSesion(PhoneAuthCredential credential) {
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verificarNuevoUsuario();
                        } else {
                            //Toast.makeText(PhoneSMSVerificationActivity.this, "Error de verificación", Toast.LENGTH_SHORT).show();
                            responseText.setText(getText(R.string.code_error));
                            responseText.setTextColor(Color.RED);
                        }
                    }
                });
            }

            private void verificarNuevoUsuario() {
                // Obtener el número de teléfono del usuario actual
                String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

                // Realizar una consulta a tu base de datos para ver si el número ya existe
                // Puedes usar Firebase Realtime Database o Firestore según tu configuración
                // Aquí se asume que estás utilizando Firebase Realtime Database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

                databaseReference.orderByChild("contactNumber").equalTo(phoneNumber)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // El número de teléfono ya existe, usuario ya registrado
                                    inicioMainActivity();
                                } else {
                                    // El número de teléfono no existe, es la primera vez que se registra
                                    abrirActivityRegistro();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Manejar errores si es necesario
                            }
                        });
            }

            private void abrirActivityRegistro() {
                // Si es la primera vez que se registra, abrir la actividad de registro
                Intent intent = new Intent(PhoneSMSVerificationActivity.this, RegistroActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            inicioMainActivity();
        }
    }

    private void inicioMainActivity() {
        Intent intent = new Intent(PhoneSMSVerificationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}