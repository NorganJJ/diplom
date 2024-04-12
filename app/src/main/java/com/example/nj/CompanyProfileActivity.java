package com.example.nj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nj.classes.Company;
import com.example.nj.classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class CompanyProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    DatabaseReference db;

    TextView name, tags;

    ShapeableImageView image;

    String email, companyUid;

    Button settings, change, upload;

    private Uri filePath;

    private final int PICK_REQUEST = 71;

    FirebaseStorage storage, storage1;
    StorageReference storageReference, storageReference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        settings = findViewById(R.id.settings_butn);
        change = findViewById(R.id.ava_butn);
        name = findViewById(R.id.profile_name);
        tags = findViewById(R.id.profile_tags);
        image = findViewById(R.id.imgProfile);
        upload = findViewById(R.id.comp_butn);

        storage = FirebaseStorage.getInstance();
        storage1 = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Companies");
        storageReference1 = storage.getReference("Companies");

        companyUid = (String)getIntent().getExtras().get("companyUid");

        email = firebaseAuth.getInstance().getCurrentUser().getEmail();

        db = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Companies");

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
                        Glide.with(getApplicationContext()).load(company.companyLogo).into(image);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.addValueEventListener(vlist);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompanySettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);;
                intent.putExtra("companyUid", companyUid);
                startActivity(intent);
                finish();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(){
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String imageref = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/"+ imageref);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CompanyProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newAvatarka(uri);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CompanyProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    public void newAvatarka(Uri reference){
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
                        map.put("companyLogo", reference.toString());
                        item.updateChildren(map);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        db.addValueEventListener(vlist1);
    }
}