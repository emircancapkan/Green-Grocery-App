
package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GroceryItemController {

    @FXML
    private ImageView itemImageView;

    @FXML
    private Label itemNameLabel;

    @FXML
    private Label itemPriceLabel;

    @FXML
    private Label quantityLabel;

    private double quantity = 0;

    private String itemName;
    private double itemPrice;
    private String imageUrl;

    @FXML
    private void initialize() {

        updateView();
    }

    public void setItem(String itemName, double itemPrice, String imageUrl) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.imageUrl = imageUrl;

        updateView();
    }

    private void updateView() {
        itemNameLabel.setText(itemName);
        itemPriceLabel.setText(String.valueOf(itemPrice));
        quantityLabel.setText(String.valueOf(quantity));


        itemImageView.setImage(new Image(getClass().getResourceAsStream(imageUrl)));
    }

    @FXML
    private void increaseQuantity() {
        quantity++;
        updateView();
    }

    @FXML
    private void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
            updateView();
        }
    }

    @FXML
    private void handleAddToCart(ActionEvent event) {
        GroceryListController controller = new GroceryListController();

        controller.handleAddToCart(quantity,itemName,itemPrice);
    }
}
