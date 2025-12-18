package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Report extends JPanel {

    public Report() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Orders Report", orderReportPanel());
        tabs.addTab("Stock Report", stockReportPanel());
        tabs.addTab("Purchase Report", purchaseReportPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // =====================================================
    // ORDERS REPORT
    // =====================================================
    private JPanel orderReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable table = new JTable(new DefaultTableModel(
                new String[]{
                        "Order ID", "Customer", "Order Date",
                        "Total Amount", "Payment Method", "Status"
                }, 0
        ));
        table.setRowHeight(26);

        panel.add(reportHeader(
                () -> exportExcel(table, "orders"),
                () -> exportPdf(table, "orders")
        ), BorderLayout.NORTH);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // STOCK REPORT
    // =====================================================
    private JPanel stockReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable table = new JTable(new DefaultTableModel(
                new String[]{
                        "Product Code", "Product Name", "Category",
                        "Quantity On Hand", "Reserved", "Reorder Point", "Status"
                }, 0
        ));
        table.setRowHeight(26);

        panel.add(reportHeader(
                () -> exportExcel(table, "stock"),
                () -> exportPdf(table, "stock")
        ), BorderLayout.NORTH);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // PURCHASE REPORT
    // =====================================================
    private JPanel purchaseReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable table = new JTable(new DefaultTableModel(
                new String[]{
                        "PO ID", "Supplier", "Order Date",
                        "Received Date", "Total Amount", "Payment Status"
                }, 0
        ));
        table.setRowHeight(26);

        panel.add(reportHeader(
                () -> exportExcel(table, "purchase"),
                () -> exportPdf(table, "purchase")
        ), BorderLayout.NORTH);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // REPORT HEADER (FILTER + EXPORT BUTTONS)
    // =====================================================
    private JPanel reportHeader(Runnable excelAction, Runnable pdfAction) {
        JPanel panel = new JPanel(new BorderLayout());

        // ---- EXPORT BUTTONS ----
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton excelBtn = new JButton("Export Excel");
        JButton pdfBtn = new JButton("Export PDF");

        excelBtn.addActionListener(e -> excelAction.run());
        pdfBtn.addActionListener(e -> pdfAction.run());

        actions.add(excelBtn);
        actions.add(pdfBtn);
        panel.add(actions, BorderLayout.EAST);

        return panel;
    }

    // =====================================================
    // EXPORT STUBS (CONNECT LATER)
    // =====================================================
    private void exportExcel(JTable table, String reportType) {
        JOptionPane.showMessageDialog(
                this,
                "Exporting " + reportType + " report to Excel...\n(Connect Apache POI here)",
                "Export Excel",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void exportPdf(JTable table, String reportType) {
        JOptionPane.showMessageDialog(
                this,
                "Exporting " + reportType + " report to PDF...\n(Connect iText/OpenPDF here)",
                "Export PDF",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
