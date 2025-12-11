package ui;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;

public class ManageUser extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTable table;
	
	
	public static void main(String[] args) {
	    JFrame frame = new JFrame("Manage User");
	    frame.setContentPane(new ManageUser());
	    frame.setSize(550, 600);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}

	/**
	 * Create the panel.
	 */
	public ManageUser() {
		setBackground(new Color(174, 207, 244));
		setLayout(null);
		
		JLabel lblMaageuser = new JLabel("MANAGE USER");
		lblMaageuser.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblMaageuser.setBounds(202, 30, 108, 32);
		add(lblMaageuser);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUsername.setBounds(72, 73, 86, 14);
		add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPassword.setBounds(72, 111, 66, 14);
		add(lblPassword);
		
		JLabel lblRole = new JLabel("Role:");
		lblRole.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblRole.setBounds(72, 155, 48, 14);
		add(lblRole);
		
		textField = new JTextField();
		textField.setBounds(168, 73, 191, 20);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(168, 111, 191, 20);
		add(textField_1);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Admin", "Manager", "Staff"}));
		comboBox.setBounds(168, 154, 189, 22);
		add(comboBox);
		
		JButton btnAdduser = new JButton("Add User");
		btnAdduser.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\a.png"));
		btnAdduser.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAdduser.setBounds(22, 224, 108, 37);
		add(btnAdduser);
		
		JButton btnUpdateUser = new JButton("Update User");
		btnUpdateUser.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\up.png"));
		btnUpdateUser.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnUpdateUser.setBounds(140, 224, 129, 37);
		add(btnUpdateUser);
		
		JButton btnDeleteUser = new JButton("Delete User");
		btnDeleteUser.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\delete_bin_16px.png"));
		btnDeleteUser.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDeleteUser.setBounds(279, 224, 129, 37);
		add(btnDeleteUser);
		
		JButton btnClear = new JButton("Clear ");
		btnClear.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\clear_symbol_16px.png"));
		btnClear.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnClear.setBounds(418, 224, 89, 37);
		add(btnClear);
		
		JLabel lblUserTable = new JLabel("User Table");
		lblUserTable.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblUserTable.setBounds(207, 291, 89, 32);
		add(lblUserTable);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 334, 473, 176);
		add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Username", "Role"
			}
		));
		scrollPane.setViewportView(table);
		
	}
}
