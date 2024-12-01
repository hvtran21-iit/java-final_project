import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Tickets extends JFrame {
    private String currentUser;
    private JTextField ticketDescField;
    private JButton submitButton;
    private JButton backButton;
    private JLabel statusLabel;

    // Constructor accepts the current user (admin or regular user)
    public Tickets(String username) {
        this.currentUser = username;
        Dao dao = new Dao();
        String role = dao.getUserRole(currentUser);
        if (role.equals("admin")) {
            setTitle("Tickets - Admin");
        } else {
            setTitle("Tickets - User");
        }
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a panel to hold the buttons (menu options)
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));  // FlowLayout arranges components in a row
        JButton createTicketButton = new JButton("Create Ticket");
        JButton viewTicketButton = new JButton("View Ticket");
        JButton updateTicketButton = new JButton("Update Ticket");
        JButton deleteTicketButton = new JButton("Delete Ticket");
        JButton closeTicketButton = new JButton("Close Ticket");
        JButton showTicketsButton = new JButton("Show Tickets");

        // Add action listeners to buttons
        createTicketButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setTitle("Create a Ticket");
                setSize(400, 300);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);

                // Initialize the ticket creation form
                ticketDescField = new JTextField(20);
                submitButton = new JButton("Submit");
                backButton = new JButton("Back");
                statusLabel = new JLabel("");

                // Set up panel and layout for ticket creation form
                JPanel panel = new JPanel(new GridLayout(3, 2));
                panel.add(new JLabel("Ticket Description:"));
                panel.add(ticketDescField);
                panel.add(submitButton);
                panel.add(backButton);
                panel.add(statusLabel);

                // Center the buttons horizontally
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.add(submitButton);
                buttonPanel.add(backButton);

                // Add components to the frame
                setContentPane(panel);
                panel.add(buttonPanel);  // Adding the button panel to the main panel

                // Action for submitting the ticket
                submitButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String ticketDesc = ticketDescField.getText();

                        // Validate inputs
                        if (ticketDesc == null || ticketDesc.trim().isEmpty()) {
                            statusLabel.setText("Please fill in all fields.");
                            JOptionPane.showMessageDialog(null, "Ticket Description cannot be empty!");
                            return;
                        }

                        // Get current date/time as the start date
                        String startDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

                        // Call Dao to insert the ticket
                        Dao dao = new Dao();
                        dao.insertTicket(currentUser, ticketDesc, startDate);

                        // Show success message
                        JOptionPane.showMessageDialog(null, "Ticket created successfully!");
                        new Tickets(username);  // Reopen Tickets window
                        setVisible(false);  // Hide current window
                    }
                });

                // Action for going back to the previous screen
                backButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new Tickets(username);
                        setVisible(false);
                    }
                });

                setVisible(true);
            }
        });

        viewTicketButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tidStr = JOptionPane.showInputDialog("Enter Ticket ID to View:");
                
                if (tidStr == null || tidStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ticket ID cannot be empty.");
                    return;
                }

                try {
                    int tid = Integer.parseInt(tidStr);
                    Dao dao = new Dao();
                    dao.viewTicket(tid);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Ticket ID. Please enter a valid number.");
                }
            }
        });

        updateTicketButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tidStr = JOptionPane.showInputDialog("Enter Ticket ID to Update:");
                
                if (tidStr == null || tidStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ticket ID cannot be empty.");
                    return;
                }

                try {
                    int tid = Integer.parseInt(tidStr);
                    String newDesc = JOptionPane.showInputDialog("Enter New Description:");
                    
                    if (newDesc == null || newDesc.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "New description cannot be empty.");
                        return;
                    }

                    Dao dao = new Dao();
                    dao.updateTicket(tid, newDesc);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Ticket ID. Please enter a valid number.");
                }
            }
        });

        deleteTicketButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tidStr = JOptionPane.showInputDialog("Enter Ticket ID to Delete:");
                
                if (tidStr == null || tidStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ticket ID cannot be empty.");
                    return;
                }

                try {
                    int tid = Integer.parseInt(tidStr);
                    Dao dao = new Dao();
                    dao.deleteTicket(tid);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Ticket ID. Please enter a valid number.");
                }
            }
        });

        closeTicketButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tidStr = JOptionPane.showInputDialog("Enter Ticket ID to Close:");
                
                if (tidStr == null || tidStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ticket ID cannot be empty.");
                    return;
                }

                try {
                    int tid = Integer.parseInt(tidStr);
                    String endDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
                    Dao dao = new Dao();
                    dao.closeTicket(tid, endDate);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Ticket ID. Please enter a valid number.");
                }
            }
        });

        showTicketsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dao dao = new Dao();
                
                // Fetch and show the role of the currentUser from the database
                String role = dao.getUserRole(currentUser);
                
                // Check if the currentUser's role is admin or user
                if (role == null) {
                    // Handle the case if role is not found (e.g., user doesn't exist in the database)
                    JOptionPane.showMessageDialog(null, "User role not found.");
                    return;
                }
                
                // Fetch and show tickets for the current user based on their role
                dao.showTickets(currentUser, role);
            }
        });

        // Add buttons to the menu panel
        menuPanel.add(createTicketButton);
        menuPanel.add(viewTicketButton);
        menuPanel.add(updateTicketButton);
        menuPanel.add(deleteTicketButton);
        menuPanel.add(closeTicketButton);
        menuPanel.add(showTicketsButton);

        // Add the menu panel to the top of the frame
        add(menuPanel, "North");

        // Add a label with the text "For all your ticketing needs" in the body area
        JLabel bodyLabel = new JLabel("For all your ticketing needs", JLabel.CENTER);
        bodyLabel.setFont(bodyLabel.getFont().deriveFont(16f));  // Set font for the label
        add(bodyLabel, "Center");

        // Customize options for admin vs user
        if (role.equals("admin")) {
            // Admin can access all ticket operations
            deleteTicketButton.setEnabled(true);
            closeTicketButton.setEnabled(true);
        } else {
            // Regular users can only view and update their own tickets
            deleteTicketButton.setEnabled(false);
            closeTicketButton.setEnabled(false);
            updateTicketButton.setEnabled(false);
            showTicketsButton.setEnabled(false);
        }

        setVisible(true);
    }
}
