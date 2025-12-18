package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageCustomers extends JPanel {

    public ManageCustomers() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);

        // ================= TOP FORM PANEL =================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== Title =====
        JLabel title = new JLabel("Manage Customer");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;

        // ===== Username =====
        addLabel(formPanel, gbc, "Name");
        JTextField txtUsername = new JTextField(20);
        addField(formPanel, gbc, txtUsername);

        // ===== Email =====
        gbc.gridy++;
        addLabel(formPanel, gbc, "Phone Number");
        JTextField txtEmail = new JTextField(20);
        addField(formPanel, gbc, txtEmail);

        // ===== Role =====
        gbc.gridy++;
        addLabel(formPanel, gbc, "Address");
        JTextField txtAddress = new JTextField(20);
        addField(formPanel, gbc, txtAddress);

        // ================= BUTTON PANEL =================
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Add Customer", new Color(13, 110, 253));
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

        // ================= TABLE PANEL =================
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel tableLabel = new JLabel("Customer List");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tablePanel.add(tableLabel, BorderLayout.NORTH);

        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Phone Number", "Address"}, 0
        );
        table.setModel(model);
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    // ================= HELPER METHODS =================
    private void addLabel(JPanel panel, GridBagConstraints gbc, String text) {
        gbc.gridx = 0;
        JLabel label = new JLabel(text + " : ");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, gbc);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, JComponent field) {
        gbc.gridx = 1;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(250, 32));
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
