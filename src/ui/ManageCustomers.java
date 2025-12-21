package ui;

import dao.CustomerDAO;
import models.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ManageCustomers extends JPanel {
    JTextField txtCusName = new JTextField(20);
    JTextField txtPhone = new JTextField(20);
    JTextField txtAddress = new JTextField(20);
    JTable table = new JTable();

    public ManageCustomers() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);

        // ================= TOP FORM PANEL =================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== Title =====
        JLabel title = new JLabel("Manage Customer");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;

        // ===== Customer Name =====
        addLabel(formPanel, gbc, "Customer name");
        addField(formPanel, gbc, txtCusName);

        // ===== Phone Number =====
        gbc.gridy++;
        addLabel(formPanel, gbc, "Phone Number");
        addField(formPanel, gbc, txtPhone);

        // ===== Address =====
        gbc.gridy++;
        addLabel(formPanel, gbc, "Address");
        addField(formPanel, gbc, txtAddress);

        // ================= BUTTON PANEL =================
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Add Customer", new Color(13, 110, 253));
        JButton btnEdit = createButton("Edit", new Color(255, 193, 7));
        JButton btnSearch = createButton("Search", new Color(25, 135, 84));
        JButton btnShowAll = createButton("Show All", new Color(129, 154, 214));
        JButton btnDelete = createButton("Delete", new Color(220, 53, 69));
        JButton btnClear = createButton("Clear", new Color(108, 117, 125));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnShowAll);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        formPanel.add(buttonPanel, gbc);

        btnAdd.addActionListener(e -> addCustomer());
        btnEdit.addActionListener(e -> {
            if (btnEdit.getText().equals("Edit")) {
                btnEdit.setText("Save Edit");
                loadData();
            } else if (btnEdit.getText().equals("Save Edit")) {
                btnEdit.setText("Edit");
                try {
                    updateData();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        btnSearch.addActionListener(e -> {
            try {
                searchCustomer();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnShowAll.addActionListener(e -> {
            loadCustomer();
        });
        btnDelete.addActionListener(e -> {
            try {
                deleteCustomer();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnClear.addActionListener(e -> {
            clearTF();
        });

        add(formPanel, BorderLayout.NORTH);

        // ================= TABLE PANEL =================
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel tableLabel = new JLabel("Customer List");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tablePanel.add(tableLabel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Phone Number", "Address"}, 0
        );
        table.setModel(model);
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
        loadCustomer();
    }

    // ================= HELPER METHODS =================
    private void addLabel(JPanel panel, GridBagConstraints gbc, String text) {
        gbc.gridx = 0;
        JLabel label = new JLabel(text + " : ");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, gbc);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, JComponent field) {
        gbc.gridx = 1;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(250, 32));
        panel.add(field, gbc);
        gbc.weightx = 0;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return btn;
    }

    private void loadCustomer() {
        try {
            CustomerDAO dao = new CustomerDAO();
            List<Customer> list = dao.getAllCustomer();
            DefaultTableModel dtm = new DefaultTableModel(
                    new String[]{"ID", "Customer Name", "Phone", "Address"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            for (Customer c : list) {
                dtm.addRow(new Object[]{
                        c.getCustomerId(), c.getCustomerName(), c.getCustomerPhone(), c.getCustomerAddress()
                });
            }

            table.setModel(dtm);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCustomer() {
        try {
            String cusName = txtCusName.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();

            if (cusName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all the fields",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CustomerDAO dao = new CustomerDAO();
            Customer cus = new Customer();

            // ❌ BUG WAS HERE: Not setting customer data!
            cus.setCustomerName(cusName);
            cus.setCustomerPhone(phone);
            cus.setCustomerAddress(address);

            dao.insertCustomer(cus);

            loadCustomer();
            clearTF();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadData() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        txtCusName.setText(table.getValueAt(row, 1).toString());
        txtPhone.setText(table.getValueAt(row, 2).toString());
        txtAddress.setText(table.getValueAt(row, 3).toString());
    }

    public void updateData() throws SQLException {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cusName = txtCusName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (cusName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        try {
            CustomerDAO dao = new CustomerDAO();
            Customer cus = new Customer(id, cusName, phone, address);

            dao.updateCustomer(cus);
            JOptionPane.showMessageDialog(this, "Customer Updated Successfully",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadCustomer();
            clearTF();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteCustomer() throws SQLException {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        try {
            if (confirm == JOptionPane.YES_OPTION) {
                CustomerDAO dao = new CustomerDAO();
                Customer cus = dao.getCustomer(id);

                if (cus != null && cus.getCustomerId() > 0) {
                    dao.deleteCustomer(cus);
                    JOptionPane.showMessageDialog(this, "Deleted Successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomer();
                    clearTF();
                } else {
                    JOptionPane.showMessageDialog(this, "Customer not found",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Delete Failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchCustomer() throws SQLException {
        String searchStr = JOptionPane.showInputDialog(this,
                "Enter search term: ",
                "Search",
                JOptionPane.QUESTION_MESSAGE);

        if (searchStr == null) {
            return; // User cancelled
        }

        searchStr = searchStr.trim();

        if (searchStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter search term",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CustomerDAO dao = new CustomerDAO();
            List<Customer> customers = dao.searchCustomers(searchStr);

            DefaultTableModel dtm = new DefaultTableModel(
                    new String[]{"ID", "Customer Name", "Phone", "Address"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Customer c : customers) {
                dtm.addRow(new Object[]{
                        c.getCustomerId(),
                        c.getCustomerName(),
                        c.getCustomerPhone(),
                        c.getCustomerAddress()
                });
            }

            table.setModel(dtm);

            if (dtm.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No records found",
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearTF() {
        txtCusName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        table.clearSelection();
    }
}