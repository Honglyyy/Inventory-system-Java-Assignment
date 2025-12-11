package ui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultComboBoxModel;
import java.awt.TextArea;
import javax.swing.ImageIcon;

public class ManagePurchase extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTable table;
	private JTextField textField_3;

	
	public static void main(String[] args) {
	    JFrame frame = new JFrame("Manage Purchase");
	    frame.setContentPane(new ManagePurchase());
	    frame.setSize(740, 470);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
	/**
	 * Create the panel.
	 */
	public ManagePurchase() {
		setBackground(new Color(174, 207, 244));
		setLayout(null);
		
		JLabel lblManagepurchase = new JLabel("MANAGE PURCHASE");
		lblManagepurchase.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblManagepurchase.setBounds(243, 11, 167, 32);
		add(lblManagepurchase);
		
		JLabel lblSupplier = new JLabel("Supplier:");
		lblSupplier.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblSupplier.setBounds(29, 97, 82, 22);
		add(lblSupplier);
		
		JLabel lblOrderdate = new JLabel("Order Date:");
		lblOrderdate.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblOrderdate.setBounds(29, 130, 82, 22);
		add(lblOrderdate);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblStatus.setBounds(29, 163, 63, 22);
		add(lblStatus);
		
		JLabel lblNote = new JLabel("Note:");
		lblNote.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNote.setBounds(29, 196, 63, 22);
		add(lblNote);
        
		
		JLabel lblProduct = new JLabel("Product:");
		lblProduct.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblProduct.setBounds(29, 240, 63, 22);
		add(lblProduct);
		
		JLabel lblQtyOrdered = new JLabel("Qty Ordered:");
		lblQtyOrdered.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblQtyOrdered.setBounds(29, 273, 82, 22);
		add(lblQtyOrdered);
		
		JLabel lblCost = new JLabel("Cost:");
		lblCost.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblCost.setBounds(29, 306, 63, 22);
		add(lblCost);
		
		textField = new JTextField();
		textField.setBounds(109, 130, 161, 22);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(109, 275, 161, 20);
		add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(109, 308, 161, 20);
		add(textField_2);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(109, 97, 161, 22);
		add(comboBox);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"draft", "sent", "confirmed", "received"}));
		comboBox_1.setBounds(109, 163, 161, 22);
		add(comboBox_1);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setBounds(109, 241, 161, 22);
		add(comboBox_2);
		
		JButton btnAddItem = new JButton("Add Item");
		btnAddItem.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\add_16px.png"));
		btnAddItem.setForeground(new Color(255, 0, 0));
		btnAddItem.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAddItem.setBounds(125, 361, 126, 32);
		add(btnAddItem);
		
		JButton btnSavePo = new JButton("Save PO");
		btnSavePo.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\save1.png"));
		btnSavePo.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSavePo.setBounds(574, 97, 129, 32);
		add(btnSavePo);
		
		JButton btnUpdatePo = new JButton("Update PO");
		btnUpdatePo.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\up.png"));
		btnUpdatePo.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnUpdatePo.setBounds(574, 140, 129, 32);
		add(btnUpdatePo);
		
		JButton btnDeletePo = new JButton("Delete PO");
		btnDeletePo.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\delete_bin_16px.png"));
		btnDeletePo.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDeletePo.setBounds(574, 183, 129, 32);
		add(btnDeletePo);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setIcon(new ImageIcon("C:\\Users\\User\\Pictures\\clear_symbol_16px.png"));
		btnClear.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnClear.setBounds(574, 226, 129, 32);
		add(btnClear);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(300, 98, 264, 297);
		add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Product", "QTY", "Cost"
			}
		));
		scrollPane.setViewportView(table);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(109, 197, 161, 33);
		add(textField_3);
		
	}
}
