package ui;

import dao.DBConn;
import dao.SupplierDAO;
import dao.UsersDAO;
import models.Supplier;
import models.Users;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageSupplier extends JPanel {
    public JTextField txtSupName = new JTextField(20);
    public JTextField txtEmail = new JTextField(20);
    public JTextField txtPhone = new JTextField(20);
    public JTextField txtAddress = new JTextField(20);
    public JTable table = new JTable();

    public ManageSupplier() {
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
        JLabel title = new JLabel("Manage Supplier");
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
        addLabel(formPanel, gbc, "Supplier Name");
        addField(formPanel, gbc, txtSupName);

        // ===== Email =====
        gbc.gridy++;
        addLabel(formPanel, gbc, "Email");
        addField(formPanel, gbc, txtEmail);

        // ===== Role =====
        gbc.gridy++;
        addLabel(formPanel, gbc, "Phone");
        addField(formPanel, gbc, txtPhone);

        gbc.gridy++;
        addLabel(formPanel, gbc, "Address");
        addField(formPanel, gbc, txtAddress);
        // ================= BUTTON PANEL =================
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Add Supplier", new Color(13, 110, 253));
        JButton btnEdit = createButton("Edit", new Color(255, 193, 7));
        JButton btnSearch = createButton("Search", new Color(25, 135, 84));
        JButton btnShowAll = createButton("Show All", new Color(129, 154, 214));
        JButton btnDelete = createButton("Delete", new Color(220, 53, 69));
        JButton btnClear = createButton("Clear", new Color(108, 117, 125));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnShowAll);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        formPanel.add(buttonPanel, gbc);
        //=================== BUTTON EVENTS ==================
        btnAdd.addActionListener(e -> addSupplier());
        btnEdit.addActionListener(e -> {
            if(btnEdit.getText().equals("Edit")) {
                loadDataToEdit();
                btnEdit.setText("Save Edit");
            }
            else if(btnEdit.getText().equals("Save Edit")) {
                btnEdit.setText("Edit");
                try {
                    updateData();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        btnSearch.addActionListener(e -> {
            try {
                searchSupplier();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnShowAll.addActionListener(e -> {loadSuppliers();});
        btnDelete.addActionListener(e -> {deleteSupplier();});
        add(formPanel, BorderLayout.NORTH);

        // ================= TABLE PANEL =================
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel tableLabel = new JLabel("Supplier List");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tablePanel.add(tableLabel, BorderLayout.NORTH);



        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        table.setRowHeight(28);

        add(tablePanel, BorderLayout.CENTER);

        loadSuppliers();
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

    private void loadSuppliers(){
        try{
            SupplierDAO dao = new SupplierDAO();
            List<Supplier> suppliers = dao.getAll();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Supplier ID", "Supplier Name", "Email", "Phone","Address"}, 0
            );

            for(Supplier s: suppliers){
                model.addRow(new Object[]{
                        s.getSupplierId(),
                        s.getSupplierName(),
                        s.getSupplierEmail(),
                        s.getSupplierPhone(),
                        s.getSupplierAddress()
                });
                table.setModel(model);
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void addSupplier(){
        try{
            String supplierName = txtSupName.getText().trim();
            String supplierEmail = txtEmail.getText().trim();
            String supplierPhone = txtPhone.getText().trim();
            String supplierAddress = txtAddress.getText().trim();

            if(supplierName.isEmpty() || supplierEmail.isEmpty() || supplierPhone.isEmpty() || supplierAddress.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields");
                return;
            }
            else{
                SupplierDAO dao = new SupplierDAO();
                Supplier supplier = new Supplier(supplierName,supplierEmail,supplierPhone,supplierAddress);

                dao.insertSupplier(supplier);
            }
            loadSuppliers();
            clearTF();
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void loadDataToEdit(){
        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row");
            return;
        }

        txtSupName.setText(table.getValueAt(row, 1).toString());
        txtEmail.setText(table.getValueAt(row, 2).toString());
        txtPhone.setText(table.getValueAt(row, 3).toString());
        txtAddress.setText(table.getValueAt(row, 4).toString());
    }

    public void updateData() throws SQLException {
        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row");
            return;
        }

        String supplierName = txtSupName.getText().trim();
        String supplierEmail = txtEmail.getText().trim();
        String supplierPhone = txtPhone.getText().trim();
        String supplierAddress = txtAddress.getText().trim();

        if(supplierName.isEmpty() || supplierEmail.isEmpty() || supplierPhone.isEmpty() || supplierAddress.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please fill all the fields");
            return;
        }

        int supplierID = Integer.parseInt(table.getValueAt(row, 0).toString());

        try{
            SupplierDAO dao = new SupplierDAO();
            Supplier sup = new Supplier(supplierID,supplierName,supplierEmail,supplierPhone,supplierAddress);

            dao.updateSupplier(sup);
            JOptionPane.showMessageDialog(null, "Supplier Updated");

            loadSuppliers();
            clearTF();
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }

    }


    public void deleteSupplier(){
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row");
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        try{
            if(confirm == JOptionPane.YES_OPTION){
                SupplierDAO dao = new SupplierDAO();
                Supplier sup = dao.get(id);

                if(sup != null){
                    dao.deleteSupplier(sup);
                    JOptionPane.showMessageDialog(null, "Deleted Successfully", "Warning", JOptionPane.WARNING_MESSAGE);
                    loadSuppliers();
                    clearTF();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Delete Failed", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void searchSupplier() throws SQLException{
        String searchStr = JOptionPane.showInputDialog(null, "Enter search term: ");
        searchStr = searchStr.trim().toLowerCase();

        if(searchStr.isEmpty()){
            JOptionPane.showMessageDialog(null, "Enter search term");
        }

        String sql = "SELECT * FROM suppliers WHERE supplier_name LIKE ? OR email LIKE ? OR  phone LIKE ? OR address LIKE ?";
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        try{
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, "%" + searchStr + "%");
            ps.setString(2, "%" + searchStr + "%");
            ps.setString(3, "%" + searchStr + "%");
            ps.setString(4, "%" + searchStr + "%");

            rs = ps.executeQuery();
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Supplier ID", "Supplier Name", "Email", "Phone","Address"}, 0
            );

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                });
            }
            table.setModel(model);
            if(model.getRowCount() == 0){
                JOptionPane.showMessageDialog(null, "No records found", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        finally{
            ps.close();
            rs.close();
            conn.close();
        }


    }

    private void clearTF(){
        txtSupName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
    }
}
