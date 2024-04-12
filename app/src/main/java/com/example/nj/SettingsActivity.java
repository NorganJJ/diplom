package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nj.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    EditText usName, usNick, ussurn, usAge, usSecond;

    Button updateus, back, updatepass;

    DatabaseReference users;
    Query query;
    String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usName = findViewById(R.id.realName);
        usNick = findViewById(R.id.nick);
        updatepass = findViewById(R.id.update_pass);
        back = findViewById(R.id.back);
        updateus = findViewById(R.id.update_data);
        ussurn = findViewById(R.id.settings_surname);
        usAge = findViewById(R.id.settings_age);
        usSecond = findViewById(R.id.settings_secondname);


        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        users = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
        query = users.orderByChild("mail").equalTo(mail);

        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    User users2 = ds.getValue(User.class);
                    assert users2 != null;
                    if(mail.equals(users2.mail)){
                        usName.setText(users2.realName);
                        usNick.setText(users2.nickName);
                        ussurn.setText(users2.surName);
                        usAge.setText(String.valueOf(users2.age));
                        usSecond.setText(users2.secondName);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        users.addValueEventListener(vlist);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        updateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener vlist1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String key = ds.getKey();
                            User users2 = ds.getValue(User.class);
                            assert users2 != null;
                            if (mail.equals(users2.mail)) {
                                DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(key);
                                HashMap map = new HashMap();
                                map.put("nickName", usNick.getText().toString());
                                map.put("surName", ussurn.getText().toString());
                                map.put("realName", usName.getText().toString());
                                map.put("secondName", usSecond.getText().toString());
                                map.put("age", Integer.parseInt(usAge.getText().toString()));
                                item.updateChildren(map);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                users.addValueEventListener(vlist1);
            }
        });
        updatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(mail);
                Toast.makeText(SettingsActivity.this, "Письмо о смене пароля направлено вам на электронную почту", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
}