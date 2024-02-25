package com.example.demo.models;

import javafx.beans.property.*;

public class Carrier {

    private final IntegerProperty CarrierID;
    private final StringProperty CarrierName;
    private final StringProperty CarrierSurname;
    private final StringProperty CarrierUsername;

    public Carrier(int id, String Username, String Name, String Surname) {
        this.CarrierID = new SimpleIntegerProperty(id);
        this.CarrierUsername = new SimpleStringProperty(Username);
        this.CarrierName = new SimpleStringProperty(Name);
        this.CarrierSurname = new SimpleStringProperty(Surname);
    }

    public int getCarrierID() {
        return CarrierID.get();
    }

    public IntegerProperty CarrierIDProperty() {
        return CarrierID;
    }

    public void setCarrierID(int carrierID) {
        this.CarrierID.set(carrierID);
    }

    public String getCarrierName() {
        return CarrierName.get();
    }

    public StringProperty CarrierNameProperty() {
        return CarrierName;
    }

    public void setCarrierName(String carrierName) {
        this.CarrierName.set(carrierName);
    }

    public String getCarrierSurname() {
        return CarrierSurname.get();
    }

    public StringProperty CarrierSurnameProperty() {
        return CarrierSurname;
    }

    public void setCarrierSurname(String carrierSurname) {
        this.CarrierSurname.set(carrierSurname);
    }

    public String getCarrierUsername() {
        return CarrierUsername.get();
    }

    public StringProperty CarrierUsernameProperty() {
        return CarrierUsername;
    }

    public void setCarrierUsername(String carrierUsername) {
        this.CarrierUsername.set(carrierUsername);
    }
}
