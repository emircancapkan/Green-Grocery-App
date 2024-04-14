package com.example.demo;

import com.example.demo.models.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class LoginController {


    public Button loginBtn;
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;


    @FXML
    public void initialize() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    login();
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    login();
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }



    @FXML
    private void login() throws IOException, SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authenticateUser(username, password)) {



            showConfirmationAlert("Login Confirmation","Welcome to Group 21's Grocery Shop! \n" + "Logged in successfully!");

        } else {
            System.out.println("Invalid credentials");
            showErrorAlert("Failed to login!","Please, use correct username or password!");
        }
    }

    @FXML
    private void register(ActionEvent event) throws IOException {
        closeCurrentWindow();
        openRegisterPage();

    }

    private boolean authenticateUser(String username, String password) {

        DatabaseConnection connectNow = new DatabaseConnection();
        try(Connection connectDB = connectNow.getConnection()) {
            String query = "SELECT * FROM Users WHERE UserName = ? AND Password = ?";
            try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
                PreparedStatement statement = connectDB.prepareStatement(query);
                statement.setString(1, usernameField.getText());
                statement.setString(2, passwordField.getText());

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    User user = new User();
                    user.setUsername(resultSet.getString("UserName"));
                    user.setFirstName(resultSet.getString("Name"));
                    user.setLastName(resultSet.getString("Surname"));
                    user.setImageUrl(resultSet.getString("Image"));
                    AppData.setLoggedInUser(user);
                    String role=resultSet.getString("Role");

                    if(Objects.equals(role, "Admin")){
                        closeCurrentWindow();
                        openOwnerPage();
                        System.out.println("Login successful");
                    }
                    else if(Objects.equals(role, "Customer")){
                        closeCurrentWindow();
                        openGroceryList();
                        System.out.println("Login successful");
                    }

                    else{
                        openCarrierPage();
                        closeCurrentWindow();
                        System.out.println("Login successful");
                    }                    return true;
                }
                else
                {
                    return false;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    private void openGroceryList() throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Grocery List");
        stage.show();

    }
    private void openRegisterPage() throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterPage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Register Page");
        stage.show();

    }

    private void openOwnerPage() throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OwnerPage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Owner Page");
        stage.show();

    }
    private void openCarrierPage() throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("carrier.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Carrier Page");
        stage.show();

    }
    private void closeCurrentWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String title, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(content);

        errorAlert.showAndWait();
    }

    private boolean showConfirmationAlert(String title, String content) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(title);
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(content);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


}
