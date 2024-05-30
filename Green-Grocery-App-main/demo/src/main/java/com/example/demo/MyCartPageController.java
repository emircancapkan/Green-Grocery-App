package com.example.demo;

import com.example.demo.models.OrderDetails;
import com.example.demo.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class MyCartPageController {

    @FXML
    private ListView<String> cartListView;

    @FXML
    private double totalPriceOfCart;

    @FXML
    private ComboBox<LocalDate> deliveryDateComboBox;

    @FXML
    private ComboBox<LocalTime> deliveryTimeComboBox;

    @FXML
    private ComboBox<String> deliveryAddressComboBox;
    @FXML
    private double totalQuantityOfCart;


    ObservableList<Object> cartItemsId = FXCollections.observableArrayList();
    ObservableList<Object> cartItemsQuantity = FXCollections.observableArrayList();
    ObservableList<String> cartItems = FXCollections.observableArrayList();
    private User loggedInUser = AppData.getLoggedInUser();
    private User user = new User();
    private int userID = user.getUserIdByUsername(loggedInUser.getUsername());
    @FXML
    private Label totalPriceLabel;
    private int cartID = ShoppingCartController.getCartId(userID);


    public void initialize() {

        getProductIds(cartID);

        initializeDateComboBox();
        initializeTimeComboBox();
        initializeAddressComboBox();

        updateCart();


        cartListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initializeAddressComboBox()
    {

        ObservableList<String> addressOptions = FXCollections.observableArrayList();

        DatabaseConnection connectNow = new DatabaseConnection();

        String query = "SELECT AddressName FROM Addresses WHERE UserId = ?";

        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(query);
            preparedStatement.setInt(1, userID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                addressOptions.add(resultSet.getString("AddressName"));
            }

            deliveryAddressComboBox.setItems(addressOptions);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    private void initializeDateComboBox() {

        ObservableList<LocalDate> dateOptions = FXCollections.observableArrayList(
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );
        deliveryDateComboBox.setItems(dateOptions);
        deliveryDateComboBox.setValue(LocalDate.now());
    }

    private void initializeTimeComboBox() {

        ObservableList<LocalTime> timeOptions = FXCollections.observableArrayList(
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0),
                LocalTime.of(17, 0),
                LocalTime.of(18, 0),
                LocalTime.of(19, 0),
                LocalTime.of(20, 0),
                LocalTime.of(21, 0)

        );
        deliveryTimeComboBox.setItems(timeOptions);
        deliveryTimeComboBox.setValue(LocalTime.of(10, 0));
    }




    private void updateCart() {
        loadCartItems(ShoppingCartController.getCartId(userID));
    }




    @FXML
    private void handleDeleteButton() throws IOException {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            boolean confirmed = showConfirmationAlert("Confirm Delete", "Are you sure you want to delete the item: " + selectedItem);

            if (confirmed) {
                if (deleteItemFromDatabase(cartID,selectedItem)) {
                    cartListView.getItems().remove(selectedItem);

                    showInformationAlert("Delete Item", "Item deleted successfully: " + selectedItem);
                } else {
                    showErrorAlert("Delete Error", "Failed to delete the item: " + selectedItem);
                }
            }
        } else {
            showInformationAlert("No Selection", "Please select an address to delete.");
        }

        handleMyCartButton();
    }

    private boolean deleteItemFromDatabase(int cartId, String itemName) {

        int productId = 0;
        String deleteQuery = "DELETE FROM cart_items WHERE cart_id = ? AND prod_id = ?";
        DatabaseConnection connectNow = new DatabaseConnection();

        try(Connection connectDB = connectNow.getConnection()) {


            String query = "SELECT id FROM grocery_items WHERE name = ?";
            PreparedStatement selectStatement = connectDB.prepareStatement(query);

            selectStatement.setString(1,itemName);



            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                productId = resultSet.getInt("id");
            }

            PreparedStatement preparedStatement = connectDB.prepareStatement(deleteQuery);

            preparedStatement.setInt(1, cartId);
            preparedStatement.setInt(2, productId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void clearCartButtonClicked() throws IOException {
        cartItemsQuantity = null;
        cartItemsId = null;
        cartItems = null;
        totalQuantityOfCart = 0.0;
        totalPriceOfCart = 0.0;

        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {
            String updateQuery = "UPDATE shopping_cart SET total_price = NULL WHERE user_id = ?";
            PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);

            updateStatement.setInt(1, userID);
            updateStatement.executeUpdate();

            updateStatement.executeUpdate();

            String deleteQuery = "DELETE FROM cart_items WHERE cart_id IN (SELECT cart_id FROM shopping_cart WHERE user_id = ?)";
            PreparedStatement deleteStatement = connectDB.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, userID);
            deleteStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        handleMyCartButton();


    }

    @FXML
    private void checkoutButtonClicked() {
        int userId = userID;
        LocalDate selectedDeliveryDate = deliveryDateComboBox.getValue();
        LocalTime selectedDeliveryTime = deliveryTimeComboBox.getValue();
        String selectedDeliveryAddress = deliveryAddressComboBox.getValue();
        int AddressId = 0;


        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {
            String insertQuery = "INSERT INTO order_details (user_id, order_items, total_price, quantity, delivery_time, orders_name, address_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStatement = connectDB.prepareStatement(insertQuery);

            String query = "SELECT AddressId FROM Addresses WHERE UserId = ? AND AddressName = ?";
            PreparedStatement selectStatement = connectDB.prepareStatement(query);

            selectStatement.setInt(1,userID);
            selectStatement.setString(2,selectedDeliveryAddress);



            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                AddressId = resultSet.getInt("AddressId");
            }

            // Set parameters for the insert statement
            insertStatement.setInt(1, userId);
            insertStatement.setString(2, cartItemsId.toString());
            insertStatement.setDouble(3, totalPriceOfCart);
            insertStatement.setString(4, cartItemsQuantity.toString());
            insertStatement.setString(5, selectedDeliveryDate.toString() + " " + selectedDeliveryTime.toString());
            insertStatement.setString(6, loggedInUser.getFirstName() + "'s" + " Order");
            insertStatement.setInt(7, AddressId);

            insertStatement.executeUpdate();

            clearCartButtonClicked();

            updateCart();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void getProductIds(int cartId)
    {
        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT prod_id, prod_quantity FROM cart_items WHERE cart_id = ?";
            PreparedStatement statement = connectDB.prepareStatement(query);

            statement.setInt(1, cartId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int prodId = resultSet.getInt("prod_id");
                Double prodQuantity = resultSet.getDouble("prod_quantity");
                cartItemsId.addAll(prodId);
                cartItemsQuantity.addAll(prodQuantity);

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void handleDetailsButton() {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            showDetailsDialog(selectedItem);
        } else {
            showInformationAlert("No Selection", "Please select an item to view details.");
        }
    }
    @FXML
    private void handleEditButton() throws IOException {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            editItem(selectedItem);
        } else {
            showInformationAlert("No Selection", "Please select an item to view details.");
        }
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

    private void editItem(String itemName) throws IOException {
        int productId = 0;
        double totalQuantity = 0.0;
        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT prod_id, prod_quantity, prod_price, " +
                    "SUM(prod_quantity) AS total_quantity, " +
                    "SUM(prod_price * prod_quantity) AS total_price " +
                    "FROM cart_items " +
                    "WHERE cart_id = ? AND prod_id = ? " +
                    "GROUP BY prod_id, prod_quantity, prod_price";
            PreparedStatement statement = connectDB.prepareStatement(query);


            String selectQuery = "SELECT id FROM grocery_items WHERE name = ?";
            PreparedStatement selectStatement = connectDB.prepareStatement(selectQuery);

            selectStatement.setString(1,itemName);

            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                productId = resultSet.getInt("id");
            }

            statement.setInt(1, cartID);
            statement.setInt(2, productId);

            resultSet = statement.executeQuery();


            if (resultSet.next()) {
                totalQuantity = resultSet.getDouble("total_quantity");
            }


        } catch (SQLException e) {
            e.printStackTrace();

        }

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText(null);

        TextField quantityField = new TextField(Double.toString(totalQuantity));

        VBox content = new VBox();
        content.getChildren().addAll(
                new Label("Edit Item Quantity:"),
                quantityField);
        dialog.getDialogPane().setContent(content);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == okButton) {
                if (quantityField.getText().isEmpty()) {

                    return false;
                }
                return editItemInDatabase(itemName,quantityField.getText());

            }

            return false;
        });

        boolean result = dialog.showAndWait().orElse(false);

        if (result) {
            handleMyCartButton();
            showInformationAlert("Edit Item", "Item edited successfully: " + itemName);
        } else {
            showErrorAlert("Edit Item Error", "Failed to edit the item: " + itemName);
        }


    }

    private boolean editItemInDatabase(String itemName, String quantity)
    {
        int productId = 0;
        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection connectDB = connectNow.getConnection()) {

            String selectQuery = "SELECT id FROM grocery_items WHERE name = ?";
            PreparedStatement selectStatement = connectDB.prepareStatement(selectQuery);

            selectStatement.setString(1,itemName);

            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                productId = resultSet.getInt("id");
            }

            String updateQuery = "UPDATE cart_items SET prod_quantity = ? WHERE cart_id = ? AND prod_id = ?";
            PreparedStatement updateStatement = connectDB.prepareStatement(updateQuery);
            updateStatement.setDouble(1, Double.parseDouble(quantity));
            updateStatement.setInt(2, cartID);
            updateStatement.setInt(3, productId);

            int rowsAffected = updateStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void showDetailsDialog(String selectedItem) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Item Details");
        dialog.setHeaderText(null);

        TextArea detailsTextArea = new TextArea(getItemDetails(selectedItem));
        detailsTextArea.setEditable(false);
        detailsTextArea.setWrapText(true);

        dialog.getDialogPane().setContent(detailsTextArea);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        dialog.setResultConverter(buttonType -> null);

        dialog.showAndWait();
    }

    private String getItemDetails(String itemName) {

        int productId = 0;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT prod_id, prod_quantity, prod_price, " +
                    "SUM(prod_quantity) AS total_quantity, " +
                    "SUM(prod_price * prod_quantity) AS total_price " +
                    "FROM cart_items " +
                    "WHERE cart_id = ? AND prod_id = ? " +
                    "GROUP BY prod_id, prod_quantity, prod_price";
            PreparedStatement statement = connectDB.prepareStatement(query);


            String selectQuery = "SELECT id FROM grocery_items WHERE name = ?";
            PreparedStatement selectStatement = connectDB.prepareStatement(selectQuery);

            selectStatement.setString(1,itemName);

            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                productId = resultSet.getInt("id");
            }

            statement.setInt(1, cartID);
            statement.setInt(2, productId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int prodID = resultSet.getInt("prod_id");
                double totalQuantity = resultSet.getDouble("total_quantity");
                double totalPrice = resultSet.getDouble("total_price");

                return "Product Name: " + itemName + "\n" +
                        "Total Quantity: " + totalQuantity + "\n" +
                        "Total Price: " + totalPrice;
            } else {
                return "Details not available for the selected item.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error fetching item details.";
        }

    }

    private void loadCartItems(int cartId) {


        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT prod_id, SUM(prod_quantity) AS total_quantity, SUM(prod_price * prod_quantity) AS total_price FROM cart_items WHERE cart_id = ? GROUP BY prod_id";
            PreparedStatement statement = connectDB.prepareStatement(query);

            statement.setInt(1, cartId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int prodId = resultSet.getInt("prod_id");
                String name = getProductName(prodId);
                double totalQuantity = resultSet.getDouble("total_quantity");
                double totalPrice = resultSet.getDouble("total_price");
                totalPriceOfCart += resultSet.getDouble("total_price");
                totalQuantityOfCart += resultSet.getDouble("total_quantity");

                cartItems.add(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        totalPriceLabel.setText("$ " + totalPriceOfCart);

        cartListView.setItems(cartItems);
    }


    private String getProductName(int prodId) {
        String productName = null;

        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT name FROM grocery_items WHERE id = ?";
            PreparedStatement statement = connectDB.prepareStatement(query);

            statement.setInt(1, prodId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                productName = resultSet.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productName;
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


    private void closeCurrentWindow() {
        Stage stage = (Stage) cartListView.getScene().getWindow();
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

    private void showInformationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmationAlert(String title, String content) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(title);
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(content);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    private void showErrorAlert(String title, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(content);

        errorAlert.showAndWait();
    }


}
