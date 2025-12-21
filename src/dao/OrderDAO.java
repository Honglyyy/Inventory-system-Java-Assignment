package dao;

import models.Order;
import models.OrderItem;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /**
     * Insert a new order with items
     * DECREASES inventory when status is "Completed"
     */
    public boolean insertOrder(Order order, List<OrderItem> items) throws SQLException {
        String orderSql = "INSERT INTO orders (customer_id, user_id, order_date, status, " +
                "total_amount, shipping_address, shipped_date, delivered_date, payment_status, " +
                "payment_method, discount_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, " +
                "unit_price, discount_percent, tax_amount, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String checkInventorySql = "SELECT quantity_on_hand FROM inventory WHERE product_id = ?";
        String updateInventorySql = "UPDATE inventory SET quantity_on_hand = quantity_on_hand - ? " +
                "WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            System.out.println("=== Inserting Order ===");
            System.out.println("Status: " + order.getStatus());
            System.out.println("Items count: " + items.size());

            // VALIDATION: Check if customer exists
            if (!validateCustomerExists(conn, order.getCustomerId())) {
                throw new SQLException("Customer ID " + order.getCustomerId() + " does not exist");
            }

            // VALIDATION: Check if user exists
            if (!validateUserExists(conn, order.getUserId())) {
                throw new SQLException("User ID " + order.getUserId() + " does not exist");
            }

            // VALIDATION: Check if all products exist and have sufficient stock
            for (OrderItem item : items) {
                if (!validateProductExists(conn, item.getProductId())) {
                    throw new SQLException("Product ID " + item.getProductId() + " does not exist");
                }

                // Check stock availability for Completed orders
                if ("Completed".equalsIgnoreCase(order.getStatus())) {
                    int availableStock = getProductStock(conn, item.getProductId());
                    if (availableStock < item.getQuantity()) {
                        throw new SQLException("Insufficient stock for product ID " + item.getProductId() +
                                ". Available: " + availableStock + ", Requested: " + item.getQuantity());
                    }
                }
            }

            // Insert order
            pstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setInt(2, order.getUserId());
            pstmt.setTimestamp(3, new Timestamp(order.getOrderDate().getTime()));
            pstmt.setString(4, order.getStatus());
            pstmt.setDouble(5, order.getTotalAmount());
            pstmt.setString(6, order.getShippingAddress());

            if (order.getShippedDate() != null) {
                pstmt.setTimestamp(7, new Timestamp(order.getShippedDate().getTime()));
            } else {
                pstmt.setNull(7, Types.TIMESTAMP);
            }

            if (order.getDeliveredDate() != null) {
                pstmt.setTimestamp(8, new Timestamp(order.getDeliveredDate().getTime()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP);
            }

            pstmt.setString(9, order.getPaymentStatus());
            pstmt.setString(10, order.getPaymentMethod());
            pstmt.setDouble(11, order.getDiscountAmount());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Order inserted: " + (rowsAffected > 0));

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);
                    System.out.println("Generated Order ID: " + orderId);

                    // Insert order items
                    pstmt = conn.prepareStatement(itemSql);
                    for (OrderItem item : items) {
                        pstmt.setInt(1, orderId);
                        pstmt.setInt(2, item.getProductId());
                        pstmt.setInt(3, item.getQuantity());
                        pstmt.setDouble(4, item.getUnitPrice());
                        pstmt.setDouble(5, item.getDiscountPercent());
                        pstmt.setDouble(6, item.getTaxAmount());
                        pstmt.setDouble(7, item.getSubtotal());
                        pstmt.executeUpdate();
                        System.out.println("Added item - Product ID: " + item.getProductId() +
                                ", Qty: " + item.getQuantity());
                    }

                    // If status is "Completed", DECREASE inventory
                    if ("Completed".equalsIgnoreCase(order.getStatus())) {
                        System.out.println("Status is Completed - DECREASING inventory...");

                        for (OrderItem item : items) {
                            int productId = item.getProductId();
                            int quantityToSubtract = item.getQuantity();

                            // Get current stock
                            pstmt = conn.prepareStatement(checkInventorySql);
                            pstmt.setInt(1, productId);
                            ResultSet invRs = pstmt.executeQuery();

                            if (invRs.next()) {
                                int currentQty = invRs.getInt("quantity_on_hand");
                                System.out.println("Product ID " + productId + " - Current qty: " + currentQty);

                                // Update (decrease) inventory
                                pstmt = conn.prepareStatement(updateInventorySql);
                                pstmt.setInt(1, quantityToSubtract);
                                pstmt.setInt(2, productId);
                                int updated = pstmt.executeUpdate();

                                System.out.println("Decreased inventory for Product ID " + productId +
                                        ": -" + quantityToSubtract + " (rows affected: " + updated + ")");
                                System.out.println("New quantity: " + (currentQty - quantityToSubtract));
                            } else {
                                throw new SQLException("No inventory record for product ID " + productId);
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
     * Update order
     * If status changes to "Completed", DECREASE inventory
     */
    public boolean updateOrder(int orderId, String newStatus, String paymentStatus,
                               String paymentMethod, Date shippedDate, String previousStatus) throws SQLException {
        String updateOrderSql = "UPDATE orders SET status = ?, payment_status = ?, " +
                "payment_method = ?, shipped_date = ? WHERE order_id = ?";

        String getItemsSql = "SELECT product_id, quantity FROM order_items WHERE order_id = ?";

        String checkInventorySql = "SELECT quantity_on_hand FROM inventory WHERE product_id = ?";
        String updateInventorySql = "UPDATE inventory SET quantity_on_hand = quantity_on_hand - ? " +
                "WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            System.out.println("=== Updating Order ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("Previous Status: " + previousStatus);
            System.out.println("New Status: " + newStatus);

            // Update order
            pstmt = conn.prepareStatement(updateOrderSql);
            pstmt.setString(1, newStatus);
            pstmt.setString(2, paymentStatus);
            pstmt.setString(3, paymentMethod);

            if (shippedDate != null) {
                pstmt.setTimestamp(4, new Timestamp(shippedDate.getTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.setInt(5, orderId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // If status changed from non-Completed to Completed, DECREASE inventory
                if ("Completed".equalsIgnoreCase(newStatus) &&
                        !"Completed".equalsIgnoreCase(previousStatus)) {

                    System.out.println("Status changed to Completed - DECREASING inventory...");

                    // Get all items for this order
                    pstmt = conn.prepareStatement(getItemsSql);
                    pstmt.setInt(1, orderId);
                    ResultSet rs = pstmt.executeQuery();

                    // Decrease inventory for each item
                    while (rs.next()) {
                        int productId = rs.getInt("product_id");
                        int quantity = rs.getInt("quantity");

                        // Check current stock
                        pstmt = conn.prepareStatement(checkInventorySql);
                        pstmt.setInt(1, productId);
                        ResultSet invRs = pstmt.executeQuery();

                        if (invRs.next()) {
                            int currentQty = invRs.getInt("quantity_on_hand");
                            System.out.println("Product ID " + productId + " - Current qty: " + currentQty);

                            if (currentQty < quantity) {
                                throw new SQLException("Insufficient stock for product ID " + productId +
                                        ". Available: " + currentQty + ", Needed: " + quantity);
                            }

                            // Update (decrease) inventory
                            pstmt = conn.prepareStatement(updateInventorySql);
                            pstmt.setInt(1, quantity);
                            pstmt.setInt(2, productId);
                            int updated = pstmt.executeUpdate();

                            System.out.println("Decreased: -" + quantity + " (New: " + (currentQty - quantity) +
                                    ", rows: " + updated + ")");
                        } else {
                            throw new SQLException("No inventory record for product ID " + productId);
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
     * Get all orders
     */
    public List<Order> getAllOrders() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();

        try {
            conn = DBConn.getConnection();
            String sql = "SELECT o.order_id, o.customer_id, c.customer_name, o.user_id, " +
                    "o.order_date, o.status, o.total_amount, o.shipping_address, " +
                    "o.shipped_date, o.delivered_date, o.payment_status, o.payment_method, " +
                    "o.discount_amount " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "ORDER BY o.order_id DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setStatus(rs.getString("status"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setShippedDate(rs.getTimestamp("shipped_date"));
                order.setDeliveredDate(rs.getTimestamp("delivered_date"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setDiscountAmount(rs.getDouble("discount_amount"));
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
     * Get order items for a specific order
     */
    public List<OrderItem> getOrderItems(int orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<OrderItem> items = new ArrayList<>();

        try {
            conn = DBConn.getConnection();
            String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, " +
                    "p.product_code, p.product_name, oi.quantity, " +
                    "oi.unit_price, oi.discount_percent, oi.tax_amount, oi.subtotal " +
                    "FROM order_items oi " +
                    "LEFT JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE oi.order_id = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setDiscountPercent(rs.getDouble("discount_percent"));
                item.setTaxAmount(rs.getDouble("tax_amount"));
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

    /**
     * Delete order
     * Note: This will NOT reverse inventory changes
     */
    public boolean deleteOrder(int orderId) throws SQLException {
        String deleteItems = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrder = "DELETE FROM orders WHERE order_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            // Delete items first (foreign key)
            pstmt = conn.prepareStatement(deleteItems);
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();

            // Delete order
            pstmt = conn.prepareStatement(deleteOrder);
            pstmt.setInt(1, orderId);
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

    // Helper methods
    public int getCustomerIdByName(String customerName) throws SQLException {
        String sql = "SELECT customer_id FROM customers WHERE customer_name = ?";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id");
            }
        }
        return -1;
    }

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

    private boolean validateCustomerExists(Connection conn, int customerId) throws SQLException {
        String sql = "SELECT customer_id FROM customers WHERE customer_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    private boolean validateUserExists(Connection conn, int userId) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    private boolean validateProductExists(Connection conn, int productId) throws SQLException {
        String sql = "SELECT product_id FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    private int getProductStock(Connection conn, int productId) throws SQLException {
        String sql = "SELECT quantity_on_hand FROM inventory WHERE product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity_on_hand");
            }
        }
        return 0;
    }
}