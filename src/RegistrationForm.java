import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class RegistrationForm {
    public JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton backButton;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public RegistrationForm() {
        // Action for Register Button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Fields cannot be empty.");
                    return;
                }

                // Check if username is already registered
                if (isUsernameAlreadyRegistered(username)) {
                    JOptionPane.showMessageDialog(mainPanel, "Username is already registered. Please choose another one.");
                    return;
                }

                // Add the account with "user" role
                boolean success = addAccount(username, password, "user");
                if (success) {
                    JOptionPane.showMessageDialog(mainPanel, "Account created successfully!");

                    // Clear the input fields
                    usernameField.setText("");
                    passwordField.setText("");

                    // Navigate to LoginForm
                    JFrame loginFrame = new JFrame("Login Form");
                    loginFrame.setContentPane(new LoginForm().getMainPanel());
                    loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    loginFrame.pack();
                    loginFrame.setLocationRelativeTo(null);
                    loginFrame.setVisible(true);

                    // Close current Registration Form
                    SwingUtilities.getWindowAncestor(mainPanel).dispose();
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Error creating account. Please try again.");
                }
            }
        });

        // Action for Back to Login Button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Login Form
                JFrame loginFrame = new JFrame("Login Form");
                loginFrame.setContentPane(new LoginForm().getMainPanel());
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginFrame.pack();
                loginFrame.setLocationRelativeTo(null);
                loginFrame.setVisible(true);

                // Close current Registration Form
                SwingUtilities.getWindowAncestor(mainPanel).dispose();
            }
        });
    }

    private boolean addAccount(String username, String password, String role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("accounts.txt", true))) {
            writer.write(username + "," + password + "," + role);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to accounts.txt: " + e.getMessage());
            return false;
        }
    }

    private boolean isUsernameAlreadyRegistered(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].trim().equalsIgnoreCase(username)) {
                    return true; // Username found
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from accounts.txt: " + e.getMessage());
        }
        return false; // Username not found
    }
}
