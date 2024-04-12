package com.example.nj;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nj.classes.Dialog;
import com.example.nj.classes.Message;
import com.example.nj.classes.User;
import com.example.nj.widget.LeBubbleTextView;
import com.example.nj.widget.LeBubbleView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<Message> adapter, adapter1;
    private FloatingActionButton sendBtn, fileBtn;

    String companyUid, dialogUid, Type, Name_chat, format, referenceOnFile, curUser;
    int empty = 0;
    int currentFile = 0;
    DatabaseReference users, dialogs, users1;
    Query query, query1, query2;

    boolean stopCount;

    private Uri filePath;

    private final int PICK_REQUEST = 71;

    int updated = 0;

    ListView listView;

    FirebaseMessaging messaging;

    EditText textfield;

    StorageReference storageReference;

    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendBtn = findViewById(R.id.btnSend);
        listView = findViewById(R.id.list_of_messages);
        fileBtn = findViewById(R.id.btnFile);
        textfield = findViewById(R.id.message_field);

        messaging = FirebaseMessaging.getInstance();

        Name_chat = (String) getIntent().getExtras().get("name_chat");
        Type = (String) getIntent().getExtras().get("type");
        companyUid = (String) getIntent().getExtras().get("companyUid");
        dialogUid = (String) getIntent().getExtras().get("dialogUid");

        users1 = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");

        users = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Message");

        dialogs = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs");


        query1 = users.orderByChild("messageCode").equalTo(companyUid);
        query2 = users.orderByChild("messageCode").equalTo(dialogUid);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Users");

        curUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        stopCount = false;

        setTitle(Name_chat);

        if (Type.equals("Общий чат")) {
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textfield.getText().toString() == "")
                        return;
                    if(textfield.getText().toString().equals("PdfFile.download") || textfield.getText().toString().equals("DocFile.download")){
                        uploadImage();
                    }
                    else{
                        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Message").push().setValue(
                                new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                        textfield.getText().toString(), companyUid, "Общий чат", "null", "null"));
                        textfield.setText("");
                        //requestToUsers();
                        //sendPushNotification();
                    }
                }
            });
            displayAllMessages();
        }
        if (Type.equals("ЛС")) {
            //clearCount();
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textfield.getText().toString() == "")
                        return;
                    if(textfield.getText().toString().equals("PdfFile.download") || textfield.getText().toString().equals("DocFile.download")){
                        uploadImage();
                    }
                    else{
                        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Message").push().setValue(
                                new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                        textfield.getText().toString(), dialogUid, "ЛС", "null", "null"));
                        updateDialogs(textfield.getText().toString());
                        textfield.setText("");
                        //newMessageAdd(dialogUid);
                    }
                }
            });
            displayUserMessages();
        }

        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeDocDialog(ChatActivity.this);
            }
        });

        listView.setClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView mess_time;
                LeBubbleTextView mess_text;
                mess_text = view.findViewById(R.id.message_text);
                mess_time = view.findViewById(R.id.message_time);
                TextView contentTextView = mess_text.getContentTextView();
                сreateDialog(ChatActivity.this, contentTextView.getText().toString(), mess_time.getText().toString());
            }
        });
    }

    private void displayAllMessages() {
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item, query1) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView mess_user, mess_time;
                LeBubbleTextView mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_text = (LeBubbleTextView) v.findViewById(R.id.message_text);
                mess_time = v.findViewById(R.id.message_time);
                mess_user.setText(model.getUserName());
                TextView contentTextView = mess_text.getContentTextView();
                contentTextView.setText(model.textMessage);
                if(model.userName.equals(curUser)){
                    mess_user.setText("Вы");

                }
                mess_time.setText(DateFormat.format("HH:mm:ss", model.getMessageTime()));
            }
        };
        listView.setAdapter(adapter);
    }

    private void displayUserMessages() {
        adapter1 = new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item, query2) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView mess_user, mess_time;
                LeBubbleTextView mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_text = (LeBubbleTextView) v.findViewById(R.id.message_text);
                mess_time = v.findViewById(R.id.message_time);
                mess_user.setText(model.getUserName());
                TextView contentTextView = mess_text.getContentTextView();
                if(model.textMessage.equals("PdfFile.download")){
                    Drawable icon = getResources().getDrawable(R.drawable.baseline_file_download_24);
                    contentTextView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                }
                if(model.textMessage.equals("DocFile.download")){
                    Drawable icon = getResources().getDrawable(R.drawable.baseline_file_download_24);
                    contentTextView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                }
                contentTextView.setText(model.textMessage);
                if(model.userName.equals(curUser)){
                    mess_user.setText("Вы");
                }
                mess_time.setText(DateFormat.format("HH:mm:ss", model.getMessageTime()));
            }
        };
        listView.setAdapter(adapter1);
    }

    public void uploadImage(){

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String imageref = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("messages/"+ imageref);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if(Type.equals("ЛС")){
                                        newMessageLS(uri);
                                    }
                                    else{
                                        newMessage(uri);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void newMessage(Uri reference){
        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Message").push().setValue(
                new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        textfield.getText().toString(), companyUid, Type, reference.toString(), format));
        textfield.setText("");
    }

    public void newMessageLS(Uri reference){
        FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Message").push().setValue(
                new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        textfield.getText().toString(), dialogUid, Type, reference.toString(), format));
        textfield.setText("");
        updateDialogsFile();
    }

    public void updateDialogsFile(){
        updated = 0;
        ValueEventListener vlist3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Dialog dialog = ds.getValue(Dialog.class);
                    assert dialog != null;
                    if(dialogUid.equals(dialog.uid)){
                        if(updated < 2){
                            DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs").child(key);
                            HashMap map = new HashMap();
                            map.put("lastMessage", "Отправлен файл");
                            item.updateChildren(map);
                            updated +=1;
                        }
                        else{
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dialogs.addListenerForSingleValueEvent(vlist3);
    }

    public void updateDialogs(String textmessage){
        updated = 0;
        ValueEventListener vlist3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Dialog dialog = ds.getValue(Dialog.class);
                    assert dialog != null;
                    if(dialogUid.equals(dialog.uid)){
                        if(updated < 2){
                            DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs").child(key);
                            HashMap map = new HashMap();
                            map.put("lastMessage", textmessage);
                            item.updateChildren(map);
                            updated +=1;
                        }
                        else{
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dialogs.addListenerForSingleValueEvent(vlist3);
    }


    public  void typeDocDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Прикрепить файл")
                .setMessage("Выберите формат файла")
                .setPositiveButton("Docx", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("application/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_REQUEST);
                        textfield.setText("DocFile.download");
                        format = ".docx";
                    }
                })
                .setNegativeButton("PDF", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("application/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_REQUEST);
                        textfield.setText("PdfFile.download");
                        format = ".pdf";
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
        }
    }

    public void сreateDialog(Activity activity, String text, String time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Сообщение")
                .setMessage("Что вы хотите сделать?")
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteMessage(text, time);
                    }
                })
                .setNegativeButton("Скачать файл", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(currentFile > 0){
                            currentFile = 0;
                        }
                        findFile(text, time);
                    }
                });
        builder.create().show();
    }

    public void findFile(String text, String time){
        ValueEventListener vlist3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Message message = ds.getValue(Message.class);
                    assert message != null;
                    if(text.equals(message.textMessage)){
                        if(message.messageReference.equals("null")){
                            if(empty == 0){
                                Toast.makeText(ChatActivity.this, "Данное сообщение не является файлом", Toast.LENGTH_SHORT).show();
                                empty = empty + 1;
                            }
                        }
                        else{
                            if(currentFile == 0){
                                referenceOnFile = message.messageReference;
                                downloadFiles(ChatActivity.this, "Mobile", message.messageFormat, DIRECTORY_DOWNLOADS, message.messageReference);
                                currentFile = currentFile + 1;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        users.addValueEventListener(vlist3);
    }

    public void downloadFiles(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    public void deleteMessage(String text, String time){
        ValueEventListener vlist2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Message message = ds.getValue(Message.class);
                    assert message != null;
                    if(curUser.equals(message.userName)){
                        if(text.equals(message.textMessage)){
                            DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Message").child(key);
                            item.removeValue();
                            Toast.makeText(ChatActivity.this, "Сообщение удалено", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dialogs.addValueEventListener(vlist2);
    }

    public void requestToUsers(){
        ValueEventListener vlist6 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(companyUid.equals(user.company_UID)){
                        if(curUser.equals(user.mail)){

                        }
                        else{
                            //sendPushNotification();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dialogs.addValueEventListener(vlist6);
    }

    private void sendPushNotification(String token) {
        // Отправка push-уведомления на устройство с токеном token
        // Используйте FirebaseMessaging для отправки уведомления
    }

    /*public void clearCount(){
        stopCount = false;
        ValueEventListener vlist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Dialog dialog = ds.getValue(Dialog.class);
                    assert dialog != null;
                    if(dialogUid.equals(dialog.uid)){
                        if(curUser.equals(dialog.actualUser)) {
                            if(stopCount = false){
                                int count = 0;
                                DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs").child(key);
                                HashMap map = new HashMap();
                                map.put("newMessages", count);
                                item.updateChildren(map);
                                stopCount = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dialogs.addValueEventListener(vlist);
    }*/

    /*public void newMessageAdd(String dialogUid){
        //stopCount = false;
        ValueEventListener vlist1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    Dialog dialog = ds.getValue(Dialog.class);
                    assert dialog != null;
                    if(dialogUid.equals(dialog.uid)){
                        if(curUser.equals(dialog.userReceiver)) {
                            //if(stopCount = false){
                                int count = dialog.newMessages;
                                count += 1;
                                DatabaseReference item = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Dialogs").child(key);
                                HashMap map = new HashMap();
                                map.put("newMessages", count);
                                item.updateChildren(map);
                                //stopCount = true;
                                break;
                            //}
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dialogs.addValueEventListener(vlist1);
    }*/
}