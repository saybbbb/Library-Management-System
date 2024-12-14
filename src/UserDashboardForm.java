import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.io.*;
import java.util.*;

public class UserDashboardForm {
    private JPanel mainPanel;
    private JTable bookTable;
    private JTextField searchField;
    private JButton borrowButton;
    private JButton viewHistoryButton;
    private JButton returnButton;
    private JButton logoutButton;
    private DefaultTableModel tableModel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private Map<String, List<String>> userBorrowedBooks = new HashMap<>();

    private Map<String, List<String>> loadUserBorrowedBooks(String currentUser) {
        Map<String, List<String>> borrowedBooks = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowed_books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!borrowedBooks.containsKey(parts[0])) {
                    borrowedBooks.put(parts[0], new ArrayList<>());
                }
                borrowedBooks.get(parts[0]).add(parts[1]);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error reading borrowed books: " + e.getMessage());
        }
        return borrowedBooks;
    }

    private void saveUserBorrowedBooks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("borrowed_books.txt"))) {
            for (Map.Entry<String, List<String>> entry : userBorrowedBooks.entrySet()) {
                String username = entry.getKey();
                for (String bookId : entry.getValue()) {
                    writer.write(username + "," + bookId);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error saving borrowed books: " + e.getMessage());
        }
    }

    public UserDashboardForm(String currentUser) {
        userBorrowedBooks = loadUserBorrowedBooks(currentUser);
        initializeTable();
        addSearchFunctionality();
        addButtonLogic(currentUser);
        userBorrowedBooks.putIfAbsent(currentUser, new ArrayList<>());
    }

    private void initializeTable() {
        String[] columnNames = {"Book ID", "Book Title", "Book Author", "Available"};
        Object[][] data = loadDataFromFile("books.txt");
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        bookTable.setModel(tableModel);
        bookTable.getTableHeader().setReorderingAllowed(false);
        bookTable.getTableHeader().setResizingAllowed(false);

        // Call refreshTable to load only available books
        refreshTable();
    }

    private void addSearchFunctionality() {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
        });
    }

    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (bookTable.getRowSorter() == null) {
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            bookTable.setRowSorter(sorter);
        }
        TableRowSorter<?> sorter = (TableRowSorter<?>) bookTable.getRowSorter();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1));
        }
    }

    private void addButtonLogic(String currentUser) {
        borrowButton.addActionListener(e -> {
            String bookId = JOptionPane.showInputDialog(mainPanel, "Enter Book ID to borrow:");
            if (bookId == null || bookId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Book ID cannot be empty.");
                return;
            }

            boolean found = false;
            for (int row = 0; row < bookTable.getRowCount(); row++) {
                if (bookTable.getValueAt(row, 0).toString().equals(bookId) &&
                        bookTable.getValueAt(row, 3).toString().equalsIgnoreCase("Yes")) {

                    String bookTitle = bookTable.getValueAt(row, 1).toString();
                    bookTable.setValueAt("No", row, 3); // Update availability to "No"

                    // Ensure the user's list exists and add the book
                    userBorrowedBooks.putIfAbsent(currentUser, new ArrayList<>());
                    userBorrowedBooks.get(currentUser).add(bookId);

                    // Persist borrowed books
                    saveUserBorrowedBooks();

                    saveHistory(currentUser, "Borrowed", bookId, bookTitle);
                    updateBooksFile();
                    JOptionPane.showMessageDialog(mainPanel, "You have successfully borrowed: " + bookTitle);
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(mainPanel, "Book ID not found or not available.");
            }

            refreshTable(); // Refresh table to show updates
        });

        returnButton.addActionListener(e -> {
            String bookId = JOptionPane.showInputDialog(mainPanel, "Enter Book ID to return:");
            if (bookId == null || bookId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Book ID cannot be empty.");
                return;
            }

            // Get the list of borrowed books for the current user
            List<String> borrowedBooks = userBorrowedBooks.getOrDefault(currentUser, new ArrayList<>());

            // Check if the user has borrowed the book
            if (!borrowedBooks.contains(bookId)) {
                JOptionPane.showMessageDialog(mainPanel, "You cannot return a book you did not borrow.");
                return;
            }

            boolean foundInFile = false;
            String bookTitle = null;

            try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"));
                 BufferedWriter writer = new BufferedWriter(new FileWriter("books_temp.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values[0].equals(bookId)) {
                        values[3] = "Yes"; // Mark as available
                        bookTitle = values[1];
                        foundInFile = true;
                    }
                    writer.write(String.join(",", values));
                    writer.newLine();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error updating book file: " + ex.getMessage());
                return;
            }

            if (!foundInFile) {
                JOptionPane.showMessageDialog(mainPanel, "Book ID not found in the file. Please check the data.");
                return;
            }

            // Replace old file with updated file
            File oldFile = new File("books.txt");
            File newFile = new File("books_temp.txt");
            if (oldFile.delete()) {
                newFile.renameTo(oldFile);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Error updating books.txt file.");
                return;
            }

            // Remove the book from the user's borrowed list
            borrowedBooks.remove(bookId);
            userBorrowedBooks.put(currentUser, borrowedBooks);

            // Persist the updated borrowed books
            saveUserBorrowedBooks();

            // Save return history
            saveHistory(currentUser, "Returned", bookId, bookTitle);

            JOptionPane.showMessageDialog(mainPanel, "You have successfully returned: " + bookTitle);

            // Refresh the table to reflect updated availability
            refreshTable();
        });

        viewHistoryButton.addActionListener(e -> {
            List<String[]> userHistory = getUserHistory(currentUser);
            if (userHistory.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "No history found for you.");
            } else {
                showUserHistoryDialog(userHistory);
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

    private void showUserHistoryDialog(List<String[]> userHistory) {
        // Create column names for the history table
        String[] columnNames = {"Action", "Book ID", "Book Title", "Date and Time"};

        // Convert history to 2D array for table model
        Object[][] historyData = new Object[userHistory.size()][4];
        for (int i = 0; i < userHistory.size(); i++) {
            String[] historyEntry = userHistory.get(i);
            historyData[i] = new Object[]{
                    historyEntry[1],  // Action
                    historyEntry[2],  // Book ID
                    historyEntry[3],  // Book Title
                    historyEntry[4]   // Timestamp
            };
        }

        DefaultTableModel historyTableModel = new DefaultTableModel(historyData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        JTable historyTable = new JTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));

        JOptionPane.showMessageDialog(
                mainPanel,
                scrollPane,
                "Your Borrowing History",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void refreshTable() {
        Object[][] allData = loadDataFromFile("books.txt");

        // Filter rows where "Available" column equals "Yes"
        List<Object[]> availableBooks = new ArrayList<>();
        for (Object[] row : allData) {
            if (row[3].toString().equalsIgnoreCase("Yes")) {
                availableBooks.add(row);
            }
        }

        // Update table with filtered data
        tableModel.setDataVector(
                availableBooks.toArray(new Object[0][]),
                new String[]{"Book ID", "Book Title", "Book Author", "Available"}
        );
    }

    private void updateBooksFile() {
        try {
            // Load all books from the file
            List<String[]> allBooks = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    allBooks.add(values);
                }
            }

            // Update the availability of books based on the JTable
            for (int row = 0; row < bookTable.getRowCount(); row++) {
                String bookId = bookTable.getValueAt(row, 0).toString();
                String newAvailability = bookTable.getValueAt(row, 3).toString();

                // Find the corresponding book in the loaded list and update its availability
                for (String[] book : allBooks) {
                    if (book[0].equals(bookId)) {
                        book[3] = newAvailability;
                        break;
                    }
                }
            }

            // Write the updated list of books back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.txt"))) {
                for (String[] book : allBooks) {
                    writer.write(String.join(",", book));
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error updating books file: " + e.getMessage());
        }
    }

    private void saveHistory(String username, String action, String bookId, String bookTitle) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("history.txt", true))) {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            writer.write(username + "," + action + "," + bookId + "," + bookTitle + "," + timestamp);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error saving history: " + e.getMessage());
        }
    }

    private List<String[]> getUserHistory(String username) {
        List<String[]> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("history.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    history.add(parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error reading history: " + e.getMessage());
        }
        return history;
    }

    private Object[][] loadDataFromFile(String filename) {
        List<Object[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values); // Add each book record as a row
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, "Error reading " + filename + ": " + e.getMessage());
        }
        return data.toArray(new Object[0][]);
    }
}
