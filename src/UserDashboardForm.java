import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDashboardForm {
    // Swing components defined by the designer
    private JPanel mainPanel;
    private JTextField textField1;
    private JButton button1;
    private JTable bookTable;
    private JTextField searchField;
    private DefaultTableModel tableModel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    // Constructor
    public UserDashboardForm() {
        // Initialize the components created by Swing UI Designer
        initializeTable(); // Call to initialize the JTable with data
        addTableClickListener();
        addSearchFunctionality();

        // You can also add other logic for the button (button1) here if needed
    }

    // Method to initialize the JTable with column names and data
    private void initializeTable() {
        // Define the column names for the JTable
        String[] columnNames = {"Book ID", "Title", "Author", "Availability"};

        // Example data (replace with dynamic data as needed)
        Object[][] data = loadDataFromFile("books.txt");

        // Create a DefaultTableModel with the data and column names
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        // Set the model to the bookTable (JTable defined in the designer)
        bookTable.setModel(tableModel);

        // Disable column reordering
        bookTable.getTableHeader().setReorderingAllowed(false);
    }

    private void addTableClickListener() {
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Get the selected row index
                int selectedRow = bookTable.getSelectedRow();

                // Retrieve the Title and Author from the selected row
                if (selectedRow != -1) { // Ensure a valid row is selected
                    String title = bookTable.getValueAt(selectedRow, 1).toString(); // Title is in column 1
                    String author = bookTable.getValueAt(selectedRow, 2).toString(); // Author is in column 2

                    // Set the text field with the selected row's Title and Author
                    textField1.setText(title + " by " + author);
                }
            }
        });
    }

    private Object[][] loadDataFromFile(String fileName) {
        List<Object[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line by commas and add to the rows list
                String[] values = line.split(",");
                rows.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions properly in production
        }
        // Convert the list to a 2D array
//        Object[][] data = new Object[rows.size()][4];
//        for (int i = 0; i < rows.size(); i++) {
//            data[i] = rows.get(i);
//        }
//
//        return data;
        return rows.toArray(new Object[0][]);
    }

    private void addSearchFunctionality() {
        // Attach a DocumentListener to the searchField
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
        String searchText = searchField.getText().trim(); // Get search text

        // Ensure row sorter is created only once
        if (bookTable.getRowSorter() == null) {
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            bookTable.setRowSorter(sorter);
        }

        TableRowSorter<?> sorter = (TableRowSorter<?>) bookTable.getRowSorter();

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null); // Show all rows if search field is empty
        } else {
            try {
                // Filter rows containing the search text (case-insensitive)
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1));
            } catch (Exception e) {
                sorter.setRowFilter(null); // Reset filter on error
                e.printStackTrace();
            }
        }
    }
}
