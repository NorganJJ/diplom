package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nj.classes.Company;
import com.example.nj.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CompanySettingsActivity extends AppCompatActivity {

    EditText name, tags, view, callingUser, ogrn, adminPassword;

    Button updateus, back;

    DatabaseReference companies;
    Query query;
    String mail, companyUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        companyUid = (String)getIntent().getExtras().get("companyUid");

        name = findViewById(R.id.name);
        tags = findViewById(R.id.tags);
        view = findViewById(R.id.companyView);
        callingUser = findViewById(R.id.calling_user);
        ogrn = findViewById(R.id.ogrn);
        adminPassword = findViewById(R.id.admin_password);
        updateus = findViewById(R.id.update_data);

        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        companies = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Companies");
        query = companies.orderByChild("uid").equalTo(companyUid);

        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Company company = ds.getValue(Company.class);
                    assert company != null;
                    if(companyUid.equals(company.uid)){
                        name.setText(company.companyName);
                        tags.setText(company.companyTags);
                        view.setText(company.companyView);
                        callingUser.setText(company.callingUser);
                        ogrn.setText(company.ogrn);
                        adminPassword.setText(company.adminPassword);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        companies.addValueEventListener(vlist);

        updateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener vlist1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String key = ds.getKey();
                            Company company = ds.getValue(Company.class);
                            assert company != null;
                            if (companyUid.equals(company.uid)) {
                                DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Companies").child(key);
                                HashMap map = new HashMap();
                                map.put("companyName", name.getText().toString());
                                map.put("companyTags", tags.getText().toString());
                                map.put("companyView", view.getText().toString());
                                map.put("callingUser", callingUser.getText().toString());
                                map.put("ogrn", ogrn.getText().toString());
                                map.put("adminPassword", adminPassword.getText().toString());
                                item.updateChildren(map);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                companies.addValueEventListener(vlist1);
            }
        });
    }
}