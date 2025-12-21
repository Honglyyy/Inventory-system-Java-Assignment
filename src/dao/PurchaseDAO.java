package dao;

import models.PurchaseOrder;
import models.PurchaseOrderItem;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

    /**
     * Insert a new purchase order with items
     * Updates inventory when status is "Completed"
     */
    public boolean insertPurchaseOrder(PurchaseOrder order, List<PurchaseOrderItem> items) throws SQLException {
        String orderSql = "INSERT INTO purchase_orders (supplier_id, user_id, order_date, " +
                "status, total_amount, received_date, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String itemSql = "INSERT INTO purchase_order_items (po_id, product_id, " +
                "quantity_ordered, unit_cost, subtotal) VALUES (?, ?, ?, ?, ?)";

        String checkInventorySql = "SELECT quantity_on_hand FROM inventory WHERE product_id = ?";
        String updateInventorySql = "UPDATE inventory SET quantity_on_hand = quantity_on_hand + ? " +
                "WHERE product_id = ?";
        String insertInventorySql = "INSERT INTO inventory (product_id, quantity_on_hand) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            System.out.println("=== Inserting Purchase Order ===");
            System.out.println("Status: " + order.getStatus());
            System.out.println("Items count: " + items.size());

            // VALIDATION: Check if user exists
            if (!validateUserExists(conn, order.getUserId())) {
                throw new SQLException("User ID " + order.getUserId() + " does not exist in users table");
            }

            // VALIDATION: Check if supplier exists
            if (!validateSupplierExists(conn, order.getSupplierId())) {
                throw new SQLException("Supplier ID " + order.getSupplierId() + " does not exist in suppliers table");
            }

            // VALIDATION: Check if all products exist
            for (PurchaseOrderItem item : items) {
                if (!validateProductExists(conn, item.getProductId())) {
                    throw new SQLException("Product ID " + item.getProductId() + " does not exist in products table");
                }
            }

            // Insert purchase order
            pstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, order.getSupplierId());
            pstmt.setInt(2, order.getUserId());
            pstmt.setTimestamp(3, new Timestamp(order.getOrderDate().getTime()));
            pstmt.setString(4, order.getStatus());
            pstmt.setDouble(5, order.getTotalAmount());

            if (order.getReceivedDate() != null) {
                pstmt.setTimestamp(6, new Timestamp(order.getReceivedDate().getTime()));
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }

            pstmt.setString(7, order.getPaymentStatus());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Purchase order inserted: " + (rowsAffected > 0));

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int poId = rs.getInt(1);
                    System.out.println("Generated PO ID: " + poId);

                    // Insert purchase order items
                    pstmt = conn.prepareStatement(itemSql);
                    for (PurchaseOrderItem item : items) {
                        pstmt.setInt(1, poId);
                        pstmt.setInt(2, item.getProductId());
                        pstmt.setInt(3, item.getQuantityOrdered());
                        pstmt.setDouble(4, item.getUnitCost());
                        pstmt.setDouble(5, item.getSubtotal());
                        pstmt.executeUpdate();
                        System.out.println("Added item - Product ID: " + item.getProductId() +
                                ", Qty: " + item.getQuantityOrdered());
                    }

                    // If status is "Completed", update inventory
                    if ("Completed".equalsIgnoreCase(order.getStatus())) {
                        System.out.println("Status is Completed - Updating inventory...");

                        for (PurchaseOrderItem item : items) {
                            int productId = item.getProductId();
                            int quantityToAdd = item.getQuantityOrdered();

                            // Check if inventory record exists
                            pstmt = conn.prepareStatement(checkInventorySql);
                            pstmt.setInt(1, productId);
                            ResultSet invRs = pstmt.executeQuery();

                            if (invRs.next()) {
                                // Inventory exists - UPDATE
                                int currentQty = invRs.getInt("quantity_on_hand");
                                System.out.println("Product ID " + productId + " - Current qty: " + currentQty);

                                pstmt = conn.prepareStatement(updateInventorySql);
                                pstmt.setInt(1, quantityToAdd);
                                pstmt.setInt(2, productId);
                                int updated = pstmt.executeUpdate();

                                System.out.println("Updated inventory for Product ID " + productId +
                                        ": +" + quantityToAdd + " (rows affected: " + updated + ")");
                                System.out.println("New quantity should be: " + (currentQty + quantityToAdd));
                            } else {
                                // Inventory doesn't exist - INSERT
                                System.out.println("Product ID " + productId + " - No inventory record, creating one");

                                pstmt = conn.prepareStatement(insertInventorySql);
                                pstmt.setInt(1, productId);
                                pstmt.setInt(2, quantityToAdd);
                                int inserted = pstmt.executeUpdate();

                                System.out.println("Created inventory for Product ID " + productId +
                                        ": " + quantityToAdd + " (rows affected: " + inserted + ")");
                            }
                        }
                    } else {
                        System.out.println("Status is NOT Completed - Skipping inventory update");
                    }

                    conn.commit();
                    System.out.println("=== Transaction committed successfully ===");
                    return true;
                }
            }

            conn.rollback();
            System.out.println("=== Transaction rolled back - no rows affected ===");
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                System.out.println("=== Transaction rolled back due to error ===");
            }
            // Enhanced error message for debugging
            System.err.println("SQL Error Details:");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Validate if user exists
     */
    private boolean validateUserExists(Connection conn, int userId) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    /**
     * Validate if supplier exists
     */
    private boolean validateSupplierExists(Connection conn, int supplierId) throws SQLException {
        String sql = "SELECT supplier_id FROM suppliers WHERE supplier_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    /**
     * Validate if product exists
     */
    private boolean validateProductExists(Connection conn, int productId) throws SQLException {
        String sql = "SELECT product_id FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    /**
     * Get the first available user ID (fallback method)
     */
    public int getFirstAvailableUserId() throws SQLException {
        String sql = "SELECT user_id FROM users ORDER BY user_id LIMIT 1";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        return -1;
    }

    /**
     * Update purchase order
     * If status changes to "Completed", update inventory
     */
    public boolean updatePurchaseOrder(int poId, String newStatus, String paymentStatus,
                                       java.util.Date receivedDate, String previousStatus) throws SQLException {
        String updateOrderSql = "UPDATE purchase_orders SET status = ?, payment_status = ?, " +
                "received_date = ? WHERE po_id = ?";

        String getItemsSql = "SELECT product_id, quantity_ordered FROM purchase_order_items WHERE po_id = ?";

        String checkInventorySql = "SELECT quantity_on_hand FROM inventory WHERE product_id = ?";
        String updateInventorySql = "UPDATE inventory SET quantity_on_hand = quantity_on_hand + ? " +
                "WHERE product_id = ?";
        String insertInventorySql = "INSERT INTO inventory (product_id, quantity_on_hand) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            System.out.println("=== Updating Purchase Order ===");
            System.out.println("PO ID: " + poId);
            System.out.println("Previous Status: " + previousStatus);
            System.out.println("New Status: " + newStatus);

            // Update purchase order
            pstmt = conn.prepareStatement(updateOrderSql);
            pstmt.setString(1, newStatus);
            pstmt.setString(2, paymentStatus);

            if (receivedDate != null) {
                pstmt.setTimestamp(3, new Timestamp(receivedDate.getTime()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            pstmt.setInt(4, poId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // If status changed from non-Completed to Completed, update inventory
                if ("Completed".equalsIgnoreCase(newStatus) &&
                        !"Completed".equalsIgnoreCase(previousStatus)) {

                    System.out.println("Status changed to Completed - Updating inventory...");

                    // Get all items for this purchase order
                    pstmt = conn.prepareStatement(getItemsSql);
                    pstmt.setInt(1, poId);
                    ResultSet rs = pstmt.executeQuery();

                    // Update inventory for each item
                    while (rs.next()) {
                        int productId = rs.getInt("product_id");
                        int quantity = rs.getInt("quantity_ordered");

                        // Check if inventory record exists
                        pstmt = conn.prepareStatement(checkInventorySql);
                        pstmt.setInt(1, productId);
                        ResultSet invRs = pstmt.executeQuery();

                        if (invRs.next()) {
                            // UPDATE existing inventory
                            int currentQty = invRs.getInt("quantity_on_hand");
                            System.out.println("Product ID " + productId + " - Current qty: " + currentQty);

                            pstmt = conn.prepareStatement(updateInventorySql);
                            pstmt.setInt(1, quantity);
                            pstmt.setInt(2, productId);
                            int updated = pstmt.executeUpdate();

                            System.out.println("Updated: +" + quantity + " (New: " + (currentQty + quantity) +
                                    ", rows: " + updated + ")");
                        } else {
                            // INSERT new inventory record
                            System.out.println("Product ID " + productId + " - Creating inventory record");

                            pstmt = conn.prepareStatement(insertInventorySql);
                            pstmt.setInt(1, productId);
                            pstmt.setInt(2, quantity);
                            int inserted = pstmt.executeUpdate();

                            System.out.println("Created inventory: " + quantity + " (rows: " + inserted + ")");
                        }
                    }
                } else {
                    System.out.println("Status not changed to Completed - No inventory update");
                }

                conn.commit();
                System.out.println("=== Transaction committed successfully ===");
                return true;
            }

            conn.rollback();
            System.out.println("=== Transaction rolled back - no rows affected ===");
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
                System.out.println("=== Transaction rolled back due to error ===");
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

    /**
     * Get all purchase orders
     */
    public List<PurchaseOrder> getAllPurchaseOrders() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PurchaseOrder> orders = new ArrayList<>();

        try {
            conn = DBConn.getConnection();
            String sql = "SELECT po.po_id, po.supplier_id, s.supplier_name, po.user_id, " +
                    "po.order_date, po.status, po.total_amount, po.received_date, po.payment_status " +
                    "FROM purchase_orders po " +
                    "LEFT JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                    "ORDER BY po.po_id DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                PurchaseOrder order = new PurchaseOrder();
                order.setPoId(rs.getInt("po_id"));
                order.setSupplierId(rs.getInt("supplier_id"));
                order.setSupplierName(rs.getString("supplier_name"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setStatus(rs.getString("status"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setReceivedDate(rs.getTimestamp("received_date"));
                order.setPaymentStatus(rs.getString("payment_status"));
                orders.add(order);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }

        return orders;
    }

    /**
     * Get supplier ID by name
     */
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

    /**
     * Get product ID by code
     */
    public int getProductIdByCode(String productCode) throws SQLException {
        String sql = "SELECT product_id FROM products WHERE product_code = ?";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        }
        return -1;
    }

    /**
     * Delete purchase order
     * Note: This will NOT reverse inventory changes
     */
    public boolean deletePurchaseOrder(int poId) throws SQLException {
        String deleteItems = "DELETE FROM purchase_order_items WHERE po_id = ?";
        String deleteOrder = "DELETE FROM purchase_orders WHERE po_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            // Delete items first (foreign key)
            pstmt = conn.prepareStatement(deleteItems);
            pstmt.setInt(1, poId);
            pstmt.executeUpdate();

            // Delete order
            pstmt = conn.prepareStatement(deleteOrder);
            pstmt.setInt(1, poId);
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

    /**
     * Get purchase order items for a specific order
     */
    public List<PurchaseOrderItem> getPurchaseOrderItems(int poId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PurchaseOrderItem> items = new ArrayList<>();

        try {
            conn = DBConn.getConnection();
            String sql = "SELECT poi.po_item_id, poi.po_id, poi.product_id, " +
                    "p.product_code, p.product_name, poi.quantity_ordered, " +
                    "poi.unit_cost, poi.subtotal " +
                    "FROM purchase_order_items poi " +
                    "LEFT JOIN products p ON poi.product_id = p.product_id " +
                    "WHERE poi.po_id = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, poId);
            rs = ps.executeQuery();

            while (rs.next()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setPoItemId(rs.getInt("po_item_id"));
                item.setPoId(rs.getInt("po_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantityOrdered(rs.getInt("quantity_ordered"));
                item.setUnitCost(rs.getDouble("unit_cost"));
                item.setSubtotal(rs.getDouble("subtotal"));
                items.add(item);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }

        return items;
    }
}