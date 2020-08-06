package com.google.androidbookingstaffapp.repository.fcmRetrofit;

import com.google.androidbookingstaffapp.model.fcm.FcmResponce;
import com.google.androidbookingstaffapp.model.fcm.FcmSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAIzs6PJI:APA91bEOXNzTg_8rWYFHz-LG5hIq-GQsvrjQRNHO2W4J8zCIewL5U3ZQhZZb1BrFyb5EBBZamn7i7ysFMrtLfyqDLxZmuHe48yqdUi6oVVNdcNKDdff29777MI6zmGxYy1S6ViHPLoH3"

    })

    @POST("fcm/send")
    Observable<FcmResponce> sendNotification(@Body FcmSendData body);
}
