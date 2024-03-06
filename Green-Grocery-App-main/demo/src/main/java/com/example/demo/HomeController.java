package com.example.demo;

import com.example.demo.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    public Label welcome;

    public ImageView userImageView;

    public Label userNameLabel;

    @FXML
    private void initialize(){
        User user_ = AppData.getLoggedInUser();


        if (user_ != null) {
            if(user_.getImageUrl() != null)
            {
                userImageView.setImage(new Image(user_.getImageUrl()));
            }else
            {
                userImageView.setImage(new Image("https://ssl.gstatic.com/docs/common/profile/alligator_lg.png"));
            }

            userNameLabel.setText(user_.getFirstName() + " " + user_.getLastName());
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
    private void handleMyOrderButton() throws IOException {

        closeCurrentWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserOrderPage.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("My Order");
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

        Stage stage = (Stage) welcome.getScene().getWindow();
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
