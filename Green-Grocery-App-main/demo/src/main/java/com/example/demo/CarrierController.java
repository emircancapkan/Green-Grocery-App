package com.example.demo;

import com.example.demo.models.Carrier;
import com.example.demo.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class CarrierController {

    public TextField CarrierIdTextField;
    public TextField CarrierUsernameTextField;
    public TextField CarrierNameTextField;
    public TextField CarrierSurnameTextField;
    public TextField searchField;
    public Button employcarrier;
    public Button firecarrier;
    public TableView<Carrier> CarrierTable;
    public TableColumn<Carrier, Integer> CarrierIdColumn;
    public TableColumn<Carrier, String> CarrierUsernameColumn;
    public TableColumn<Carrier, String> CarrierNameColumn;
    public TableColumn<Carrier, String> CarrierSurnameColumn;
    public MenuItem employcarrierMenu;
    public MenuItem firecarrierMenu;
    private ObservableList<Carrier> CarrierList;
    public DialogPane CarrierDialogPane;


    @FXML
    private void initialize() throws SQLException {


        CarrierIdColumn.setCellValueFactory(cellData -> cellData.getValue().CarrierIDProperty().asObject());
        CarrierNameColumn.setCellValueFactory(cellData -> cellData.getValue().CarrierNameProperty());
        CarrierSurnameColumn.setCellValueFactory(cellData -> cellData.getValue().CarrierSurnameProperty());
        CarrierUsernameColumn.setCellValueFactory(cellData -> cellData.getValue().CarrierUsernameProperty());

        employcarrier.setVisible(false);
        firecarrier.setVisible(false);


        CarrierList = FXCollections.observableArrayList();

        List<Carrier> Products = getCarriers();

        CarrierList.addAll(Products);

        CarrierTable.setItems(CarrierList);

        CarrierTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        CarrierDialogPane.setVisible(false);



        CarrierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                openDialogForCarrier(newSelection);
            }
        });


    }

    private void openDialogForCarrier(Carrier carrier) {

        CarrierDialogPane.setHeaderText("Carrier Information");

        CarrierIdTextField.setText(String.valueOf(carrier.getCarrierID()));
        CarrierNameTextField.setText(carrier.getCarrierName());
        CarrierSurnameTextField.setText(carrier.getCarrierSurname());
        CarrierUsernameTextField.setText(carrier.getCarrierUsername());

    }

    @FXML
    private void Handleemploycarrier(ActionEvent event)
    {
        ClearTextFields();
        CarrierDialogPane.setVisible(true);

        if(CarrierTable != null)
        {
            CarrierTable.setDisable(true);
            CarrierTable.getSelectionModel().clearSelection();
        }

        employcarrier.setVisible(true);
        firecarrier.setVisible(false);
    }

    @FXML
    private void Handlefirecarrier(ActionEvent event)
    {
        ClearTextFields();
        CarrierDialogPane.setVisible(true);

        if(CarrierTable != null)
        {
            CarrierTable.setDisable(false);
            CarrierTable.getSelectionModel().clearSelection();
        }

        employcarrier.setVisible(false);
        firecarrier.setVisible(true);
    }

    @FXML
    private void employCarrier(ActionEvent event) throws SQLException, NumberFormatException {

        int ID = ExtractInt(CarrierIdTextField.getText());
        String Name = CarrierNameTextField.getText();
        String Surname = CarrierSurnameTextField.getText();
        String Username = CarrierUsernameTextField.getText();

        Carrier carrier = new Carrier(ID, Username, Name, Surname);

        if(!IsEmptyTextField("add"))
        {
            Connection connector = connectDB();

            boolean checkerID = isDuplicate(carrier.getCarrierID(), connector, "UserID");
            boolean checkerName = isDuplicate(carrier.getCarrierName(), connector, "Name");

            if(!checkerID && !checkerName)
            {
                CarrierList.add(carrier);

                String query = "INSERT INTO users (UserID, UserName, Name, Surname, Role, Password) VALUES (?,?,?,?,?,?)";

                final PreparedStatement statement = connector.prepareStatement(query);

                statement.setInt(1, carrier.getCarrierID());
                statement.setString(2, carrier.getCarrierUsername());
                statement.setString(3, carrier.getCarrierName());
                statement.setString(4, carrier.getCarrierSurname());
                statement.setString(5, "Carrier");
                statement.setString(6, carrier.getCarrierUsername());

                statement.executeUpdate();

                CarrierTable.refresh();
            }
            else
            {
                showAlert("Duplicate Entry");

                if(checkerID)
                {
                    CarrierIdTextField.clear();
                }

                if(checkerName)
                {
                    CarrierNameTextField.clear();
                }
            }
        }
        else
        {
            showAlert("Incomplete Information");
        }
    }

    private static Connection connectDB()
    {
        DatabaseConnection connector = new DatabaseConnection();

        return connector.getConnection();
    }

    private void showAlert(String AlertDescription)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(AlertDescription);

        alert.showAndWait();
    }

    private boolean IsEmptyTextField(String choice)
    {
        boolean ID_text = CarrierIdTextField.getText().isEmpty();
        boolean Name_text = CarrierNameTextField.getText().isEmpty();
        boolean Surname_text = CarrierSurnameTextField.getText().isEmpty();
        boolean Username_text = CarrierUsernameTextField.getText().isEmpty();

        return ID_text || Name_text || Surname_text || Username_text;
    }

    private boolean isDuplicate(Object obj, Connection connect, String cond) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE " + cond + " = ?";

        final PreparedStatement statement = connect.prepareStatement(query);

        statement.setObject(1, obj);

        ResultSet Data = statement.executeQuery();

        if(Data.next())
        {
            return Data.getInt(1) > 0;
        }
        return false;
    }

    private void ClearTextFields()
    {
        if(!CarrierIdTextField.getText().isEmpty())
        {
            CarrierIdTextField.clear();
        }

        if(!CarrierNameTextField.getText().isEmpty())
        {
            CarrierNameTextField.clear();
        }

        if(!CarrierSurnameTextField.getText().isEmpty())
        {
            CarrierSurnameTextField.clear();
        }

        if(!CarrierUsernameTextField.getText().isEmpty())
        {
            CarrierUsernameTextField.clear();
        }
    }

    @FXML
    private void fireCarrier(ActionEvent event) throws SQLException {

        Carrier selectedCarrier = CarrierTable.getSelectionModel().getSelectedItem();

        if(selectedCarrier != null)
        {
            CarrierList.remove(selectedCarrier);

            Connection connector = connectDB();

            String query = "DELETE FROM users WHERE UserID=? AND Role=?";

            final PreparedStatement statement = connector.prepareStatement(query);

            statement.setInt(1, selectedCarrier.getCarrierID());
            statement.setString(2, "Carrier");

            statement.executeUpdate();
        }
        else
        {
            showAlert("Unselected Carrier");
        }
    }

    @FXML
    private void searchProduct(ActionEvent event) {

        String Keyword = searchField.getText().toLowerCase();

        if(!Keyword.isEmpty())
        {
            ObservableList<Carrier> filteredList = CarrierList.filtered(p -> p.getCarrierName().toLowerCase().contains(Keyword));
            CarrierTable.setItems(filteredList);
        }
        else
        {
            CarrierTable.setItems(CarrierList);
        }
    }


    private List<Carrier> getCarriers() throws SQLException {

        List<Carrier> Carrierlist = new ArrayList<>();

        DatabaseConnection connector = new DatabaseConnection();

        Connection connect = connector.getConnection();

        String query = "SELECT * FROM users WHERE Role=?";

        final PreparedStatement statement = connect.prepareStatement(query);

        statement.setString(1, "Carrier");

        ResultSet Data = statement.executeQuery();

        while(Data.next())
        {
            int CarrierId = Data.getInt("UserID");
            String CarrierUsername = Data.getString("UserName");
            String CarrierSurname = Data.getString("Surname");
            String CarrierName = Data.getString("Name");

            Carrier added_carrier = new Carrier(CarrierId, CarrierUsername, CarrierName, CarrierSurname);
            Carrierlist.add(added_carrier);
        }

        return Carrierlist;
    }

    private int ExtractInt(String Price)
    {
        Pattern pattern = Pattern.compile("\\d+");

        Matcher matcher = pattern.matcher(Price);

        if (matcher.find())
        {
            return parseInt(matcher.group());
        }
        return 0;
    }
}
