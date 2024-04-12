package com.example.nj;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.nj.classes.Message;
import com.example.nj.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    DatabaseReference users = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
    String curUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);


        // Выводим токен в лог для отладки
        //Log.d(TAG, "Refreshed token: " + token);

        //findMy(token);
    }

    /*public void findMy(String token){
        ValueEventListener vlist3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String key = ds.getKey();
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(curUser.equals(user.mail)){
                        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://new-journey-528fd-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
                        usersRef.child(key).child("token").setValue(token);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        users.addValueEventListener(vlist3);
    }*/

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        // Дополнительные данные из поля data уведомления
        Map<String, String> data = remoteMessage.getData();

        // Обработка полученного уведомления
        if (title != null && body != null) {
            // Показать уведомление в системном лотке
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(com.firebase.ui.R.drawable.ic_google)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0, builder.build());
        }
    }
}
