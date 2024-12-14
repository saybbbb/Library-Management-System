import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Ensure the accounts.txt file exists
        ensureFileExists("accounts.txt");
        ensureFileExists("books.txt");
        ensureFileExists("history.txt");

        // Create an instance of LoginForm
        LoginForm loginForm = new LoginForm();

        // Create a JFrame to hold the LoginForm
        JFrame loginFrame = new JFrame("Login Form");
        loginFrame.setContentPane(loginForm.getMainPanel()); // Set the content pane to the main panel from LoginForm
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the app when window is closed
        loginFrame.pack(); // Resize the frame to fit the components
        loginFrame.setLocationRelativeTo(null); // Center the JFrame on the screen
        loginFrame.setVisible(true); // Show the frame
    }

    private static void ensureFileExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                // Create a new file if it doesn't exist
                boolean created = file.createNewFile();
                if (created) {
                    System.out.println(fileName + " file created.");
                } else {
                    System.out.println("Error creating " + fileName + " file.");
                }
            } catch (IOException e) {
                System.out.println("Error checking or creating " + fileName + ": " + e.getMessage());
            }
        } else {
            System.out.println(fileName + " file already exists.");
        }
    }
}

// TODO: Fix return book
// TODO: Refresh table column size