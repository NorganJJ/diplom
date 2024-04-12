package com.example.nj.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nj.classes.Company;
import com.example.nj.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> name;

    private final MutableLiveData<String> tags;

    private final MutableLiveData<String> image;

    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    DatabaseReference db = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Companies");
    DatabaseReference db1 = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");

    public HomeViewModel() {
        name = new MutableLiveData<>();
        tags = new MutableLiveData<>();
        image = new MutableLiveData<>();
        findUser();
    }

    private void findUser(){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(email.equals(user.mail)){
                        findCompany(user.company_UID);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db1.addValueEventListener(vlist);
    }

    private void findCompany(String uid){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Company company = ds.getValue(Company.class);
                    assert company != null;
                    if(uid.equals(company.uid)){
                        name.setValue(company.companyName);
                        tags.setValue(company.companyTags);
                        image.setValue(company.companyLogo);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.addValueEventListener(vlist);
    }

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getTag() {
        return tags;
    }

    public LiveData<String> getImage() {
        return image;
    }
}