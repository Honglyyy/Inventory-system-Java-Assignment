package ui;

import com.mysql.cj.xdevapi.PreparableStatement;
import dao.CategoryDAO;
import dao.DBConn;
import models.Category;
import net.proteanit.sql.DbUtils;

import java.sql.*;

import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCategory extends JPanel {
    public JTextField categoryTF = new JTextField(20);
    public JTable table = new JTable();

    public ManageCategory() {
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
        JLabel title = new JLabel("Manage Category");
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
        addLabel(formPanel, gbc, "Category Name");
        addField(formPanel, gbc, categoryTF);

        // ================= BUTTON PANEL =================
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Add Category", new Color(25, 135, 84));
        JButton btnEdit = createButton("Edit", new Color(255, 193, 7));
        JButton btnDelete = createButton("Delete", new Color(220, 53, 69));
        JButton btnSearch = createButton("Search", new Color(13, 110, 253));
        JButton btnShowAll = createButton("Show All", new Color(129, 154, 214));
//        btnShowAll.setForeground(Color.BLACK);
        JButton btnClear = createButton("Clear", new Color(108, 117, 125));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnShowAll);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        formPanel.add(buttonPanel, gbc);

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
        add(tablePanel, BorderLayout.CENTER);

        //================= BUTTON EVENT===================
        btnAdd.addActionListener(e -> addCategory());
        btnEdit.addActionListener(e -> {
            if(btnEdit.getText().equals("Edit")) {
                btnEdit.setText("Save Edit");
                loadData();
            }
            else if(btnEdit.getText().equals("Save Edit")) {
                btnEdit.setText("Edit");
                updateCategory();
            }
        });
        btnSearch.addActionListener(e -> {
                try {
                    searchCategory();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
        });
        btnShowAll.addActionListener(e -> loadTable());
        btnDelete.addActionListener(e -> deleteCategory());
        btnClear.addActionListener(e -> clearTF());


        loadTable();
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

    private void loadTable(){
        try{
            CategoryDAO dao = new CategoryDAO();
            List<Category> cate = dao.readCategory();
            DefaultTableModel dtm = new DefaultTableModel(new String[]{"Category ID", "Category Name"}, 0);

            for(Category cat : cate){
                dtm.addRow(new Object[]{
                    cat.getCategory_id(), cat.getCategory_name()
                });
            }

            table.setModel(dtm);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void addCategory(){
        try{
            String categoryVal = categoryTF.getText();
            if(categoryVal == null || categoryVal.length() == 0){
                JOptionPane.showMessageDialog(null, "Please enter a category", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            else{
                CategoryDAO categoryDAO = new CategoryDAO();
                Category category = new Category(categoryVal);

                categoryDAO.insertCategory(category);
            }
            loadTable();
            clearTF();
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }



    private void deleteCategory(){
        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete this record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        try{
            if(confirm == JOptionPane.YES_OPTION){
                CategoryDAO categoryDAO = new CategoryDAO();
                Category cate = categoryDAO.get(id);

                if(cate != null){
                    categoryDAO.deleteCategory(cate);
                    JOptionPane.showMessageDialog(null, "Category Deleted Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTable();  // FIXED: Refresh the table after deletion
                    clearTF();    // FIXED: Clear the text field
                } else {
                    JOptionPane.showMessageDialog(null, "Category not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchCategory() throws Exception {
        String searchStr = JOptionPane.showInputDialog("Enter search term(Category ID or Category Name): ");
        if(searchStr == null || searchStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a search term!");
            return;
        }

        searchStr = searchStr.trim();
        String sql = "SELECT * FROM product_categories WHERE category_id LIKE ? OR category_name LIKE ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchStr + "%");
            ps.setString(2, "%" + searchStr + "%");
            rs = ps.executeQuery();

            DefaultTableModel dtm = new DefaultTableModel(new String[]{"Category ID", "Category Name"}, 0);

            while(rs.next()) {
                dtm.addRow(new Object[]{
                        rs.getString("category_id"),
                        rs.getString("category_name")
                });
            }

            table.setModel(dtm);

            if(dtm.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No categories found matching: " + searchStr);
            }

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching categories: " + e.getMessage());
        } finally {
            if(rs != null) rs.close();
            if(ps != null) ps.close();
            if(conn != null) conn.close();
        }
    }

    public void loadData(){
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        categoryTF.setText(table.getValueAt(row, 1).toString());
    }

    public void updateCategory() {
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(null, "Please select a row", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String categoryName = categoryTF.getText().trim();
        if(categoryName.isEmpty()){
            JOptionPane.showMessageDialog(null, "Category name cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(table.getValueAt(row, 0).toString());

        try{
            CategoryDAO categoryDAO = new CategoryDAO();
            Category category = new Category(id, categoryName);

            categoryDAO.updateCategory(category);

            JOptionPane.showMessageDialog(null, "Category updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            loadTable();
            clearTF();
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearTF(){
        categoryTF.setText("");
    }
}
