package com.google.androidbookingstaffapp.model.Interface;

import com.google.androidbookingstaffapp.model.entities.City;

import java.util.List;

public interface IOnstateLoadListner {

    void onLoadStateSuccess(List<City> cityList);
    void onLoadStateFailed(String message);


}
