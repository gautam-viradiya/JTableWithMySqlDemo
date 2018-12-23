// Swing JTable demo with my sql
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

class JTableDemo {
	JFrame f;
	JPanel p1, p2, p3;
	JTabbedPane tp;

	JLabel l1, l2, l3;
	JTextField tf1, tf2, tf3;
	JScrollPane sp1;
	JButton savebtn, resetbtn, editbtn;

	JTableDemo() {
		f = new JFrame("Form");
		p1 = new JPanel(new GridLayout(5, 2));
		p2 = new JPanel(new GridLayout(5, 2));

		tp = new JTabbedPane();
		l1 = new JLabel("Id");
		l2 = new JLabel("Name");
		l3 = new JLabel("City");

		tf1 = new JTextField(12);
		tf2 = new JTextField(12);
		tf3 = new JTextField(12);

		savebtn = new JButton(" Add ");
		resetbtn = new JButton(" Reset");
		editbtn = new JButton(" Display Data ");

		p1.add(l1);
		p1.add(tf1);
		p1.add(l2);
		p1.add(tf2);
		p1.add(l3);
		p1.add(tf3);

		p1.add(savebtn);
		p1.add(resetbtn);

		p2.add(editbtn);

		resetbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				clear();
			}
		});
		savebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int id;
				String name, city;
				id = Integer.parseInt(tf1.getText());
				name = tf2.getText();
				city = tf3.getText();

				String url = "jdbc:mysql://localhost:3307/mydb";
				String userid = "root";
				String password = "root";
				try {
					Connection connection = DriverManager.getConnection(url,
							userid, password);
					Statement st = connection.createStatement();

					if (name != "" && city != "") {
						st.executeUpdate("insert into emp values('" + id
								+ "','" + name + "','" + city + "')");
						System.out.println("Insert record successfully");
						clear();
					} else {
						System.out.println("Please fill up textfield");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		editbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				TableFromMySqlDatabase frame = new TableFromMySqlDatabase();
				// frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	void dis() {
		f.getContentPane().add(tp);
		tp.addTab("Add Record", p1);
		tp.addTab("Edit/Delete Record", p2);

		f.setSize(350, 180);
		f.setVisible(true);
		f.setResizable(true);
	}

	void clear() {
		tf1.setText("");
		tf2.setText("");
		tf3.setText("");
	}

	public static void main(String z[]) {
		JTableDemo data = new JTableDemo();
		data.dis();
	}
}

class TableFromMySqlDatabase extends JFrame {
	public TableFromMySqlDatabase() {
		Vector columnNames = new Vector();
		Vector data = new Vector();

		// Connect to an MySQL Database, run query, get result set
		String url = "jdbc:mysql://localhost:3307/mydb";
		String userid = "root";
		String password = "root";
		String sql = "SELECT * FROM emp";

		try {
			Connection connection = DriverManager.getConnection(url, userid,
					password);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();

			// Get column names
			for (int i = 1; i <= columns; i++) {
				columnNames.add(md.getColumnName(i));
			}

			// Get row data
			while (rs.next()) {
				Vector row = new Vector(columns);

				for (int i = 1; i <= columns; i++) {
					row.add(rs.getObject(i));
				}
				row.add("Delete");
				row.add("Update");
				data.add(row);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		columnNames.add("Action1");
		columnNames.add("Action2");
		// Create table with database data
		JTable table = new JTable(data, columnNames) {
			public Class getColumnClass(int column) {
				for (int row = 0; row < getRowCount(); row++) {
					Object o = getValueAt(row, column);

					if (o != null) {
						return o.getClass();
					}
				}
				return Object.class;
			}
		};

		table.getColumn("Action1").setCellRenderer(new ButtonRenderer());
		table.getColumn("Action1").setCellEditor(
				new ButtonEditor(new JCheckBox(), table));
		table.getColumn("Action2").setCellRenderer(new ButtonRenderer());
		table.getColumn("Action2").setCellEditor(
				new ButtonEditor(new JCheckBox(), table));

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane);

		JPanel buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	/*
	 * public static void main(String[] args) { TableFromMySqlDatabase frame =
	 * new TableFromMySqlDatabase();
	 * frame.setDefaultCloseOperation(EXIT_ON_CLOSE); frame.pack();
	 * frame.setVisible(true); }
	 */

}

class ButtonRenderer extends JButton implements TableCellRenderer {
	public ButtonRenderer() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		setText((value == null) ? "" : value.toString());
		return this;
	}
}

class ButtonEditor extends DefaultCellEditor {
	protected JButton button;

	private String label;
	JTable t1;
	private boolean isPushed;
	int index;
	String name, city;

	public ButtonEditor(JCheckBox checkBox, JTable t1) {
		super(checkBox);
		this.t1 = t1;
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		index = Integer.parseInt(t1.getModel().getValueAt(row, 0).toString());
		name = t1.getModel().getValueAt(row, 1).toString();
		city = t1.getModel().getValueAt(row, 2).toString();

		label = (value == null) ? "" : value.toString();
		button.setText(label);
		isPushed = true;
		return button;
	}

	// update, delete
	public Object getCellEditorValue() {
		if (isPushed) {
			String url = "jdbc:mysql://localhost:3307/mydb";
			String userid = "root";
			String password = "root";

			try {
				Connection connection = DriverManager.getConnection(url,
						userid, password);
				Statement stmt = connection.createStatement();

				if (label.equalsIgnoreCase("Update")) {
					stmt
							.executeUpdate("update emp set name='" + name
									+ "', city='" + city + "' where id='"
									+ index + "'");

					JOptionPane.showMessageDialog(button, label
							+ ":Update recoed success");

				} else if (label.equalsIgnoreCase("Delete")) {
					stmt.executeUpdate("delete from emp where id='" + index
							+ "'");

					JOptionPane.showMessageDialog(button, label
							+ ":Delete record success");

				}

			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
		isPushed = false;
		return new String(label);
	}

	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}