package com.google.androidbookingstaffapp.model.entities;

import android.os.StrictMode;

import java.util.List;

public class Invoice {

    private String salonId , salonName , salonAddress ;
    private String barberId , barberName ;
    private String customerName , customerPhone;
    private String imagUrl ;
    private List<CartItem> shoppingItemList;
    private List<BarberServices> barberServices;
    private double finalPrice;

    public String getSalonId() {
        return salonId;
    }

    public Invoice setSalonId(String salonId) {
        this.salonId = salonId;
        return this;
    }

    public String getSalonName() {
        return salonName;
    }

    public Invoice setSalonName(String salonName) {
        this.salonName = salonName;
        return this;
    }

    public String getSalonAddress() {
        return salonAddress;
    }

    public Invoice setSalonAddress(String salonAddress) {
        this.salonAddress = salonAddress;
        return this;
    }

    public String getBarberId() {
        return barberId;
    }

    public Invoice setBarberId(String barberId) {
        this.barberId = barberId;
        return this;
    }

    public String getBarberName() {
        return barberName;
    }

    public Invoice setBarberName(String barberName) {
        this.barberName = barberName;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Invoice setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public Invoice setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public String getImagUrl() {
        return imagUrl;
    }

    public Invoice setImagUrl(String imagUrl) {
        this.imagUrl = imagUrl;
        return this;
    }

    public List<CartItem> getShoppingItemList() {
        return shoppingItemList;
    }

    public Invoice setShoppingItemList(List<CartItem> shoppingItemList) {
        this.shoppingItemList = shoppingItemList;
        return this;
    }

    public List<BarberServices> getBarberServices() {
        return barberServices;
    }

    public Invoice setBarberServices(List<BarberServices> barberServices) {
        this.barberServices = barberServices;
        return this;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public Invoice setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
        return this;
    }

    public Invoice() {
    }
}
