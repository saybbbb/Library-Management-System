import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardForm {
    private JPanel mainPanel;
    private JTable bookTable;
    private JButton addBookButton;
    private JButton editBookButton;
    private JButton deleteBookButton;
    private JButton viewHistoryButton;
    private JButton logoutButton;
    private DefaultTableModel tableModel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public AdminDashboardForm() {
        initializeTable();
        addButtonLogic();
    }

    private void initializeTable() {
        String[] columnNames = {"Book ID", "Book Title", "Book Author", "Available"};
        Object[][] data = loadDataFromFile("books.txt"); // Load data from file
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        bookTable.setModel(tableModel);
        bookTable.getTableHeader().setReorderingAllowed(false);
        bookTable.getTableHeader().setResizingAllowed(false);
    }

    private void addButtonLogic() {
        addBookButton.addActionListener(e -> {
            String bookId;
            while (true) {
                bookId = JOptionPane.showInputDialog(mainPanel, "Enter Book ID:");

                if (bookId == null) {return;}
                if (bookId.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Book ID cannot be empty.");
                    continue;
                }

                // Check for duplicate Book ID
                boolean isDuplicate = false;
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    if (tableModel.getValueAt(row, 0).equals(bookId)) {
                        JOptionPane.showMessageDialog(mainPanel, "Book ID already exists. Please enter a different ID.");
                        isDuplicate = true;
                        break;
                    }
                }

                if (isDuplicate) {continue;}

                break;
            }

            String bookTitle;
            while (true) {
                bookTitle = JOptionPane.showInputDialog(mainPanel, "Enter Book Title:");

                if (bookTitle == null) {return;}
                if (bookTitle.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Book Title cannot be empty.");
                    continue;
                }

                break;
            }

            String bookAuthor;
            while (true) {
                bookAuthor = JOptionPane.showInputDialog(mainPanel, "Enter Book Author:");

                if (bookAuthor == null) {return;}
                if (bookAuthor.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Book Author cannot be empty.");
                    continue;
                }

                break;
            }

            tableModel.addRow(new Object[]{bookId, bookTitle, bookAuthor, "Yes"});
            saveTableToFile();
            JOptionPane.showMessageDialog(mainPanel, "Book added successfully.");
        });

        editBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainPanel, "Please select a book to edit.");
                return;
            }

            String bookId = (String) tableModel.getValueAt(selectedRow, 0);
            String currentTitle = (String) tableModel.getValueAt(selectedRow, 1);
            String currentAuthor = (String) tableModel.getValueAt(selectedRow, 2);

            String newTitle;
            while (true) {
                newTitle = JOptionPane.showInputDialog(mainPanel, "Enter new title:", currentTitle);

                if (newTitle == null) {return;}
                if (newTitle.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Book Title cannot be empty.");
                    continue;
                }

                break;
            }

            String newAuthor;
            while (true) {
                newAuthor = JOptionPane.showInputDialog(mainPanel, "Enter new author:", currentAuthor);

                if (newAuthor == null) {return;}
                if (newAuthor.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Book Author cannot be empty.");
                    continue;
                }

                break;
            }

            // Check if anything actually changed
            if (newTitle.trim().equals(currentTitle) && newAuthor.trim().equals(currentAuthor)) {
                JOptionPane.showMessageDialog(mainPanel, "No changes made.");
                return;
            }

            tableModel.setValueAt(newTitle.trim(), selectedRow, 1);
            tableModel.setValueAt(newAuthor.trim(), selectedRow, 2);
            saveTableToFile();
            JOptionPane.showMessageDialog(mainPanel, "Book updated successfully.");
        });

        deleteBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainPanel, "Please select a book to delete.");
                return;
            }

            String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
            String availability = (String) tableModel.getValueAt(selectedRow, 3);

            // Check if book is currently available
            if (!"Yes".equals(availability)) {
                JOptionPane.showMessageDialog(mainPanel, "Cannot delete a book that is not available.");
                return;
            }

            // Confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(
                    mainPanel,
                    "Are you sure you want to delete the book: " + bookTitle + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                saveTableToFile();
                JOptionPane.showMessageDialog(mainPanel, "Book deleted successfully.");
            }
        });

        viewHistoryButton.addActionListener(e -> {
            List<String[]> userHistory = getAllUsersHistory();
            if (userHistory.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "No history found.");
            } else {
                showAllHistoryDialog(userHistory);
            }
        });

        logoutButton.addActionListener(e -> {
            JFrame loginFrame = new JFrame("Login Form");
            LoginForm loginForm = new LoginForm();
            loginFrame.setContentPane(loginForm.getMainPanel()); // Set the content pane to the main panel from LoginForm
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the app when window is closed
            loginFrame.pack(); // Resize the frame to fit the components
            loginFrame.setLocationRelativeTo(null); // Center the JFrame on the screen
            loginFrame.setVisible(true); // Show the frame

            SwingUtilities.getWindowAncestor(mainPanel).dispose();
        });
    }

    private List<String[]> getAllUsersHistory() {
        List<String[]> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("history.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                history.add(parts);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error reading history: " + e.getMessage());
        }
        return history;
    }

    private void showAllHistoryDialog(List<String[]> userHistory) {
        // Create column names for the history table
        String[] columnNames = {"User", "Action", "Book ID", "Book Title", "Date and Time"};

        // Convert history to 2D array for table model
        Object[][] historyData = new Object[userHistory.size()][5];
        for (int i = 0; i < userHistory.size(); i++) {
            String[] historyEntry = userHistory.get(i);
            historyData[i] = new Object[]{
                    historyEntry[0],  // User
                    historyEntry[1],  // Action
                    historyEntry[2],  // Book ID
                    historyEntry[3]   // Book Title
            };
        }

        DefaultTableModel historyTableModel = new DefaultTableModel(historyData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        // Create the JTable with the history data
        JTable historyTable = new JTable(historyTableModel);

        // Add the table to a scroll pane to handle overflow
        JScrollPane scrollPane = new JScrollPane(historyTable);

        // Set a preferred size for the dialog
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 300));

        // Show the history in a dialog with the table
        JOptionPane.showMessageDialog(
                mainPanel,
                scrollPane,
                "Borrowing History",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void saveTableToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.txt"))) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String bookId = tableModel.getValueAt(row, 0).toString();
                String bookTitle = tableModel.getValueAt(row, 1).toString();
                String bookAuthor = tableModel.getValueAt(row, 2).toString();
                String availability = tableModel.getValueAt(row, 3).toString();
                writer.write(bookId + "," + bookTitle + "," + bookAuthor + "," + availability);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error saving books: " + e.getMessage());
        }
    }

    private Object[][] loadDataFromFile(String filename) {
        List<Object[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error reading " + filename + ": " + e.getMessage());
        }
        return data.toArray(new Object[0][]);
    }
}


