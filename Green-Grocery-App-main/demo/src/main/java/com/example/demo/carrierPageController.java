package com.example.demo;

import com.example.demo.models.Carrier;
import com.example.demo.models.OrderDetail;
import com.example.demo.models.OrderProductDetail;
import com.example.demo.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import com.example.demo.LoginController;
import javafx.stage.Stage;

public class carrierPageController{
    public Button cancelproduct;
    @FXML
    public TableView<OrderDetail> OrderTable;
    public TableView<OrderProductDetail> OrderProductTable;
    @FXML
    public TableColumn<OrderDetail, Integer> OrderIdColumn;
    @FXML
    public TableColumn<OrderDetail, String> OrderNameColumn;
    @FXML
    public TableColumn<OrderDetail, Double> TotalPriceColumn;
    @FXML
    public TableColumn<OrderDetail, String> OrderDateColumn;
    @FXML
    public TableColumn<OrderDetail, String> DeliveryDateColumn;
    @FXML
    public TableColumn<OrderProductDetail, String> OrderProductNameColumn;
    @FXML
    public TableColumn<OrderProductDetail, Double> OrderProductQuantityColumn;
    @FXML
    public TableColumn<OrderProductDetail, Double> OrderProductPriceColumn;
    @FXML
    public TextField OrderNameTextField;
    @FXML
    public TextField OrderPriceTextField;
    @FXML
    public TextField DeliveryDateTextField;
    @FXML
    public TextField OrderDateTextField;
    @FXML
    public TextField OrderIdTextField;
    public TextField searchField;
    public Button viewproduct;
    public Button viewproduct1;
    public TableColumn<OrderDetail, String> AssignedCarrierColumn;
    public Button assigncarrier;
    public ChoiceBox<String> FilteringChoiceBox;
    public TextField AddressTextField;
    public Button acceptproduct;
    public Button acceptproduct1;

    private ObservableList<OrderDetail> OrderList;

    private ObservableList<OrderProductDetail> OrderProductList;

    @FXML
    public DialogPane OrderDialogPane;


    @FXML
    private void initialize() throws SQLException
    {
        OrderIdColumn.setCellValueFactory(cellData -> cellData.getValue().OrderIdProperty().asObject());
        OrderNameColumn.setCellValueFactory(cellData -> cellData.getValue().OrderNameProperty());
        TotalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().TotalPriceProperty().asObject());
        OrderDateColumn.setCellValueFactory(cellData -> cellData.getValue().OrderDateProperty());
        DeliveryDateColumn.setCellValueFactory(cellData -> cellData.getValue().DeliveryDateProperty());

        OrderList = FXCollections.observableArrayList();

        List<OrderDetail> Orders = getOrders();

        OrderList.addAll(Orders);

        OrderTable.setItems(OrderList);

