package com.example.nj.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.nj.EmployeeDialogActivity;
import com.example.nj.ShowInvitationsActivity;
import com.example.nj.StartActivity;
import com.example.nj.classes.Invitation;
import com.example.nj.databinding.FragmentHomeBinding;
import com.example.nj.classes.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    ShapeableImageView imageView;

    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    DatabaseReference db = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Invitations");
    DatabaseReference db1 = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView name = binding.companyName;
        final TextView tags = binding.companyTags;
        imageView = binding.img;
        final Button enterButton = binding.enterButn;
        final Button showButton = binding.showButn;
        homeViewModel.getName().observe(getViewLifecycleOwner(), name::setText);
        homeViewModel.getTag().observe(getViewLifecycleOwner(), tags::setText);
        homeViewModel.getImage().observe(getViewLifecycleOwner(), image -> {

            loadImageIntoImageView(image);
        });
        /*if(name.getText().toString().equals("")){
            Glide.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/new-journey-528fd.appspot.com/o/Users%2FAccount-User-PNG-Clipart.png?alt=media&token=cc307523-6837-4124-9835-23ee73ba364a").into(imageView);
            Toast.makeText(getContext(), "Бублик", Toast.LENGTH_SHORT).show();
            name.setText("В данный момент вы не состоите в организации");
        }
        else{
            Glide.with(getContext()).load(imageText.getText().toString()).into(imageView);
            Toast.makeText(getContext(), "Good" + imageText.getText().toString(), Toast.LENGTH_SHORT).show();
        }*/

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findUser();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findInvite();
            }
        });

        return root;
    }

    private void loadImageIntoImageView(String image) {
        Glide.with(getContext())
                .load(image)
                .into(imageView);
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
                        String company = user.company_UID;
                        if(company.equals("null")){
                            Toast.makeText(getContext(),"В данный момент вы не состоите в компании", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        else {
                            Intent intent = new Intent(getContext(), EmployeeDialogActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("companyUid", company);
                            startActivity(intent);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db1.addValueEventListener(vlist);
    }

    private void findInvite(){
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Invitation invitation = ds.getValue(Invitation.class);
                    assert invitation != null;
                    if(email.equals(invitation.userReciever)){
                        String invitationUid = invitation.uid;
                        String company = invitation.companyRecieverUid;
                            Intent intent = new Intent(getContext(), ShowInvitationsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.addValueEventListener(vlist);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}