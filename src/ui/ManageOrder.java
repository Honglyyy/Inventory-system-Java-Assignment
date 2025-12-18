package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageOrder extends JPanel {

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
    }

    // =====================================================
    // ALL ORDER PANEL
    // =====================================================
    private JPanel createAllOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("All Order");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.WHITE);

        // ---------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = baseGbc();

        addLabel(form, gbc, 0, 0, "Order ID");
        addField(form, gbc, 1, 0, new JTextField());

        addLabel(form, gbc, 2, 0, "Customer Name");
        addField(form, gbc, 3, 0, new JTextField());

        addLabel(form, gbc, 0, 1, "Total");
        addField(form, gbc, 1, 1, new JTextField());

        addLabel(form, gbc, 2, 1, "Status");
        addField(form, gbc, 3, 1,
                new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"}));

        addLabel(form, gbc, 0, 2, "Payment");
        addField(form, gbc, 1, 2,
                new JComboBox<>(new String[]{"Cash", "Card", "Transfer"}));

        // ---------- BUTTON ROW ----------
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

        // ---------- TABLE ----------
        JTable table = new JTable(new DefaultTableModel(
                new String[]{"Order ID", "Customer", "Order Date", "Total", "Payment", "Status"}, 0
        ));
        table.setRowHeight(28);

        content.add(new JScrollPane(table), BorderLayout.CENTER);

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
        addField(form, gbc, 1, 0, new JComboBox<>());

        addLabel(form, gbc, 2, 0, "Product Code");
        addField(form, gbc, 3, 0, new JComboBox<>());

        addLabel(form, gbc, 0, 1, "Product Name");
        addField(form, gbc, 1, 1, new JTextField());

        addLabel(form, gbc, 2, 1, "Unit Price");
        addField(form, gbc, 3, 1, new JTextField());

        addLabel(form, gbc, 0, 2, "Quantity");
        addField(form, gbc, 1, 2, new JTextField());

        addLabel(form, gbc, 2, 2, "Payment");
        addField(form, gbc, 3, 2,
                new JComboBox<>(new String[]{"Cash", "Card", "Transfer"}));

        addLabel(form, gbc, 0, 3, "Status");
        addField(form, gbc, 1, 3,
                new JComboBox<>(new String[]{"Pending", "Completed"}));

        addLabel(form, gbc, 2, 3, "Address");
        addField(form, gbc, 3, 3, new JTextField());

        content.add(form, BorderLayout.NORTH);

        // ---------- TABLE ----------
        JTable table = new JTable(new DefaultTableModel(
                new String[]{"Product Code", "Product Name", "Qty", "Unit Price", "Total"}, 0
        ));
        table.setRowHeight(28);

        content.add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- BUTTONS ----------
        JPanel btnPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton addOrdBtn = createButton("Add Order", new Color(13, 110, 253));
        JButton editOrdBtn = createButton("Edit Order", new Color(255, 193, 7));
        JButton deleteOrdBtn = createButton("Delete Order", new Color(220, 53, 69));
        JButton saveOrdBtn = createButton("Save Order", new Color(25, 135, 84));
        JButton clearBtn = createButton("Clear", new Color(108, 117, 125));


        btnPanel.add(addOrdBtn);
        btnPanel.add(editOrdBtn);
        btnPanel.add(deleteOrdBtn);
        btnPanel.add(saveOrdBtn);
        btnPanel.add(clearBtn);

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
}
