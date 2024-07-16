package com.example.demo;

import com.example.demo.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProfilePageController {

    @FXML
    private ImageView profileImage;


    @FXML
    private Label usernameLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label roleLabel;
    private User loggedInUser = AppData.getLoggedInUser();
    private User user = new User();
    private int userID = user.getUserIdByUsername(loggedInUser.getUsername());

    private String password = "";

    DatabaseConnection connectNow = new DatabaseConnection();


    @FXML
    public void initialize() {
        if(user.getImageUrl() != null)
        {
            profileImage.setImage(new Image(user.getImageUrl()));
        }else
        {
            profileImage.setImage(new Image("https://ssl.gstatic.com/docs/common/profile/alligator_lg.png"));
        }

        loadUserData();
    }

    private void loadUserData() {

        try (Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT UserName, Name, Surname, Role, Image FROM Users WHERE UserID = ?";
            try (PreparedStatement statement = connectDB.prepareStatement(query)) {
                statement.setInt(1, userID);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String userName = resultSet.getString("UserName");
                    String name = resultSet.getString("Name");
                    String surname = resultSet.getString("Surname");
                    String role = resultSet.getString("Role");

                    usernameLabel.setText(userName);
                    nameLabel.setText(name);
                    surnameLabel.setText(surname);
                    roleLabel.setText(role);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePasswordButton()
    {

        String selectQuery = "SELECT Password FROM Users WHERE UserId = ?";

        DatabaseConnection connectNow = new DatabaseConnection();
        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                password = resultSet.getString("Password");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText(null);


        PasswordField passwordField = new PasswordField();
        PasswordField rePasswordField = new PasswordField();



        VBox content = new VBox();
        content.getChildren().addAll(
                new Label("Password:"),
                passwordField,
                new Label("Re-Password:"),
                rePasswordField

        );

        dialog.getDialogPane().setContent(content);


        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);




        dialog.setResultConverter(buttonType -> {
            if (buttonType == okButton) {
                if (passwordField.getText().isEmpty() || rePasswordField.getText().isEmpty()) {

                    return false;
                }
                if(!passwordField.getText().equals(rePasswordField.getText()))
                {
                    return false;
                }

                return editPasswordInDatabase(
                        passwordField.getText());
            }
            return false;
        });


        boolean result = dialog.showAndWait().orElse(false);


        if (result) {
            loadUserData();
            showConfirmationAlert("Change Password", "Password change successfully."  );
        } else {
            showErrorAlert("Change Password Error", "Failed to change the password.");
        }

    }

    private boolean editPasswordInDatabase(String newPassword)
    {

        DatabaseConnection connectNow = new DatabaseConnection();

        String updateQuery = "UPDATE Users SET Password = ?" +
                "WHERE UserId = ?";

        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, userID);


            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @FXML
    private void handleEditButton() {

        String userName = "";
        String name = "";
        String surname = "";


        String selectQuery = "SELECT UserName, Name, Surname " +
                "FROM Users WHERE UserId = ?";
        DatabaseConnection connectNow = new DatabaseConnection();
        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                userName = resultSet.getString("UserName");
                name = resultSet.getString("Name");
                surname = resultSet.getString("Surname");


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }




            Dialog<Boolean> dialog = new Dialog<>();
            dialog.setTitle("Edit Profile");
            dialog.setHeaderText(null);


            TextField userNameField = new TextField(userName);
            TextField nameField = new TextField(name);
            TextField surnameField = new TextField(surname);



            VBox content = new VBox();
            content.getChildren().addAll(
                    new Label("Username:"),
                    userNameField,
                    new Label("Name:"),
                    nameField,
                    new Label("Surname:"),
                    surnameField

            );

            dialog.getDialogPane().setContent(content);


            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);




            dialog.setResultConverter(buttonType -> {
                if (buttonType == okButton) {
                    if (userNameField.getText().isEmpty() || nameField.getText().isEmpty() || surnameField.getText().isEmpty()) {

                        return false;
                    }

                    return editProfileInDatabase(
                            userNameField.getText(),
                            nameField.getText(),
                            surnameField.getText());
                }
                return false;
            });


            boolean result = dialog.showAndWait().orElse(false);


            if (result) {
                loadUserData();
                showConfirmationAlert("Edit Profile", "Profile edited successfully."  );
            } else {
                showErrorAlert("Edit Profile Error", "Failed to edit the profile.");
            }

    }


    private boolean editProfileInDatabase(String newUserName,
                                          String newName, String newSurname) {


        DatabaseConnection connectNow = new DatabaseConnection();

        String updateQuery = "UPDATE Users SET UserName = ?, Name = ?, Surname = ? " +
                "WHERE UserId = ?";

        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setString(1, newUserName);
            preparedStatement.setString(2, newName);
            preparedStatement.setString(3, newSurname);
            preparedStatement.setInt(4, userID);


            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
    private void Home(ActionEvent event) throws IOException {
        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Grocery List");
        stage.show();
    }

    private void closeCurrentWindow() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
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
