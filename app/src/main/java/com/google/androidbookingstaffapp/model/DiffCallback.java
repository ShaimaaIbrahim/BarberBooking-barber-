package com.google.androidbookingstaffapp.model;

import com.google.androidbookingstaffapp.model.entities.MyNotification;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

public class DiffCallback extends DiffUtil.Callback {

    private List<MyNotification> oldList;
    private List<MyNotification> newList;

    public DiffCallback(List<MyNotification> oldList, List<MyNotification> newList) {
        this.newList=newList;
        this.oldList=oldList;
    }


    @Override
    public int getOldListSize() {
        return oldList.size();
    }


    @Override
    public int getNewListSize() {
        return newList.size();
    }


    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getUid()==newList.get(newItemPosition).getUid();
    }


    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return  oldList.get(oldItemPosition)==newList.get(newItemPosition);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
