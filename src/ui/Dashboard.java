package ui;


import java.awt.Color;


import javax.swing.*;
import java.awt.Font;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;

	/**
	 * Create the panel.
	 */
	public Dashboard() {
		
		setBackground(new Color(255, 255, 255));
		setLayout(null);
		
		table = new JTable();
		table.setBackground(new Color(128, 0, 0));
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"User ID", "UserName", "Email", "Contact"},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
			},
			new String[] {
				"New column", "New column", "New column", "New column"
			}
		));
		table.setBounds(29, 326, 341, 201);
		add(table);
		
		JPanel panel = new JPanel();
		panel.setBounds(29, 27, 308, 255);
		add(panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(351, 27, 308, 255);
		add(panel_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(697, 27, 308, 255);
		add(panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(415, 305, 459, 255);
		add(panel_3);
	}
}
