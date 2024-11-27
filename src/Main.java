import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Ensure the accounts.txt file exists
        ensureAccountsFileExists();

        // Create an instance of LoginForm
        LoginForm loginForm = new LoginForm();

        // Create a JFrame to hold the LoginForm
        JFrame loginFrame = new JFrame("Login Form");
        loginFrame.setContentPane(loginForm.getMainPanel()); // Set the content pane to the main panel from LoginForm
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the app when window is closed
        loginFrame.pack();  // Resize the frame to fit the components
        loginFrame.setVisible(true); // Show the frame
    }

    /**
     * Ensures that the accounts.txt file exists.
     * If the file doesn't exist, it will create it.
     */
    private static void ensureAccountsFileExists() {
        File file = new File("accounts.txt");
        if (!file.exists()) {
            try {
                // Create a new file if it doesn't exist
                boolean created = file.createNewFile();
                if (created) {
                    System.out.println("accounts.txt file created.");
                } else {
                    System.out.println("Error creating accounts.txt file.");
                }
            } catch (IOException e) {
                System.out.println("Error checking or creating accounts.txt: " + e.getMessage());
            }
        } else {
            System.out.println("accounts.txt file already exists.");
        }
    }
}
