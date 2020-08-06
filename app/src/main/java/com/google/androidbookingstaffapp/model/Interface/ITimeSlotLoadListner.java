package com.google.androidbookingstaffapp.model.Interface;

import com.google.androidbookingstaffapp.model.entities.BookingInformation;

import java.util.List;

public interface ITimeSlotLoadListner {

    void LoadTimeSlotSuccess(List<BookingInformation> timeSlotList);
    void LoadTimeSlotFailed(String message);
    void LoadTimeSlotEmpty();
}
