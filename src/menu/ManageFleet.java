package menu;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.Vector;

public class ManageFleet extends JPanel {
	
	private JPanel panel;
	private JPanel panelSouth;
	private JPanel panelSouthUp;
	
	private JTable tableCenter;
	private JScrollPane scroll;
	private JMenuBar menuBarSouthDown;
		
	private JButton deleteTruck = new JButton("Delete truck");
	private JButton addTruck = new JButton("Add new truck");
		
	private JLabel brandLabel = new JLabel("Brand: ");
	private JLabel modelLabel = new JLabel("Model: ");
	private JLabel yearLabel = new JLabel("Year: ");
	private JLabel registationNumberLabel = new JLabel("Registation number: ");
	private JLabel palletsLabel = new JLabel("Pallets capacity: ");
	private JLabel permissibleLoadLabel = new JLabel("Permissible load (kg): ");
	
	private JTextField brandTextField = new JTextField();
	private JTextField modelTextField = new JTextField();
	private JComboBox<Integer> yearCombobox = new JComboBox<Integer>();
	private JTextField registarationNumberTextField = new JTextField();
	private JTextField palletsTextField = new JTextField();
	private JTextField permissibleLoadTextField = new JTextField();

	public ManageFleet() {
		panel = this;
		panel.setLayout(new BorderLayout());
				
		initComponents();
	}
	
	private void initComponents() {
		deleteTruck.setForeground(Color.GREEN); 			 deleteTruck.setFont(new Font("Tahoma", Font.BOLD, 13));
		deleteTruck.setIcon(new ImageIcon("img/Delete.png"));
//		addTruck.setForeground(Color.GREEN); 			 addTruck.setFont(new Font("Tahoma", Font.PLAIN, 13));
		addTruck.setIcon(new ImageIcon("img/Apply.png"));
		
		Calendar calendar = Calendar.getInstance();	
		int currentYear = calendar.get(Calendar.YEAR);
		for(int i = 1980; i <= currentYear ; i++) {			
			yearCombobox.addItem(i);
		}
		yearCombobox.setSelectedItem(currentYear);
		
		initializeNorthPanel();
		
		actionsForComponents();
	}

	private void initializeNorthPanel() {
		panelSouth = new JPanel();
		panelSouth.setLayout(new BorderLayout());
		
		tableCenter = createCenterTable();
	
		scroll = new JScrollPane(tableCenter);
		
		panelSouthUp = new JPanel();
		panelSouthUp.setLayout(new FlowLayout());
		FlowLayout flowLayout = (FlowLayout) panelSouthUp.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);		
		
		panelSouthUp.add(brandLabel);
		panelSouthUp.add(brandTextField);
		panelSouthUp.add(modelLabel);
		panelSouthUp.add(modelTextField);
		panelSouthUp.add(yearLabel);
		panelSouthUp.add(yearCombobox);
		panelSouthUp.add(registationNumberLabel);
		panelSouthUp.add(registarationNumberTextField);
		panelSouthUp.add(palletsLabel);
		panelSouthUp.add(palletsTextField);
		panelSouthUp.add(permissibleLoadLabel);
		panelSouthUp.add(permissibleLoadTextField);
		panelSouthUp.add(addTruck);
		
		for(int i = 0; i < panelSouthUp.getComponentCount(); i++) {
			if(panelSouthUp.getComponent(i).getClass() == JTextField.class) {
				((JTextField) panelSouthUp.getComponent(i)).setPreferredSize(new Dimension(90, addTruck.getPreferredSize().height));
			}
		}
		
		
		menuBarSouthDown = new JMenuBar();
		menuBarSouthDown.add(deleteTruck);			
		
