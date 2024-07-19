package com.example.demo;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class RegisterPageController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repasswordField;


    @FXML
    public void initialize() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                register();
            }
        });
        firstNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                register();
            }
        });
        lastNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                register();
            }
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                register();
            }
        });
        repasswordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                register();
            }
        });
    }

    @FXML
    private void register() {
        String userName = usernameField.getText();
        String name = firstNameField.getText();
        String surname = lastNameField.getText();
        String password = passwordField.getText();
        String repassword = repasswordField.getText();
        String role = "Customer";
        if (userName.isEmpty() || name.isEmpty() || surname.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            showErrorAlert("Register Error","Please, fill the blank parts!");
            return;
        }


        if(password.equals(repassword))
        {
            DatabaseConnection connectNow = new DatabaseConnection();
            try (Connection connectDB = connectNow.getConnection()) {
                String query = "INSERT INTO Users (UserName, Name, Surname, Password, Role) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
                    preparedStatement.setString(1, userName);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, surname);
                    preparedStatement.setString(4, password);
                    preparedStatement.setString(5, role);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Registration is successful", "Registration completed successfully.");
                        closeCurrentWindow();
                        openLoginPage();

                    } else {
                        showAlert("Error", "An error occurred during the registration process.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Error while connecting database.");
            }

        }
        else
        {
            showAlert("Mismatch", "Password and Re-Password are not same!");
        }

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void openLoginPage() throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Login Page");
        stage.show();

    }
    private void closeCurrentWindow() {

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void back(ActionEvent event) throws IOException{
        closeCurrentWindow();
        openLoginPage();
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
