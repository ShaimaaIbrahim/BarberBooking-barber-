package com.google.androidbookingstaffapp.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.androidbookingstaffapp.R;
import com.google.androidbookingstaffapp.model.DiffCallback;
import com.google.androidbookingstaffapp.model.entities.MyNotification;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.viewHolder> {

    private Context context;
    private List<MyNotification >myNotificationList;

    public MyNotificationAdapter(Context context, List<MyNotification> myNotificationList) {
        this.context = context;
        this.myNotificationList = myNotificationList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_notification_item , parent , false);
        return new MyNotificationAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MyNotification myNotification = myNotificationList.get(position);

        holder.txt_notification_content.setText(myNotification.getContent());
        holder.txt_notification_title.setText(myNotification.getTitle());
    }

    @Override
    public int getItemCount() {
        return myNotificationList.size();
    }

    public void updateList(List<MyNotification> myNotifications) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(this.myNotificationList , myNotifications));
                myNotificationList.addAll(myNotifications);
                diffResult.dispatchUpdatesTo(this);
    }

    class viewHolder extends RecyclerView.ViewHolder{

        private TextView txt_notification_title;
        private TextView txt_notification_content;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            txt_notification_title=itemView.findViewById(R.id.txt_notification_title);
            txt_notification_content=itemView.findViewById(R.id.txt_notification_content);

        }
    }
}
