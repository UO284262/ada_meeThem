package com.ada.ada_meethem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneIntroductionActivity extends AppCompatActivity {

    Spinner countryCode;
    EditText phoneNumber;
    Button sendNumberButton;
    TextView responseText;

    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_introduction);

        phoneNumber = findViewById(R.id.phoneNumber);
        sendNumberButton = findViewById(R.id.sendNumberButton);
        responseText = findViewById(R.id.responseText);
        countryCode = findViewById(R.id.countryCode);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCode.setAdapter(adapter);

        countryCode.setSelection(0);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");

        sendNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCountryCode = ((String) countryCode.getSelectedItem()).split(" ")[0];

                if(!phoneNumber.getText().toString().isEmpty()) {
                    if(isValidFormat(phoneNumber.getText().toString(), selectedCountryCode)) {
                        String phNumber = selectedCountryCode + phoneNumber.getText().toString();
                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(phNumber) // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(PhoneIntroductionActivity.this) // (optional) Activity for callback binding
                                        // If no activity is passed, reCAPTCHA verification can not be used.
                                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    } else {
                        responseText.setText(getText(R.string.format_error));
                        responseText.setTextColor(Color.RED);
                    }
                } else {
                    responseText.setText(getString(R.string.no_number));
                    responseText.setTextColor(Color.RED);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                iniciarSesion(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("ERROR VERIFICACIÓN", e.getMessage());
                responseText.setText(getText(R.string.verif_error));
                responseText.setTextColor(Color.RED);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                responseText.setText(getText(R.string.sended_sms));
                responseText.setTextColor(Color.BLACK);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PhoneIntroductionActivity.this, PhoneSMSVerificationActivity.class);
                        intent.putExtra("auth", s);
                        startActivity(intent);
                    }
                }, 1000);
            }
        };
    }

    private boolean isValidFormat(String phNumber, String selectedCountryCode) {
        if(selectedCountryCode.equals("+34") && phNumber.length() == 9)
            return true;
        if(selectedCountryCode.equals("+1") && phNumber.length() == 10)
            return true;
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            existenciaUsuario();
        }
    }

    private void iniciarSesion(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    existenciaUsuario();
                } else {
                    responseText.setText(task.getException().getMessage());
                    responseText.setTextColor(Color.RED);
                }
            }
        });
    }

    private void existenciaUsuario() {
        inicioMainActivity();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://meethem-8955a-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

        Query query = databaseReference.orderByChild("contactNumber").equalTo(mAuth.getCurrentUser().getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verifica si se encontró algún usuario
                boolean userExists = dataSnapshot.exists();

                if(!userExists) {
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(PhoneIntroductionActivity.this, PhoneIntroductionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de la consulta
            }
        });
    }

    private void inicioMainActivity() {
        Intent intent = new Intent(PhoneIntroductionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void inicioRegistroActivity() {
        Intent intent = new Intent(PhoneIntroductionActivity.this, RegistroActivity.class);
        startActivity(intent);
        finish();
    }

}