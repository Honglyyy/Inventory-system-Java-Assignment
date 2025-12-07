package ui;

import java.awt.Color;
import javax.swing.border.*;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;

public class ManageCustomers extends JPanel  {

	private static final long serialVersionUID = 1L;
	private JTextField txtCustomerName;
	private JTextField txtEmail;
	private JTextField txtPhone;
	private JTextField txtAddress;
	private JTable tblManageCustomer;

	/**
	 * Create the panel.
	 */
	public ManageCustomers() {
		setBackground(new Color(240, 248, 255));
		setLayout(null);
		
		JLabel lblManageCustomer = new JLabel("MANAGE CUSTOMERS ");
		lblManageCustomer.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblManageCustomer.setBounds(197, 32, 238, 34);
		add(lblManageCustomer);
		
		JLabel lblCustomerName = new JLabel("Customer Name:");
		lblCustomerName.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblCustomerName.setBounds(73, 105, 138, 14);
		add(lblCustomerName);
		
		txtCustomerName = new JTextField();
		txtCustomerName.setBounds(216, 97, 230, 34);
		add(txtCustomerName);
		txtCustomerName.setColumns(10);
		
		JLabel lblEmail = new JLabel("Email: ");
		lblEmail.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblEmail.setBounds(150, 173, 57, 14);
		add(lblEmail);
		
		txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(216, 165, 230, 34);
		add(txtEmail);
		
		JLabel lblPhone = new JLabel("Phone: ");
		lblPhone.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblPhone.setBounds(150, 236, 57, 14);
		add(lblPhone);
		
		txtPhone = new JTextField();
		txtPhone.setColumns(10);
		txtPhone.setBounds(217, 228, 230, 34);
		add(txtPhone);
		
		JLabel lblAddress = new JLabel(" Address:");
		lblAddress.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAddress.setBounds(129, 305, 82, 14);
		add(lblAddress);
		
		txtAddress = new JTextField();
		txtAddress.setColumns(10);
		txtAddress.setBounds(216, 297, 230, 34);
		add(txtAddress);
		
		JButton btnAddCustomer = new JButton("Add Customer");
		btnAddCustomer.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnAddCustomer.setBounds(25, 364, 138, 34);
		btnAddCustomer.setBackground(new Color(46, 204, 113));
		add(btnAddCustomer);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnUpdate.setBounds(197, 364, 113, 34);
		   btnUpdate.setBackground(new Color(52, 152, 219));
		add(btnUpdate);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnDelete.setBounds(322, 364, 113, 34);
		btnDelete.setBackground(new Color(231, 76, 60));
		add(btnDelete);
		
		JButton btnClearManageCustomer = new JButton("Clear");
		btnClearManageCustomer.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnClearManageCustomer.setBounds(450, 364, 113, 34);
		btnClearManageCustomer.setBackground(new Color(149, 165, 166));
		add(btnClearManageCustomer);
		
		tblManageCustomer = new JTable();
		tblManageCustomer.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		tblManageCustomer.setModel(new DefaultTableModel(
			new Object[][] {
				{"Customer Name", "Email", "Phone", "Address"},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
			},
			new String[] {
				"New column", "New column", "New column", "New column"
			}
		));
		tblManageCustomer.setBounds(25, 420, 539, 116);
		add(tblManageCustomer);
	}
}
