package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nj.classes.Company;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompanyProfileAuthorizeActivity extends AppCompatActivity {

    Button nextadd, nextshow, nextcompany;
    EditText pass;
    DatabaseReference users;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String company_UID;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile_authorize);

        nextadd = findViewById(R.id.add_cwork);
        nextcompany = findViewById(R.id.change_company);
        pass = findViewById(R.id.adminpass);
        users = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Companies");
        company_UID = (String) getIntent().getExtras().get("companyUid");
        bottomNavigationView = findViewById(R.id.bottom_nav1);

        bottomNavigationView.setSelectedItemId(R.id.frag_itemm);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.frag_item:
                        Intent intent = new Intent(getApplicationContext(), EmployeeDialogActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("companyUid", company_UID);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.frag_itemm:
                        return true;
                }
                return false;
            }
        });

        nextadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curpass = pass.getText().toString();
                ValueEventListener vlist = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            Company company = ds.getValue(Company.class);
                            assert company != null;
                            if(company_UID.equals(company.uid)){
                                if(curpass.equals(company.adminPassword)){
                                    next();
                                }
                                else{
                                    Toast.makeText(CompanyProfileAuthorizeActivity.this, "Неверно указан пароль", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CompanyProfileAuthorizeActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                };
                users.addValueEventListener(vlist);
            }
        });

        /*nextshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curpass = pass.getText().toString();
                ValueEventListener vlist = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            Employee employee = ds.getValue(Employee.class);
                            assert employee != null;
                            if(curpass.equals(employee.employeeAdmin)){
                                if(curpass.length() > 6){
                                    nextshow();
                                }
                                else{
                                    Toast.makeText(AdminWindow.this, "Неверно указан пароль", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        Toast.makeText(AdminWindow.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminWindow.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                };
                users.addValueEventListener(vlist);
            }
        });*/

        nextcompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curpass = pass.getText().toString();
                ValueEventListener vlist = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            Company company = ds.getValue(Company.class);
                            assert company != null;
                            if(company_UID.equals(company.uid)){
                                if(curpass.equals(company.adminPassword)){
                                    nextComp();
                                }
                                else{
                                    Toast.makeText(CompanyProfileAuthorizeActivity.this, "Неверно указан пароль", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CompanyProfileAuthorizeActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                };
                users.addValueEventListener(vlist);
            }
        });
    }

    private void next(){
        Intent intent = new Intent(getApplicationContext(), ShowUsersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("companyUid", company_UID);
        startActivity(intent);
    }

    /*private void nextshow(){
        Intent intent = new Intent(getApplicationContext(), Show_Workers.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mail", userMail);
        intent.putExtra("code", Code);
        startActivity(intent);
    }*/

    private void nextComp(){
        Intent intent = new Intent(getApplicationContext(), CompanyProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("companyUid", company_UID);
        startActivity(intent);
    }
}