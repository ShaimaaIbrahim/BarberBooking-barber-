package com.google.androidbookingstaffapp.model.entities;

public class Barber {

    private String name , username , password, barberId ;
    private int rating;

    public Barber() {
    }

    public String getName() {
        return name;
    }

    public Barber setName(String name) {
        this.name = name;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Barber setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Barber setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getBarberId() {
        return barberId;
    }

    public Barber setBarberId(String barberId) {
        this.barberId = barberId;
        return this;
    }

    public int getRating() {
        return rating;
    }

    public Barber setRating(int rating) {
        this.rating = rating;
        return this;
    }
}
