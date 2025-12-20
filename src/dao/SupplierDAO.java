package dao;

import models.Supplier;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public Supplier get(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Supplier supplier = null;
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try{
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if(rs.next()){
                supplier = new Supplier();
                int supplierId = rs.getInt("supplier_id");
                String supplierName = rs.getString("supplier_name");
                String supplierEmail = rs.getString("email");
                String supplierPhone = rs.getString("phone");
                String supplierAddress = rs.getString("address");

                supplier.setSupplierId(supplierId);
                supplier.setSupplierName(supplierName);
                supplier.setSupplierEmail(supplierEmail);
                supplier.setSupplierPhone(supplierPhone);
                supplier.setSupplierAddress(supplierAddress);
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e);
        }
        finally{
            rs.close();
            ps.close();
            conn.close();
        }
        return supplier;

    }

    public List<Supplier> getAll() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Supplier> suppliers = new ArrayList<>();

        try{
            conn = DBConn.getConnection();
            String sql =  "SELECT * FROM suppliers";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                int supplierId = rs.getInt("supplier_id");
                String supplierName = rs.getString("supplier_name");
                String supplierEmail = rs.getString("email");
                String supplierPhone = rs.getString("phone");
                String supplierAddress = rs.getString("address");

                Supplier supplier = new Supplier(supplierId, supplierName, supplierEmail, supplierPhone, supplierAddress);
                suppliers.add(supplier);
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        finally{
            ps.close();
            rs.close();
            conn.close();
        }

        return suppliers;
    }

    public void insertSupplier(Supplier supplier) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = DBConn.getConnection();
            String sql = "INSERT INTO suppliers (supplier_name, email, phone, address   ) VALUES ( ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getSupplierEmail());
            ps.setString(3, supplier.getSupplierPhone());
            ps.setString(4, supplier.getSupplierAddress());

           int rowAffected =  ps.executeUpdate();

           if(rowAffected == 0){
               throw new SQLException("Insert supplier failed");
           }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        finally{
            ps.close();
            conn.close();
        }
    }

    public void updateSupplier(Supplier supplier) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "UPDATE suppliers SET supplier_name = ? , email = ? , phone = ?  , address = ? WHERE supplier_id = ? ";

        try{
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getSupplierEmail());
            ps.setString(3, supplier.getSupplierPhone());
            ps.setString(4, supplier.getSupplierAddress());
            ps.setInt(5, supplier.getSupplierId());

            int rowAffected = ps.executeUpdate();

            if(rowAffected == 0){
                throw new SQLException("Update failed: No rows affected.");
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        finally {
            ps.close();
            conn.close();
        }
    }

    public void deleteSupplier(Supplier supplier) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try{
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, supplier.getSupplierId());

            ps.executeUpdate();
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        finally {
            ps.close();
            conn.close();
        }
    }
}