		panelSouth.add(panelSouthUp, BorderLayout.NORTH);
		panelSouth.add(menuBarSouthDown, BorderLayout.CENTER);
		
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(panelSouth, BorderLayout.SOUTH);
	}

	private void actionsForComponents() {
		
		addTruck.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				truckAddingMethod();
			}
		});
		
		permissibleLoadTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(!(e.getKeyChar()>= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '.'))
					e.consume();			
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_ENTER)
					truckAddingMethod();
			}			
		});
		
		palletsTextField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(!(e.getKeyChar()>= '0' && e.getKeyChar() <= '9'))
					e.consume();
			
			}			
		});	
		
		deleteTruck.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tableCenter.getSelectedRow();
				
				if(selectedRow != -1) {
					int truckId = (int) tableCenter.getValueAt(selectedRow, 1);
					try {
						Connection conn = DB.getConnection();
						PreparedStatement statement = conn.prepareStatement("DELETE FROM fleet WHERE truck_id=?");
						statement.setString(1, String.valueOf(truckId));
						statement.executeUpdate();
						conn.close();
						
						JOptionPane.showMessageDialog(panel, "Truck deleted from library", "Success", JOptionPane.INFORMATION_MESSAGE);		
						refreshCenterTable();
						
					} catch (Exception e1) {						
						e1.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(panel, "Select truck to remove.", "Truck not selected", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
	}
	
	protected void truckAddingMethod() {
		boolean flag = true;		
		for(int i = 0; i < panelSouthUp.getComponentCount(); i++) {
			JComponent component = (JComponent) panelSouthUp.getComponent(i);
			if(component.getClass() == JTextField.class) {
				int textFieldLength = ((JTextField) component).getText().length();
				if(textFieldLength == 0) {					
					flag = false;
				}				
			}
		}
		
		if(flag) {
			try {
				String model = modelTextField.getText();
				String brand = brandTextField.getText();
				int year = (int) yearCombobox.getSelectedItem();
				String registrationNumber = registarationNumberTextField.getText();
				int pallets = Integer.parseInt(palletsTextField.getText());
				double capacity = pallets * 1589.76; capacity = Math.round(capacity * 100.0) / 100.0;
				double permissibleLoad = Double.parseDouble(permissibleLoadTextField.getText());
				
				Connection conn = DB.getConnection();
				
				
				PreparedStatement statement = conn.prepareStatement("INSERT INTO fleet(model, brand, year, registration_number, capacity, permissible_load, pallets) VALUES (?,?,?,?,?,?,?)");
				statement.setString(1, model);
				statement.setString(2, brand);
				statement.setString(3, String.valueOf(year));
				statement.setString(4, registrationNumber);
				statement.setString(5, String.valueOf(capacity));
				statement.setString(6, String.valueOf(permissibleLoad));
				statement.setString(7, String.valueOf(pallets));
				statement.executeUpdate();
				conn.close();
				
				System.out.println("Added new truck");
				JOptionPane.showMessageDialog(panel, "Truck successfuly added to fleet.", "Success", JOptionPane.INFORMATION_MESSAGE);
										
				refreshCenterTable();
			} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e1) {
				JOptionPane.showMessageDialog(panel, "This registration number already exists in fleet database", "Duplicate registration number", JOptionPane.WARNING_MESSAGE);
			} catch (Exception e) {				
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(panel, "Truck has to be full described. Fill all textfields.", "Missing data", JOptionPane.WARNING_MESSAGE);
		}
		
	}
	

	private JTable createCenterTable() {
		
		try {
			Connection conn = DB.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT truck_id, model, brand, year, registration_number, capacity, permissible_load, pallets FROM fleet");
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("no.");
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				columnNames.add(metaData.getColumnName(i));
			}
			
			int j = 1;
			while(rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				vector.add(j++);
				for(int i = 1; i <= metaData.getColumnCount(); i++) {
					vector.add(rs.getObject(i));
				}
				data.add(vector);
			}			
						
			tableCenter = new JTable(data, columnNames);
			
			tableCenter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
			tableCenter.setAutoCreateRowSorter(true);
			tableCenter.getTableHeader().setReorderingAllowed(false);
						
			int truckID = tableCenter.getColumn("truck_id").getModelIndex();
			int no = tableCenter.getColumn("no.").getModelIndex();
			int capacity = tableCenter.getColumn("capacity").getModelIndex();
			int pallets = tableCenter.getColumn("pallets").getModelIndex();
			int permissibleLoad = tableCenter.getColumn("permissible_load").getModelIndex();
			int year = tableCenter.getColumn("year").getModelIndex();
			
			DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNames) {
				
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				
				@Override
				public Class<?> getColumnClass(int columnIndex) {
							
					Class retVal = Object.class;

			        if(getRowCount() > 0)
			            retVal =  getValueAt(0, columnIndex).getClass();

			        return retVal;
				}						
			};
			DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
			DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
			cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
						
			tableCenter.setModel(defaultTableModel);
			tableCenter.setAutoCreateRowSorter(true);			
			
			tableCenter.getColumnModel().getColumn(no).setCellRenderer(cellRenderer);
			tableCenter.getColumnModel().getColumn(truckID).setCellRenderer(cellRenderer);
			tableCenter.getColumnModel().getColumn(year).setCellRenderer(leftRenderer);			
			
			tableCenter.getColumnModel().getColumn(no).setMaxWidth(40);
			tableCenter.getColumnModel().getColumn(truckID).setMaxWidth(60);
									
			conn.close();
			
			return tableCenter;
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;		
	}
	
	public void refreshCenterTable(){
		
		try {
			panel.remove(scroll);
		} catch (Exception e) {	}
		
		tableCenter = createCenterTable();
		scroll = new JScrollPane(tableCenter);
		
		panel.add(scroll, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TreeMap<String,String> map = new TreeMap<String, String>();
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
			}
		});

	}
}
