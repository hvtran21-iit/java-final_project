import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Dao {
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement checkStmt = null;
    private PreparedStatement insertStmt = null;

    // Constructor to initialize the DB connection
    public Dao() {
        conn = DBConnect.connect(); // Assuming DBConnect.connect() is a method that returns a connection
    }

    // Create tables if they do not exist
    public void createTable() {
        String createTicketsTableSQL = "CREATE TABLE IF NOT EXISTS htran_tickets ("
                + "tid INT AUTO_INCREMENT PRIMARY KEY, "
                + "user VARCHAR(100), "
                + "ticketDesc TEXT, "
                + "start_date DATETIME, "
                + "end_date DATETIME, "
                + "status VARCHAR(20) DEFAULT 'open');";

        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS htran_users ("
                + "username VARCHAR(100) PRIMARY KEY, "
                + "password VARCHAR(100), "
                + "role VARCHAR(20));";  // 'user' or 'admin'

        try {
            if (conn == null) {
                conn = DBConnect.connect();
            }
            stmt = conn.createStatement();  // Initialize statement object

            // Create tables
            stmt.executeUpdate(createTicketsTableSQL);
            stmt.executeUpdate(createUsersTableSQL);
            //JOptionPane.showMessageDialog(null, "Tables created successfully.");
            insertDefaultUsersIfNotExist(); // Insert default users if they don't already exist
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error creating tables: " + e.getMessage());
        }
    }

    // Insert default users if they do not exist
    public void insertDefaultUsersIfNotExist() {
        String checkUserSQL = "SELECT * FROM htran_users WHERE username = ?";

        // Default users to insert
        String[][] users = {
            {"admin", "admin123", "admin"},
            {"user1", "user123", "user"},
            {"user2", "user123", "user"},
            {"henry_tran", "pineapplesaremyfavorite", "admin"}
        };

        try {
            checkStmt = conn.prepareStatement(checkUserSQL);
            insertStmt = conn.prepareStatement("INSERT INTO htran_users (username, password, role) VALUES (?, ?, ?)");

            for (String[] user : users) {
                checkStmt.setString(1, user[0]);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    insertStmt.setString(1, user[0]);
                    insertStmt.setString(2, user[1]);
                    insertStmt.setString(3, user[2]);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error inserting default users: " + e.getMessage());
        }
    }

    // Insert a new ticket and return the generated ticket ID
    public int insertTicket(String user, String ticketDesc, String startDate) {
        String query = "INSERT INTO htran_tickets (user, ticket_desc, start_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user);
            stmt.setString(2, ticketDesc);
            stmt.setString(3, startDate);
            stmt.executeUpdate();

            // Retrieve the generated ticket ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);  // Return the generated ticket ID
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error inserting ticket: " + e.getMessage());
        }
        return -1;
    }

    // Update ticket description
    public void updateTicket(int tid, String newDesc) {
        String query = "UPDATE htran_tickets SET ticket_desc = ? WHERE tid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newDesc);
            stmt.setInt(2, tid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating ticket: " + e.getMessage());
        }
    }

    // Delete ticket by ticket ID
    public void deleteTicket(int tid) {
        String query = "DELETE FROM htran_tickets WHERE tid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, tid);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete ticket " + tid + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Ticket deleted successfully.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting ticket: " + e.getMessage());
        }
    }

    // View ticket by ticket ID
    public void viewTicket(int tid) {
        String query = "SELECT * FROM htran_tickets WHERE tid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, tid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String ticketInfo = "Ticket ID: " + rs.getInt("tid") + "\n" +
                                    "User: " + rs.getString("user") + "\n" +
                                    "Description: " + rs.getString("ticket_desc") + "\n" +
                                    "Start Date: " + rs.getString("start_date") + "\n" +
                                    "End Date: " + rs.getString("end_date") + "\n" +
                                    "Status: " + rs.getString("status");
                JOptionPane.showMessageDialog(null, ticketInfo);
            } else {
                JOptionPane.showMessageDialog(null, "Ticket not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error viewing ticket: " + e.getMessage());
        }
    }

    public void closeTicket(int tid, String endDate) {
        String query = "UPDATE htran_tickets SET status = 'closed', end_date = ? WHERE tid = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set the current date as end_date
            stmt.setString(1, endDate); // Current date
            // Set the ticket ID (tid)
            stmt.setInt(2, tid);



            // Execute the update
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Ticket closed successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No ticket found with the given ID.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing ticket: " + e.getMessage());
        }
    }
    // Method to get the role of a user based on their username
    public String getUserRole(String username) {
        String query = "SELECT role FROM htran_users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            // If a role is found for the user, return it
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching user role: " + e.getMessage());
        }
        return null;  // Return null if no role is found
    }

    public String[] getAllTickets(String username, String role) {
        ArrayList<String> ticketList = new ArrayList<>();
        String query;
        
        query = "SELECT tid, ticket_desc, user, status FROM htran_tickets ORDER BY tid";
        if ("null".equals(role)) {
            JOptionPane.showMessageDialog(null, "Invalid role: " + role);
            return new String[0];  // Return an empty array for invalid roles
        }
    
        try {
            // Prepare the statement with the correct query
            PreparedStatement stmt = conn.prepareStatement(query);
    
            // Execute the query
            ResultSet rs = stmt.executeQuery();
    
            // Process the results
            while (rs.next()) {
                String ticketDesc = rs.getString("ticket_desc");
                String status = rs.getString("status");
                String user = rs.getString("user");  // This should be correct in htran_tickets
                int tid = rs.getInt("tid");
    
                // Debugging: print to console to check what tickets are fetched
                System.out.println("Fetched Ticket - ID: " + tid + ", User: " + user + ", Status: " + status + ", Description: " + ticketDesc);
    
                ticketList.add("Ticket ID: " + tid + " | User: " + user + " | Status: " + status + " | Description: " + ticketDesc);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching tickets: " + e.getMessage());
        }
    
        // Return the ticket descriptions as a String array
        return ticketList.toArray(new String[0]);
    }
    
    
        // Show tickets based on user role
        public void showTickets(String username, String role) {
            String[] tickets = getAllTickets(username, role);
    
            // Debugging: print the array length to ensure tickets are being fetched
            System.out.println("Tickets fetched: " + tickets.length);
    
            // Display the tickets in a dialog
            if (tickets.length == 0) {
                JOptionPane.showMessageDialog(null, "No tickets found for this user.");
            } else {
                StringBuilder ticketsDisplay = new StringBuilder("Tickets:\n");
                for (String ticket : tickets) {
                    ticketsDisplay.append(ticket).append("\n");
                }
    
                JOptionPane.showMessageDialog(null, ticketsDisplay.toString());
            }
        }
}
    

