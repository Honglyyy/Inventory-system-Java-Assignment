package ui;

import java.awt.Color;


import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ManageProduct extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTable table;
	
	
	public static void main(String[] args) {
	    JFrame frame = new JFrame("Manage Product");
	    frame.setContentPane(new ManageProduct());
	    frame.setSize(600, 720);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}

	/**
	 * Create the panel.
	 */
	public ManageProduct() {
		setBackground(new Color(174, 207, 244));
		setLayout(null);
		
		JLabel lblManageProduct = new JLabel("MANAGE PRODUCT");
		lblManageProduct.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblManageProduct.setBounds(228, 22, 141, 29);
		add(lblManageProduct);
		
		JLabel lblProductCode = new JLabel("Product Code:");
		lblProductCode.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblProductCode.setBounds(41, 62, 93, 29);
		add(lblProductCode);
		
		JLabel lblPrductName = new JLabel("Prduct Name:");
		lblPrductName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPrductName.setBounds(313, 62, 79, 29);
		add(lblPrductName);
		
		JLabel lblCategory = new JLabel("Category:");
		lblCategory.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblCategory.setBounds(41, 91, 65, 29);
		add(lblCategory);
		
		JLabel lblSupplier = new JLabel("Supplier:");
		lblSupplier.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblSupplier.setBounds(313, 91, 65, 29);
		add(lblSupplier);
		
		JLabel lblUnitPrice = new JLabel("Unit Price:");
		lblUnitPrice.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblUnitPrice.setBounds(41, 120, 65, 29);
		add(lblUnitPrice);
		
		JLabel lblCostPrice = new JLabel("Cost Price:");
		lblCostPrice.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblCostPrice.setBounds(313, 120, 65, 29);
		add(lblCostPrice);
		
		JLabel lblExpirationDate = new JLabel("Expiration Date:");
		lblExpirationDate.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblExpirationDate.setBounds(41, 152, 109, 29);
		add(lblExpirationDate);
		
		JLabel lblProductLocation = new JLabel("Product Location:");
		lblProductLocation.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblProductLocation.setBounds(41, 181, 109, 29);
		add(lblProductLocation);
		
		JLabel lblWarehouseZone = new JLabel("Warehouse Zone:");
		lblWarehouseZone.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblWarehouseZone.setBounds(160, 181, 109, 29);
		add(lblWarehouseZone);
		
		JLabel lblAisle = new JLabel("Aisle:");
		lblAisle.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblAisle.setBounds(395, 181, 47, 29);
		add(lblAisle);
		
		JLabel lblShelf = new JLabel("Shelf:");
		lblShelf.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblShelf.setBounds(160, 206, 65, 29);
		add(lblShelf);
		
		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblDescription.setBounds(41, 238, 79, 29);
		add(lblDescription);
		
		textField = new JTextField();
		textField.setBounds(144, 67, 140, 20);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(407, 67, 139, 20);
		add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(144, 125, 140, 20);
		add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(407, 125, 139, 20);
		add(textField_3);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(144, 157, 402, 20);
		add(textField_4);
		
		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setBounds(273, 186, 96, 20);
		add(textField_5);
		
		textField_6 = new JTextField();
		textField_6.setColumns(10);
		textField_6.setBounds(429, 186, 96, 20);
		add(textField_6);
		
		textField_7 = new JTextField();
		textField_7.setColumns(10);
		textField_7.setBounds(273, 215, 96, 20);
		add(textField_7);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(144, 95, 140, 22);
		add(comboBox);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(407, 95, 139, 22);
		add(comboBox_1);
		
		textField_8 = new JTextField();
		textField_8.setColumns(10);
		textField_8.setBounds(144, 243, 402, 20);
		add(textField_8);
		
		JButton btnUploadImage = new JButton("Upload Img");
		btnUploadImage.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\image_16px.png"));
		btnUploadImage.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnUploadImage.setBounds(41, 299, 132, 40);
		add(btnUploadImage);
		
		JButton btnAddproduct = new JButton("Add Product");
		btnAddproduct.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\a.png"));
		btnAddproduct.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAddproduct.setBounds(41, 366, 140, 35);
		add(btnAddproduct);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\update_left_rotation_24px.png"));
		btnUpdate.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnUpdate.setBounds(191, 366, 132, 35);
		add(btnUpdate);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\delete_bin_16px.png"));
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDelete.setBounds(333, 366, 109, 35);
		add(btnDelete);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\clear_symbol_16px.png"));
		btnClear.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnClear.setBounds(454, 366, 92, 35);
		add(btnClear);
		
		JLabel lblProductTable = new JLabel("Product Table");
		lblProductTable.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblProductTable.setBounds(228, 409, 114, 29);
		add(lblProductTable);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(41, 453, 505, 193);
		add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Code", "Name", "Category", "Supplier", "Price", "Stock", "Zone", "Aisle", "Shelf"
			}
		));
		scrollPane.setViewportView(table);
		
	}

}
