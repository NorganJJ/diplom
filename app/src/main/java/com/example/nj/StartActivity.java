package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private Button signIn;
    private ImageButton signUp;

    FirebaseAuth firebaseAuth;

    private EditText emailText, passwordText;

    String email, password;

    Button updatepass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        signIn = findViewById(R.id.buttonLogin);
        signUp = findViewById(R.id.buttonRegister);

        emailText = findViewById(R.id.editTextEmail);
        passwordText = findViewById(R.id.editTextPassword);
        updatepass = findViewById(R.id.btnForgotPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {

        }
        else{
            Toast.makeText(StartActivity.this, "Вы авторизованы", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finishAffinity();
        }

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validData();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });

        updatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailText.setError("Неправильно введена почта");
                } else if (TextUtils.isEmpty(emailText.getText().toString())){
                    emailText.setError("Пожалуйста введите почту для смены пароля");
                }
                else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailText.getText().toString());
                    Toast.makeText(StartActivity.this, "Письмо о смене пароля направлено вам на электронную почту", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            }
        });
    }

    private void validData(){
        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Неправильно введена почта");
        } else if (TextUtils.isEmpty(password))
        {
            passwordText.setError("Пароль не должен быть пустым");
        }
        else
        {
            firebaseAuthorization();
        }
    }

    private void firebaseAuthorization(){
        firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StartActivity.this, "Неверные данные для авторизации", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}