package com.google.androidbookingstaffapp.model.Interface;

import com.google.androidbookingstaffapp.model.entities.MyNotification;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface INotificationLoadListner {

    void OnNotificationLoadSuccess(List<MyNotification> myNotifications , DocumentSnapshot lastDecument);
    void OnNotificationLoadFailed(String message);
}
