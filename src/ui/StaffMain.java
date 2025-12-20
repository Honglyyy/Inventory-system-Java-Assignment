package ui;

import javax.swing.*;
import java.awt.*;

public class StaffMain extends JFrame {

    private JPanel contentPanel;

    public StaffMain() {
        setTitle("Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // MAIN LAYOUT
        setLayout(new BorderLayout());

        // ===================== HEADER =====================
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(1000, 80));
        header.setBackground(new Color(230, 230, 230));

        
        JLabel logo = new JLabel("LOGO");
        logo.setHorizontalAlignment(SwingConstants.LEFT);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(logo, BorderLayout.WEST);

        // Company name center
        JLabel companyName = new JLabel("My Company Name");
        companyName.setFont(new Font("Arial", Font.BOLD, 24));
        companyName.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(companyName, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===================== LEFT MENU (SIDEBAR) =====================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(10, 1, 0, 5));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(245, 245, 245));

        JButton btnDashboard = new JButton("Dashboard");
//        JButton btnUser = new JButton("Manage User");
        JButton btnSupplier = new JButton("Manage Supplier");
        JButton btnCategory = new JButton("Manage Category");
        JButton btnProduct = new JButton("Manage Product");
        JButton btnOrder = new JButton("Manage Order");
        JButton btnPurchase = new JButton("Manage Purchase");
        JButton btnCustomers = new JButton("Manage Customers");
        JButton btnStock = new JButton("Stock movement");
        JButton btnReports = new JButton("Report");

        sidebar.add(btnDashboard);
//        sidebar.add(btnUser);
        sidebar.add(btnSupplier);
        sidebar.add(btnCategory);
        sidebar.add(btnProduct);
        sidebar.add(btnOrder);
        sidebar.add(btnPurchase);
        sidebar.add(btnCustomers);
        sidebar.add(btnStock);
        sidebar.add(btnReports);

        add(sidebar, BorderLayout.WEST);

        // ===================== CONTENT AREA =====================
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());   // so it can load other panels
        contentPanel.setBackground(Color.WHITE);

        add(contentPanel, BorderLayout.CENTER);

        // ===================== FOOTER =====================
        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(1000, 40));
        footer.setBackground(new Color(230, 230, 230));

        footer.add(new JLabel("© 2025 My Company. All rights reserved."));
        add(footer, BorderLayout.SOUTH);

        // ===================== BUTTON ACTIONS =====================
        btnDashboard.addActionListener(e -> setContent(new Dashboard()));
//        btnUser.addActionListener(e -> setContent(new ManageUser()));
        btnSupplier.addActionListener(e -> setContent(new ManageSupplier()));
        btnCategory.addActionListener(e -> setContent(new ManageCategory()));
        btnProduct.addActionListener(e -> setContent(new ManageProduct()));
        btnOrder.addActionListener(e -> setContent(new ManageOrder()));
        btnPurchase.addActionListener(e -> setContent(new ManagePurchase()));
        btnCustomers.addActionListener(e -> setContent(new ManageCustomers()));
        btnReports.addActionListener(e -> setContent(new Report()));

        setVisible(true);
    }

    // This loads a new panel into the content area
    public void setContent(JComponent component) {
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

//    public static void main(String[] args) {
//        new StaffMain();
//    }
}
