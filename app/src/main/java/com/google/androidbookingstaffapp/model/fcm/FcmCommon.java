package com.google.androidbookingstaffapp.model.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.core.app.NotificationCompat;
import io.paperdb.Paper;

public class FcmCommon {

    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content" ;

    public  enum TOKEN_TYPE{
        CLIENT
        , BARBER , MANAGER
    }

    public static void showNotification(Context context
            , int noti_id, String title, String content, Intent intent) {

        PendingIntent pendingIntent = null;

        if (intent!=null){
            pendingIntent = PendingIntent.getActivity( context
                    , noti_id , intent , PendingIntent.FLAG_UPDATE_CURRENT);

        }
        String notification_channel_id =
                "my_noti_channel_01";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    notification_channel_id,
                    "Foreground BarberServices Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            notificationChannel.setDescription("Channel Discription");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long [] {0 ,1000 , 500 , 1000 });
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context , notification_channel_id);
            builder.setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.barber)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources() , R.mipmap.barber));

            if (pendingIntent!=null)
                builder.setContentIntent(pendingIntent);
            Notification mNotification =  builder.build();

            notificationManager.notify(noti_id , mNotification);

        }
    }


    public static void updateToken(Context context , String token) {

        Paper.init(context);
        String user = Paper.book().read(Common.LOGGED_KEY);

        if (user!=null){

            if (!TextUtils.isEmpty(user)){

                MyToken myToken = new MyToken();

                myToken.setToken(token);
                myToken.setToken_type(TOKEN_TYPE.BARBER); // because token come from client app
                myToken.setUserPhone(user);

                FirebaseFirestore.getInstance().collection("Tokens")
                        .document(user)
                        .set(myToken)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }); } } }

}
