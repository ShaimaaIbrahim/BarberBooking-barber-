package com.google.androidbookingstaffapp.model.Interface;

import com.google.androidbookingstaffapp.model.entities.BarberServices;

import java.util.List;

public interface IServiceLoadListner {

    void OnLoadServiceSuccess(List<BarberServices> barberServicesList);

    void OnLoadServiceFailed(String message);
}
