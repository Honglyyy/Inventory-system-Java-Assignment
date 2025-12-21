package ui;

import dao.DBConn;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class Report extends JPanel {

    private JTable orderTable;
    private JTable purchaseTable;
    private DefaultTableModel orderModel;
    private DefaultTableModel purchaseModel;

    private JDateChooser orderFromDate;
    private JDateChooser orderToDate;
    private JDateChooser purchaseFromDate;
    private JDateChooser purchaseToDate;

    private JComboBox<String> orderStatusFilter;
    private JComboBox<String> purchaseStatusFilter;

    // Summary labels as instance variables
    private JLabel totalOrdersLabel;
    private JLabel totalRevenueLabel;
    private JLabel totalPurchasesLabel;
    private JLabel totalCostLabel;

    public Report() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Orders Report", orderReportPanel());
        tabs.addTab("Purchase Report", purchaseReportPanel());

        add(tabs, BorderLayout.CENTER);

        // Load initial data
        loadOrderReport(null, null, "All");
        loadPurchaseReport(null, null, "All");
    }

    // =====================================================
    // ORDERS REPORT
    // =====================================================
    private JPanel orderReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        orderModel = new DefaultTableModel(
                new String[]{
                        "Order ID", "Customer", "Order Date", "Status",
                        "Payment Method", "Total Amount", "Shipping Address"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setRowHeight(26);

        // Filter Panel
        JPanel filterPanel = createOrderFilterPanel();

        // Header with export buttons
        JPanel header = reportHeader(
                () -> exportOrderExcel(),
                () -> exportOrderPdf(),
                filterPanel
        );

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Summary Panel
        panel.add(createOrderSummaryPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrderFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("From:"));
        orderFromDate = new JDateChooser();
        orderFromDate.setPreferredSize(new Dimension(120, 28));
        panel.add(orderFromDate);

        panel.add(new JLabel("To:"));
        orderToDate = new JDateChooser();
        orderToDate.setPreferredSize(new Dimension(120, 28));
        panel.add(orderToDate);

        panel.add(new JLabel("Status:"));
        orderStatusFilter = new JComboBox<>(new String[]{"All", "Pending", "Completed", "Cancelled"});
        orderStatusFilter.setPreferredSize(new Dimension(120, 28));
        panel.add(orderStatusFilter);

        JButton filterBtn = new JButton("Filter");
        filterBtn.setBackground(new Color(13, 110, 253));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.addActionListener(e -> {
            Date fromDate = orderFromDate.getDate();
            Date toDate = orderToDate.getDate();
            String status = (String) orderStatusFilter.getSelectedItem();
            loadOrderReport(fromDate, toDate, status);
        });
        panel.add(filterBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBackground(new Color(108, 117, 125));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> {
            orderFromDate.setDate(null);
            orderToDate.setDate(null);
            orderStatusFilter.setSelectedIndex(0);
            loadOrderReport(null, null, "All");
        });
        panel.add(resetBtn);

        return panel;
    }

    private JPanel createOrderSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        totalOrdersLabel = new JLabel("Total Orders: 0");
        totalOrdersLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalRevenueLabel = new JLabel("Total Revenue: $0.00");
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalRevenueLabel.setForeground(new Color(25, 135, 84));

        panel.add(totalOrdersLabel);
        panel.add(totalRevenueLabel);

        return panel;
    }

    private void loadOrderReport(Date fromDate, Date toDate, String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConn.getConnection();

            StringBuilder sql = new StringBuilder(
                    "SELECT o.order_id, c.customer_name, o.order_date, o.status, " +
                            "o.payment_method, o.total_amount, o.shipping_address " +
                            "FROM orders o " +
                            "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                            "WHERE 1=1 "
            );

            if (fromDate != null) {
                sql.append("AND DATE(o.order_date) >= ? ");
            }
            if (toDate != null) {
                sql.append("AND DATE(o.order_date) <= ? ");
            }
            if (!"All".equals(status)) {
                sql.append("AND o.status = ? ");
            }

            sql.append("ORDER BY o.order_date DESC");

            ps = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (fromDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(fromDate.getTime()));
            }
            if (toDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(toDate.getTime()));
            }
            if (!"All".equals(status)) {
                ps.setString(paramIndex++, status);
            }

            rs = ps.executeQuery();

            orderModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            int totalOrders = 0;
            double totalRevenue = 0.0;

            while (rs.next()) {
                double amount = rs.getDouble("total_amount");

                orderModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        sdf.format(rs.getTimestamp("order_date")),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        String.format("$%.2f", amount),
                        rs.getString("shipping_address")
                });

                totalOrders++;
                if ("Completed".equals(rs.getString("status"))) {
                    totalRevenue += amount;
                }
            }

            // Update summary
            updateOrderSummary(totalOrders, totalRevenue);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading order report: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

    private void updateOrderSummary(int totalOrders, double totalRevenue) {
        totalOrdersLabel.setText("Total Orders: " + totalOrders);
        totalRevenueLabel.setText("Total Revenue: $" + String.format("%.2f", totalRevenue));
    }

    // =====================================================
    // PURCHASE REPORT
    // =====================================================
    private JPanel purchaseReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        purchaseModel = new DefaultTableModel(
                new String[]{
                        "PO ID", "Supplier", "Order Date", "Status",
                        "Payment Status", "Total Amount", "Received Date"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        purchaseTable = new JTable(purchaseModel);
        purchaseTable.setRowHeight(26);

        // Filter Panel
        JPanel filterPanel = createPurchaseFilterPanel();

        // Header with export buttons
        JPanel header = reportHeader(
                () -> exportPurchaseExcel(),
                () -> exportPurchasePdf(),
                filterPanel
        );

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(purchaseTable), BorderLayout.CENTER);

        // Summary Panel
        panel.add(createPurchaseSummaryPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPurchaseFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("From:"));
        purchaseFromDate = new JDateChooser();
        purchaseFromDate.setPreferredSize(new Dimension(120, 28));
        panel.add(purchaseFromDate);

        panel.add(new JLabel("To:"));
        purchaseToDate = new JDateChooser();
        purchaseToDate.setPreferredSize(new Dimension(120, 28));
        panel.add(purchaseToDate);

        panel.add(new JLabel("Status:"));
        purchaseStatusFilter = new JComboBox<>(new String[]{"All", "Pending", "Completed", "Cancelled"});
        purchaseStatusFilter.setPreferredSize(new Dimension(120, 28));
        panel.add(purchaseStatusFilter);

        JButton filterBtn = new JButton("Filter");
        filterBtn.setBackground(new Color(13, 110, 253));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.addActionListener(e -> {
            Date fromDate = purchaseFromDate.getDate();
            Date toDate = purchaseToDate.getDate();
            String status = (String) purchaseStatusFilter.getSelectedItem();
            loadPurchaseReport(fromDate, toDate, status);
        });
        panel.add(filterBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBackground(new Color(108, 117, 125));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> {
            purchaseFromDate.setDate(null);
            purchaseToDate.setDate(null);
            purchaseStatusFilter.setSelectedIndex(0);
            loadPurchaseReport(null, null, "All");
        });
        panel.add(resetBtn);

        return panel;
    }

    private JPanel createPurchaseSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        totalPurchasesLabel = new JLabel("Total Purchases: 0");
        totalPurchasesLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalCostLabel = new JLabel("Total Cost: $0.00");
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalCostLabel.setForeground(new Color(220, 53, 69));

        panel.add(totalPurchasesLabel);
        panel.add(totalCostLabel);

        return panel;
    }

    private void loadPurchaseReport(Date fromDate, Date toDate, String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConn.getConnection();

            StringBuilder sql = new StringBuilder(
                    "SELECT po.po_id, s.supplier_name, po.order_date, po.status, " +
                            "po.payment_status, po.total_amount, po.received_date " +
                            "FROM purchase_orders po " +
                            "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                            "WHERE 1=1 "
            );

            if (fromDate != null) {
                sql.append("AND DATE(po.order_date) >= ? ");
            }
            if (toDate != null) {
                sql.append("AND DATE(po.order_date) <= ? ");
            }
            if (!"All".equals(status)) {
                sql.append("AND po.status = ? ");
            }

            sql.append("ORDER BY po.order_date DESC");

            ps = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (fromDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(fromDate.getTime()));
            }
            if (toDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(toDate.getTime()));
            }
            if (!"All".equals(status)) {
                ps.setString(paramIndex++, status);
            }

            rs = ps.executeQuery();

            purchaseModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            int totalPurchases = 0;
            double totalCost = 0.0;

            while (rs.next()) {
                double amount = rs.getDouble("total_amount");
                Timestamp receivedDate = rs.getTimestamp("received_date");

                purchaseModel.addRow(new Object[]{
                        rs.getInt("po_id"),
                        rs.getString("supplier_name"),
                        sdf.format(rs.getTimestamp("order_date")),
                        rs.getString("status"),
                        rs.getString("payment_status"),
                        String.format("$%.2f", amount),
                        receivedDate != null ? sdf.format(receivedDate) : "N/A"
                });

                totalPurchases++;
                if ("Completed".equals(rs.getString("status"))) {
                    totalCost += amount;
                }
            }

            // Update summary
            updatePurchaseSummary(totalPurchases, totalCost);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading purchase report: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

    private void updatePurchaseSummary(int totalPurchases, double totalCost) {
        totalPurchasesLabel.setText("Total Purchases: " + totalPurchases);
        totalCostLabel.setText("Total Cost: $" + String.format("%.2f", totalCost));
    }

    // =====================================================
    // REPORT HEADER (FILTER + EXPORT BUTTONS)
    // =====================================================
    private JPanel reportHeader(Runnable excelAction, Runnable pdfAction, JPanel filterPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(filterPanel, BorderLayout.CENTER);

        // Export buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setBackground(Color.WHITE);

        JButton excelBtn = new JButton("Export Excel");
        excelBtn.setBackground(new Color(25, 135, 84));
        excelBtn.setForeground(Color.WHITE);
        excelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        excelBtn.addActionListener(e -> excelAction.run());

//        JButton pdfBtn = new JButton("📄 Export PDF");
//        pdfBtn.setBackground(new Color(220, 53, 69));
//        pdfBtn.setForeground(Color.WHITE);
//        pdfBtn.setFont(new Font("Arial", Font.BOLD, 12));
//        pdfBtn.addActionListener(e -> pdfAction.run());

        actions.add(excelBtn);
//        actions.add(pdfBtn);
        panel.add(actions, BorderLayout.EAST);

        return panel;
    }

    // =====================================================
    // EXPORT METHODS
    // =====================================================
    private void exportOrderExcel() {
        try {
            exportTableToCSV(orderTable, "OrderReport.csv");
            JOptionPane.showMessageDialog(this,
                    "Order report exported successfully to OrderReport.csv!",
                    "Export Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportPurchaseExcel() {
        try {
            exportTableToCSV(purchaseTable, "PurchaseReport.csv");
            JOptionPane.showMessageDialog(this,
                    "Purchase report exported successfully to PurchaseReport.csv!",
                    "Export Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportOrderPdf() {
        JOptionPane.showMessageDialog(this,
                "PDF export feature coming soon!\n\n" +
                        "To implement:\n" +
                        "1. Add iText or OpenPDF library\n" +
                        "2. Create PDF document\n" +
                        "3. Add table data\n" +
                        "4. Save to file",
                "Export PDF",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportPurchasePdf() {
        JOptionPane.showMessageDialog(this,
                "PDF export feature coming soon!\n\n" +
                        "To implement:\n" +
                        "1. Add iText or OpenPDF library\n" +
                        "2. Create PDF document\n" +
                        "3. Add table data\n" +
                        "4. Save to file",
                "Export PDF",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Simple CSV export (works without external libraries)
    private void exportTableToCSV(JTable table, String filename) throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(filename));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();

            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();

                // Write headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.print(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();

                // Write data
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object value = model.getValueAt(row, col);
                        writer.print(value != null ? value.toString() : "");
                        if (col < model.getColumnCount() - 1) {
                            writer.print(",");
                        }
                    }
                    writer.println();
                }
            }
        }
    }
}