import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final JButton loginButton;
    private JLabel statusLabel;

    // Declare DB objects at the class level
    private Connection conn = null;
    private PreparedStatement stmt = null;

    public Login() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        statusLabel = new JLabel("");

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(statusLabel);

        // Add login button action listener
        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                // Proceed to the next screen (ticket window)
                new Tickets(username);  // Pass username to Tickets to track user role
                setVisible(false);  // Close login window
            } else {
                statusLabel.setText("Invalid credentials, please try again.");
            }
        });

        // Establish DB connection before showing UI
        conn = DBConnect.connect();  // Assuming DBConnect.connect() is a method that returns a connection

        add(panel);
        setVisible(true);
    }

    // Authenticate the user with the database
    private boolean authenticate(String username, String password) {
        String query = "SELECT * FROM htran_users WHERE username = ? AND password = ?";
        try {
            // Initialize PreparedStatement outside the try block
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Successful authentication
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error authenticating user: " + e.getMessage());
        } finally {
            // Close the PreparedStatement and ResultSet after use
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error closing resources: " + e.getMessage());
            }
        }
        return false;  // Return false if no user was found
    }
}
