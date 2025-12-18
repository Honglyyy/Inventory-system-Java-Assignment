package dao;

import models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public void insertCategory(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            conn = DBConn.getConnection();
            String sql = "INSERT INTO `product_categories`(`category_name`) VALUES (?);";
            ps = conn.prepareStatement(sql);

            ps.setString(1, category.getCategory_name());

            ps.executeUpdate();
            DBConn.closePreparedStatement(ps);
            DBConn.closeConnection(conn);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Category> readCategory() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();
        try{
            conn = DBConn.getConnection();
            String sql = "SELECT * FROM `product_categories`;";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                int cate_id = rs.getInt("category_id");
                String cate_name = rs.getString("category_name");

                Category cat = new Category(cate_id, cate_name);
                categories.add(cat);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Category get(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Category category = null;
        String sql = "SELECT * FROM `product_categories` WHERE `category_id` = ?;";

        try{
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if(rs.next()){
                category = new Category();
                int categoryId = rs.getInt("category_id");
                String categoryName = rs.getString("category_name");
                category.setCategory_id(categoryId);
                category.setCategory_name(categoryName);  // FIXED: Set category name
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {  // FIXED: Uncommented for proper cleanup
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return category;
    }

    public void deleteCategory(Category cate){
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "DELETE FROM `product_categories` WHERE `category_id` = ?;";

        try{
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cate.getCategory_id());
            ps.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {  // FIXED: Uncommented for proper cleanup
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateCategory(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;

        String sql = "UPDATE product_categories SET category_name = ? WHERE category_id = ?";

        try {
            conn = DBConn.getConnection();

            ps = conn.prepareStatement(sql);
            ps.setString(1, category.getCategory_name());
            ps.setInt(2, category.getCategory_id());

            int rowsAffected = ps.executeUpdate();

            if(rowsAffected == 0){
                throw new SQLException("Update failed: Category with ID " + category.getCategory_id() + " not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating category: " + e.getMessage(), e);
        } finally {
            try {
                if(ps != null) ps.close();
                if(conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
