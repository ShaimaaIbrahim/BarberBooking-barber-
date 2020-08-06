package com.google.androidbookingstaffapp.model.Interface;

import com.google.androidbookingstaffapp.model.entities.Salon;

import java.util.List;

public interface IBrabchLoadListner {

    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);
}
