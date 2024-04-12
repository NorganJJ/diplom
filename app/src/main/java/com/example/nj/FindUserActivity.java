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
import android.widget.Toast;

import com.example.nj.classes.Company;
import com.example.nj.classes.Dialog;
import com.example.nj.classes.Invitation;
import com.example.nj.classes.Message;
import com.example.nj.classes.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FindUserActivity extends AppCompatActivity {

    ListView listView;
    private FirebaseListAdapter<User> adapter;

    DatabaseReference users;
    FirebaseAuth mAuth;

    DatabaseReference db, db1, db2;

    Query query, query1;

    Button chatBtn;

    String curUser, companyUid;

    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        listView = findViewById(R.id.list_of_users);

        companyUid = (String) getIntent().getExtras().get("companyUid");

        users = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
        query = users.orderByChild("company_UID").equalTo(companyUid);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser().getEmail();

        displayUsers();
        listView.setClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(FindUserActivity.this)
                        .setTitle("Написать пользователю")
                        .setMessage("Вы уверены что хотите написать данному пользователю?")

                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Добавление сотрудника с компанию
                                TextView user_mail;
                                user_mail = view.findViewById(R.id.user_mail);
                                check=0;
                                if(user_mail.getText().equals(curUser)){
                                    Toast.makeText(FindUserActivity.this, "Нельзя создать диалог с самим собой", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    checkDialogs(user_mail.getText().toString());
                                    if(check == 0){
                                        String uid = String.valueOf(UUID.randomUUID());
                                        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs").push().setValue(new Dialog(uid, curUser, user_mail.getText().toString(), "Пользователь " + curUser + " начал диалог", 0));
                                        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs").push().setValue(new Dialog(uid, user_mail.getText().toString(), curUser, "Пользователь " + curUser + " начал диалог", 0));
                                        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Message").push().setValue(
                                                new Message("Оповещение",
                                                        "Пользователь " + curUser + " начал диалог", uid, "ЛС", "null", "null"));
                                        Intent intent = new Intent(getApplicationContext(), EmployeeDialogActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("companyUid", companyUid);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void checkDialogs(String email){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Dialog dialog = ds.getValue(Dialog.class);
                    assert dialog != null;
                    if(email.equals(dialog.userReceiver)){
                        if(curUser.equals(dialog.actualUser)){
                            Toast.makeText(FindUserActivity.this, "Нельзя создавать диалоги повторно", Toast.LENGTH_SHORT).show();
                            check +=1;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FindUserActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        };
        users.addListenerForSingleValueEvent(vlist);
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
                ShapeableImageView user_photo;
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