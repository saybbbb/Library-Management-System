import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    public LoginGUI() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validation logic
                if (username.equals("admin") && password.equals("password123")) {
                    JOptionPane.showMessageDialog(mainPanel, "Login Successful!");
                    messageLabel.setText("");
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Library Management System");
        frame.setContentPane(new LoginGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
