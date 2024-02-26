package com.example.demo;

import com.example.demo.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class ARUProductController {

    @FXML
    public TextField ProductIdTextField;

    @FXML
    public TextField ProductNameTextField;

    @FXML
    public TextField ProductPriceTextField;

    @FXML
    public TextField ProductStockTextField;

    @FXML
    public TextField ProductTypeTextField;
    @FXML
    public TextField ProductThresholdTextField;
    public Button addproduct;
    public Button removeproduct;
    public Button updateproduct;
    public Button searchproduct;
    public ChoiceBox FilteringChoiceBox;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    @FXML
    private Button addProductButton;

    @FXML
    private Button removeProductButton;

    @FXML
    private Button updateProductButton;

    @FXML
    private Button searchProductButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Product> ProductTable;

    @FXML
    private TableColumn<Product, Integer> productIdColumn;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, Double> productPriceColumn;

    @FXML
    private TableColumn<Product, Double> productStockColumn;

    @FXML
    private TableColumn<Product, Double> productThresholdColumn;

    @FXML
    private TableColumn<Product, String> productTypeColumn;

    private ObservableList<Product> ProductList;

    @FXML
    public DialogPane ProductDialogPane;


    @FXML
    private void initialize() throws SQLException {



        productIdColumn.setCellValueFactory(cellData -> cellData.getValue().ProductIdProperty().asObject());
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().ProductNameProperty());
        productPriceColumn.setCellValueFactory(cellData -> cellData.getValue().ProductPriceProperty().asObject());
        productStockColumn.setCellValueFactory(cellData -> cellData.getValue().ProductStockProperty().asObject());
        productThresholdColumn.setCellValueFactory(cellData -> cellData.getValue().ProductThresholdProperty().asObject());
        productTypeColumn.setCellValueFactory(cellData -> cellData.getValue().ProductTypeProperty());

        addproduct.setVisible(false);
        updateproduct.setVisible(false);
        removeproduct.setVisible(false);

        ProductList = FXCollections.observableArrayList();

        List<Product> Products = getProducts();

        ProductList.addAll(Products);

        ProductTable.setItems(ProductList);

        ProductTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ProductDialogPane.setVisible(false);

        ProductTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
            {
                openDialogForProduct(newSelection);
            }
        });

    }

    private void openDialogForProduct(Product product) {

        ProductDialogPane.setHeaderText("Product Information");



        ProductIdTextField.setText(String.valueOf(product.getProductID()));
        ProductNameTextField.setText(product.getProductName());
        ProductPriceTextField.setText(String.valueOf(product.getProductPrice()));
        ProductStockTextField.setText(String.valueOf(product.getProductStock()));
        ProductThresholdTextField.setText(String.valueOf(product.getProductThreshold()));
        ProductTypeTextField.setText(product.getProductType());


    }

    @FXML
    private void HandleaddProduct(ActionEvent event) throws SQLException {
        ClearTextFields();
        ProductDialogPane.setVisible(true);
        ProductIdTextField.setEditable(true);
        ProductNameTextField.setEditable(true);
        ProductPriceTextField.setEditable(true);
        ProductStockTextField.setEditable(true);
        ProductThresholdTextField.setEditable(true);

        if(ProductTable != null)
        {
            ProductTable.setDisable(true);
            ProductTable.getSelectionModel().clearSelection();
        }

        addproduct.setVisible(true);
        updateproduct.setVisible(false);
        removeproduct.setVisible(false);
        categoryChoiceBox.setVisible(true);
        ProductTypeTextField.setVisible(false);
    }

    @FXML
    private void HandleupdateProduct(ActionEvent event) throws SQLException {
        ClearTextFields();
        ProductDialogPane.setVisible(true);
        ProductIdTextField.setEditable(false);
        ProductNameTextField.setEditable(false);
        ProductTypeTextField.setEditable(false);
        ProductStockTextField.setEditable(true);
        ProductPriceTextField.setEditable(true);
        ProductThresholdTextField.setEditable(true);

        if(ProductTable != null)
        {
            ProductTable.setDisable(false);
            ProductTable.getSelectionModel().clearSelection();
        }

        addproduct.setVisible(false);
        updateproduct.setVisible(true);
        removeproduct.setVisible(false);
        categoryChoiceBox.setVisible(false);
        ProductTypeTextField.setVisible(true);
    }

    @FXML
    private void HandleremoveProduct(ActionEvent event) throws SQLException {
        ClearTextFields();
        ProductDialogPane.setVisible(true);
        ProductIdTextField.setEditable(false);
        ProductNameTextField.setEditable(false);
        ProductPriceTextField.setEditable(false);
        ProductStockTextField.setEditable(false);
        ProductThresholdTextField.setEditable(false);
        ProductTypeTextField.setEditable(false);

        if(ProductTable != null)
        {
            ProductTable.setDisable(false);
            ProductTable.getSelectionModel().clearSelection();
        }

        addproduct.setVisible(false);
        updateproduct.setVisible(false);
        removeproduct.setVisible(true);
        categoryChoiceBox.setVisible(false);
        ProductTypeTextField.setVisible(true);
    }

    @FXML
    private void HandleFilteringChoiceBox(ActionEvent event) throws SQLException {

        String category = String.valueOf(FilteringChoiceBox.getValue());

        if("All".equals(category))
        {
            ProductTable.setItems(ProductList);
        }
        else
        {
            if("Vegetable".equals(category))
            {
                ObservableList<Product> VegetableFilteredList = ProductList.filtered(p -> p.getProductType().contains("Vegetable"));
                ProductTable.setItems(VegetableFilteredList);
            }
            else if("Fruit".equals(category))
            {
                ObservableList<Product> FruitFilteredList = ProductList.filtered(p -> p.getProductType().contains("Fruit"));
                ProductTable.setItems(FruitFilteredList);
            }
        }
    }

    @FXML
    private void addProduct(ActionEvent event) throws SQLException, NumberFormatException {

        String id = ProductIdTextField.getText();
        String name = ProductNameTextField.getText();
        String price = ProductPriceTextField.getText();
        String stock = ProductStockTextField.getText();
        String threshold = ProductThresholdTextField.getText();
        String type = categoryChoiceBox.getValue();

        Map<String, String> ProductHT = createHT();

        if(!isEmptyTextField("add"))
        {
                Connection connector = connectDB();

                int ID = ExtractInt(ProductIdTextField.getText());
                String Name = ProductNameTextField.getText();
                double Price = ExtractDouble(ProductPriceTextField.getText());
                int Stock = ExtractInt(ProductStockTextField.getText());
                double Threshold = ExtractDouble(ProductThresholdTextField.getText());
                String Type = categoryChoiceBox.getValue();

                Product product = new Product(ID, Name, Price, Stock, Threshold, false, Type);

                boolean checkerID = isDuplicate(product.getProductID(), connector, "id");
                boolean checkerName = isDuplicate(product.getProductName(), connector, "name");

                if(!checkerID && !checkerName)
                {
                    if(product.getProductStock() > product.getProductThreshold())
                    {
                        ProductList.add(product);

                        String query = "INSERT INTO grocery_items (id, name, price, stock_quantity, threshold, Type, image_url) VALUES (?,?,?,?,?,?,?)";

                        final PreparedStatement statement = connector.prepareStatement(query);

                        statement.setInt(1, product.getProductID());
                        statement.setString(2, product.getProductName());
                        statement.setDouble(3, product.getProductPrice());
                        statement.setDouble(4, product.getProductStock());
                        statement.setDouble(5, product.getProductThreshold());
                        statement.setString(6, product.getProductType());
                        statement.setString(7, "https://i.hizliresim.com/duzrvsr.png");

                        statement.executeUpdate();

                        ProductTable.refresh();
                    }
                    else
                    {
                        showAlert("Invalid Stock-Threshold Relation");
                    }
                }
                else
                {
                    showAlert("Duplicate Entry");

                    if(checkerID)
                    {
                        ProductIdTextField.clear();
                    }

                    if(checkerName)
                    {
                        ProductNameTextField.clear();
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

    private boolean isEmptyTextField(String choice)
    {
        boolean ID_text = ProductIdTextField.getText().isEmpty();
        boolean Name_text = ProductNameTextField.getText().isEmpty();
        boolean Price_text = ProductPriceTextField.getText().isEmpty();
        boolean Stock_text = ProductStockTextField.getText().isEmpty();
        boolean Threshold_text = ProductThresholdTextField.getText().isEmpty();
        boolean Type_text = categoryChoiceBox.getItems().isEmpty();

        return ID_text || Name_text || Price_text || Stock_text || Threshold_text || Type_text;
    }

    private boolean isDuplicate(Object obj, Connection connect, String cond) throws SQLException {
        String query = "SELECT COUNT(*) FROM grocery_items WHERE " + cond + " = ?";

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
        if(!ProductIdTextField.getText().isEmpty())
        {
            ProductIdTextField.clear();
        }

        if(!ProductNameTextField.getText().isEmpty())
        {
            ProductNameTextField.clear();
        }

        if(!ProductPriceTextField.getText().isEmpty())
        {
            ProductPriceTextField.clear();
        }

        if(!ProductStockTextField.getText().isEmpty())
        {
            ProductStockTextField.clear();
        }

        if(!ProductThresholdTextField.getText().isEmpty())
        {
            ProductThresholdTextField.clear();
        }

        if(!ProductTypeTextField.getText().isEmpty())
        {
            ProductTypeTextField.clear();
        }

        if(!categoryChoiceBox.getItems().isEmpty())
        {
            categoryChoiceBox.setValue(null);
        }
    }

    @FXML
    private void removeProduct(ActionEvent event) throws SQLException {

        Product selectedProduct = ProductTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null)
        {
            ProductList.remove(selectedProduct);

            Connection connector = connectDB();

            String query = "DELETE FROM grocery_items WHERE id=?";

            final PreparedStatement statement = connector.prepareStatement(query);

            statement.setInt(1, selectedProduct.getProductID());

            statement.executeUpdate();
        }
        else
        {
            showAlert("Unselected Product");
        }
    }

    @FXML
    private void updateProduct(ActionEvent event) throws SQLException {

        Product selectedProduct = ProductTable.getSelectionModel().getSelectedItem();

        if(selectedProduct != null)
        {
            if(ProductPriceTextField.getText().isEmpty() || ProductStockTextField.getText().isEmpty() || ProductThresholdTextField.getText().isEmpty())
            {
                showAlert("Incomplete Information");
            }
            else
            {
                Connection connector = connectDB();

                int selectedID = selectedProduct.getProductID();

                selectedProduct.setProductID(ExtractInt(ProductIdTextField.getText()));
                selectedProduct.setProductName(ProductNameTextField.getText());
                selectedProduct.setProductPrice(ExtractDouble(ProductPriceTextField.getText()));
                selectedProduct.setProductStock(ExtractDouble(ProductStockTextField.getText()));
                selectedProduct.setProductThreshold(ExtractDouble(ProductThresholdTextField.getText()));

                if(selectedProduct.getProductStock() <= selectedProduct.getProductThreshold())
                {
                    checkThreshold(selectedProduct);
                }

                int index = ProductList.indexOf(selectedProduct);

                if (index != -1)
                {
                    ProductList.set(index, selectedProduct);
                }

                String query = "UPDATE grocery_items SET id = ?, name = ?, price = ?, stock_quantity = ?, threshold = ? WHERE id=?";

                final PreparedStatement statement = connector.prepareStatement(query);

                statement.setInt(1, selectedProduct.getProductID());
                statement.setString(2, selectedProduct.getProductName());
                // If Price column type is double, the method should be "setDouble".
                statement.setDouble(3, selectedProduct.getProductPrice());
                statement.setDouble(4, selectedProduct.getProductStock());
                statement.setDouble(5, selectedProduct.getProductThreshold());
                statement.setInt(6, selectedID);

                statement.executeUpdate();

                ProductTable.refresh();
            }
        }
        else
        {
            showAlert("Unselected Product");
        }
    }

    @FXML
    private void searchProduct(ActionEvent event) {

        String Keyword = searchField.getText().toLowerCase();

        if(!Keyword.isEmpty())
        {
            ObservableList<Product> filteredList = ProductList.filtered(p -> p.getProductName().toLowerCase().contains(Keyword));
            ProductTable.setItems(filteredList);
        }
        else
        {
            ProductTable.setItems(ProductList);
        }
    }


    private void checkThreshold(Product product) throws SQLException {

        if(!product.getCheckerThreshold())
        {
            Connection connector = connectDB();

            String query = "UPDATE grocery_items SET Price = Price*2 WHERE stock_quantity <= threshold AND id = ?";

            final PreparedStatement statement = connector.prepareStatement(query);

            statement.setInt(1, product.getProductID());

            statement.executeUpdate();

            product.setProductPrice(product.getProductPrice()*2);

            int index = ProductList.indexOf(product);

            if(index != -1)
            {
                ProductList.set(index, product);
            }

            product.setCheckerThreshold(true);
        }
    }

    private List<Product> getProducts() throws SQLException {

        List<Product> productlist = new ArrayList<>();

        DatabaseConnection connector = new DatabaseConnection();

        Connection connect = connector.getConnection();

        String query = "SELECT * FROM grocery_items";

        PreparedStatement statement = connect.prepareStatement(query);

        ResultSet Data = statement.executeQuery();

        while(Data.next())
        {
            int productId = Data.getInt("id");
            String productName = Data.getString("name");
            double productprice = Data.getDouble("price");
            int productstock = Data.getInt("stock_quantity");
            double productthreshold = Data.getDouble("threshold");
            String producttype = Data.getString("Type");

            boolean checker = productstock <= productthreshold;

            Product added_product = new Product(productId, productName, productprice, productstock, productthreshold, checker, producttype);
            productlist.add(added_product);
        }

        return productlist;
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

    private Double ExtractDouble(String str)
    {
        if (!str.isEmpty())
        {
            return parseDouble(str);
        }
        return (double) 0;
    }

    private Map<String,String> createHT()
    {
        Map<String, String> ProductHT = new HashMap<>();

        ProductHT.put("apple", "Fruit");
        ProductHT.put("banana", "Fruit");
        ProductHT.put("carrot", "Vegetable");
        ProductHT.put("tomato", "Fruit");
        ProductHT.put("spinach", "Vegetable");
        ProductHT.put("orange", "Fruit");
        ProductHT.put("broccoli", "Vegetable");
        ProductHT.put("grapes", "Fruit");
        ProductHT.put("cucumber", "Vegetable");
        ProductHT.put("pineapple", "Fruit");
        ProductHT.put("kiwi", "Fruit");
        ProductHT.put("lettuce", "Vegetable");
        ProductHT.put("corn", "Vegetable");
        ProductHT.put("parsley", "Vegetable");
        ProductHT.put("onion", "Vegetable");
        ProductHT.put("garlic", "Vegetable");
        ProductHT.put("pear", "Fruit");
        ProductHT.put("peach", "Fruit");
        ProductHT.put("apricot", "Fruit");
        ProductHT.put("watermelon", "Fruit");

        return ProductHT;
    }

    private boolean checkSpecialCharacter(String str)
    {
        return str.contains("'[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\/-]'");
    }

    private boolean checkCharacter(String str)
    {
        return str.contains("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }
}
