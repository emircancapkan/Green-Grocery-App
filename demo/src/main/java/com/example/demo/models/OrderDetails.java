package com.example.demo.models;

public class OrderDetails {


    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    // Constructors
    public OrderDetails() {
    }

    public OrderDetails(String productName, int quantity, double unitPrice, double totalPrice) {

        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getter and Setter methods


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // toString method for debugging or logging purposes

}
