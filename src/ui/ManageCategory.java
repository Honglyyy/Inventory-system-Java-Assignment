package ui;

import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;

public class ManageCategory extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField txtCategoryName;
	private JTable tblManageCategory;

	/**
	 * Create the panel.
	 */
	public ManageCategory() {
		setBackground(new Color(230, 230, 250));
		setLayout(null);
		
		JLabel lblManageCategory = new JLabel("MANAGE CATEGORY");
		lblManageCategory.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblManageCategory.setBounds(195, 24, 203, 19);
		add(lblManageCategory);
		
		JLabel lblCategoryName = new JLabel("Category Name :");
		lblCategoryName.setBackground(new Color(255, 0, 0));
		lblCategoryName.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblCategoryName.setBounds(69, 65, 122, 26);
		add(lblCategoryName);
		
		txtCategoryName = new JTextField();
		txtCategoryName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtCategoryName.setBounds(205, 62, 195, 36);
		add(txtCategoryName);
		txtCategoryName.setColumns(10);
		
		JButton btnAddCategory = new JButton("Add Category");
		btnAddCategory.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnAddCategory.setBounds(29, 132, 135, 36);
		btnAddCategory.setBackground(new Color(46, 204, 113)); 
		add(btnAddCategory);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.setBackground(new Color(255, 255, 255));
		btnUpdate.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnUpdate.setBounds(187, 132, 106, 36);
		btnUpdate.setBackground(new Color(52, 152, 219)); 
		add(btnUpdate);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnDelete.setBounds(313, 132, 106, 36);
		btnDelete.setBackground(new Color(231, 76, 60)); 
		add(btnDelete);
		
		JButton btnClearManageCategory = new JButton("Clear");
		btnClearManageCategory.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnClearManageCategory.setBounds(435, 132, 106, 36);
		btnClearManageCategory.setBackground(new Color(149, 165, 166));
		add(btnClearManageCategory);
		
		tblManageCategory = new JTable();
		tblManageCategory.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
			}
			public void ancestorMoved(AncestorEvent event) {
			}
			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		tblManageCategory.setModel(new DefaultTableModel(
			new Object[][] {
				{"Name"},
				{null},
				{null},
			},
			new String[] {
				"Name"
			}
		));
		tblManageCategory.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tblManageCategory.setBounds(105, 212, 359, 138);
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setHorizontalAlignment(JLabel.CENTER);
		tblManageCategory.getColumnModel().getColumn(0).setHeaderRenderer(headerRenderer);
		add(tblManageCategory);
	}
}
