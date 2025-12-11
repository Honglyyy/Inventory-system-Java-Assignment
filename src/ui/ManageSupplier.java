package ui;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

public class ManageSupplier extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTable table;

	public static void main(String[] args) {
	    JFrame frame = new JFrame("Manage Supplier");
	    frame.setContentPane(new ManageSupplier());
	    frame.setSize(560, 600);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
	
	/**
	 * Create the panel.
	 */
	public ManageSupplier() {
		setBackground(new Color(174, 207, 244));
		setLayout(null);
		
		JLabel lblManagesupplier = new JLabel("MANAGE SUPPLIER");
		lblManagesupplier.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblManagesupplier.setBounds(178, 25, 162, 31);
		add(lblManagesupplier);
		
		JLabel lblSuppliername = new JLabel("Supplier Name:");
		lblSuppliername.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSuppliername.setBounds(42, 76, 96, 22);
		add(lblSuppliername);
		
		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEmail.setBounds(42, 109, 59, 22);
		add(lblEmail);
		
		JLabel lblPhone = new JLabel("Phone:");
		lblPhone.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPhone.setBounds(42, 142, 59, 22);
		add(lblPhone);
		
		JLabel lblAddresss = new JLabel("Address:");
		lblAddresss.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAddresss.setBounds(42, 175, 59, 22);
		add(lblAddresss);
		
		JLabel lblCountry = new JLabel("Country:");
		lblCountry.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCountry.setBounds(42, 208, 59, 22);
		add(lblCountry);
		
		textField = new JTextField();
		textField.setBounds(160, 79, 276, 20);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(160, 112, 276, 20);
		add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(160, 145, 276, 20);
		add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(160, 178, 276, 20);
		add(textField_3);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(160, 211, 276, 20);
		add(textField_4);
		
		JButton btnAddsupplier = new JButton("Add Supplier");
		btnAddsupplier.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\a.png"));
		btnAddsupplier.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAddsupplier.setBounds(20, 263, 131, 27);
		add(btnAddsupplier);
		
		JButton btnUpdateSupplier = new JButton("Update Supplier");
		btnUpdateSupplier.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\up.png"));
		btnUpdateSupplier.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnUpdateSupplier.setBounds(161, 263, 152, 27);
		add(btnUpdateSupplier);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\delete_bin_16px.png"));
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDelete.setBounds(323, 263, 96, 27);
		add(btnDelete);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\clear_symbol_16px.png"));
		btnClear.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnClear.setBounds(429, 263, 94, 27);
		add(btnClear);
		
		JLabel lblSupplierTable = new JLabel("Supplier Table");
		lblSupplierTable.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblSupplierTable.setBounds(188, 301, 116, 31);
		add(lblSupplierTable);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 343, 503, 201);
		add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Supplier ID", "Name", "Email", "Phone", "Address", "Country"
			}
		));
		scrollPane.setViewportView(table);
		
	}

}
