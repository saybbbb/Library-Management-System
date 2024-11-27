import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RegistrationForm {
    public JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton signInButton;
    private JLabel messageLabel;

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
                    messageLabel.setText("Fields cannot be empty.");
                    return;
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("accounts.txt", true))) {
                    writer.write(username + "," + password);
                    writer.newLine();
                    JOptionPane.showMessageDialog(mainPanel, "Registration Successful!");
                    usernameField.setText("");
                    passwordField.setText("");
                    messageLabel.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    messageLabel.setText("Error saving account.");
                }
            }
        });

        // Action for Back to Login Button
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Login Form
                JFrame loginFrame = new JFrame("Login Form");
                LoginForm loginForm = new LoginForm();
                loginFrame.setContentPane(new LoginForm().getMainPanel());
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginFrame.pack();
                loginFrame.setLocationRelativeTo(null);
                loginFrame.setVisible(true);

                // Close current Registration Form
                ((JFrame) SwingUtilities.getWindowAncestor(mainPanel)).dispose();
            }
        });
    }
}
