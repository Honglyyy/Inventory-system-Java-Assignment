package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageProduct extends JPanel {

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
        JLabel title = new JLabel("Manage Product");
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

        // ===== LEFT COLUMN =====
        addLabel(formPanel, gbc, 0, "Product Code");
        JTextField txtCode = new JTextField(20);
        addField(formPanel, gbc, 1, txtCode);

        addLabel(formPanel, gbc, 2, "Product Name");
        JTextField txtName = new JTextField(20);
        addField(formPanel, gbc, 3, txtName);

        // ===== CATEGORY & SUPPLIER COMBO BOX =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Category");
        JComboBox<String> cmbCategory = new JComboBox<>();
        addField(formPanel, gbc, 1, cmbCategory);

        addLabel(formPanel, gbc, 2, "Supplier");
        JComboBox<String> cmbSupplier = new JComboBox<>();
        addField(formPanel, gbc, 3, cmbSupplier);


        // ===== UNIT PRICE & COST PRICE =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Unit Price");
        JTextField txtUnitPrice = new JTextField(20);
        addField(formPanel, gbc, 1, txtUnitPrice);

        addLabel(formPanel, gbc, 2, "Cost Price");
        JTextField txtCostPrice = new JTextField(20);
        addField(formPanel, gbc, 3, txtCostPrice);

        // ===== WAREHOUSE & AISLE =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Warehouse Zone");
        JTextField txtZone = new JTextField(20);
        addField(formPanel, gbc, 1, txtZone);


        addLabel(formPanel, gbc, 2, "Aisle");
        JTextField txtAisle = new JTextField(20);
        addField(formPanel, gbc, 3, txtAisle);


        // ===== SHELF =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Shelf");
        JTextField txtShelf = new JTextField(20);
        addField(formPanel, gbc, 1, txtShelf);

        // ===== IMAGE UPLOAD =====
        gbc.gridy++;
        addLabel(formPanel, gbc, 0, "Product Image");

        JButton btnImage = new JButton("Choose Image");
        gbc.gridx = 1;
        formPanel.add(btnImage, gbc);

        JLabel imagePreview = new JLabel("No Image", SwingConstants.CENTER);
        imagePreview.setPreferredSize(new Dimension(120, 120));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        formPanel.add(imagePreview, gbc);

        gbc.gridwidth = 1;

        // ===== DESCRIPTION =====
        gbc.gridy++;

        addLabel(formPanel, gbc, 0, "Description");
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextArea txtDescription = new JTextArea(3, 20);
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

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Add Product", new Color(13, 110, 253));
        JButton btnEdit = createButton("Edit", new Color(255, 193, 7));
        JButton btnSearch = createButton("Delete", new Color(220, 53, 69));
        JButton btnDelete = createButton("Search", new Color(25, 135, 84));
        JButton btnClear = createButton("Clear", new Color(108, 117, 125));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // ================= TABLE =================
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel tableLabel = new JLabel("Product List");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tablePanel.add(tableLabel, BorderLayout.NORTH);

        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "Code", "Name", "Unit Price", "Cost Price",
                        "Category", "Supplier", "Stock"
                }, 0
        );
        table.setModel(model);
        table.setRowHeight(28);

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
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
}
