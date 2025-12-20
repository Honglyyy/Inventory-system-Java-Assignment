package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings;

public class ManagePurchase extends JPanel {
    public JTextField txtAllPurchaseId = new JTextField();
    public JTextField txtAllSupplierName  = new JTextField();
    public JTextField txtAllTotal = new JTextField();
    public JComboBox cmbAllStatus =  new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});
    public JComboBox cmbAllPayment = new JComboBox<>(new String[]{"Cash", "Card", "Transfer"});
    public JTextField txtAllReceivedDate = new JTextField();
    JTable tableAllPurchase = new JTable(new DefaultTableModel(
            new String[]{"Order ID", "Supplier", "Order Date", "Total", "Payment", "Status", "Received Date"}, 0
    ));

    public JComboBox cmbProductCode = new JComboBox();
    public JTextField txtSupplierName  = new JTextField();
    public JTextField txtProductName  = new JTextField();
    public JTextField txtCostPrice = new JTextField();
    public JTextField txtQuantity = new  JTextField();
    public JComboBox cmbStatus =  new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});
    public JComboBox cmbPayment = new JComboBox<>(new String[]{"Cash", "Card", "Transfer"});

    // **NEW: Total text field**
    public JTextField txtTotal = new JTextField();

    JTable tablePurchase = new JTable(new DefaultTableModel(
            new String[]{"Product Code", "Product Name", "Qty", "Unit Price",}, 0
    ));

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
    }

    // =====================================================
    // ALL ORDER PANEL
    // =====================================================
    private JPanel createAllOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("All Purchase");
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
        addField(form, gbc, 1, 1,txtAllTotal);

        addLabel(form, gbc, 2, 1, "Status");
        addField(form, gbc, 3, 1, cmbAllStatus);

        addLabel(form, gbc, 0, 2, "Payment");
        addField(form, gbc, 1, 2,cmbAllPayment);

        addLabel(form, gbc, 2, 2, "Received Date");
        addField(form, gbc, 3, 2, txtAllReceivedDate);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnRow.setBackground(Color.WHITE);

        btnRow.add(createButton("Search", new Color(25, 135, 84)));
        btnRow.add(createButton("Edit", new Color(255, 193, 7)));
        btnRow.add(createButton("Delete", new Color(220, 53, 69)));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        form.add(btnRow, gbc);
        gbc.gridwidth = 1;

        content.add(form, BorderLayout.NORTH);

        tableAllPurchase.setRowHeight(28);

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

        JLabel title = new JLabel("New Purchase");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.WHITE);

        // ---------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = baseGbc();

        addLabel(form, gbc, 0, 0, "Supplier Name");
        addField(form, gbc, 1, 0,txtSupplierName);

        addLabel(form, gbc, 2, 0, "Product Code");
        addField(form, gbc, 3, 0,cmbProductCode);

        addLabel(form, gbc, 0, 1, "Product Name");
        addField(form, gbc, 1, 1, txtProductName);

        addLabel(form, gbc, 2, 1, "Cost Price");
        addField(form, gbc, 3, 1,txtCostPrice);

        addLabel(form, gbc, 0, 2, "Quantity");
        addField(form, gbc, 1, 2, txtQuantity);

        addLabel(form, gbc, 2, 2, "Payment");
        addField(form, gbc, 3, 2, cmbPayment);

        addLabel(form, gbc, 0, 3, "Status");
        addField(form, gbc, 1, 3,cmbStatus);

        content.add(form, BorderLayout.NORTH);

        // ---------- TABLE + TOTAL ----------
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(Color.WHITE);

        tablePurchase.setRowHeight(28);
        tablePanel.add(new JScrollPane(tablePurchase), BorderLayout.CENTER);

        // **NEW: Total Panel at bottom of table**
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        totalPanel.setBackground(Color.WHITE);

        JLabel lblTotal = new JLabel("Total Amount : ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));

        txtTotal.setPreferredSize(new Dimension(200, 35));
        txtTotal.setFont(new Font("Arial", Font.BOLD, 16));
        txtTotal.setEditable(false); // Make it read-only
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setText("$0.00");

        totalPanel.add(lblTotal);
        totalPanel.add(txtTotal);

        tablePanel.add(totalPanel, BorderLayout.SOUTH);

        content.add(tablePanel, BorderLayout.CENTER);

        // ---------- BUTTONS ----------
        JPanel btnPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        btnPanel.add(createButton("Add Purchase", new Color(13, 110, 253)));
        btnPanel.add(createButton("Edit", new Color(255, 193, 7)));
        btnPanel.add(createButton("Delete", new Color(220, 53, 69)));
        btnPanel.add(createButton("Search", new Color(25, 135, 84)));
        btnPanel.add(createButton("Clear", new Color(108, 117, 125)));

        content.add(btnPanel, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // HELPERS
    // =====================================================
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

    // **NEW: Helper method to calculate and update total**
    public void calculateTotal() {
        double total = 0.0;
        DefaultTableModel model = (DefaultTableModel) tablePurchase.getModel();

        for(int i = 0; i < model.getRowCount(); i++) {
            try {
                double qty = Double.parseDouble(model.getValueAt(i, 2).toString());
                double price = Double.parseDouble(model.getValueAt(i, 3).toString());
                total += qty * price;
            } catch(Exception e) {
                // Skip invalid rows
            }
        }

        txtTotal.setText(String.format("$%.2f", total));
    }
}