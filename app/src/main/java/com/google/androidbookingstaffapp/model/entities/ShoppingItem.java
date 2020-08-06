package com.google.androidbookingstaffapp.model.entities;

public class ShoppingItem {

    private String name , image , id ;
    private long price ;


    public ShoppingItem() {
    }

    public String getName() {
        return name;
    }

    public ShoppingItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public ShoppingItem setImage(String image) {
        this.image = image;
        return this;
    }

    public String getId() {
        return id;
    }

    public ShoppingItem setId(String id) {
        this.id = id;
        return this;
    }

    public long getPrice() {
        return price;
    }

    public ShoppingItem setPrice(long price) {
        this.price = price;
        return this;
    }
}
