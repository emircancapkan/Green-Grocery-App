package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class OwnerPageController {

    public AnchorPane MenuAnchorPane;

    public VBox vbox;
    @FXML
    private Button viewAllOrdersReportsButton;

    @FXML
    private Button employfireCarrierButton;

    @FXML
    private Button addremoveupdateProductButton;

    @FXML
    private SplitPane splitPane;


    @FXML
    private void initialize()
    {
        System.out.println("OwnerPageController initialized");
    }

    @FXML
    private void addremoveupdateProduct() throws IOException
    {
        loadFXML("ARUproductPage.fxml");
    }

    @FXML
    private void employfireCarrier() throws IOException
    {
        loadFXML("CarrierPage.fxml");
    }

    @FXML
    private void viewAllOrders() throws IOException
    {
        loadFXML("OrderPage.fxml");
    }

    @FXML
    private void viewAllReports() throws IOException
    {
        loadFXML("OrderPage.fxml");
    }

    private void loadFXML(String FileName) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FileName));
        AnchorPane anchorPane = loader.load();

        anchorPane.setPrefSize(anchorPane.getPrefWidth(), anchorPane.getPrefHeight());

        MenuAnchorPane.getChildren().setAll(anchorPane);
    }

    @FXML
    private void back(ActionEvent event) throws IOException{
        closeCurrentWindow();
        openLoginPage();
    }

    private void closeCurrentWindow() {

        Stage stage = (Stage) MenuAnchorPane.getScene().getWindow();
        stage.close();
    }

    private void openLoginPage() throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Login Page");
        stage.show();

    }
}
