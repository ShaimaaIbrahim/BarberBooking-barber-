package com.google.androidbookingstaffapp.model.entities;

import com.google.firebase.Timestamp;
import com.google.androidbookingstaffapp.model.entities.CartItem;

import java.util.List;

public class BookingInformation {

    private  String bookingId , cityBook ,customerName, customerPhone , time , barberId , barberName , salonId , salonName , salonAddress;
    private int slot;
    private Timestamp timestamp;
    private boolean done;
    private List<CartItem> cartItemList;

    public BookingInformation() {
    }

    public String getBookingId() {
        return bookingId;
    }

    public BookingInformation setBookingId(String bookingId) {
        this.bookingId = bookingId;
        return this;
    }

    public String getCityBook() {
        return cityBook;
    }

    public BookingInformation setCityBook(String cityBook) {
        this.cityBook = cityBook;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BookingInformation setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public BookingInformation setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public String getTime() {
        return time;
    }

    public BookingInformation setTime(String time) {
        this.time = time;
        return this;
    }

    public String getBarberId() {
        return barberId;
    }

    public BookingInformation setBarberId(String barberId) {
        this.barberId = barberId;
        return this;
    }

    public String getBarberName() {
        return barberName;
    }

    public BookingInformation setBarberName(String barberName) {
        this.barberName = barberName;
        return this;
    }

    public String getSalonId() {
        return salonId;
    }

    public BookingInformation setSalonId(String salonId) {
        this.salonId = salonId;
        return this;
    }

    public String getSalonName() {
        return salonName;
    }

    public BookingInformation setSalonName(String salonName) {
        this.salonName = salonName;
        return this;
    }

    public String getSalonAddress() {
        return salonAddress;
    }

    public BookingInformation setSalonAddress(String salonAddress) {
        this.salonAddress = salonAddress;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public BookingInformation setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public BookingInformation setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public boolean isDone() {
        return done;
    }

    public BookingInformation setDone(boolean done) {
        this.done = done;
        return this;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }


}
