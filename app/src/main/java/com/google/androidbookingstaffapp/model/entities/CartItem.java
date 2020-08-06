package com.google.androidbookingstaffapp.model.entities;


public class CartItem  {

    public CartItem(String productId, String productName, String userPhone, String productImage, long productPrice, int productQuantity) {
        ProductId = productId;
        ProductName = productName;
        this.userPhone = userPhone;
        ProductImage = productImage;
        ProductPrice = productPrice;
        ProductQuantity = productQuantity;
    }

    private String ProductId;


    private String ProductName;


    private String userPhone;


    private String ProductImage;



    private long ProductPrice;


    private int ProductQuantity;

    public String getProductId() {
        return ProductId;
    }

    public void setProductId( String productId) {
        ProductId = productId;

    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;

    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;

    }

    public long getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(long productPrice) {
        ProductPrice = productPrice;

    }

    public int getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        ProductQuantity = productQuantity;

    }

    public String getUserPhone() {
        return userPhone;
    }

    public void  setUserPhone(String userPhone) {
        this.userPhone = userPhone;

    }


    public CartItem() {
    }


}