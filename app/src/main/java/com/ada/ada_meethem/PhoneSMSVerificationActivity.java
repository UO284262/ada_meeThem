package com.ada.ada_meethem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

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
                if(!code.isEmpty()) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(intentAuth, code);
                    iniciarSesion(credential);
                } else {
                    Toast.makeText(PhoneSMSVerificationActivity.this, "Ingrese el código de verificación", Toast.LENGTH_SHORT).show();
                }
            }

            private void iniciarSesion(PhoneAuthCredential credential) {
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            inicioMainActivity();
                        } else {
                            Toast.makeText(PhoneSMSVerificationActivity.this, "Error de verificación", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            inicioMainActivity();
        }
    }

    private void inicioMainActivity() {
        Intent intent = new Intent(PhoneSMSVerificationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}