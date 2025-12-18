package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Dashboard extends JPanel {

    public Dashboard() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));

        add(createKpiPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    // ================= KPI CARDS =================
    private JPanel createKpiPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 15));
        panel.setBackground(getBackground());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        panel.add(kpiCard("Products", "128"));
        panel.add(kpiCard("Low Stock", "6"));
        panel.add(kpiCard("Today Orders", "14"));
        panel.add(kpiCard("Revenue", "$12,450"));
        panel.add(kpiCard("Pending PO", "4"));

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
        panel.add(createLowStock());

        return panel;
    }

    private JPanel createRecentOrders() {
        JPanel panel = sectionPanel("Recent Orders");

        JTable table = new JTable(new DefaultTableModel(
                new String[]{"Order ID", "Customer", "Date", "Total", "Status"}, 0
        ));
        table.setRowHeight(26);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLowStock() {
        JPanel panel = sectionPanel("Low Stock Alerts");

        JTable table = new JTable(new DefaultTableModel(
                new String[]{"Product", "On Hand", "Reorder Point"}, 0
        ));
        table.setRowHeight(26);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
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
}
