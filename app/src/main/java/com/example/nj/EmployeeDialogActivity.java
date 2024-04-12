package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nj.classes.Company;
import com.example.nj.classes.Dialog;
import com.example.nj.classes.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EmployeeDialogActivity extends AppCompatActivity {

    private FirebaseListAdapter<Dialog> adapter;

    private Button teams, admin;

    private FloatingActionButton newMsg;
    BottomNavigationView bottomNavigationView;

    String userMail, companyUid;

    int Code;

    DatabaseReference db, db1, db2;

    int empty;

    DatabaseReference users;
    FirebaseAuth mAuth;

    Query query, queryfinder;

    DatabaseReference dialogs;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dialog);

        teams = findViewById(R.id.team_speak);
        bottomNavigationView = findViewById(R.id.bottom_nav1);
        listView = findViewById(R.id.list_of_dialogs);
        newMsg = findViewById(R.id.fab);

        db = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
        db1 = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs");
        dialogs = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs");
        query = dialogs.orderByChild("actualUser").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        displayUsers();
        listView.setClickable(true);

        newMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("companyUid", companyUid);
                startActivity(intent);
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView user_mail;
                user_mail = view.findViewById(R.id.user_mail);
                getDialog(user_mail.getText().toString());
            }
        });

        companyUid = (String)getIntent().getExtras().get("companyUid");

        bottomNavigationView.setSelectedItemId(R.id.frag_item);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.frag_item:
                        return true;
                    case R.id.frag_itemm:
                        Intent intent = new Intent(getApplicationContext(), CompanyProfileAuthorizeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("companyUid", companyUid);
                        startActivity(intent);
                        finish();
                        return true;
                }
                return false;
            }
        });

        teams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("name_chat", "Общий чат");
                intent.putExtra("type", "Общий чат");
                intent.putExtra("companyUid", companyUid);
                intent.putExtra("dialogUid", "null");
                startActivity(intent);
            }
        });
    }

    private void displayUsers(){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(2, Color.BLACK);
        listView.setBackground(drawable);
        adapter = new FirebaseListAdapter<Dialog>(this, Dialog.class, R.layout.list_dialog, query)
        {
            @Override
            protected void populateView(View v, Dialog model, int position) {
                empty = 1;
                TextView user_mail, user_name, user_surn, new_messages;
                ImageView user_photo;
                user_mail = v.findViewById(R.id.user_mail);
                user_name = v.findViewById(R.id.user_name);
                user_photo = v.findViewById(R.id.user_logo);
                user_surn = v.findViewById(R.id.user_surn);
                new_messages = v.findViewById(R.id.countmessage);
                String dialog_uid = model.uid;
                String receiver_user = model.userReceiver;
                new_messages.setText( "Сообщение: " + model.lastMessage);
                nameUser(user_surn, user_name, user_mail, user_photo, dialog_uid, receiver_user);
            }
        };
        listView.setAdapter(adapter);
    }

    public void nameUser(TextView surname, TextView name, TextView mail, ImageView user_photo, String dialogUid, String receiverUser){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(receiverUser.equals(user.mail)){
                        name.setText(user.realName);
                        mail.setText(user.mail);
                        surname.setText(user.surName);
                        Picasso.get()
                                .load(user.photo)
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

    public void getDialog(String receiverUser){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Dialog dialog = ds.getValue(Dialog.class);
                    assert dialog != null;
                    if(receiverUser.equals(dialog.userReceiver)){
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("name_chat", "Чат с " + receiverUser);
                        intent.putExtra("type", "ЛС");
                        intent.putExtra("dialogUid", dialog.uid);
                        intent.putExtra("companyUid", "null");
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db1.addValueEventListener(vlist);
    }
}