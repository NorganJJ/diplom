package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nj.classes.Company;
import com.example.nj.classes.Invitation;
import com.example.nj.classes.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ShowInvitationsActivity extends AppCompatActivity {

    ListView listView;
    private FirebaseListAdapter<Invitation> adapter;
    DatabaseReference invitations;
    FirebaseAuth mAuth;

    DatabaseReference db, db1, db2;

    Query query, query1;

    Button chatBtn;

    String curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_invitations);

        listView = findViewById(R.id.list_of_users);

        invitations = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Invitations");
        query = invitations;

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser().getEmail();

        db = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Companies");
        db1 = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
        db2 = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Invitations");

        displayInvitations();
        listView.setClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(ShowInvitationsActivity.this)
                        .setTitle("Принять приглашение")
                        .setMessage("Вы уверены что хотите вступить в данную компанию?")

                        .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Добавление сотрудника с компанию
                                TextView company_name;
                                company_name = view.findViewById(R.id.user_name);
                                // Поиск по имени чтобы найти айди компании
                                ValueEventListener vlist = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren())
                                        {
                                            Company company = ds.getValue(Company.class);
                                            assert company != null;
                                            if(company_name.getText().equals(company.companyName)){
                                                String companyUid = company.uid;
                                                addUser(companyUid);
                                                deleteInvitation(companyUid);
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                db.addValueEventListener(vlist);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Убрать приглашение", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView company_name;
                                company_name = view.findViewById(R.id.user_name);
                                // Удаление заявки из списка
                                ValueEventListener vlist = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren())
                                        {
                                            Company company = ds.getValue(Company.class);
                                            assert company != null;
                                            if(company_name.getText().equals(company.companyName)){
                                                String companyUid = company.uid;
                                                deleteInvitation(companyUid);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                db.addValueEventListener(vlist);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void deleteInvitation(String companyUid){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Invitation invitation = ds.getValue(Invitation.class);
                    assert invitation != null;
                    if(curUser.equals(invitation.userReciever)){
                        if(companyUid.equals(invitation.companyRecieverUid)){
                            DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Invitations").child(key);
                            item.removeValue();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db2.addListenerForSingleValueEvent(vlist);
    }

    private void addUser(String companyUid){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(curUser.equals(user.mail)){
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
        db1.addValueEventListener(vlist);
    }


    private void displayInvitations(){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(2, Color.BLACK);
        listView.setBackground(drawable);
        adapter = new FirebaseListAdapter<Invitation>(this, Invitation.class, R.layout.list_user, query)
        {
            @Override
            protected void populateView(View v, Invitation model, int position) {
                TextView user_surn, user_name, user_mail;
                ImageView user_photo;
                user_surn = v.findViewById(R.id.user_surn);
                user_name = v.findViewById(R.id.user_name);
                user_photo = v.findViewById(R.id.user_logo);
                user_mail = v.findViewById(R.id.user_mail);
                String company_uid = model.companyRecieverUid;
                nameUser(user_surn, user_name, user_mail, user_photo, company_uid);
            }
        };
        listView.setAdapter(adapter);
    }

    public void nameUser(TextView surname, TextView name, TextView mail, ImageView user_photo, String companyUid){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Company company = ds.getValue(Company.class);
                    assert company != null;
                    if(companyUid.equals(company.uid)){
                        name.setText(company.companyName);
                        mail.setText(company.companyTags);
                        Picasso.get()
                                .load(company.companyLogo)
                                .into(user_photo);
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