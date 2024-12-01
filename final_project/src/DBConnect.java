import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

/*
 * HENRY TRAN, htran21@hawk.iit.edu
 * LAB #3 for ITMD 411
 * DUE DATE: NOVEMBER 17th
 *
 * This lab focuses on SQL table creation and insertion using previous data. This file sets up the Database connection.
 *
 *
 */

public class DBConnect {
    // Database connection details
    static final String url = "jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false";
    static final String username = "fp411";
    static final String password = "411";
    
    // Connection to the database
    public static Connection connect() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage());
            return null;
        }
    }
}
