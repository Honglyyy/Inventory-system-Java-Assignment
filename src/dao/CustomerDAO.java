package dao;

import models.Customer;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public Customer getCustomer(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        Customer cus = new Customer();
        try {
            con = DBConn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                int cusId = rs.getInt("customer_id");
                String cusName = rs.getString("customer_name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                cus.setCustomerId(cusId);
                cus.setCustomerName(cusName);
                cus.setCustomerPhone(phone);
                cus.setCustomerAddress(address);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cus;
    }

    public List<Customer> getAllCustomer() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                int cusId = rs.getInt("customer_id");
                String cusName = rs.getString("customer_name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");

                list.add(new Customer(cusId, cusName, phone, address));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void insertCustomer(Customer cus) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO customers (customer_name, phone, address) VALUES (?, ?, ?)";

        try {
            con = DBConn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cus.getCustomerName());
            ps.setString(2, cus.getCustomerPhone());
            ps.setString(3, cus.getCustomerAddress());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Insert failed: No rows affected");
            }

            JOptionPane.showMessageDialog(null, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error adding Customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new SQLException(e);
        } finally {
            DBConn.closePreparedStatement(ps);
            DBConn.closeConnection(con);
        }
    }

    public void updateCustomer(Customer cus) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "UPDATE customers SET customer_name = ?, phone = ?, address = ? WHERE customer_id = ?";
        try {
            con = DBConn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cus.getCustomerName());
            ps.setString(2, cus.getCustomerPhone());
            ps.setString(3, cus.getCustomerAddress());
            ps.setInt(4, cus.getCustomerId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Update failed: No rows affected");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            throw new SQLException(e);
        } finally {
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }

    public void deleteCustomer(Customer cus) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        // ❌ BUG WAS HERE: "user_id" should be "customer_id"
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try {
            con = DBConn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, cus.getCustomerId());

            ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            throw new SQLException(e);
        } finally {
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }

    /**
     * Search customers by any field
     */
    public List<Customer> searchCustomers(String searchTerm) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_id LIKE ? OR customer_name LIKE ? OR phone LIKE ? OR address LIKE ?";

        try {
            con = DBConn.getConnection();
            ps = con.prepareStatement(sql);

            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);

            rs = ps.executeQuery();

            while (rs.next()) {
                int cusId = rs.getInt("customer_id");
                String cusName = rs.getString("customer_name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");

                list.add(new Customer(cusId, cusName, phone, address));
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        }

        return list;
    }
}