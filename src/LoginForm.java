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
    private JLabel messageLabel;

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

                if (validateCredentials(username, password)) {
                    JOptionPane.showMessageDialog(mainPanel, "Login Successful!");
                    messageLabel.setText("");
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            }
        });

        // Action for Create Account Button
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Registration Form
                JFrame registrationFrame = new JFrame("Registration Form");
                RegistrationForm registrationForm = new RegistrationForm();
                registrationFrame.setContentPane(new RegistrationForm().getMainPanel());
                registrationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                registrationFrame.pack();
                registrationFrame.setLocationRelativeTo(null);
                registrationFrame.setVisible(true);

                // Close current Login Form
                ((JFrame) SwingUtilities.getWindowAncestor(mainPanel)).dispose();
            }
        });
    }

    /**
     * Validates the credentials against the data in accounts.txt
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return true if credentials are valid, false otherwise.
     */

    private boolean validateCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Split line into username and password
                if (parts.length == 2) {
                    String fileUsername = parts[0].trim();
                    String filePassword = parts[1].trim();

                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        return true; // Match found
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error reading accounts file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; // No match found
    }
}
