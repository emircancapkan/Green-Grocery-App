package com.example.demo.models;

public class GroceryItem {

    private String name;
    private double price;
    private String imageUrl;
    private double stockQuantity;

    private double threshold;
    private String type;

    public GroceryItem(String name, double price, String imageUrl, double stockQuantity, double threshold, String type) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.threshold=threshold;
        this.type=type;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }

    public double getThreshold(){ return threshold;}

    public String getType(){ return type;}

}
