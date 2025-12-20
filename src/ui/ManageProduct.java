package ui;

import dao.DBConn;
import dao.ProductDAO;
import models.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ManageProduct extends JPanel {
    public JTextField txtCode = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JComboBox<String> cmbCategory = new JComboBox<>();
    public JComboBox<String> cmbSupplier = new JComboBox<>();
    public JTextField txtUnitPrice = new JTextField(20);
    public JTextField txtCostPrice = new JTextField(20);
    public JTextField txtZone = new JTextField(20);
    public JTextArea txtDescription = new JTextArea(3, 20);
    public JTable table = new JTable();

    // Store selected product ID for edit/delete operations
    private int selectedProductId = -1;

    public ManageProduct() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);

        // ================= FORM PANEL =================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== Title =====
        JLabel title = new JLabel("Manage Product Stock");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;

        // ===== ROW 1: Product Code & Product Name =====
        addLabel(formPanel, gbc, 0, "Product Code");
        addField(formPanel, gbc, 1, txtCode);

        addLabel(formPanel, gbc, 2, "Product Name");
        addField(formPanel, gbc, 3, txtName);

        // ===== ROW 2: Category & Supplier =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Category");
        addField(formPanel, gbc, 1, cmbCategory);

        addLabel(formPanel, gbc, 2, "Supplier");
        addField(formPanel, gbc, 3, cmbSupplier);

        // ===== ROW 3: Unit Price & Cost Price =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Unit Price");
        addField(formPanel, gbc, 1, txtUnitPrice);

        addLabel(formPanel, gbc, 2, "Cost Price");
        addField(formPanel, gbc, 3, txtCostPrice);

        // ===== ROW 4: Warehouse Zone =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Warehouse Zone");
        addField(formPanel, gbc, 1, txtZone);

        // ===== ROW 5: Description =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Description");
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        formPanel.add(descScroll, gbc);

        gbc.gridwidth = 1;

        // ================= BUTTONS =================
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Add Product", new Color(13, 110, 253));
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

        // ===== BUTTON ACTIONS =====
        btnAdd.addActionListener(e -> addProduct());
        btnEdit.addActionListener(e -> editProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnSearch.addActionListener(e -> searchProduct());
        btnShowAll.addActionListener(e -> loadProduct());
        btnClear.addActionListener(e -> clearFields());

        add(formPanel, BorderLayout.NORTH);

        // ================= TABLE =================
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel tableLabel = new JLabel("Product List");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tablePanel.add(tableLabel, BorderLayout.NORTH);

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
        table.setRowHeight(28);

        // ===== TABLE SELECTION LISTENER =====
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromTable();
            }
        });

        // ===== LOAD DATA =====
        loadSupplierName();
        loadCategoryName();
        loadProduct();
    }

    // ================= HELPERS =================
    private void addLabel(JPanel panel, GridBagConstraints gbc, int x, String text) {
        gbc.gridx = x;
        JLabel label = new JLabel(text + " : ");
        label.setFont(new Font("Arial", Font.PLAIN, 15));
        panel.add(label, gbc);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int x, JComponent field) {
        gbc.gridx = x;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(220, 32));
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

    private void loadProduct() {
        try {
            ProductDAO dao = new ProductDAO();
            List<Product> products = dao.getAllProducts();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{
                            "ID", "Code", "Name", "Quantity", "Unit Price", "Cost Price",
                            "Category", "Supplier", "Zone", "Description"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            for (Product p : products) {
                model.addRow(new Object[]{
                        p.getProductId(),
                        p.getProductCode(),
                        p.getProductName(),
                        p.getQuantityOnHand(),
                        p.getUnitPrice(),
                        p.getCostPrice(),
                        p.getCategoryName(),
                        p.getSupplierName(),
                        p.getWarehouseZone(),
                        p.getDescription()
                });
            }

            table.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFieldsFromTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            selectedProductId = (int) table.getValueAt(selectedRow, 0);
            txtCode.setText(table.getValueAt(selectedRow, 1).toString());
            txtName.setText(table.getValueAt(selectedRow, 2).toString());
            txtUnitPrice.setText(table.getValueAt(selectedRow, 4).toString());
            txtCostPrice.setText(table.getValueAt(selectedRow, 5).toString());

            String category = table.getValueAt(selectedRow, 6).toString();
            String supplier = table.getValueAt(selectedRow, 7).toString();

            cmbCategory.setSelectedItem(category);
            cmbSupplier.setSelectedItem(supplier);

            txtZone.setText(table.getValueAt(selectedRow, 8).toString());
            txtDescription.setText(table.getValueAt(selectedRow, 9).toString());
        }
    }

    private void loadCategoryName() {
        try {
            Connection conn = DBConn.getConnection();
            String sql = "SELECT category_name FROM product_categories ORDER BY category_name";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            cmbCategory.addItem("--- Select Category ---");
            while (rs.next()) {
                cmbCategory.addItem(rs.getString("category_name"));
            }
            pst.close();
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSupplierName() {
        try {
            Connection conn = DBConn.getConnection();
            String sql = "SELECT supplier_name FROM suppliers ORDER BY supplier_name";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            cmbSupplier.addItem("--- Select Supplier ---");
            while (rs.next()) {
                cmbSupplier.addItem(rs.getString("supplier_name"));
            }
            pst.close();
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        try {
            // Validation
            if (txtCode.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in Product Code and Name!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cmbCategory.getSelectedIndex() == 0 || cmbSupplier.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select Category and Supplier!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get IDs
            ProductDAO dao = new ProductDAO();
            String categoryName = (String) cmbCategory.getSelectedItem();
            String supplierName = (String) cmbSupplier.getSelectedItem();

            int categoryId = dao.getCategoryIdByName(categoryName);
            int supplierId = dao.getSupplierIdByName(supplierName);

            if (categoryId == -1 || supplierId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid Category or Supplier!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create Product
            Product product = new Product();
            product.setProductCode(txtCode.getText().trim());
            product.setProductName(txtName.getText().trim());
            product.setCategoryId(categoryId);
            product.setSupplierId(supplierId);
            product.setDescription(txtDescription.getText().trim());
            product.setUnitPrice(Double.parseDouble(txtUnitPrice.getText().trim()));
            product.setCostPrice(Double.parseDouble(txtCostPrice.getText().trim()));
            product.setWarehouseZone(txtZone.getText().trim());

            // Insert Product
            boolean success = dao.insertProduct(product);

            if (success) {
                JOptionPane.showMessageDialog(this, "Product added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadProduct();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add product!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for prices!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editProduct() {
        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table to edit!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Validation
            if (txtCode.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in Product Code and Name!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cmbCategory.getSelectedIndex() == 0 || cmbSupplier.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select Category and Supplier!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Confirm edit
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to update this product?",
                    "Confirm Edit", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Get IDs
            ProductDAO dao = new ProductDAO();
            String categoryName = (String) cmbCategory.getSelectedItem();
            String supplierName = (String) cmbSupplier.getSelectedItem();

            int categoryId = dao.getCategoryIdByName(categoryName);
            int supplierId = dao.getSupplierIdByName(supplierName);

            if (categoryId == -1 || supplierId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid Category or Supplier!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create Product with updated values
            Product product = new Product();
            product.setProductId(selectedProductId);
            product.setProductCode(txtCode.getText().trim());
            product.setProductName(txtName.getText().trim());
            product.setCategoryId(categoryId);
            product.setSupplierId(supplierId);
            product.setDescription(txtDescription.getText().trim());
            product.setUnitPrice(Double.parseDouble(txtUnitPrice.getText().trim()));
            product.setCostPrice(Double.parseDouble(txtCostPrice.getText().trim()));
            product.setWarehouseZone(txtZone.getText().trim());

            // Update Product
            boolean success = dao.updateProduct(product);

            if (success) {
                JOptionPane.showMessageDialog(this, "Product updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadProduct();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update product!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for prices!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table to delete!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this product?\nThis action cannot be undone!",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            ProductDAO dao = new ProductDAO();
            boolean success = dao.deleteProduct(selectedProductId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadProduct();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchProduct() {
        String searchStr = JOptionPane.showInputDialog(null, "Enter search term: ");

        if (searchStr == null) {
            return; // User cancelled
        }

        searchStr = searchStr.trim().toLowerCase();

        if (searchStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter search term",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT \n" +
                "    p.product_id,\n" +
                "    p.product_code,\n" +
                "    p.product_name,\n" +
                "    c.category_name,\n" +
                "    s.supplier_name,\n" +
                "    p.description,\n" +
                "    p.unit_price,\n" +
                "    p.cost_price,\n" +
                "    COALESCE(i.quantity_on_hand, 0) AS quantity_on_hand,\n" +
                "    pl.warehouse_zone\n" +
                "FROM products p\n" +
                "LEFT JOIN product_categories c ON p.category_id = c.category_id\n" +
                "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id\n" +
                "LEFT JOIN inventory i ON p.product_id = i.product_id\n" +
                "LEFT JOIN product_locations pl ON p.product_id = pl.product_id\n" +
                "WHERE LOWER(p.product_code) LIKE ? OR LOWER(p.product_name) LIKE ? " +
                "OR LOWER(c.category_name) LIKE ? OR LOWER(s.supplier_name) LIKE ? " +
                "OR LOWER(pl.warehouse_zone) LIKE ? OR LOWER(p.description) LIKE ?\n" +
                "GROUP BY p.product_id, p.product_code, p.product_name, \n" +
                "         c.category_name, s.supplier_name,\n" +
                "         p.description, p.unit_price, p.cost_price, i.quantity_on_hand, \n" +
                "         pl.warehouse_zone\n" +
                "ORDER BY p.product_id;";

        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);

            String searchPattern = "%" + searchStr + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);
            ps.setString(6, searchPattern);

            rs = ps.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{
                            "ID", "Code", "Name", "Quantity", "Unit Price", "Cost Price",
                            "Category", "Supplier", "Zone", "Description"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_code"),
                        rs.getString("product_name"),
                        rs.getInt("quantity_on_hand"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("cost_price"),
                        rs.getString("category_name"),
                        rs.getString("supplier_name"),
                        rs.getString("warehouse_zone"),
                        rs.getString("description")
                });
            }

            table.setModel(model);

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No records found",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        selectedProductId = -1;
        txtCode.setText("");
        txtName.setText("");
        cmbCategory.setSelectedIndex(0);
        cmbSupplier.setSelectedIndex(0);
        txtUnitPrice.setText("");
        txtCostPrice.setText("");
        txtZone.setText("");
        txtDescription.setText("");
        table.clearSelection();
    }
}