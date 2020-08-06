package com.google.androidbookingstaffapp.model.fcm;

import android.annotation.SuppressLint;

import com.google.androidbookingstaffapp.model.Common.Common;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import androidx.annotation.NonNull;
import io.paperdb.Paper;


@SuppressLint("Registered")
public class FCMService extends FirebaseMessagingService {

    public FCMService() {
    }

    // new user => every user has unique token

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FcmCommon.updateToken(this , s);
    }

    // when recieving new message
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


               FcmCommon.showNotification(this , new Random().nextInt()
                       , remoteMessage.getData().get(FcmCommon.TITLE_KEY)
                       , remoteMessage.getData().get(FcmCommon.CONTENT_KEY)
                       , null);
           }
       }


