
package com.example.demo;

import com.example.demo.models.GroceryItem;
import com.example.demo.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GroceryListController {

    @FXML
    private VBox groceryListVBox;

    @FXML
    private TextField quantityTextField;

    @FXML
    private Label welcomeLabel;

    @FXML
    private ImageView userImageView;

    @FXML
    private Label userNameLabel;


    @FXML
    private void initialize() {
        User user = AppData.getLoggedInUser();

        if (user != null) {
            if(user.getImageUrl() != null)
            {
                userImageView.setImage(new Image(user.getImageUrl()));
            }else
            {
                userImageView.setImage(new Image("https://ssl.gstatic.com/docs/common/profile/alligator_lg.png"));
            }

            userNameLabel.setText(user.getFirstName() + " " + user.getLastName());
        }

        List<GroceryItem> groceryItems = getGroceryItems();
        for (GroceryItem item : groceryItems) {
            if(item.getStockQuantity() != 0)
            {
                addItem(item.getName(), item.getPrice(), item.getImageUrl(), item.getThreshold(), item.getType());

            }

        }


    }

    @FXML
    public void handleAddToCart(Double quantity, String itemName, Double itemPrice) {
        int prodID = ShoppingCartController.getProductId(itemName);
        int userId = ShoppingCartController.getUserID();
        int cartID = ShoppingCartController.getCartId(userId);

        if(quantity <= 0)
        {
            showErrorAlert("Wrong Quantity Enter", "Please, write correct quantity number!");
            return;
        }

        if (cartID == -1) {
            ShoppingCartController.createCart(userId);
            cartID = ShoppingCartController.getCartId(userId);
        }
        Double stockQuantity = 0.0;

        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {
            String updateQuery = "SELECT stock_quantity FROM grocery_items WHERE id = ?";
            PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);

            updateStatement.setInt(1, prodID);

            ResultSet resultSet = updateStatement.executeQuery();
            if (resultSet.next())
            {
                stockQuantity = resultSet.getDouble("stock_quantity");

            }




        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(quantity > stockQuantity)
        {
            showErrorAlert("Not Enough Stock","Please decrease your weight level because of not enough stock!");
            return;
        }

        stockQuantity -= quantity;

        try (Connection connectDB = connectNow.getConnection()) {
            String updateQuery = "UPDATE grocery_items SET stock_quantity = ?  WHERE id = ?";
            PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);

            updateStatement.setDouble(1, stockQuantity);
            updateStatement.setInt(2, prodID);


            updateStatement.executeUpdate();



        } catch (SQLException e) {
            e.printStackTrace();
        }


        ShoppingCartController.addToCart(cartID, userId, prodID, quantity, itemPrice);
        System.out.println(quantity + " " + itemName + " added to the cart!");

    }

    private void addItem(String itemName, Double itemPrice, String imageUrl, double threshold,String type) {
        HBox groceryItem = new HBox();
        groceryItem.getStyleClass().add("grocery-item");

        if(imageUrl == null)
        {
            imageUrl = "https://i.hizliresim.com/duzrvsr.png";
        }

        ImageView imageView = new ImageView(new Image(imageUrl));
        imageView.setFitHeight(60.0);
        imageView.setFitWidth(60.0);



        Label nameLabel = new Label(itemName.replace("\n", ""));
        Label priceLabel = new Label("$ " + itemPrice);


        HBox quantityBox = createQuantityBox();


        groceryItem.setSpacing(10);
        groceryItem.setPadding(new Insets(10));

        Button addButton = new Button("Add to Cart");

        addButton.getStyleClass().add("add-to-cart-button");
        addButton.setOnAction(event -> {

            handleAddToCart(Double.parseDouble(quantityTextField.getText()),itemName,itemPrice);

        });


        groceryItem.setAlignment(Pos.CENTER);
        quantityBox.setAlignment(Pos.CENTER);


        nameLabel.setWrapText(true);
        priceLabel.setWrapText(true);


        groceryItem.getChildren().addAll(imageView, nameLabel, priceLabel, quantityBox, addButton);
        groceryListVBox.getChildren().add(groceryItem);
    }

    private HBox createQuantityBox() {
        HBox quantityBox = new HBox();
        quantityBox.getStyleClass().add("quantity-box");

        quantityTextField = new TextField();
        quantityTextField.getStyleClass().add("quantity-textfield");
        quantityTextField.setPrefWidth(80);

        quantityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Double newQuantity = Double.parseDouble(newValue);
                quantityTextField.setText(String.valueOf(newQuantity));
            } catch (NumberFormatException e) {
                quantityTextField.setText(oldValue);
            }
        });

        quantityBox.getChildren().addAll(quantityTextField);

        return quantityBox;
    }



    @FXML
    private void handleHomeButton() throws IOException {
        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Home");
        stage.show();

    }
    @FXML
    private void handleProductsButton() throws IOException {
        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GroceryList.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Grocery List");
        stage.show();


    }
    @FXML
    private void handleAboutButton() throws IOException {
        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutUsPage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("About Us");
        stage.show();


    }
    @FXML
    private void handleContactButton() throws IOException {
        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ContactUsPage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Contact Us");
        stage.show();


    }
    @FXML
    private void handleProfileButton() throws IOException {
        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfilePage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("My Profile");
        stage.show();

    }
    @FXML
    private void handleAddressButton() throws IOException {

        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddressListPage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Address List");
        stage.show();

    }
    @FXML
    private void handleMyCartButton() throws IOException {

        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MyCartPage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("My Cart");
        stage.show();
    }
    @FXML
    private void handleMyOrderButton() throws IOException {

        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserOrderPage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("My Order");
        stage.show();
    }

    private void closeCurrentWindow() {
        Stage stage = (Stage) userNameLabel.getScene().getWindow();
        stage.close();
    }

    private void openLoginPage() throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Login Page");
        stage.show();

    }

    @FXML
    private void back(ActionEvent event) throws IOException{
        closeCurrentWindow();
        openLoginPage();
    }






    public static List<GroceryItem> getGroceryItems() {
        List<GroceryItem> groceryItems = new ArrayList<>();
        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT * FROM grocery_items";
            PreparedStatement statement = connectDB.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                String imageUrl = resultSet.getString("image_url");
                double stock_quantity = resultSet.getDouble("stock_quantity");
                double threshold= resultSet.getDouble("threshold");
                String type=resultSet.getString("Type");

                if(stock_quantity <= threshold)
                {
                    price *= 2;
                }

                GroceryItem groceryItem = new GroceryItem(name, price, imageUrl, stock_quantity, threshold,type);
                groceryItems.add(groceryItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groceryItems;
    }

    private void showErrorAlert(String title, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(content);

        errorAlert.showAndWait();
    }
}
