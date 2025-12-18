package ui;

import dao.DBConn;
import dao.UsersDAO;
import models.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUser extends JPanel{
    public JTextField txtUsername = new JTextField(20);
    public JTextField txtPassword = new JTextField(20);
    public JComboBox<String> cmbRole = new JComboBox<>(
            new String[]{"Admin", "Manager", "Staff"}
    );
    public JTable table = new JTable();
    public ManageUser() {
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
        JLabel title = new JLabel("Manage User");
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
        addLabel(formPanel, gbc, "Username");
        addField(formPanel, gbc, txtUsername);

        // ===== Email =====
        gbc.gridy++;
        addLabel(formPanel, gbc, "Password");
        addField(formPanel, gbc, txtPassword);

        // ===== Role =====
        gbc.gridy++;
        addLabel(formPanel, gbc, "Role");
        addField(formPanel, gbc, cmbRole);

        // ================= BUTTON PANEL =================
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Add User", new Color(13, 110, 253));
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

        //================== BUTTONS EVENT ==============
        btnAdd.addActionListener(e -> {addUser();});
        btnEdit.addActionListener(e -> {
            if(btnEdit.getText().equals("Edit")) {
                btnEdit.setText("Save Edit");
                loadData();
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
                searchUser();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnShowAll.addActionListener(e -> {loadUsers();});
        btnDelete.addActionListener(e -> {deleteUser();});
        btnClear.addActionListener(e -> {clearTF();});

        add(formPanel, BorderLayout.NORTH);

        // ================= TABLE PANEL =================
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel tableLabel = new JLabel("User List");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tablePanel.add(tableLabel, BorderLayout.NORTH);


        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        table.setRowHeight(28);

        add(tablePanel, BorderLayout.CENTER);
        loadUsers();
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

    private void loadUsers() {
        try{
            UsersDAO dao = new UsersDAO();
            List<Users> users = dao.getAllUsers();
            DefaultTableModel dtm = new DefaultTableModel(
                    new String[]{"ID","Username", "Password", "Role"}, 0
            );

            for(Users user : users){
                dtm.addRow(new Object[]{user.getUser_id(),user.getUsername(), user.getPassword(), user.getRole()});
            }
            table.setModel(dtm);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addUser(){
        try{
            String  username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            String role = cmbRole.getSelectedItem().toString();
            if(username.isEmpty() || password.isEmpty() || role.isEmpty()){
                JOptionPane.showMessageDialog(null, "Please fill all the fields");
                return;
            }
            else{
                UsersDAO dao = new UsersDAO();
                Users user = new Users(username, password, role);

                dao.insertUser(user);
            }
            loadUsers();
            clearTF();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadData(){
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row");
            return;
        }

        txtUsername.setText(table.getValueAt(row, 1).toString());
        txtPassword.setText(table.getValueAt(row, 2).toString());
        cmbRole.setSelectedItem(table.getValueAt(row, 3).toString());
    }

    public void updateData() throws SQLException {
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row");
            return;
        }

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = cmbRole.getSelectedItem().toString();
        if(username.isEmpty() || password.isEmpty() || role.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please fill all the fields");
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        try{
            UsersDAO dao = new UsersDAO();
            Users user = new  Users(id, username, password, role);

            dao.updateUser(user);
            JOptionPane.showMessageDialog(null, "User Updated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            loadUsers();
            clearTF();
        }
        catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Please fill all the fields");
        }

    }

    public void deleteUser(){
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        try{
            if(confirm == JOptionPane.YES_OPTION){
                UsersDAO dao = new UsersDAO();
                Users user = dao.getUser(id);

                if(user != null){
                    dao.deleteUser(user);
                    JOptionPane.showMessageDialog(null, "Deleted Successfully", "Warning", JOptionPane.WARNING_MESSAGE);
                    loadUsers();
                    clearTF();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Delete Failed", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }
    public void searchUser() throws SQLException {
        String searchStr = JOptionPane.showInputDialog(null, "Enter search term: ", "Search", JOptionPane.QUESTION_MESSAGE);
        searchStr = searchStr.trim();
        if(searchStr == null){
            JOptionPane.showMessageDialog(null, "Enter search term", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        String sql = "SELECT * FROM users WHERE user_id LIKE ? OR username LIKE ? OR role LIKE ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;

        try{
            con = DBConn.getConnection();
            ps =  con.prepareStatement(sql);

            ps.setString(1, "%" +  searchStr + "%");
            ps.setString(2, "%" +  searchStr + "%");
            ps.setString(3, "%" +  searchStr + "%");
            rs =  ps.executeQuery();

            DefaultTableModel dtm = new  DefaultTableModel(new String[]{"ID","Username","Password","Role"}, 0);

            while(rs.next()){
                dtm.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                });
            }
            table.setModel(dtm);

            if(dtm.getRowCount() == 0){
                JOptionPane.showMessageDialog(null, "No records found", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
//        finally {
//            ps.close();
//            rs.close();
//            con.close();
//        }
    }



    private void clearTF(){
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
    }
}
