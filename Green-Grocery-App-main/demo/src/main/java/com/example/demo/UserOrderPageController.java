package com.example.demo;

import com.example.demo.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserOrderPageController {

    @FXML
    private ListView<String> orderListView;


    private User loggedInUser = AppData.getLoggedInUser();
    private User user = new User();
    private int userID = user.getUserIdByUsername(loggedInUser.getUsername());
    @FXML
    public void initialize() {
        fetchAndPopulateOrderDetails(userID);
    }

    public void fetchAndPopulateOrderDetails(int userId) {
        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT order_id, total_price, order_date, delivery_time, orders_name " +
                    "FROM order_details " +
                    "WHERE user_id = ? AND Carrier IS NULL";
            PreparedStatement statement = connectDB.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            orderListView.getItems().clear();

            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                double totalPrice = resultSet.getDouble("total_price");
                Timestamp orderDate = resultSet.getTimestamp("order_date");
                String deliveryTime = resultSet.getString("delivery_time");
                String ordersName = resultSet.getString("orders_name");

                String orderDetails =
                        "Total Price: " + totalPrice + "\n" +
                        "Order Date: " + orderDate + "\n" +
                        "Delivery Time: " + deliveryTime + "\n" +
                        "Orders Name: " + ordersName;

                orderListView.getItems().add(orderDetails);
            }

        } catch (SQLException e) {
            e.printStackTrace();
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


    @FXML
    private void handleDeleteButton() throws IOException {
        String selectedItem = orderListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            boolean confirmed = showConfirmationAlert("Confirm Delete", "Are you sure you want to delete the item: " + selectedItem);
            int orderId = extractOrderId(selectedItem);
            if (confirmed) {
                deleteSelectedOrder(orderId);
            }
        } else {
            showInformationAlert("No Selection", "Please select an address to delete.");
        }

        handleMyCartButton();
    }

    private int extractOrderId(String orderDetails) {
        Pattern pattern = Pattern.compile("Order ID: (\\d+)");
        Matcher matcher = pattern.matcher(orderDetails);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Unable to extract orderId from orderDetails: " + orderDetails);
        }
    }

    public void deleteSelectedOrder(int orderId) {
        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection connectDB = connectNow.getConnection()) {
            String deleteQuery = "DELETE FROM order_details WHERE order_id = ?";
            try (PreparedStatement deleteStatement = connectDB.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, orderId);
                int rowsAffected = deleteStatement.executeUpdate();

                if (rowsAffected > 0) {
                    removeOrderFromListView(orderId);
                } else {
                    System.out.println("Order deletion failed. Order not found in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeOrderFromListView(int orderId) {
        for (String orderDetails : orderListView.getItems()) {
            if (orderDetails.contains("Order ID: " + orderId)) {
                orderListView.getItems().remove(orderDetails);
                break;
            }
        }
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
        Stage stage = (Stage) orderListView.getScene().getWindow();
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
