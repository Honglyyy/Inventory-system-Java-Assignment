package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockMovement extends JPanel {

    public StockMovement() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Inventory", inventoryPanel());
        tabs.add("Stock Movements", movementPanel());
        tabs.add("Expiration", expirationPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // ================= INVENTORY =================
    private JPanel inventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTable table = new JTable(new DefaultTableModel(
                new String[]{
                        "Product Code", "Product Name", "Category",
                        "On Hand", "Reserved", "Available",
                        "Reorder Point", "Status"
                }, 0
        ));
        table.setRowHeight(26);

        panel.add(filterBar(), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel filterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bar.add(new JLabel("Product"));
        bar.add(new JTextField(12));
        bar.add(new JLabel("Category"));
        bar.add(new JComboBox<>());
        bar.add(new JCheckBox("Low Stock Only"));
        return bar;
    }

    // ================= MOVEMENTS =================
    private JPanel movementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTable table = new JTable(new DefaultTableModel(
                new String[]{
                        "Date", "Product", "Type", "Quantity",
                        "User", "Notes"
                }, 0
        ));
        table.setRowHeight(26);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ================= EXPIRATION =================
    private JPanel expirationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTable table = new JTable(new DefaultTableModel(
                new String[]{
                        "Product", "Expiration Date", "Quantity", "Status"
                }, 0
        ));
        table.setRowHeight(26);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
