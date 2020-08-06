package com.google.androidbookingstaffapp.model.Interface;

import android.content.DialogInterface;

public interface IDialogeClickListner {

    void onClickPositiveButton(DialogInterface dialogInterface , String userName , String passWord);

    void onClickNegativeButton(DialogInterface dialogInterface);

}
