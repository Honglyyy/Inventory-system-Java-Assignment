package dao;

import models.Users;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    public Users login(String username, String password) {
        // Fixed SQL query - removed the unused role parameter
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    Users user = new Users();
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            throw new RuntimeException("Database error during login", e);
        }
    }

    public Users getUser(int id){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Users user = new Users();
        try{
            con = DBConn.getConnection();
            ps =  con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if(rs.next()) {
                int  userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password_hash = rs.getString("password_hash");
                String role = rs.getString("role");
                user.setUser_id(userId);
                user.setUsername(username);
                user.setPassword(password_hash);
                user.setRole(role);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);;
        }
        return user;
    }
    public List<Users> getAllUsers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Users> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try{
            conn = DBConn.getConnection();
            ps =  conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()) {
                int userID = rs.getInt("user_id");
                String username =  rs.getString("username");
                String password = rs.getString("password_hash");
                String role = rs.getString("role");

                list.add(new Users(userID,username, password, role));
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        return list;
    }

    public void insertUser(Users user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try{
            con = DBConn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            int rowsAffected = ps.executeUpdate();

            if(rowsAffected == 0){
                throw new SQLException("Insert failed: No rows affected");
            }

            JOptionPane.showMessageDialog(null, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error adding user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        finally {
            DBConn.closePreparedStatement(ps);
            DBConn.closeConnection(con);
        }
    }
    public void updateUser(Users user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "UPDATE `users` SET `username`= ?,`password_hash`= ? ,`role`= ? WHERE user_id=?";
        try{
            con = DBConn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getUser_id());

            int rowsAffected = ps.executeUpdate();

            if(rowsAffected == 0){
                throw new SQLException("Update failed: No rows affected");
            }

        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        finally {
            ps.close();
            con.close();
        }
    }

    public void deleteUser(Users user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "DELETE FROM `users` WHERE user_id=?";

        try{
            con = DBConn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, user.getUser_id());

            ps.executeUpdate();
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        finally {
            ps.close();
            con.close();
        }
    }
}