import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginForm {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;


    public JPanel getMainPanel() {
        return mainPanel;
    }

    public LoginForm() {
        // Action for Login Button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                String role = validateCredentials(username, password);
                if (role != null) {
                    if (role.equals("admin")) {
                        JOptionPane.showMessageDialog(mainPanel, "Welcome");
                        // TODO: Open Admin Dashboard
                    } else if (role.equals("user")) {
                        JOptionPane.showMessageDialog(mainPanel, "Welcome " + username + "!");
                        // Open User Dashboard
                        // Replace with the logged-in user's name
                        UserDashboardForm userDashboard = new UserDashboardForm(username);
                        JFrame userDashboardFrame = new JFrame("User Dashboard");
                        userDashboardFrame.setContentPane(userDashboard.getMainPanel()); // Use the main panel of UserDashboardForm
                        userDashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        userDashboardFrame.pack();
                        userDashboardFrame.setLocationRelativeTo(null); // Center the frame
                        userDashboardFrame.setVisible(true);

                        // Hide or dispose of the LoginForm
                        SwingUtilities.getWindowAncestor(mainPanel).dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid username or password.");
                }
            }
        });

        // Action for Create Account Button
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Registration Form
                JFrame registrationFrame = new JFrame("Registration Form");
                registrationFrame.setContentPane(new RegistrationForm().getMainPanel());
                registrationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                registrationFrame.pack();
                registrationFrame.setLocationRelativeTo(null);
                registrationFrame.setVisible(true);

                // Close current Login Form
                SwingUtilities.getWindowAncestor(mainPanel).dispose();
            }
        });
    }

    private String validateCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Split line into username and password
                if (parts.length == 3) {
                    String fileUsername = parts[0].trim();
                    String filePassword = parts[1].trim();
                    String storedRole = parts[2].trim();

                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        return storedRole; // Return the role (admin/user)
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error reading accounts file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null; // Return null if credentials are invalid
    }
}
