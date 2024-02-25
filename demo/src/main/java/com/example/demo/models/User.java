package com.example.demo.models;

import com.example.demo.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String imageUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getUserIdByUsername(String username) {
        int userId = -1; // Varsayılan olarak -1 veya başka bir hata durumu değeri
        DatabaseConnection connectNow = new DatabaseConnection();
        // SQL sorgusu
        String query = "SELECT UserID FROM Users WHERE Username = ?";

        try(Connection connectDB = connectNow.getConnection()) {
            PreparedStatement statement = connectDB.prepareStatement(query);

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("UserId");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

}
