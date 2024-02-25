package com.example.demo.models;

import javafx.beans.property.*;

import java.sql.Timestamp;

public class OrderProductDetail {
    private final StringProperty OrderProductName;
    private final DoubleProperty OrderProductQuantity;
    private final DoubleProperty OrderProductPrice;

    public OrderProductDetail(String orderName, double quantity, double totalPrice) {
        this.OrderProductName = new SimpleStringProperty(orderName);
        this.OrderProductQuantity = new SimpleDoubleProperty(quantity);
        this.OrderProductPrice = new SimpleDoubleProperty(totalPrice);
    }

    public String getOrderProductName() {
        return OrderProductName.get();
    }

    public StringProperty OrderProductNameProperty() {
        return OrderProductName;
    }

    public void setOrderProductName(String orderProductname) {
        this.OrderProductName.set(orderProductname);
    }

    public double getOrderProductQuantity() {
        return OrderProductQuantity.get();
    }

    public DoubleProperty OrderProductQuantityProperty() {
        return OrderProductQuantity;
    }

    public void setOrderProductQuantity(double orderProductQuantity) {
        this.OrderProductQuantity.set(orderProductQuantity);
    }

    public double getOrderProductPrice() {
        return OrderProductPrice.get();
    }

    public DoubleProperty OrderProductPriceProperty() {
        return OrderProductPrice;
    }

    public void setOrderProductPrice(double orderProductPrice) {
        this.OrderProductPrice.set(orderProductPrice);
    }
}
