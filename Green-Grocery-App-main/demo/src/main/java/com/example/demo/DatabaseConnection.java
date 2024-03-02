package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public Connection databaseLink;

    public Connection getConnection()
    {
        String databaseName = "Grocery";
        String databaseUser = "root";
        String databasePassword = "1234567b";

        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return databaseLink;
    }
}
