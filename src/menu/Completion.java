package menu;

import db.DB;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

public class Completion extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JPanel panelNorth;
	private JPanel panelSouth;
	private JTable tableNorth;
	protected JTable tableSouth;
	private JScrollPane scrollNorth;
	protected JScrollPane scrollSouth;

	private JButton beginCompletion = new JButton("Begin completion");
	private JButton okButton = new JButton("OK");
	private JButton finishCompletion = new JButton("Finish");
	
	private JLabel amountCompletedLabel = new JLabel(" Amount completed: ");
	protected JLabel noLabel = new JLabel();
	private JLabel volumeLabel = new JLabel();
	private JLabel valueLabel = new JLabel();
	private JLabel pcsLabel = new JLabel();
	private JLabel client = new JLabel(" Client: ");
	private JLabel client2 = new JLabel();
	private JLabel order = new JLabel(" | order: ");
	private JLabel order2 = new JLabel();
	
	private JMenuBar menuBarNorth;
	private JMenuBar menuBarSouthDown;
	
	private JTextField amountCompleted = new JTextField();
	private JMenuBar menuBarSouthUp;
	protected HashMap<String, Object> orderParameters;
	
	/**
	 * Setting basic configuration for main JPanel, buttons appearance and invoking initComponents() method.
	 * Main panel is shared for South Panel and North Panel.
	 */
	public Completion() {
		panel = this;
		panel.setLayout(new GridLayout(2,1,0,0));
		okButton.setForeground(Color.GREEN); 			 okButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		okButton.setIcon(new ImageIcon("img/Good mark.png"));
		beginCompletion.setForeground(Color.GREEN); 			 beginCompletion.setFont(new Font("Tahoma", Font.PLAIN, 13));
		beginCompletion.setIcon(new ImageIcon("img/Down.png"));
		finishCompletion.setForeground(Color.GREEN); 			 finishCompletion.setFont(new Font("Tahoma", Font.PLAIN, 13));
		finishCompletion.setIcon(new ImageIcon("img/Apply.png"));
		amountCompleted.setToolTipText("Integer only.");
				
		initComponents();
	}
	
	/**
	 * Invokes {link {@link #initializeNorthPanel()}} method and {link {@link #actionsForComponents()}} method.
	 */
	private void initComponents() {
		initializeNorthPanel();
		actionsForComponents();
	}
	
	/**
	 * Configures south panel
	 */
	private void initializeSouthPanel() {
		panelSouth = new JPanel();
		panelSouth.setLayout(new BorderLayout());
		scrollSouth = new JScrollPane(tableSouth);
		panelSouth.add(scrollSouth, BorderLayout.CENTER);
		
		menuBarSouthDown = new JMenuBar();
		menuBarSouthUp = new JMenuBar();
		
		menuBarSouthUp.add(client);
		menuBarSouthUp.add(client2);
		menuBarSouthUp.add(order);
		menuBarSouthUp.add(order2);
		
		amountCompleted.setMaximumSize(new Dimension(100,20));
		
		menuBarSouthDown.add(amountCompletedLabel);
		menuBarSouthDown.add(amountCompleted);
		menuBarSouthDown.add(okButton);
		menuBarSouthDown.add(finishCompletion);
		menuBarSouthDown.add(noLabel);
		menuBarSouthDown.add(pcsLabel);
		menuBarSouthDown.add(volumeLabel);
		menuBarSouthDown.add(valueLabel);
		
		panelSouth.add(menuBarSouthUp, BorderLayout.NORTH);	
		panelSouth.add(menuBarSouthDown, BorderLayout.SOUTH);
		panel.add(panelSouth);

		tableSouth.setRowSelectionInterval(0, 0);
		
		amountCompleted.setText(String.valueOf(tableSouth.getValueAt(tableSouth.getSelectedRow(), 12)));
		amountCompleted.requestFocus();
		noLabel.setText(" Selected product number: " + tableSouth.getValueAt(tableSouth.getSelectedRow(), 0));	
		client2.setText(String.valueOf(tableNorth.getValueAt(tableNorth.getSelectedRow(), 1)));
		order2.setText(String.valueOf(tableNorth.getValueAt(tableNorth.getSelectedRow(), 2)));
				
		tableSouth.getSelectionModel().addListSelectionListener(e -> {
			noLabel.setText(" Selected product number: " + tableSouth.getValueAt(tableSouth.getSelectedRow(), 0));
			amountCompleted.setText(String.valueOf(tableSouth.getValueAt(tableSouth.getSelectedRow(), 12)));
			amountCompleted.requestFocus();
		});
		
		tableSouth.getModel().addTableModelListener(e -> {
			orderParameters = new HashMap<>();
			double volume = 0;
			double value = 0;
			double pcs = 0;

			for(int i = 0; i < tableSouth.getRowCount(); i++) {
				volume += Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 7))) * Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 13)));
				value += Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 11))) * Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 13)));
				pcs += Double.parseDouble(String.valueOf(tableSouth.getValueAt(i, 13)));
			}

			orderParameters.put("volume", volume);
			orderParameters.put("value", value);
			orderParameters.put("pcs", pcs);

			volumeLabel.setText(", Volume: " + volume);
			valueLabel.setText(", Value: " + String.format("%.2f", value));
			pcsLabel.setText(", Pcs: " + pcs);
		});
	}

	/**
	 * Configures North Panel
	 */
	private void initializeNorthPanel() {		
		
		tableNorth = new ManageOrders().getTableNorth();
		tableNorth.getTableHeader().setReorderingAllowed(false);
		tableNorth.setAutoCreateRowSorter(true);
		tableNorth.getColumnModel().getColumn(0).setMaxWidth(40);
		panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
		
		scrollNorth = new JScrollPane(tableNorth);
		
		menuBarNorth = new JMenuBar();
		menuBarNorth.add(beginCompletion);		
		
		panelNorth.add(scrollNorth, BorderLayout.CENTER);
		panelNorth.add(menuBarNorth, BorderLayout.SOUTH);
				
		panel.add(panelNorth);
	}

	/**
	 * Configures actions for particular components
	 */
	private void actionsForComponents() {
		
		finishCompletion.addActionListener(e -> finalizeCompletion());
		
		beginCompletion.addActionListener(e -> {
			try {
				panel.remove(panelSouth);
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
			}

			int rowSelected = tableNorth.getSelectedRow();

			String order;
			if(rowSelected == -1) {
				tableNorth.setRowSelectionInterval(0, 0);
				order = String.valueOf(tableNorth.getValueAt(0, 2));
			} else {
				order = String.valueOf(tableNorth.getValueAt(rowSelected, 2));
			}

			Vector<String> columnNames = new Vector<>();
			columnNames.add("no.");
			Vector<Vector<Object>> data = new Vector<>();

			try {
				Connection conn = DB.getConnection();
				PreparedStatement statement = conn.prepareStatement("SELECT lib.id, lib.name, lib.manufacturer, lib.`country of production`, lib.weight, lib.unit, lib.`volume (dm3)`" +
						", lib.barcode, p.purchase_price, p.`markup(%)`, p.sell_price, p.amount_ordered, p.amount_completed FROM productlist p JOIN productlibrary lib ON p.product_id=lib.id WHERE order_id=?");
				statement.setString(1, order);
				ResultSet rs = statement.executeQuery();

				ResultSetMetaData colNames = rs.getMetaData();
				for(int i = 1; i <= colNames.getColumnCount(); i++) {
					columnNames.add(colNames.getColumnName(i));
				}
				columnNames.add("status");

				int i = 1;
				while(rs.next()) {
					Vector<Object> vector = new Vector<>();
					vector.add(i++);
					for(int j = 1; j <= colNames.getColumnCount(); j++) {
						vector.add(rs.getObject(j));
					}
					vector.add(Boolean.FALSE);
					data.add(vector);
				}
				conn.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNames) {

				@Override
				public boolean isCellEditable(int row, int column) {
					return column <= 0;
				}

				@Override
				public Class<?> getColumnClass(int column) {
					Class<?> retVal = Object.class;

					if(getRowCount() > 0)
						retVal =  getValueAt(0, column).getClass();

					return retVal;
				}
			};

			tableSouth = new JTable(defaultTableModel);
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
			tableSouth.getTableHeader().setReorderingAllowed(false);
			tableSouth.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
			tableSouth.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
			tableSouth.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
			tableSouth.getColumnModel().getColumn(2).setPreferredWidth(190);

			tableSouth.getColumnModel().getColumn(0).setMaxWidth(40);
			tableSouth.getColumnModel().getColumn(1).setMaxWidth(40);
			tableSouth.getColumnModel().getColumn(14).setMaxWidth(40);
			tableSouth.getColumnModel().getColumn(6).setMaxWidth(40);

			initializeSouthPanel();
		});
		
		amountCompleted.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if(!(e.getKeyChar()>= '0' && e.getKeyChar() <= '9'))
					e.consume();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButtonMethod();
				}
			}
		});
		
		okButton.addActionListener(e -> okButtonMethod());
	}
	
	/**
	 * method for okButton
	 */
	private void okButtonMethod() {
		int row = tableSouth.getSelectedRow();
		double valueFromTextField = 0;
	
		if(!(amountCompleted.getText().equals(""))) {
			valueFromTextField = Double.parseDouble((amountCompleted.getText()));
			}
	
		if (valueFromTextField == 0) {
			int choise = JOptionPane.showConfirmDialog(panel, "Are you sure this product isn't in warehouse?", "Not a single one.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if(choise == 0) {
				tableSouth.setValueAt(valueFromTextField, row, 13);
				tableSouth.setValueAt(Boolean.TRUE, row, 14);				
				if(tableSouth.getSelectedRow()+1 <= tableSouth.getRowCount()-1) {
					tableSouth.setRowSelectionInterval(tableSouth.getSelectedRow()+1, tableSouth.getSelectedRow()+1);
				}
			} else {
				amountCompleted.setText("");
				amountCompleted.requestFocus();
			}
		} else if (valueFromTextField > Double.parseDouble(String.valueOf(tableSouth.getValueAt(row, 12)))) {
			JOptionPane.showMessageDialog(panel, "Client did not order that much.", "Too many pcs.", JOptionPane.WARNING_MESSAGE);
			amountCompleted.requestFocus();
		} else {
			tableSouth.setValueAt(valueFromTextField, row, 13);
			tableSouth.setValueAt(Boolean.TRUE, row, 14);						
			if(tableSouth.getSelectedRow()+1 <= tableSouth.getRowCount()-1) {
				tableSouth.setRowSelectionInterval(tableSouth.getSelectedRow()+1, tableSouth.getSelectedRow()+1);
			}
		}				
	}
		
	private void finalizeCompletion() {
		
		boolean flag = true;
		for(int i = 0; i < tableSouth.getRowCount(); i++) {
			if(!((Boolean) tableSouth.getValueAt(i, 14))) {
				flag = false;
			}
		}
		
		if(flag){
			for(int i = 0; i < tableSouth.getRowCount(); i++) {
				String id = String.valueOf(tableSouth.getValueAt(i, 1));
				double completedAmount = (double) tableSouth.getValueAt(i, 13);
				double orderedAmount = (double) tableSouth.getValueAt(i, 12);				
				
				try {	
					Connection conn = DB.getConnection();
					if(completedAmount == orderedAmount) {
						PreparedStatement statement = conn.prepareStatement("UPDATE productlist SET amount_completed = ? WHERE product_id = ? AND order_id = ?");
						statement.setString(1, String.valueOf(completedAmount));
						statement.setString(2, id);
						statement.setString(3, String.valueOf(order2.getText()));
						statement.executeUpdate();
						System.out.println("Completed amount for product id: " + id + ", order id: " + order2.getText() +  " updated in product list");
						
						PreparedStatement statement2 = conn.prepareStatement("UPDATE orders SET status = true, percent_complete = 100 WHERE order_id = ?");
						statement2.setString(1, String.valueOf(order2.getText()));
						statement2.executeUpdate();					
						refreshNorthTable();
						System.out.println("Only set status to `true` in orders");
						
						conn.close();

					} else if(completedAmount != orderedAmount) {
						
						PreparedStatement statement = conn.prepareStatement("UPDATE productlist SET amount_completed = ? WHERE product_id = ? AND order_id = ?");
						statement.setString(1, String.valueOf(completedAmount));
						statement.setString(2, id);
						statement.setString(3, String.valueOf(order2.getText()));
						statement.executeUpdate();
						System.out.println("Completed amount for product id: " + id + ", order id: " + order2.getText() + " updated in product list");
														
						double difference = orderedAmount - completedAmount;
						
						PreparedStatement statement2 = conn.prepareStatement("SELECT amount, reserved FROM productlibrary WHERE id = ?"); 
						statement2.setString(1, id);
						ResultSet rs = statement2.executeQuery();
						double amountFromDB = 0;
						double reservedFromDB = 0;
																	
						while(rs.next()) {
							amountFromDB = rs.getDouble("amount");	
							reservedFromDB = rs.getDouble("reserved");
						}
						amountFromDB = amountFromDB + difference;
						reservedFromDB = reservedFromDB - difference;
						
						PreparedStatement statement3 = conn.prepareStatement("UPDATE productlibrary SET amount = ?, reserved = ? WHERE id = ?");
						statement3.setString(1, String.valueOf(amountFromDB));
						statement3.setString(2, String.valueOf(reservedFromDB));
						statement3.setString(3, id);
						statement3.executeUpdate();
						System.out.println("Reserved updated in stock.");
						
						PreparedStatement statement4 = conn.prepareStatement("UPDATE orders SET volume = ?, pcs = ?, value = ?, status = ? WHERE order_id = ?");
						statement4.setString(1, String.valueOf(orderParameters.get("volume")));
						statement4.setString(2, String.valueOf(orderParameters.get("pcs")));
						statement4.setString(3, String.valueOf(orderParameters.get("value")));
						statement4.setString(4, String.valueOf(1));
						statement4.setString(5, String.valueOf(order2.getText()));
						statement4.executeUpdate();
						
						conn.close();						
												
						System.out.println("Orders updated");
						refreshNorthTable();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			JOptionPane.showMessageDialog(panel, "Order successfuly completed.", "Finished", JOptionPane.INFORMATION_MESSAGE);	
			panelSouth.removeAll();
			panelSouth.revalidate();
			panelSouth.repaint();
		} else {
			JOptionPane.showMessageDialog(panel, "There is one or more products not completed.", "Not all product completed", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	protected void refreshNorthTable() {
		try {
			panelNorth.remove(scrollNorth);
			tableNorth = new ManageOrders().getTableNorth();
			scrollNorth = new JScrollPane(tableNorth);
			panelNorth.add(scrollNorth, BorderLayout.CENTER);
			panelNorth.revalidate();
			panelNorth.repaint();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				TreeMap<String,String> map = new TreeMap<>();
				map.put("name", "Pawe�");
				map.put("lastName", "D�browski");
				map.put("id", "4");
				map.put("login", "d");
				map.put("YWN", "20205999");
				map.put("password", "d");
				map.put("access", "1");

				Menu menu = new Menu(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
