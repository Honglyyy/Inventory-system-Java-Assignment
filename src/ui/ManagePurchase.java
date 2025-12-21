package ui;

import dao.DBConn;
import dao.PurchaseDAO;
import models.PurchaseOrder;
import models.PurchaseOrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagePurchase extends JPanel {
    // All Purchase Panel Fields
    public JTextField txtAllPurchaseId = new JTextField();
    public JTextField txtAllSupplierName = new JTextField();
    public JTextField txtAllTotal = new JTextField();
    public JComboBox<String> cmbAllStatus = new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});
    public JComboBox<String> cmbAllPayment = new JComboBox<>(new String[]{"Cash", "Card", "Transfer"});
    public JTextField txtAllReceivedDate = new JTextField();
    JTable tableAllPurchase = new JTable(new DefaultTableModel(
            new String[]{"Order ID", "Supplier", "Order Date", "Total", "Payment", "Status", "Received Date"}, 0
    ));

    // New Purchase Panel Fields
    public JComboBox<String> cmbProductCode = new JComboBox<>();
    public JTextField txtSupplierName = new JTextField();
    public JTextField txtProductName = new JTextField();
    public JTextField txtCostPrice = new JTextField();
    public JTextField txtQuantity = new JTextField();
    public JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});
    public JComboBox<String> cmbPayment = new JComboBox<>(new String[]{"Cash", "Card", "Transfer"});
    public JTextField txtTotal = new JTextField();

    JTable tablePurchase = new JTable(new DefaultTableModel(
            new String[]{"Product Code", "Product Name", "Qty", "Unit Price", "Subtotal"}, 0
    ));

    // Store current items in cart
    private List<PurchaseOrderItem> cartItems = new ArrayList<>();
    private int selectedPurchaseId = -1;
    private int currentUserId = 3; // You should get this from your login session

    public ManagePurchase() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createAllOrderPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createNewOrderPanel());

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Load initial data
        loadProductCodes();
        loadSupplierNames();
        loadAllPurchaseOrders();
    }

    // =====================================================
    // ALL ORDER PANEL
    // =====================================================
    private JPanel createAllOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("All Purchase Orders");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = baseGbc();

        addLabel(form, gbc, 0, 0, "Purchase ID");
        addField(form, gbc, 1, 0, txtAllPurchaseId);

        addLabel(form, gbc, 2, 0, "Supplier Name");
        addField(form, gbc, 3, 0, txtAllSupplierName);

        addLabel(form, gbc, 0, 1, "Total");
        addField(form, gbc, 1, 1, txtAllTotal);

        addLabel(form, gbc, 2, 1, "Status");
        addField(form, gbc, 3, 1, cmbAllStatus);

        addLabel(form, gbc, 0, 2, "Payment");
        addField(form, gbc, 1, 2, cmbAllPayment);

        addLabel(form, gbc, 2, 2, "Received Date");
        addField(form, gbc, 3, 2, txtAllReceivedDate);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnRow.setBackground(Color.WHITE);

        JButton btnSearch = createButton("Search", new Color(25, 135, 84));
        JButton btnEdit = createButton("Edit", new Color(255, 193, 7));
        JButton btnDelete = createButton("Delete", new Color(220, 53, 69));
        JButton btnRefresh = createButton("Refresh", new Color(129, 154, 214));

        btnSearch.addActionListener(e -> searchPurchaseOrder());
        btnEdit.addActionListener(e -> editPurchaseOrder());
        btnDelete.addActionListener(e -> deletePurchaseOrder());
        btnRefresh.addActionListener(e -> loadAllPurchaseOrders());

        btnRow.add(btnSearch);
        btnRow.add(btnEdit);
        btnRow.add(btnDelete);
        btnRow.add(btnRefresh);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        form.add(btnRow, gbc);
        gbc.gridwidth = 1;

        content.add(form, BorderLayout.NORTH);

        tableAllPurchase.setRowHeight(28);
        tableAllPurchase.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateAllPurchaseFields();
            }
        });

        // Add double-click listener to show purchase order items
        tableAllPurchase.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableAllPurchase.getSelectedRow();
                    if (row >= 0) {
                        int poId = Integer.parseInt(tableAllPurchase.getValueAt(row, 0).toString());
                        showPurchaseItemsDialog(poId);
                    }
                }
            }
        });

        content.add(new JScrollPane(tableAllPurchase), BorderLayout.CENTER);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // NEW ORDER PANEL
    // =====================================================
    private JPanel createNewOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel title = new JLabel("New Purchase Order");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.WHITE);

        // ---------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = baseGbc();

        addLabel(form, gbc, 0, 0, "Supplier Name");
        addField(form, gbc, 1, 0, txtSupplierName);

        addLabel(form, gbc, 2, 0, "Product Code");
        addField(form, gbc, 3, 0, cmbProductCode);

        addLabel(form, gbc, 0, 1, "Product Name");
        txtProductName.setEditable(false);
        addField(form, gbc, 1, 1, txtProductName);

        addLabel(form, gbc, 2, 1, "Cost Price");
        txtCostPrice.setEditable(false);
        addField(form, gbc, 3, 1, txtCostPrice);

        addLabel(form, gbc, 0, 2, "Quantity");
        addField(form, gbc, 1, 2, txtQuantity);

        addLabel(form, gbc, 2, 2, "Payment");
        addField(form, gbc, 3, 2, cmbPayment);

        addLabel(form, gbc, 0, 3, "Status");
        addField(form, gbc, 1, 3, cmbStatus);

        JPanel addItemBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addItemBtn.setBackground(Color.WHITE);
        JButton btnAddItem = createButton("Add to Cart", new Color(40, 167, 69));
        btnAddItem.addActionListener(e -> addItemToCart());
        addItemBtn.add(btnAddItem);

        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        form.add(addItemBtn, gbc);

        // Product selection listener
        cmbProductCode.addActionListener(e -> loadProductDetails());

        content.add(form, BorderLayout.NORTH);

        // ---------- TABLE + TOTAL ----------
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(Color.WHITE);

        tablePurchase.setRowHeight(28);
        tablePanel.add(new JScrollPane(tablePurchase), BorderLayout.CENTER);

        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        totalPanel.setBackground(Color.WHITE);

        JLabel lblTotal = new JLabel("Total Amount : ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));

        txtTotal.setPreferredSize(new Dimension(200, 35));
        txtTotal.setFont(new Font("Arial", Font.BOLD, 16));
        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setText("$0.00");

        totalPanel.add(lblTotal);
        totalPanel.add(txtTotal);

        tablePanel.add(totalPanel, BorderLayout.SOUTH);

        content.add(tablePanel, BorderLayout.CENTER);

        // ---------- BUTTONS ----------
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnSubmit = createButton("Submit Purchase", new Color(13, 110, 253));
        JButton btnRemoveItem = createButton("Remove Item", new Color(220, 53, 69));
        JButton btnClear = createButton("Clear", new Color(108, 117, 125));

        btnSubmit.addActionListener(e -> submitPurchaseOrder());
        btnRemoveItem.addActionListener(e -> removeItemFromCart());
        btnClear.addActionListener(e -> clearNewPurchaseFields());

        btnPanel.add(btnSubmit);
        btnPanel.add(btnRemoveItem);
        btnPanel.add(btnClear);

        content.add(btnPanel, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // DATA LOADING METHODS
    // =====================================================
    private void loadProductCodes() {
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT product_code FROM products ORDER BY product_code");
             ResultSet rs = ps.executeQuery()) {

            cmbProductCode.removeAllItems();
            cmbProductCode.addItem("--- Select Product ---");

            while (rs.next()) {
                cmbProductCode.addItem(rs.getString("product_code"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void loadSupplierNames() {
        // Auto-complete functionality can be added here
    }

    private void loadProductDetails() {
        if (cmbProductCode.getSelectedIndex() == 0) {
            txtProductName.setText("");
            txtCostPrice.setText("");
            return;
        }

        String productCode = (String) cmbProductCode.getSelectedItem();

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT p.product_name, p.cost_price, s.supplier_name " +
                             "FROM products p " +
                             "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                             "WHERE p.product_code = ?")) {

            ps.setString(1, productCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtProductName.setText(rs.getString("product_name"));
                txtCostPrice.setText(String.valueOf(rs.getDouble("cost_price")));
                txtSupplierName.setText(rs.getString("supplier_name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading product details: " + e.getMessage());
        }
    }

    private void loadAllPurchaseOrders() {
        try {
            PurchaseDAO dao = new PurchaseDAO();
            List<PurchaseOrder> orders = dao.getAllPurchaseOrders();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Order ID", "Supplier", "Order Date", "Total", "Payment", "Status", "Received Date"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (PurchaseOrder order : orders) {
                model.addRow(new Object[]{
                        order.getPoId(),
                        order.getSupplierName(),
                        order.getOrderDate() != null ? sdf.format(order.getOrderDate()) : "",
                        String.format("$%.2f", order.getTotalAmount()),
                        order.getPaymentStatus(),
                        order.getStatus(),
                        order.getReceivedDate() != null ? sdf.format(order.getReceivedDate()) : ""
                });
            }

            tableAllPurchase.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading purchase orders: " + e.getMessage());
        }
    }

    // =====================================================
    // CART MANAGEMENT
    // =====================================================
    private void addItemToCart() {
        try {
            if (cmbProductCode.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a product!");
                return;
            }

            if (txtQuantity.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter quantity!");
                return;
            }

            String productCode = (String) cmbProductCode.getSelectedItem();
            String productName = txtProductName.getText();
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            double unitCost = Double.parseDouble(txtCostPrice.getText().trim());
            double subtotal = quantity * unitCost;

            // Get product ID
            PurchaseDAO dao = new PurchaseDAO();
            int productId = dao.getProductIdByCode(productCode);

            if (productId == -1) {
                JOptionPane.showMessageDialog(this, "Product not found!");
                return;
            }

            // Create item
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProductId(productId);
            item.setProductCode(productCode);
            item.setProductName(productName);
            item.setQuantityOrdered(quantity);
            item.setUnitCost(unitCost);
            item.setSubtotal(subtotal);

            cartItems.add(item);
            updateCartTable();
            calculateTotal();

            // Clear fields
            cmbProductCode.setSelectedIndex(0);
            txtQuantity.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage());
        }
    }

    private void removeItemFromCart() {
        int selectedRow = tablePurchase.getSelectedRow();
        if (selectedRow >= 0) {
            cartItems.remove(selectedRow);
            updateCartTable();
            calculateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove!");
        }
    }

    private void updateCartTable() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Product Code", "Product Name", "Qty", "Unit Price", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (PurchaseOrderItem item : cartItems) {
            model.addRow(new Object[]{
                    item.getProductCode(),
                    item.getProductName(),
                    item.getQuantityOrdered(),
                    String.format("$%.2f", item.getUnitCost()),
                    String.format("$%.2f", item.getSubtotal())
            });
        }

        tablePurchase.setModel(model);
    }

    public void calculateTotal() {
        double total = 0.0;
        for (PurchaseOrderItem item : cartItems) {
            total += item.getSubtotal();
        }
        txtTotal.setText(String.format("$%.2f", total));
    }

    // =====================================================
    // PURCHASE ORDER OPERATIONS
    // =====================================================
    private void submitPurchaseOrder() {
        try {
            if (txtSupplierName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter supplier name!");
                return;
            }

            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add items to cart!");
                return;
            }

            PurchaseDAO dao = new PurchaseDAO();
            int supplierId = dao.getSupplierIdByName(txtSupplierName.getText().trim());

            if (supplierId == -1) {
                JOptionPane.showMessageDialog(this, "Supplier not found!");
                return;
            }

            // Create purchase order
            PurchaseOrder order = new PurchaseOrder();
            order.setSupplierId(supplierId);
            order.setUserId(currentUserId);
            order.setOrderDate(new Date());
            order.setStatus((String) cmbStatus.getSelectedItem());
            order.setPaymentStatus((String) cmbPayment.getSelectedItem());

            double total = 0.0;
            for (PurchaseOrderItem item : cartItems) {
                total += item.getSubtotal();
            }
            order.setTotalAmount(total);

            if ("Completed".equals(order.getStatus())) {
                order.setReceivedDate(new Date());
            }

            // Insert purchase order
            boolean success = dao.insertPurchaseOrder(order, cartItems);

            if (success) {
                String message = "Purchase order created successfully!";
                if ("Completed".equals(order.getStatus())) {
                    message += "\nInventory has been updated.";
                }
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);

                clearNewPurchaseFields();
                loadAllPurchaseOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create purchase order!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateAllPurchaseFields() {
        int selectedRow = tableAllPurchase.getSelectedRow();
        if (selectedRow >= 0) {
            selectedPurchaseId = Integer.parseInt(tableAllPurchase.getValueAt(selectedRow, 0).toString());
            txtAllPurchaseId.setText(String.valueOf(selectedPurchaseId));
            txtAllSupplierName.setText(tableAllPurchase.getValueAt(selectedRow, 1).toString());
            txtAllTotal.setText(tableAllPurchase.getValueAt(selectedRow, 3).toString());
            cmbAllPayment.setSelectedItem(tableAllPurchase.getValueAt(selectedRow, 4).toString());
            cmbAllStatus.setSelectedItem(tableAllPurchase.getValueAt(selectedRow, 5).toString());
            txtAllReceivedDate.setText(tableAllPurchase.getValueAt(selectedRow, 6).toString());
        }
    }

    private void editPurchaseOrder() {
        if (selectedPurchaseId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order to edit!");
            return;
        }

        try {
            // Get previous status
            String previousStatus = cmbAllStatus.getSelectedItem().toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Update this purchase order?",
                    "Confirm Edit", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            String newStatus = (String) cmbAllStatus.getSelectedItem();
            String paymentStatus = (String) cmbAllPayment.getSelectedItem();
            Date receivedDate = "Completed".equals(newStatus) ? new Date() : null;

            PurchaseDAO dao = new PurchaseDAO();

            // Get the previous status from database
            String prevStatus = null;
            try (Connection conn = DBConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT status FROM purchase_orders WHERE po_id = ?")) {
                ps.setInt(1, selectedPurchaseId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    prevStatus = rs.getString("status");
                }
            }

            boolean success = dao.updatePurchaseOrder(selectedPurchaseId, newStatus,
                    paymentStatus, receivedDate, prevStatus);

            if (success) {
                String message = "Purchase order updated successfully!";
                if ("Completed".equals(newStatus) && !"Completed".equals(prevStatus)) {
                    message += "\nInventory has been updated.";
                }
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllPurchaseOrders();
                clearAllPurchaseFields();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deletePurchaseOrder() {
        if (selectedPurchaseId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order to delete!");
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this purchase order?\nNote: This will NOT reverse inventory changes!",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) return;

            PurchaseDAO dao = new PurchaseDAO();
            boolean success = dao.deletePurchaseOrder(selectedPurchaseId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Purchase order deleted successfully!");
                loadAllPurchaseOrders();
                clearAllPurchaseFields();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void searchPurchaseOrder() {
        // Implement search similar to your supplier search
        String searchStr = JOptionPane.showInputDialog(null, "Enter search term (ID or Supplier): ");

        if (searchStr == null || searchStr.trim().isEmpty()) {
            return;
        }

        // Add your search implementation here
    }

    private void showPurchaseItemsDialog(int poId) {
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT poi.product_id, p.product_code, p.product_name, " +
                             "poi.quantity_ordered, poi.unit_cost, poi.subtotal " +
                             "FROM purchase_order_items poi " +
                             "JOIN products p ON poi.product_id = p.product_id " +
                             "WHERE poi.po_id = ? " +
                             "ORDER BY poi.po_item_id")) {

            ps.setInt(1, poId);
            ResultSet rs = ps.executeQuery();

            // Create table model
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Product Code", "Product Name", "Quantity", "Unit Cost", "Subtotal"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            double totalAmount = 0.0;

            while (rs.next()) {
                String productCode = rs.getString("product_code");
                String productName = rs.getString("product_name");
                int quantity = rs.getInt("quantity_ordered");
                double unitCost = rs.getDouble("unit_cost");
                double subtotal = rs.getDouble("subtotal");

                model.addRow(new Object[]{
                        productCode,
                        productName,
                        quantity,
                        String.format("$%.2f", unitCost),
                        String.format("$%.2f", subtotal)
                });

                totalAmount += subtotal;
            }

            // Create table
            JTable itemsTable = new JTable(model);
            itemsTable.setRowHeight(28);
            itemsTable.setFont(new Font("Arial", Font.PLAIN, 13));
            itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

            // Create dialog
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Purchase Order Items - PO #" + poId, true);
            dialog.setLayout(new BorderLayout(10, 10));

            // Add table in scroll pane
            JScrollPane scrollPane = new JScrollPane(itemsTable);
            scrollPane.setPreferredSize(new Dimension(700, 300));
            dialog.add(scrollPane, BorderLayout.CENTER);

            // Add total panel at bottom
            JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            totalPanel.setBackground(Color.WHITE);

            JLabel lblTotal = new JLabel("Total Amount:");
            lblTotal.setFont(new Font("Arial", Font.BOLD, 16));

            JLabel lblTotalValue = new JLabel(String.format("$%.2f", totalAmount));
            lblTotalValue.setFont(new Font("Arial", Font.BOLD, 18));
            lblTotalValue.setForeground(new Color(13, 110, 253));

            totalPanel.add(lblTotal);
            totalPanel.add(lblTotalValue);

            // Close button
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setBackground(Color.WHITE);
            JButton btnClose = createButton("Close", new Color(108, 117, 125));
            btnClose.addActionListener(e -> dialog.dispose());
            btnPanel.add(btnClose);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBackground(Color.WHITE);
            bottomPanel.add(totalPanel, BorderLayout.NORTH);
            bottomPanel.add(btnPanel, BorderLayout.SOUTH);

            dialog.add(bottomPanel, BorderLayout.SOUTH);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading purchase order items: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================
    private void clearNewPurchaseFields() {
        txtSupplierName.setText("");
        cmbProductCode.setSelectedIndex(0);
        txtQuantity.setText("");
        cmbStatus.setSelectedIndex(0);
        cmbPayment.setSelectedIndex(0);
        cartItems.clear();
        updateCartTable();
        calculateTotal();
    }

    private void clearAllPurchaseFields() {
        selectedPurchaseId = -1;
        txtAllPurchaseId.setText("");
        txtAllSupplierName.setText("");
        txtAllTotal.setText("");
        cmbAllStatus.setSelectedIndex(0);
        cmbAllPayment.setSelectedIndex(0);
        txtAllReceivedDate.setText("");
        tableAllPurchase.clearSelection();
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc,
                          int x, int y, String text) {
        gbc.gridx = x;
        gbc.gridy = y;
        JLabel label = new JLabel(text + " : ");
        label.setFont(new Font("Arial", Font.PLAIN, 15));
        panel.add(label, gbc);
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                          int x, int y, JComponent field) {
        gbc.gridx = x;
        gbc.gridy = y;
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
}