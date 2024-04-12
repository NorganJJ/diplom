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
import android.widget.Toast;

import com.example.nj.classes.Invitation;
import com.example.nj.classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {

    private EditText pass_reg, email_reg, pass_repeat;
    private Button butt_reg;
    private String email, password, passwordRepeat;
    private FirebaseAuth firebaseAuth;

    int check = 0;

    FirebaseStorage storage, storage1;
    StorageReference storageReference, storageReference1;

    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email_reg = findViewById(R.id.editTextEmail);
        pass_reg = findViewById(R.id.editTextPassword);
        pass_repeat = findViewById(R.id.editTextPasswordrepeat);
        butt_reg = findViewById(R.id.buttonReg);

        users = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");

        firebaseAuth = FirebaseAuth.getInstance();

        butt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validData();
            }
        });
    }

    private void validData(){
        check = 0;
        email = email_reg.getText().toString().trim();
        password = pass_reg.getText().toString().trim();
        passwordRepeat = pass_repeat.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_reg.setError("Неправильно введена почта");
        }else if (TextUtils.isEmpty(password)){
            pass_reg.setError("Пароль не должен быть пустым");
        }else if (password.length() <6){
            pass_reg.setError("Пароль должен содержать минимум 6 символов");
        } else if (passwordRepeat.equals(password)) {
                firebaseCheck(email);
                if(check == 0){
                    firebaseRegistration();
                }
            }
        else{
            pass_repeat.setError("Пароли должны быть одинаковыми");
        }
    }

    private void firebaseCheck(String email){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(email.equals(user.mail)){
                        Toast.makeText(RegistrationActivity.this, "Данная почта уже используется", Toast.LENGTH_SHORT).show();
                        check +=1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegistrationActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        };
        users.addListenerForSingleValueEvent(vlist);
    }

    private void firebaseRegistration(){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String currentUserEmail = firebaseUser.getEmail();
                        Toast.makeText(RegistrationActivity.this,"Пользователь " + currentUserEmail+" зарегистрирован", Toast.LENGTH_SHORT)
                                .show();
                        String uid = String.valueOf(UUID.randomUUID());
                        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").push().setValue(new User(uid, email, password, 18, "User" + uid, ".", ".", ".", "https://firebasestorage.googleapis.com/v0/b/new-journey-528fd.appspot.com/o/Users%2FAccount-User-PNG-Clipart.png?alt=media&token=cc307523-6837-4124-9835-23ee73ba364a", "null"));
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}