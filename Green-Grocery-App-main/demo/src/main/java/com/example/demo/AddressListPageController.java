package com.example.demo;

import com.example.demo.models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddressListPageController implements Initializable {

    @FXML
    private ListView<String> addressListView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadAddresses();
    }


    private void loadAddresses() {
        ObservableList<String> addresses = FXCollections.observableArrayList();

        User loggedInUser = AppData.getLoggedInUser();
        User user = new User();
        int userId = user.getUserIdByUsername(loggedInUser.getUsername());
        DatabaseConnection connectNow = new DatabaseConnection();

        String query = "SELECT AddressId, AddressName FROM Addresses WHERE UserId = ?";

        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(query);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                addresses.add(resultSet.getString("AddressName"));
            }

            addressListView.setItems(addresses);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButton() {

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);


        TextField addressField = new TextField();
        TextField neighborhoodField = new TextField();
        TextField apartmentNoField = new TextField();
        TextField flatNoField = new TextField();
        TextField districtField = new TextField();
        TextField cityField = new TextField();
        TextField countryField = new TextField();

        grid.add(new Label("Address:"), 0, 0);
        grid.add(addressField, 1, 0);
        grid.add(new Label("Neighborhood:"), 0, 1);
        grid.add(neighborhoodField, 1, 1);
        grid.add(new Label("Apartment No:"), 0, 2);
        grid.add(apartmentNoField, 1, 2);
        grid.add(new Label("Flat No:"), 0, 3);
        grid.add(flatNoField, 1, 3);
        grid.add(new Label("District:"), 0, 4);
        grid.add(districtField, 1, 4);
        grid.add(new Label("City:"), 0, 5);
        grid.add(cityField, 1, 5);
        grid.add(new Label("Country:"), 0, 6);
        grid.add(countryField, 1, 6);


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Address");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setContent(grid);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        Button addButtonNode = (Button) dialog.getDialogPane().lookupButton(addButton);
        addButtonNode.setOnAction(e -> {
            String newAddress = addressField.getText();
            String neighborhood = neighborhoodField.getText();
            String apartmentNo = apartmentNoField.getText();
            String flatNo = flatNoField.getText();
            String district = districtField.getText();
            String city = cityField.getText();
            String country = countryField.getText();

            if (newAddress.isEmpty() || neighborhood.isEmpty() || apartmentNo.isEmpty() || flatNo.isEmpty() || district.isEmpty() || city.isEmpty() || country.isEmpty()) {
                Platform.runLater(() -> showErrorAlert("Input Error", "Please fill in all fields."));
                dialog.setResult(ButtonType.OK);
                dialog.close();
                return;
            }

            if (addAddressToDatabase(newAddress, neighborhood, apartmentNo, flatNo, district, city, country)) {
                loadAddresses();
                Platform.runLater(() -> showInformationAlert("Add Address", "Address added successfully: " + newAddress));
                dialog.setResult(ButtonType.OK);
                dialog.close();
            } else {
                Platform.runLater(() -> showErrorAlert("Add Address Error", "Failed to add the address: " + newAddress));
                dialog.setResult(ButtonType.OK);
                dialog.close();
            }
        });
        dialog.showAndWait();



    }

    private boolean addAddressToDatabase(String newAddress, String neighborhood, String apartmentNo,
                                         String flatNo, String district, String city, String country) {
        User loggedInUser = AppData.getLoggedInUser();
        User user = new User();
        int userId = user.getUserIdByUsername(loggedInUser.getUsername());
        DatabaseConnection connectNow = new DatabaseConnection();

        String checkDuplicateQuery = "SELECT COUNT(*) FROM Addresses WHERE UserId = ? AND AddressName = ?";
        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement checkStatement = connectDB.prepareStatement(checkDuplicateQuery);
            checkStatement.setInt(1, userId);
            checkStatement.setString(2, newAddress);

            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String insertQuery = "INSERT INTO Addresses (UserId, AddressName, NeighborhoodName, ApartmentNo, FlatNo, District, City, Country) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";



        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, newAddress);
            preparedStatement.setString(3, neighborhood);
            preparedStatement.setString(4, apartmentNo);
            preparedStatement.setString(5, flatNo);
            preparedStatement.setString(6, district);
            preparedStatement.setString(7, city);
            preparedStatement.setString(8, country);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @FXML
    private void handleEditButton() {
        String selectedAddress = addressListView.getSelectionModel().getSelectedItem();
        String neighborhood = "";
        String apartmentNo = "";
        String flatNo = "";
        String district = "";
        String city = "";
        String country = "";

        User loggedInUser = AppData.getLoggedInUser();
        User user = new User();
        int userId = user.getUserIdByUsername(loggedInUser.getUsername());

        String selectQuery = "SELECT NeighborhoodName, ApartmentNo, FlatNo, District, City, Country " +
                "FROM Addresses WHERE UserId = ? AND AddressName = ?";
        DatabaseConnection connectNow = new DatabaseConnection();
        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, selectedAddress);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                neighborhood = resultSet.getString("NeighborhoodName");
                apartmentNo = resultSet.getString("ApartmentNo");
                flatNo = resultSet.getString("FlatNo");
                district = resultSet.getString("District");
                city = resultSet.getString("City");
                country = resultSet.getString("Country");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (selectedAddress != null) {
            Dialog<Boolean> dialog = new Dialog<>();
            dialog.setTitle("Edit Address");
            dialog.setHeaderText(null);

            TextField addressField = new TextField(selectedAddress);
            TextField neighborhoodField = new TextField(neighborhood);
            TextField apartmentNoField = new TextField(apartmentNo);
            TextField flatNoField = new TextField(flatNo);
            TextField districtField = new TextField(district);
            TextField cityField = new TextField(city);
            TextField countryField = new TextField(country);

            VBox content = new VBox();
            content.getChildren().addAll(
                    new Label("Edit Address:"),
                    addressField,
                    new Label("Neighborhood:"),
                    neighborhoodField,
                    new Label("Apartment No:"),
                    apartmentNoField,
                    new Label("Flat No:"),
                    flatNoField,
                    new Label("District:"),
                    districtField,
                    new Label("City:"),
                    cityField,
                    new Label("Country:"),
                    countryField
            );

            dialog.getDialogPane().setContent(content);

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);



            dialog.setResultConverter(buttonType -> {
                if (buttonType == okButton) {
                    if (addressField.getText().isEmpty() || neighborhoodField.getText().isEmpty() || apartmentNoField.getText().isEmpty() || flatNoField.getText().isEmpty() || districtField.getText().isEmpty() || cityField.getText().isEmpty() || countryField.getText().isEmpty()) {
                        
                        return false;
                    }
                    return editAddressInDatabase(selectedAddress,
                            addressField.getText(),
                            neighborhoodField.getText(),
                            apartmentNoField.getText(),
                            flatNoField.getText(),
                            districtField.getText(),
                            cityField.getText(),
                            countryField.getText());
                }

                return false;
            });

            boolean result = dialog.showAndWait().orElse(false);

            if (result) {
                loadAddresses();
                showInformationAlert("Edit Address", "Address edited successfully: " + selectedAddress);
            } else {
                showErrorAlert("Edit Address Error", "Failed to edit the address: " + selectedAddress);
            }
        } else {
            showInformationAlert("No Selection", "Please select an address to edit.");
        }
    }

    private boolean editAddressInDatabase(String oldAddress, String newAddress,
                                          String newNeighborhood, String newApartmentNo,
                                          String newFlatNo, String newDistrict,
                                          String newCity, String newCountry) {
        User loggedInUser = AppData.getLoggedInUser();
        User user = new User();
        int userId = user.getUserIdByUsername(loggedInUser.getUsername());
        DatabaseConnection connectNow = new DatabaseConnection();

        String updateQuery = "UPDATE Addresses SET AddressName = ?, NeighborhoodName = ?, ApartmentNo = ?, FlatNo = ?, District = ?, City = ?, Country = ? " +
                "WHERE UserId = ? AND AddressName = ?";

        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, newAddress);
            preparedStatement.setString(2, newNeighborhood);
            preparedStatement.setString(3, newApartmentNo);
            preparedStatement.setString(4, newFlatNo);
            preparedStatement.setString(5, newDistrict);
            preparedStatement.setString(6, newCity);
            preparedStatement.setString(7, newCountry);
            preparedStatement.setInt(8, userId);
            preparedStatement.setString(9, oldAddress);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    private void handleDeleteButton() {
        String selectedAddress = addressListView.getSelectionModel().getSelectedItem();
        User loggedInUser = AppData.getLoggedInUser();
        User user = new User();
        int userId = user.getUserIdByUsername(loggedInUser.getUsername());
        if (selectedAddress != null) {
            boolean confirmed = showConfirmationAlert("Confirm Delete", "Are you sure you want to delete the address: " + selectedAddress);

            if (confirmed) {
                if (deleteAddressFromDatabase(userId,selectedAddress)) {
                    addressListView.getItems().remove(selectedAddress);

                    showInformationAlert("Delete Address", "Address deleted successfully: " + selectedAddress);
                } else {
                    showErrorAlert("Delete Error", "Failed to delete the address: " + selectedAddress);
                }
            }
        } else {
            showInformationAlert("No Selection", "Please select an address to delete.");
        }
    }

    private boolean deleteAddressFromDatabase(int userId, String addressName) {
        String deleteQuery = "DELETE FROM Addresses WHERE UserId = ? AND AddressName = ?";
        DatabaseConnection connectNow = new DatabaseConnection();

        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, addressName);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleDetailsButton() {
        String selectedAddress = addressListView.getSelectionModel().getSelectedItem();

        if (selectedAddress != null) {
            showDetailsDialog(selectedAddress);
        } else {
            showInformationAlert("No Selection", "Please select an address to view details.");
        }
    }

    private void showDetailsDialog(String selectedAddress) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Address Details");
        dialog.setHeaderText(null);

        TextArea detailsTextArea = new TextArea(getAddressDetails(selectedAddress));
        detailsTextArea.setEditable(false);
        detailsTextArea.setWrapText(true);

        dialog.getDialogPane().setContent(detailsTextArea);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        dialog.setResultConverter(buttonType -> null);

        dialog.showAndWait();
    }

    private String getAddressDetails(String addressName) {
        User loggedInUser = AppData.getLoggedInUser();
        User user = new User();
        int userId = user.getUserIdByUsername(loggedInUser.getUsername());
        DatabaseConnection connectNow = new DatabaseConnection();

        String selectQuery = "SELECT * FROM Addresses WHERE UserId = ? AND AddressName = ?";

        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, addressName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return "Address Name: " + resultSet.getString("AddressName") + "\n" +
                        "Neighborhood: " + resultSet.getString("NeighborhoodName") + "\n" +
                        "Apartment No: " + resultSet.getString("ApartmentNo") + "\n" +
                        "Flat No: " + resultSet.getString("FlatNo") + "\n" +
                        "District: " + resultSet.getString("District") + "\n" +
                        "City: " + resultSet.getString("City") + "\n" +
                        "Country: " + resultSet.getString("Country");
            } else {
                return "Details not available for the selected address.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error fetching address details.";
        }
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
        Stage stage = (Stage) addressListView.getScene().getWindow();
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

}
