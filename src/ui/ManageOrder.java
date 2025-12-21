package ui;

import dao.DBConn;
import dao.OrderDAO;
import models.Order;
import models.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManageOrder extends JPanel {
    // All Order Panel Fields
    public JTextField txtAllOrderId = new JTextField();
    public JTextField txtAllCustomerName = new JTextField();
    public JTextField txtAllTotal = new JTextField();
    public JComboBox<String> cmbAllStatus = new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});
    public JComboBox<String> cmbAllPayment = new JComboBox<>(new String[]{"Cash", "Card", "Transfer"});
    JTable tableAllOrders = new JTable(new DefaultTableModel(
            new String[]{"Order ID", "Customer", "Order Date", "Total", "Payment", "Status"}, 0
    ));

    // New Order Panel Fields
    public JComboBox<String> cmbProductCode = new JComboBox<>();
    public JTextField txtCustomerName = new JTextField();
    public JTextField txtProductName = new JTextField();
    public JTextField txtUnitPrice = new JTextField();
    public JTextField txtQuantity = new JTextField();
    public JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});
    public JComboBox<String> cmbPayment = new JComboBox<>(new String[]{"Cash", "Card", "Transfer"});
    public JTextField txtAddress = new JTextField();
    public JTextField txtTotal = new JTextField();

    JTable tableOrder = new JTable(new DefaultTableModel(
            new String[]{"Product Code", "Product Name", "Qty", "Unit Price", "Subtotal"}, 0
    ));

    // Store current items in cart
    private List<OrderItem> cartItems = new ArrayList<>();
    private int selectedOrderId = -1;
    private int currentUserId = 3; // Change this to your actual user ID

    public ManageOrder() {
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
        loadCustomerNames();
        loadAllOrders();
    }

    // =====================================================
    // ALL ORDER PANEL
    // =====================================================
    private JPanel createAllOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("All Orders");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = baseGbc();

        addLabel(form, gbc, 0, 0, "Order ID");
        addField(form, gbc, 1, 0, txtAllOrderId);

        addLabel(form, gbc, 2, 0, "Customer Name");
        addField(form, gbc, 3, 0, txtAllCustomerName);

        addLabel(form, gbc, 0, 1, "Total");
        addField(form, gbc, 1, 1, txtAllTotal);

        addLabel(form, gbc, 2, 1, "Status");
        addField(form, gbc, 3, 1, cmbAllStatus);

        addLabel(form, gbc, 0, 2, "Payment");
        addField(form, gbc, 1, 2, cmbAllPayment);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnRow.setBackground(Color.WHITE);

        JButton btnSearch = createButton("Search", new Color(25, 135, 84));
        JButton btnEdit = createButton("Edit", new Color(255, 193, 7));
        JButton btnDelete = createButton("Delete", new Color(220, 53, 69));
        JButton btnRefresh = createButton("Refresh", new Color(129, 154, 214));

        btnSearch.addActionListener(e -> searchOrder());
        btnEdit.addActionListener(e -> editOrder());
        btnDelete.addActionListener(e -> deleteOrder());
        btnRefresh.addActionListener(e -> loadAllOrders());

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

        tableAllOrders.setRowHeight(28);
        tableAllOrders.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateAllOrderFields();
            }
        });

        content.add(new JScrollPane(tableAllOrders), BorderLayout.CENTER);

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

        JLabel title = new JLabel("New Order");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.WHITE);

        // ---------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = baseGbc();

        addLabel(form, gbc, 0, 0, "Customer Name");
        addField(form, gbc, 1, 0, txtCustomerName);

        addLabel(form, gbc, 2, 0, "Product Code");
        addField(form, gbc, 3, 0, cmbProductCode);

        addLabel(form, gbc, 0, 1, "Product Name");
        txtProductName.setEditable(false);
        addField(form, gbc, 1, 1, txtProductName);

        addLabel(form, gbc, 2, 1, "Unit Price");
        txtUnitPrice.setEditable(false);
        addField(form, gbc, 3, 1, txtUnitPrice);

        addLabel(form, gbc, 0, 2, "Quantity");
        addField(form, gbc, 1, 2, txtQuantity);

        addLabel(form, gbc, 2, 2, "Payment");
        addField(form, gbc, 3, 2, cmbPayment);

        addLabel(form, gbc, 0, 3, "Status");
        addField(form, gbc, 1, 3, cmbStatus);

        addLabel(form, gbc, 2, 3, "Shipping Address");
        addField(form, gbc, 3, 3, txtAddress);

        JPanel addItemBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addItemBtn.setBackground(Color.WHITE);
        JButton btnAddItem = createButton("Add to Cart", new Color(40, 167, 69));
        btnAddItem.addActionListener(e -> addItemToCart());
        addItemBtn.add(btnAddItem);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        form.add(addItemBtn, gbc);

        // Product selection listener
        cmbProductCode.addActionListener(e -> loadProductDetails());

        content.add(form, BorderLayout.NORTH);

        // ---------- TABLE + TOTAL ----------
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(Color.WHITE);

        tableOrder.setRowHeight(28);
        tablePanel.add(new JScrollPane(tableOrder), BorderLayout.CENTER);

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

        JButton btnSubmit = createButton("Submit Order", new Color(13, 110, 253));
        JButton btnRemoveItem = createButton("Remove Item", new Color(220, 53, 69));
        JButton btnClear = createButton("Clear", new Color(108, 117, 125));

        btnSubmit.addActionListener(e -> submitOrder());
        btnRemoveItem.addActionListener(e -> removeItemFromCart());
        btnClear.addActionListener(e -> clearNewOrderFields());

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

    private void loadCustomerNames() {
        // You can add autocomplete here later
    }

    private void loadProductDetails() {
        if (cmbProductCode.getSelectedIndex() == 0) {
            txtProductName.setText("");
            txtUnitPrice.setText("");
            return;
        }

        String productCode = (String) cmbProductCode.getSelectedItem();

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT p.product_name, p.unit_price, COALESCE(i.quantity_on_hand, 0) as stock " +
                             "FROM products p " +
                             "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                             "WHERE p.product_code = ?")) {

            ps.setString(1, productCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtProductName.setText(rs.getString("product_name"));
                txtUnitPrice.setText(String.valueOf(rs.getDouble("unit_price")));
                int stock = rs.getInt("stock");

                // Show stock info
                txtProductName.setToolTipText("Available stock: " + stock);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading product details: " + e.getMessage());
        }
    }

    private void loadAllOrders() {
        try {
            OrderDAO dao = new OrderDAO();
            List<Order> orders = dao.getAllOrders();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Order ID", "Customer", "Order Date", "Total", "Payment", "Status"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (Order order : orders) {
                model.addRow(new Object[]{
                        order.getOrderId(),
                        order.getCustomerName(),
                        order.getOrderDate() != null ? sdf.format(order.getOrderDate()) : "",
                        String.format("$%.2f", order.getTotalAmount()),
                        order.getPaymentMethod(),
                        order.getStatus()
                });
            }

            tableAllOrders.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
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
            double unitPrice = Double.parseDouble(txtUnitPrice.getText().trim());
            double subtotal = quantity * unitPrice;

            // Get product ID and check stock
            OrderDAO dao = new OrderDAO();
            int productId = dao.getProductIdByCode(productCode);

            if (productId == -1) {
                JOptionPane.showMessageDialog(this, "Product not found!");
                return;
            }

            // Check available stock
            try (Connection conn = DBConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "SELECT COALESCE(quantity_on_hand, 0) as stock FROM inventory WHERE product_id = ?")) {
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int availableStock = rs.getInt("stock");
                    if (quantity > availableStock) {
                        JOptionPane.showMessageDialog(this,
                                "Insufficient stock!\nAvailable: " + availableStock + ", Requested: " + quantity,
                                "Stock Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // Create item
            OrderItem item = new OrderItem();
            item.setProductId(productId);
            item.setProductCode(productCode);
            item.setProductName(productName);
            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            item.setDiscountPercent(0.0);
            item.setTaxAmount(0.0);
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
        int selectedRow = tableOrder.getSelectedRow();
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

        for (OrderItem item : cartItems) {
            model.addRow(new Object[]{
                    item.getProductCode(),
                    item.getProductName(),
                    item.getQuantity(),
                    String.format("$%.2f", item.getUnitPrice()),
                    String.format("$%.2f", item.getSubtotal())
            });
        }

        tableOrder.setModel(model);
    }

    public void calculateTotal() {
        double total = 0.0;
        for (OrderItem item : cartItems) {
            total += item.getSubtotal();
        }
        txtTotal.setText(String.format("$%.2f", total));
    }

    // =====================================================
    // ORDER OPERATIONS
    // =====================================================
    private void submitOrder() {
        try {
            if (txtCustomerName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter customer name!");
                return;
            }

            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add items to cart!");
                return;
            }

            OrderDAO dao = new OrderDAO();
            int customerId = dao.getCustomerIdByName(txtCustomerName.getText().trim());

            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Customer not found!");
                return;
            }

            // Create order
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setUserId(currentUserId);
            order.setOrderDate(new Date());
            order.setStatus((String) cmbStatus.getSelectedItem());
            order.setPaymentMethod((String) cmbPayment.getSelectedItem());
            order.setPaymentStatus("Pending");
            order.setShippingAddress(txtAddress.getText().trim());
            order.setDiscountAmount(0.0);

            double total = 0.0;
            for (OrderItem item : cartItems) {
                total += item.getSubtotal();
            }
            order.setTotalAmount(total);

            if ("Completed".equals(order.getStatus())) {
                order.setShippedDate(new Date());
            }

            // Insert order
            boolean success = dao.insertOrder(order, cartItems);

            if (success) {
                String message = "Order created successfully!";
                if ("Completed".equals(order.getStatus())) {
                    message += "\n✓ Inventory has been decreased.";
                }
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);

                clearNewOrderFields();
                loadAllOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create order!");
            }

        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("Insufficient stock")) {
                JOptionPane.showMessageDialog(this, errorMsg, "Stock Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + errorMsg);
            }
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateAllOrderFields() {
        int selectedRow = tableAllOrders.getSelectedRow();
        if (selectedRow >= 0) {
            selectedOrderId = Integer.parseInt(tableAllOrders.getValueAt(selectedRow, 0).toString());
            txtAllOrderId.setText(String.valueOf(selectedOrderId));
            txtAllCustomerName.setText(tableAllOrders.getValueAt(selectedRow, 1).toString());
            txtAllTotal.setText(tableAllOrders.getValueAt(selectedRow, 3).toString());
            cmbAllPayment.setSelectedItem(tableAllOrders.getValueAt(selectedRow, 4).toString());
            cmbAllStatus.setSelectedItem(tableAllOrders.getValueAt(selectedRow, 5).toString());
        }
    }

    private void editOrder() {
        if (selectedOrderId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to edit!");
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Update this order?",
                    "Confirm Edit", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            String newStatus = (String) cmbAllStatus.getSelectedItem();
            String paymentStatus = "Completed".equals(newStatus) ? "Paid" : "Pending";
            String paymentMethod = (String) cmbAllPayment.getSelectedItem();
            Date shippedDate = "Completed".equals(newStatus) ? new Date() : null;

            OrderDAO dao = new OrderDAO();

            // Get the previous status from database
            String prevStatus = null;
            try (Connection conn = DBConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT status FROM orders WHERE order_id = ?")) {
                ps.setInt(1, selectedOrderId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    prevStatus = rs.getString("status");
                }
            }

            boolean success = dao.updateOrder(selectedOrderId, newStatus,
                    paymentStatus, paymentMethod, (java.sql.Date) shippedDate, prevStatus);

            if (success) {
                String message = "Order updated successfully!";
                if ("Completed".equals(newStatus) && !"Completed".equals(prevStatus)) {
                    message += "\n✓ Inventory has been decreased.";
                }
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllOrders();
                clearAllOrderFields();
            }

        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("Insufficient stock")) {
                JOptionPane.showMessageDialog(this, errorMsg, "Stock Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + errorMsg);
            }
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteOrder() {
        if (selectedOrderId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete!");
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this order?\nNote: This will NOT reverse inventory changes!",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) return;

            OrderDAO dao = new OrderDAO();
            boolean success = dao.deleteOrder(selectedOrderId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Order deleted successfully!");
                loadAllOrders();
                clearAllOrderFields();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void searchOrder() {
        String searchStr = JOptionPane.showInputDialog(null, "Enter search term (ID or Customer): ");
        if (searchStr == null || searchStr.trim().isEmpty()) {
            return;
        }
        // Add your search implementation here
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================
    private void clearNewOrderFields() {
        txtCustomerName.setText("");
        cmbProductCode.setSelectedIndex(0);
        txtQuantity.setText("");
        txtAddress.setText("");
        cmbStatus.setSelectedIndex(0);
        cmbPayment.setSelectedIndex(0);
        cartItems.clear();
        updateCartTable();
        calculateTotal();
    }

    private void clearAllOrderFields() {
        selectedOrderId = -1;
        txtAllOrderId.setText("");
        txtAllCustomerName.setText("");
        txtAllTotal.setText("");
        cmbAllStatus.setSelectedIndex(0);
        cmbAllPayment.setSelectedIndex(0);
        tableAllOrders.clearSelection();
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