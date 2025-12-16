package org.yearup.models;

import java.util.Date;

public class Orders {
    private ShoppingCart shoppingCart;
    private Integer userId;
    private Date date;
    private String address;
    private String city;
    private String state;
    private String zip;
    private Integer shippingAmount;

    public Orders(ShoppingCart shoppingCart, Integer userId, Date date, String address, String city, String state, String zip, Integer shippingAmount) {
        this.shoppingCart = shoppingCart;
        this.userId = userId;
        this.date = date;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.shippingAmount = shippingAmount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Integer getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(Integer shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}
