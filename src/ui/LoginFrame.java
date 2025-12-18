package ui;

import dao.UsersDAO;
import models.Users;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameTF;
    private JPasswordField passwordPF;

    public LoginFrame() {
        setTitle("Inventory Manager - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);                // Frame size 500x500
        setLocationRelativeTo(null);      // Center on screen
        setResizable(false);              // Prevent resizing

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // ===== TITLE PANEL =====
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 10, 10, 10));

        JLabel title = new JLabel("Inventory Manager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Professional Inventory Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Color.GRAY);

        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitle);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;  // Makes fields stretch full width

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(usernameLabel, gbc);

        // Username field
        gbc.gridy++;
        usernameTF = new JTextField();
        usernameTF.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameTF.setPreferredSize(new Dimension(400, 45));  // Longer field
        formPanel.add(usernameTF, gbc);

        // Password label
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(passwordLabel, gbc);

        // Password field
        gbc.gridy++;
        passwordPF = new JPasswordField();
        passwordPF.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordPF.setPreferredSize(new Dimension(400, 45)); // Longer field
        formPanel.add(passwordPF, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));

        JButton btnLogin = new JButton("Sign In");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(400, 50)); // Full-width button

        buttonPanel.add(btnLogin);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== LOGIN ACTION =====
        btnLogin.addActionListener(e -> login());

        // Allow Enter key to submit
        passwordPF.addActionListener(e -> login());
    }

    private void login() {
        String username = usernameTF.getText().trim();
        String password = new String(passwordPF.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        UsersDAO usersDAO = new UsersDAO();
        Users user = usersDAO.login(username, password);

        if (user != null) {
            if(user.getRole().equals("admin")){
                JOptionPane.showMessageDialog(
                        this,
                        "Welcome " + user.getUsername(),
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
                new AdminMain();
                dispose();
            }
            else if(user.getRole().equals("staff")){
                JOptionPane.showMessageDialog(
                        this,
                        "Welcome " + user.getUsername(),
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
                new StaffMain();
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            // Clear password field for security
            passwordPF.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}