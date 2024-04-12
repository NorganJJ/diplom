package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nj.classes.Company;
import com.example.nj.classes.Dialog;
import com.example.nj.classes.Invitation;
import com.example.nj.classes.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class ShowUsersActivity extends AppCompatActivity {

    ListView listView;
    private FirebaseListAdapter<User> adapter;

    DatabaseReference users, dialogs;
    FirebaseAuth mAuth;

    DatabaseReference db, db1, db2;

    Query query, query1;

    Button chatBtn;

    String curUser, companyUid, m_Text;

    FloatingActionButton btnNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        listView = findViewById(R.id.list_of_users);
        btnNew = findViewById(R.id.addUser);

        companyUid = (String) getIntent().getExtras().get("companyUid");

        users = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
        dialogs = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs");
        query = users.orderByChild("company_UID").equalTo(companyUid);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser().getEmail();

        displayUsers();

        listView.setClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(ShowUsersActivity.this)
                        .setTitle("Удалить сотрудника")
                        .setMessage("Вы уверены что хотите удалить сотрудника?")

                        .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Добавление сотрудника с компанию
                                TextView user_mail;
                                user_mail = view.findViewById(R.id.user_mail);
                                // Поиск по имени чтобы найти айди компании
                                ValueEventListener vlist = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren())
                                        {
                                            User user = ds.getValue(User.class);
                                            assert user != null;
                                            if(user_mail.getText().equals(user.mail)){
                                                if(curUser.equals(user.mail)){
                                                    Toast.makeText(ShowUsersActivity.this, "Вы не можете удалить себя", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    String userMail = user.mail;
                                                    deleteUser(userMail);
                                                    deleteDialogs(userMail);
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                users.addValueEventListener(vlist);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowUsersActivity.this)
                        .setTitle("Добавить пользователя")
                        .setMessage("Введите эл. почту пользователя")
                        .setIcon(android.R.drawable.ic_dialog_alert);

                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        if(m_Text == "")
                            return;
                        ValueEventListener vlist = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren())
                                {
                                    User user = ds.getValue(User.class);
                                    assert user != null;
                                    if(m_Text.equals(user.mail)){
                                        if(companyUid.equals(user.company_UID)){
                                            Toast.makeText(ShowUsersActivity.this, "Данный пользователь уже состоит в вашей организации", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            String uid = String.valueOf(UUID.randomUUID());
                                            FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Invitations").push().setValue(new Invitation(uid, companyUid, m_Text));
                                            Toast.makeText(ShowUsersActivity.this, "Приглашение отправлено", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ShowUsersActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                            }
                        };
                        users.addListenerForSingleValueEvent(vlist);
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void deleteUser(String userMail){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(userMail.equals(user.mail)){
                        DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(key);
                        HashMap map = new HashMap();
                        map.put("company_UID", "null");
                        item.updateChildren(map);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        users.addListenerForSingleValueEvent(vlist);
    }

    private void deleteDialogs(String userMail){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Dialog dialog = ds.getValue(Dialog.class);
                    assert dialog != null;
                    if(userMail.equals(dialog.actualUser)){
                        DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs").child(key);
                        item.removeValue();
                    }
                    if(userMail.equals(dialog.userReceiver)){
                        DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs").child(key);
                        item.removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dialogs.addListenerForSingleValueEvent(vlist);
    }

    private void displayUsers(){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(2, Color.BLACK);
        listView.setBackground(drawable);
        adapter = new FirebaseListAdapter<User>(this, User.class, R.layout.list_user, query)
        {
            @Override
            protected void populateView(View v, User model, int position) {
                TextView user_surn, user_name, user_mail;
                ImageView user_photo;
                user_surn = v.findViewById(R.id.user_surn);
                user_name = v.findViewById(R.id.user_name);
                user_photo = v.findViewById(R.id.user_logo);
                user_mail = v.findViewById(R.id.user_mail);
                user_surn.setText(model.surName);
                user_name.setText(model.realName);
                user_mail.setText(model.mail);
                Picasso.get()
                        .load(model.photo)
                        .into(user_photo);
            }
        };
        listView.setAdapter(adapter);
    }
}