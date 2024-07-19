package com.example.demo;

import com.example.demo.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShoppingCartController {

    private static DatabaseConnection databaseConnection = new DatabaseConnection();

    public ShoppingCartController(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static void createCart(int userId) {
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "INSERT INTO shopping_cart (user_id, total_price) VALUES (?, 0.0)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addToCart(int cartId, int userId, int prodId, Double quantity, double price) {
        try (Connection connection = databaseConnection.getConnection()) {



            String sql = "INSERT INTO cart_items (cart_id, prod_id, prod_quantity, prod_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, cartId);
                preparedStatement.setInt(2, prodId);
                preparedStatement.setDouble(3, quantity);
                preparedStatement.setDouble(4, price);
                preparedStatement.executeUpdate();
                updateCartTotalPrice(connection, cartId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateCartTotalPrice(Connection connection, int cartId) throws SQLException {
        String totalSql = "SELECT SUM(prod_quantity * prod_price) AS total_price " +
                "FROM cart_items " +
                "WHERE cart_id = ?";

        try (PreparedStatement totalStatement = connection.prepareStatement(totalSql)) {
            totalStatement.setInt(1, cartId);

            try (ResultSet resultSet = totalStatement.executeQuery()) {
                if (resultSet.next()) {
                    double total_price = resultSet.getDouble("total_price");
                    String updateTotalSql = "UPDATE shopping_cart SET total_price = ? WHERE cart_id = ?";

                    try (PreparedStatement updateTotalStatement = connection.prepareStatement(updateTotalSql)) {
                        updateTotalStatement.setDouble(1, total_price);
                        updateTotalStatement.setInt(2, cartId);
                        updateTotalStatement.executeUpdate();
                    }
                }
            }
        }
    }


    public void removeFromCart(int cartId, int productId) {
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "DELETE FROM shopping_cart WHERE cart_id = ? AND prod_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, cartId);
                preparedStatement.setInt(2, productId);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewCart(int cartId) {
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "SELECT * FROM shopping_cart WHERE cart_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, cartId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int prodId = resultSet.getInt("prod_id");
                        int prodQuantity = resultSet.getInt("prod_quantity");
                        double prodPrice = resultSet.getDouble("prod_price");
                        double totalPrice = resultSet.getDouble("total_price");


                        System.out.println("Product ID: " + prodId + ", Quantity: " + prodQuantity + ", Price: " + prodPrice + ", Total Price: " + totalPrice);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static int getUserID(){
        User loggedInUser = AppData.getLoggedInUser();
        User user = new User();
        return user.getUserIdByUsername(loggedInUser.getUsername());
    }
    public static int initializeCartForUser(int userId) {
        int cartId = getCartId(userId);
        if (cartId == -1) {
            try (Connection connection = databaseConnection.getConnection()) {
                String insertSql = "INSERT INTO shopping_cart(user_id) VALUES (?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertStatement.setInt(1, userId);
                    int affectedRows = insertStatement.executeUpdate();
                    if (affectedRows > 0) {
                        try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                cartId = generatedKeys.getInt(1);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cartId;
    }
    public static int getCartId(int userId) {
        int cartId = -1;
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "SELECT cart_id FROM shopping_cart WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        cartId = resultSet.getInt("cart_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartId;
    }
    public static int getProductId(String itemName) {
        int productId = -1;
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "SELECT id FROM grocery_items WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, itemName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        productId = resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productId;
    }
    public static double totalPrice(int cartId) {
        double totalPrice = 0.0;
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "SELECT SUM(prod_price) AS total_price FROM shopping_cart WHERE cart_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, cartId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        totalPrice = resultSet.getDouble("total_price");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPrice * ((double) 120 /100);
    }
}
