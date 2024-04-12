package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nj.classes.Company;
import com.example.nj.classes.Invitation;
import com.example.nj.classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.UUID;

public class InvitationActivity extends AppCompatActivity {

    EditText empMail;
    Button addWork;
    String companyUID;

    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        users = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
        empMail = findViewById(R.id.emplMail);
        addWork = findViewById(R.id.addNewWork);
        companyUID = (String) getIntent().getExtras().get("companyUid");

        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String invitemail = empMail.getText().toString();
                if(invitemail == "")
                    return;
                ValueEventListener vlist = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);
                            assert user != null;
                            if(invitemail.equals(user.mail)){
                                if(companyUID.equals(user.company_UID)){
                                    Toast.makeText(InvitationActivity.this, "Данный пользователь уже состоит в вашей организации", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    String uid = String.valueOf(UUID.randomUUID());
                                    FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Invitations").push().setValue(new Invitation(uid, companyUID, empMail.getText().toString()));
                                    Intent intent = new Intent(getApplicationContext(), EmployeeDialogActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("companyUid", companyUID);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(InvitationActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                };
                users.addValueEventListener(vlist);
            }
        });
    }
}