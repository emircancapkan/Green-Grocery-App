package com.example.demo.models;

import javafx.beans.property.*;

import java.sql.Timestamp;
import java.util.Map;

public class OrderDetail {
    private final IntegerProperty OrderID;
    private final StringProperty OrderName;
    private final  StringProperty OrderDate;
    private final StringProperty DeliveryDate;
    private final DoubleProperty TotalPrice;
    private final StringProperty AssignedCarrier;

    public OrderDetail(int orderID, String orderName, String orderDate, String deliveryDate, double totalPrice, String carrier) {
        this.OrderID = new SimpleIntegerProperty(orderID);
        this.OrderName = new SimpleStringProperty(orderName);
        this.OrderDate = new SimpleStringProperty(orderDate);
        this.DeliveryDate = new SimpleStringProperty(deliveryDate);
        this.TotalPrice = new SimpleDoubleProperty(totalPrice);
        this.AssignedCarrier = new SimpleStringProperty(carrier);
    }

    public String getOrderName() {
        return OrderName.get();
    }

    public StringProperty OrderNameProperty() {
        return OrderName;
    }

    public void setOrderName(String ordername) {
        this.OrderName.set(ordername);
    }

    public int getOrderID() {
        return OrderID.get();
    }

    public IntegerProperty OrderIdProperty() {
        return OrderID;
    }

    public void setOrderID(int orderid) {
        this.OrderID.set(orderid);
    }

    public String getOrderDate() {
        return OrderDate.get();
    }

    public StringProperty OrderDateProperty() {
        return OrderDate;
    }

    public void setOrderDate(String orderdate) {
        this.OrderDate.set(orderdate);
    }

    public String getDeliveryDate() {
        return DeliveryDate.get();
    }

    public StringProperty DeliveryDateProperty() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliverydate) {
        this.DeliveryDate.set(deliverydate);
    }

    public double getTotalPrice() {
        return TotalPrice.get();
    }

    public DoubleProperty TotalPriceProperty() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalprice) {
        this.TotalPrice.set(totalprice);
    }

    public String getAssignedCarrier() {
        return AssignedCarrier.get();
    }

    public StringProperty AssignedCarrierProperty() {
        return AssignedCarrier;
    }

    public void setAssignedCarrier(String assignedCarrier) {
        this.AssignedCarrier.set(assignedCarrier);
    }

}
