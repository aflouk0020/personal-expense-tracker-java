package com.taha.expensetracker;

import com.taha.expensetracker.util.DatabaseConnection;

import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection successful.");
            } else {
                System.out.println("Database connection failed.");
            }
        } catch (Exception e) {
            System.out.println("Error connecting to database:");
            e.printStackTrace();
        }
    }
}