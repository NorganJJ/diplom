package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nj.classes.Company;
import com.example.nj.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class RegistrationCompanyActivity extends AppCompatActivity {

    private EditText company_name, company_tags, company_view, password_company;
    private Button butt_reg;
    private String name, tags, view, password;
    private FirebaseAuth firebaseAuth;

    FirebaseStorage storage, storage1;
    StorageReference storageReference, storageReference1;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_company);

        company_name = findViewById(R.id.companyName);
        company_tags = findViewById(R.id.tagsCompany);
        company_view = findViewById(R.id.viewCompany);
        password_company = findViewById(R.id.passwordCompany);
        butt_reg = findViewById(R.id.buttonReg);
        db = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");

        firebaseAuth = FirebaseAuth.getInstance();

        butt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validData();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void validData() throws ParseException {
        name = company_name.getText().toString().trim();
        tags = company_tags.getText().toString().trim();
        view = company_view.getText().toString().trim();
        password = password_company.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            company_name.setError("Название компании должно быть заполнено");
        }else if(TextUtils.isEmpty(tags)){
            company_tags.setError("Укажите теги компании");
        } else if(TextUtils.isEmpty(view)){
            company_view.setError("Добавьте описание деятельности комании");
        }else if(TextUtils.isEmpty(password)){
            password_company.setError("Напишите пароль администратора для управления данными организации");
        }else if(password.length() < 6){
            password_company.setError("Пароль должен быть не менее 6-ти символов");
        }
        else{
            companyRegistration();
        }
    }


    private void companyRegistration() throws ParseException {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String currentUserEmail = firebaseUser.getEmail();
        Toast.makeText(RegistrationCompanyActivity.this,"Компания " + name+" зарегистрирована", Toast.LENGTH_SHORT)
                .show();
        String uid = String.valueOf(UUID.randomUUID());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        date = dateFormat.parse(String.valueOf(new Date().getDate() + "." + new Date().getMonth() + ".2024"));
        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Companies").push().setValue(new Company(uid, name, "000000000", view, tags, date, ".", "https://firebasestorage.googleapis.com/v0/b/new-journey-528fd.appspot.com/o/Users%2FAccount-User-PNG-Clipart.png?alt=media&token=cc307523-6837-4124-9835-23ee73ba364a", password));
        addUser(uid, currentUserEmail);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addUser(String companyUid, String userEmail) throws ParseException{
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    User users = ds.getValue(User.class);
                    assert users != null;
                    if(userEmail.equals(users.mail)){
                        DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(key);
                        HashMap map = new HashMap();
                        map.put("company_UID", companyUid);
                        item.updateChildren(map);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.addValueEventListener(vlist);
    }
}