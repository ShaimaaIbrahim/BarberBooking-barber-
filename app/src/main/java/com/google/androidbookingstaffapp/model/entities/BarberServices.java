package com.google.androidbookingstaffapp.model.entities;

public class BarberServices {

    private String name;
    private long price;

    public BarberServices() {
    }

    public String getName() {
        return name;
    }

    public BarberServices setName(String name) {
        this.name = name;
        return this;
    }

    public long getPrice() {
        return price;
    }

    public BarberServices setPrice(long price) {
        this.price = price;
        return this;
    }
}
