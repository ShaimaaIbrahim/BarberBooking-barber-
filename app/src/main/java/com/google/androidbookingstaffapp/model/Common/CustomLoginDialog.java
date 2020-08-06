package com.google.androidbookingstaffapp.model.Common;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.androidbookingstaffapp.model.Interface.IDialogeClickListner;
import com.google.androidbookingstaffapp.R;

public class CustomLoginDialog {

    public static CustomLoginDialog customLoginDialog;
    public IDialogeClickListner iDialogeClickListner;

    private TextInputEditText edit_name;
    private TextInputEditText edit_password;

    private Button btn_login, btn_cancel;
    private TextView txt_title;

    public static CustomLoginDialog getInstance() {
        if (customLoginDialog==null){
            customLoginDialog=  new CustomLoginDialog();
        }
        return customLoginDialog;
    }

    public void showLoginDialoge(String title , String positiveText , String negativeText , final Context context ,
                                 final IDialogeClickListner iDialogeClickListner){

        this.iDialogeClickListner=iDialogeClickListner;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogeView = LayoutInflater.from(context).inflate(R.layout.layout_login , null);

        dialog.setContentView(dialogeView);


        //init values view
        edit_name=dialogeView.findViewById(R.id.txt_name);
        edit_password=dialogeView.findViewById(R.id.txt_password);
        txt_title = dialogeView.findViewById(R.id.txt_title);
        btn_cancel=dialogeView.findViewById(R.id.btn_cancel);
        btn_login=dialogeView.findViewById(R.id.btn_login);


        if (!TextUtils.isEmpty(title))  {
            txt_title.setText(title);
            txt_title.setVisibility(View.VISIBLE);
        }
        btn_login.setText(positiveText);
        btn_cancel.setText(negativeText);

        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                iDialogeClickListner.onClickPositiveButton(dialog , edit_name.getText().toString()
                , edit_password.getText().toString());

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogeClickListner.onClickNegativeButton(dialog);
            }
        });

    }
}
