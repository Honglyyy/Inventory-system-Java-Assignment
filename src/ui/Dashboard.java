package ui;

import dao.DBConn;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard extends JPanel {

    JTable todayTbl = new JTable();
    JTable purchaseTbl = new JTable();

    public Dashboard() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));

        add(createKpiPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        // 🔥 LOAD DATA AFTER UI IS READY
        loadOrderTbl();
        loadPurchaseTbl();
    }

    // ================= KPI CARDS =================
    private JPanel createKpiPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 15));
        panel.setBackground(getBackground());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        panel.add(kpiCard("All Product Stock", String.valueOf(loadProductStock())));
        panel.add(kpiCard("Today Orders", String.valueOf(loadTodayOrd())));
        panel.add(kpiCard("Today Revenue", "$ " + loadRevenue()));

        return panel;
    }

    private JPanel kpiCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);

        return card;
    }

    // ================= CENTER =================
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setBackground(getBackground());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        panel.add(createRecentOrders());
        panel.add(createPurchasePanel());

        return panel;
    }

    private JPanel createRecentOrders() {
        JPanel panel = sectionPanel("Today's Orders");

        todayTbl.setRowHeight(26);
        panel.add(new JScrollPane(todayTbl), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPurchasePanel() {
        JPanel panel = sectionPanel("Today's Purchases");

        purchaseTbl.setRowHeight(26);
        panel.add(new JScrollPane(purchaseTbl), BorderLayout.CENTER);

        return panel;
    }

    private JPanel sectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(lbl, BorderLayout.NORTH);
        return panel;
    }

    // ================= KPI DATA =================
    public int loadProductStock() {
        String sql = "SELECT SUM(quantity_on_hand) AS sum_qty FROM inventory";
        try (Connection con = DBConn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt("sum_qty");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int loadTodayOrd() {
        String sql = "SELECT COUNT(*) AS ord_qty FROM orders WHERE order_date >= CURDATE() AND order_date < CURDATE() + INTERVAL 1 DAY";
        try (Connection con = DBConn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt("ord_qty");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double loadRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount),0) AS revenue FROM orders WHERE order_date >= CURDATE() AND order_date < CURDATE() + INTERVAL 1 DAY";
        try (Connection con = DBConn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble("revenue");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ================= TABLE DATA =================
    public void loadOrderTbl() {
        String sql =
                "SELECT o.order_id, c.customer_name, o.total_amount, o.status, o.payment_status " +
                        "FROM orders o " +
                        "JOIN customers c ON o.customer_id = c.customer_id " +
                        "WHERE o.order_date >= CURDATE() AND o.order_date < CURDATE() + INTERVAL 1 DAY";

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Order ID", "Customer", "Total", "Status", "Payment"}, 0
        );

        try (Connection con = DBConn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("payment_status")
                });
            }

            todayTbl.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPurchaseTbl() {
        String sql =
                "SELECT p.po_id, s.supplier_name, p.total_amount, p.status " +
                        "FROM purchase_orders p " +
                        "JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                        "WHERE p.order_date >= CURDATE() AND p.order_date < CURDATE() + INTERVAL 1 DAY";

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Purchase ID", "Supplier", "Total", "Status"}, 0
        );

        try (Connection con = DBConn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("po_id"),
                        rs.getString("supplier_name"),
                        rs.getDouble("total_amount"),
                        rs.getString("status")
                });
            }

            purchaseTbl.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