        OrderProductNameColumn.setCellValueFactory(cellData -> cellData.getValue().OrderProductNameProperty());
        OrderProductQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().OrderProductQuantityProperty().asObject());
        OrderProductPriceColumn.setCellValueFactory(cellData -> cellData.getValue().OrderProductPriceProperty().asObject());

        OrderProductList = FXCollections.observableArrayList();

        OrderDialogPane.setVisible(false);
        viewproduct.setVisible(false);

        OrderTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        OrderDialogPane.setVisible(false);
        OrderIdTextField.setEditable(false);
        OrderNameTextField.setEditable(false);
        OrderPriceTextField.setEditable(false);
        OrderDateTextField.setEditable(false);
        DeliveryDateTextField.setEditable(false);
        AddressTextField.setEditable(false);
        acceptproduct.setVisible(false);
        acceptproduct1.setVisible(false);

        OrderIdTextField.setText(" ");
        OrderNameTextField.setText(" ");
        OrderPriceTextField.setText(" ");
        OrderDateTextField.setText(" ");
        DeliveryDateTextField.setText(" ");
        AddressTextField.setText(" ");
    }

    private void openDialogForOrder(OrderDetail order) throws SQLException {
        OrderIdTextField.setText(String.valueOf(order.getOrderID()));
        OrderNameTextField.setText(order.getOrderName());
        OrderPriceTextField.setText(String.valueOf(order.getTotalPrice()));
        OrderDateTextField.setText(order.getOrderDate());
        DeliveryDateTextField.setText(order.getDeliveryDate());

        String Address = String.valueOf(getOrderAddress(order));

        AddressTextField.setText(Address);
    }

    @FXML
    private void HandleFilteringChoiceBox(ActionEvent event)
    {

    }

    @FXML
    private void Handlevieworder(ActionEvent event) throws SQLException {

        ClearTextFields();
        OrderDialogPane.setVisible(true);

        if(OrderProductTable != null)
        {
            OrderTable.getSelectionModel().clearSelection();
        }


        if(OrderProductTable != null)
        {
            OrderProductTable.getItems().clear();
        }

        acceptproduct.setVisible(false);
        acceptproduct1.setVisible(false);
        viewproduct.setVisible(true);
        viewproduct.setDisable(false);
    }

    @FXML
    private void Handleacceptorder(ActionEvent event) throws SQLException {

        ClearTextFields();
        OrderDialogPane.setVisible(true);

        if(OrderProductTable != null)
        {
            OrderTable.getSelectionModel().clearSelection();
        }


        if(OrderProductTable != null)
        {
            OrderProductTable.getSelectionModel().clearSelection();
        }

        acceptproduct.setVisible(true);
        acceptproduct1.setVisible(false);
        viewproduct.setVisible(true);
    }

    @FXML
    private void viewProduct(ActionEvent event) throws SQLException
    {
        OrderDetail selectedOrder = OrderTable.getSelectionModel().getSelectedItem();

        if(selectedOrder.getOrderID() == ExtractInt(OrderIdTextField.getText()) && Objects.equals(selectedOrder.getOrderName(), OrderNameTextField.getText()) && selectedOrder.getTotalPrice() == ExtractDouble(OrderPriceTextField.getText()) && Objects.equals(selectedOrder.getOrderDate(), OrderDateTextField.getText()) && Objects.equals(selectedOrder.getDeliveryDate(), DeliveryDateTextField.getText()))
        {
            showAlert("Warning");
            OrderProductTable.getItems().clear();
            OrderProductList = FXCollections.observableArrayList();
            List<OrderProductDetail> orderproductlist = getOrderProductDetails(selectedOrder);
            OrderProductList.addAll(orderproductlist);
            OrderProductTable.setItems(OrderProductList);
        }
        else
        {
            OrderProductList = FXCollections.observableArrayList();
            List<OrderProductDetail> orderproductlist = getOrderProductDetails(selectedOrder);
            OrderProductList.addAll(orderproductlist);
            OrderProductTable.setItems(OrderProductList);

        }
    }

    @FXML
    private void acceptProduct(ActionEvent event) throws SQLException
    {
        OrderDetail selectedOrder = OrderTable.getSelectionModel().getSelectedItem();

        if(selectedOrder != null)
        {
            Connection connector = connectDB();

            User loggedInUser = AppData.getLoggedInUser();
            User user = new User();
            String username = loggedInUser.getUsername();

            String Query = "UPDATE order_details SET Carrier = ? WHERE order_id = ?";

            final PreparedStatement statement = connector.prepareStatement(Query);

            int SelectedOrderID = ExtractInt(OrderIdTextField.getText());
            String Address = String.valueOf(getOrderAddress(selectedOrder));

            statement.setString(1, username);
            statement.setInt(2, SelectedOrderID);

            statement.executeUpdate();

            AddressTextField.setText(Address);

            OrderList.remove(selectedOrder);
        }
        else
        {
            showAlert("Unselected Order");
        }
    }

    private void showAlert(String AlertDescription)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(AlertDescription);

        alert.showAndWait();
    }

    @FXML
    private void searchProduct(ActionEvent event) {

        String Keyword = searchField.getText().toLowerCase();

        if(!Keyword.isEmpty())
        {
            ObservableList<OrderDetail> filteredList = OrderList.filtered(p -> p.getOrderName().toLowerCase().contains(Keyword));
            OrderTable.setItems(filteredList);
        }
        else
        {
            OrderTable.setItems(OrderList);
        }
    }

    private void ClearTextFields()
    {
        if(!OrderIdTextField.getText().isEmpty())
        {
            OrderIdTextField.clear();
        }

        if(!OrderNameTextField.getText().isEmpty())
        {
            OrderNameTextField.clear();
        }

        if(!OrderPriceTextField.getText().isEmpty())
        {
            OrderPriceTextField.clear();
        }

        if(!OrderDateTextField.getText().isEmpty())
        {
            OrderDateTextField.clear();
        }

        if(!DeliveryDateTextField.getText().isEmpty())
        {
            DeliveryDateTextField.clear();
        }
    }

    private static Connection connectDB()
    {
        DatabaseConnection connector = new DatabaseConnection();

        return connector.getConnection();
    }

    private OrderDetail getSelectedOrder()
    {
        OrderDetail selectedorder = OrderTable.getSelectionModel().getSelectedItem();
        return selectedorder;
    }
    private List<OrderProductDetail> getOrderProductDetails(OrderDetail order) throws SQLException {

        List<OrderProductDetail> orderproductList = new ArrayList<>();

        Connection connector = connectDB();

        if(order != null)
        {
            if(OrderProductTable.hasProperties())
            {
                OrderProductTable.getItems().clear();
            }

            Hashtable<Integer, Double> OrderProductHT = getOrderDetails(order);

            for (Integer key : OrderProductHT.keySet()) {
                String Query = "SELECT name, price FROM grocery_items WHERE id = ?";

                final PreparedStatement statement = connector.prepareStatement(Query);

                statement.setInt(1, key);

                ResultSet Data = statement.executeQuery();

                while(Data.next())
                {
                    String Name = Data.getString("name");
                    double price = Data.getDouble("price");

                    double totalprice = price*OrderProductHT.get(key);

                    OrderProductDetail OrderProduct = new OrderProductDetail(Name, OrderProductHT.get(key), totalprice);

                    orderproductList.add(OrderProduct);
                }
            }
            openDialogForOrder(order);
        }
        else
        {
            showAlert("Unselected Order");
            ClearTextFields();

            if(OrderProductTable != null)
            {
                OrderProductTable.getItems().clear();
            }
        }

        return orderproductList;
    }

    private Hashtable<Integer, Double> getOrderDetails(OrderDetail order) throws SQLException {

        Hashtable<Integer, Double> hashtable = new Hashtable<>();

        Connection connector = connectDB();

        String Query = "SELECT order_items, quantity FROM order_details WHERE order_id = ?";

        final PreparedStatement statement = connector.prepareStatement(Query);

        statement.setInt(1, order.getOrderID());

        ResultSet Data = statement.executeQuery();

        while(Data.next())
        {
            String items = Data.getString("order_items");
            String quantity = Data.getString("quantity");

            String[] splittedItems = items.replaceAll("[\\[\\]]", "").split("[,\\s]+");
            String[] splittedQuantity = quantity.replaceAll("[\\[\\]]", "").split("[,\\s]+");

            for(int i = 0; i < splittedItems.length; i++)
            {
                if(hashtable.containsKey(ExtractInt(splittedItems[i])))
                {
                    double current = hashtable.get(ExtractInt(splittedItems[i]));
                    double New = current + ExtractDouble(splittedQuantity[i]);
                    hashtable.put(ExtractInt(splittedItems[i]), New);
                }
                else
                {
                    hashtable.put(ExtractInt(splittedItems[i]), ExtractDouble(splittedQuantity[i]));
                }
            }
        }
        return hashtable;
    }

    private int ExtractInt(String str)
    {

        if (!str.isEmpty())
        {
            return parseInt(str);
        }
        return 0;
    }

    private Double ExtractDouble(String str)
    {
        if (!str.isEmpty())
        {
            return parseDouble(str);
        }
        return (double) 0;
    }


    private List<OrderDetail> getOrders() throws SQLException {

        List<OrderDetail> orderlist = new ArrayList<>();

        DatabaseConnection connector = new DatabaseConnection();

        Connection connect = connector.getConnection();

        String query = "SELECT * FROM order_details WHERE Carrier IS NULL";

        PreparedStatement statement = connect.prepareStatement(query);

        ResultSet Data = statement.executeQuery();

        while(Data.next())
        {
            int orderId = Data.getInt("order_id");
            double totalprice = Data.getDouble("total_price");
            Timestamp Orderdate = Data.getTimestamp("order_date");
            String orderName = Data.getString("orders_name");
            String deliverytime = Data.getString("delivery_time");
            String AssignedCarrier = Data.getString("Carrier");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String orderdate = dateFormat.format(Orderdate);

            OrderDetail added_order = new OrderDetail(orderId, orderName, orderdate, deliverytime, totalprice, AssignedCarrier);
            orderlist.add(added_order);
        }

        return orderlist;
    }

    private StringBuilder getOrderAddress(OrderDetail order) throws SQLException
    {
        StringBuilder Address = new StringBuilder();

        Connection connector = connectDB();
        
        String OrderDetailQuery = "SELECT address_id FROM order_details WHERE order_id = ?";

        final PreparedStatement statement1 = connector.prepareStatement(OrderDetailQuery);

        statement1.setInt(1, order.getOrderID());

        ResultSet Data = statement1.executeQuery();

        while(Data.next())
        {
            int AddressID = Data.getInt("address_id");

            String AddressQuery = "SELECT * FROM addresses WHERE AddressId = ?";

            final PreparedStatement statement2 = connector.prepareStatement(AddressQuery);

            statement2.setInt(1, AddressID);

            ResultSet AddressData = statement2.executeQuery();

            while(AddressData.next())
            {
                String AddressName = AddressData.getString("AddressName");
                String Neighborhood = AddressData.getString("NeighborhoodName");
                String AptNo = AddressData.getString("ApartmentNo");
                String Flat = AddressData.getString("FlatNo");
                String district = AddressData.getString("District");
                String city = AddressData.getString("City");
                String country = AddressData.getString("Country");

                Address.append(AddressName);
                Address.append(Neighborhood);
                Address.append(AptNo);
                Address.append(Flat);
                Address.append(district);
                Address.append(city);
                Address.append(country);
            }
        }
        return Address;
    }

    private int getAddressID(OrderDetail order) throws SQLException {
        int AddressID = 0;

        Connection connector = connectDB();

        String Query = "SELECT address_id FROM order_details WHERE order_id = ?";

        final PreparedStatement statement = connector.prepareStatement(Query);

        statement.setInt(1, order.getOrderID());

        ResultSet Data = statement.executeQuery();

        while(Data.next())
        {
            AddressID = Data.getInt("address_id");
        }
        return AddressID;
    }
    private StringBuilder getUserName(OrderDetail order) throws SQLException
    {
        StringBuilder Username = new StringBuilder();

        Connection connector = connectDB();

        String Query = "SELECT UserId FROM addresses WHERE AddressId = ?";

        int AddressID = getAddressID(order);

        final PreparedStatement statement = connector.prepareStatement(Query);

        statement.setInt(1, AddressID);

        ResultSet Data = statement.executeQuery();

        while(Data.next())
        {
            int ID = Data.getInt("UserId");

            String UsersQuery = "SELECT UserName FROM Users Where UserID = ?";

            final PreparedStatement statement1 = connector.prepareStatement(UsersQuery);

            statement1.setInt(1, ID);

            ResultSet UserNameData = statement1.executeQuery();

            while(UserNameData.next())
            {
                Username.append(UserNameData.getString("UserName"));
            }
        }
        return Username;
    }

    private void openLoginPage() throws IOException {

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Login Page");
        stage.show();

    }
    private void closeCurrentWindow() {

        Stage stage = (Stage) OrderNameTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void back(ActionEvent event) throws IOException{
        closeCurrentWindow();
        openLoginPage();
    }
}