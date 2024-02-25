package com.example.demo.models;


import javafx.beans.property.*;


public class Product {

    private final IntegerProperty ProductID;
    private final StringProperty ProductName;
    private final DoubleProperty ProductPrice;
    private final DoubleProperty ProductStockQuantity;
    private final DoubleProperty ProductThreshold;
    private final BooleanProperty checkerThreshold;
    private final StringProperty ProductType;


    public Product(int id, String Name, double Price, double StockQuantity, double threshold, boolean checker, String Type) {
        this.ProductID = new SimpleIntegerProperty(id);
        this.ProductName = new SimpleStringProperty(Name);
        this.ProductPrice = new SimpleDoubleProperty(Price);
        this.ProductStockQuantity = new SimpleDoubleProperty(StockQuantity);
        this.ProductThreshold = new SimpleDoubleProperty(threshold);
        this.checkerThreshold = new SimpleBooleanProperty(checker);
        this.ProductType = new SimpleStringProperty(Type);
    }

    public int getProductID() {
        return ProductID.get();
    }

    public IntegerProperty ProductIdProperty() {
        return ProductID;
    }

    public void setProductID(int productId) {
        this.ProductID.set(productId);
    }

    public String getProductName() {
        return ProductName.get();
    }

    public StringProperty ProductNameProperty() {
        return ProductName;
    }

    public void setProductName(String productName) {
        this.ProductName.set(productName);
    }

    public double getProductPrice() {
        return ProductPrice.get();
    }

    public DoubleProperty ProductPriceProperty() {
        return ProductPrice;
    }

    public void setProductPrice(double productPrice) {
        this.ProductPrice.set(productPrice);
    }

    public double getProductStock() {
        return ProductStockQuantity.get();
    }

    public DoubleProperty ProductStockProperty() {
        return ProductStockQuantity;
    }

    public void setProductStock(double productStock) {
        this.ProductStockQuantity.set(productStock);
    }

    public double getProductThreshold() {
        return ProductThreshold.get();
    }

    public DoubleProperty ProductThresholdProperty() {
        return ProductThreshold;
    }

    public void setProductThreshold(double productThreshold) {
        this.ProductThreshold.set(productThreshold);
    }

    public boolean getCheckerThreshold() {
        return checkerThreshold.get();
    }

    public BooleanProperty CheckerThresholdProperty() {
        return checkerThreshold;
    }

    public void setCheckerThreshold(boolean CheckerThreshold) {
        this.checkerThreshold.set(CheckerThreshold);
    }

    public String getProductType() {
        return ProductType.get();
    }

    public StringProperty ProductTypeProperty() {
        return ProductType;
    }

    public void setProductType(String productType) {
        this.ProductType.set(productType);
    }


}
