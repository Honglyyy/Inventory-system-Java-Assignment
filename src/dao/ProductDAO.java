package dao;

import models.Product;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> getAllProducts() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        try {
            conn = DBConn.getConnection();
            String sql = "SELECT \n" +
                    "    p.product_id,\n" +
                    "    p.product_code,\n" +
                    "    p.product_name,\n" +
                    "    c.category_name,\n" +
                    "    s.supplier_name,\n" +
                    "    p.description,\n" +
                    "    p.unit_price,\n" +
                    "    p.cost_price,\n" +
                    "    COALESCE(i.quantity_on_hand, 0) AS quantity_on_hand,\n" +
                    "    pl.warehouse_zone\n" +
                    "FROM products p\n" +
                    "LEFT JOIN product_categories c ON p.category_id = c.category_id\n" +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id\n" +
                    "LEFT JOIN inventory i ON p.product_id = i.product_id\n" +
                    "LEFT JOIN product_locations pl ON p.product_id = pl.product_id\n" +
                    "GROUP BY p.product_id, p.product_code, p.product_name, \n" +
                    "         c.category_name, s.supplier_name,\n" +
                    "         p.description, p.unit_price, p.cost_price, i.quantity_on_hand, \n" +
                    "         pl.warehouse_zone\n" +
                    "ORDER BY p.product_id;";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Product prod = new Product();
                prod.setProductId(rs.getInt("product_id"));
                prod.setProductCode(rs.getString("product_code"));
                prod.setProductName(rs.getString("product_name"));
                prod.setCategoryName(rs.getString("category_name"));
                prod.setSupplierName(rs.getString("supplier_name"));
                prod.setDescription(rs.getString("description"));
                prod.setUnitPrice(rs.getDouble("unit_price"));
                prod.setCostPrice(rs.getDouble("cost_price"));
                prod.setQuantityOnHand(rs.getInt("quantity_on_hand"));
                prod.setWarehouseZone(rs.getString("warehouse_zone"));
                products.add(prod);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
        return products;
    }

    public int getCategoryIdByName(String categoryName) throws SQLException {
        String sql = "SELECT category_id FROM product_categories WHERE category_name = ?";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("category_id");
            }
        }
        return -1;
    }

    public int getSupplierIdByName(String supplierName) throws SQLException {
        String sql = "SELECT supplier_id FROM suppliers WHERE supplier_name = ?";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplierName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("supplier_id");
            }
        }
        return -1;
    }

    public boolean insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (product_code, product_name, category_id, supplier_id, " +
                "description, unit_price, cost_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String inventorySql = "INSERT INTO inventory (product_id, quantity_on_hand) VALUES (?, ?)";
        String locationSql = "INSERT INTO product_locations (product_id, warehouse_zone) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, product.getProductCode());
            pstmt.setString(2, product.getProductName());
            pstmt.setInt(3, product.getCategoryId());
            pstmt.setInt(4, product.getSupplierId());
            pstmt.setString(5, product.getDescription());
            pstmt.setDouble(6, product.getUnitPrice());
            pstmt.setDouble(7, product.getCostPrice());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int productId = rs.getInt(1);

                    // Insert into inventory
                    pstmt = conn.prepareStatement(inventorySql);
                    pstmt.setInt(1, productId);
                    pstmt.setInt(2, 0);
                    pstmt.executeUpdate();

                    // Insert into product_locations
                    pstmt = conn.prepareStatement(locationSql);
                    pstmt.setInt(1, productId);
                    pstmt.setString(2, product.getWarehouseZone());
                    pstmt.executeUpdate();

                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public boolean updateProduct(Product product) throws SQLException {
        String productSql = "UPDATE products SET product_code = ?, product_name = ?, " +
                "category_id = ?, supplier_id = ?, description = ?, " +
                "unit_price = ?, cost_price = ? WHERE product_id = ?";
        String locationSql = "UPDATE product_locations SET warehouse_zone = ? WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            // Update products table
            pstmt = conn.prepareStatement(productSql);
            pstmt.setString(1, product.getProductCode());
            pstmt.setString(2, product.getProductName());
            pstmt.setInt(3, product.getCategoryId());
            pstmt.setInt(4, product.getSupplierId());
            pstmt.setString(5, product.getDescription());
            pstmt.setDouble(6, product.getUnitPrice());
            pstmt.setDouble(7, product.getCostPrice());
            pstmt.setInt(8, product.getProductId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update product_locations table
                pstmt = conn.prepareStatement(locationSql);
                pstmt.setString(1, product.getWarehouseZone());
                pstmt.setInt(2, product.getProductId());
                pstmt.executeUpdate();

                conn.commit();
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public boolean deleteProduct(int productId) throws SQLException {
        String deleteInventory = "DELETE FROM inventory WHERE product_id = ?";
        String deleteLocation = "DELETE FROM product_locations WHERE product_id = ?";
        String deleteProduct = "DELETE FROM products WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            // Delete from inventory first (foreign key)
            pstmt = conn.prepareStatement(deleteInventory);
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();

            // Delete from product_locations
            pstmt = conn.prepareStatement(deleteLocation);
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();

            // Delete from products
            pstmt = conn.prepareStatement(deleteProduct);
            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Product> searchProducts(String searchTerm) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();

        try {
            conn = DBConn.getConnection();
            String sql = "SELECT \n" +
                    "    p.product_id,\n" +
                    "    p.product_code,\n" +
                    "    p.product_name,\n" +
                    "    c.category_name,\n" +
                    "    s.supplier_name,\n" +
                    "    p.description,\n" +
                    "    p.unit_price,\n" +
                    "    p.cost_price,\n" +
                    "    COALESCE(i.quantity_on_hand, 0) AS quantity_on_hand,\n" +
                    "    pl.warehouse_zone\n" +
                    "FROM products p\n" +
                    "LEFT JOIN product_categories c ON p.category_id = c.category_id\n" +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id\n" +
                    "LEFT JOIN inventory i ON p.product_id = i.product_id\n" +
                    "LEFT JOIN product_locations pl ON p.product_id = pl.product_id\n" +
                    "WHERE p.product_code LIKE ? OR p.product_name LIKE ?\n" +
                    "GROUP BY p.product_id, p.product_code, p.product_name, \n" +
                    "         c.category_name, s.supplier_name,\n" +
                    "         p.description, p.unit_price, p.cost_price, i.quantity_on_hand, \n" +
                    "         pl.warehouse_zone\n" +
                    "ORDER BY p.product_id;";

            ps = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            rs = ps.executeQuery();

            while (rs.next()) {
                Product prod = new Product();
                prod.setProductId(rs.getInt("product_id"));
                prod.setProductCode(rs.getString("product_code"));
                prod.setProductName(rs.getString("product_name"));
                prod.setCategoryName(rs.getString("category_name"));
                prod.setSupplierName(rs.getString("supplier_name"));
                prod.setDescription(rs.getString("description"));
                prod.setUnitPrice(rs.getDouble("unit_price"));
                prod.setCostPrice(rs.getDouble("cost_price"));
                prod.setQuantityOnHand(rs.getInt("quantity_on_hand"));
                prod.setWarehouseZone(rs.getString("warehouse_zone"));
                products.add(prod);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }

        return products;
    }
}