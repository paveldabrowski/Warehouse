package menu;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import db.DB;
import user.User;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.TreeMap;
import java.util.Vector;

public class ManageClients extends JPanel {
	
	private JPanel panel;
	private JPanel panelSouth;
	protected User user;
	
	private JTable tableCenter;

	private JScrollPane scrollNorth;
	
	private JPanel panelSouthUp;
	private JMenuBar menuBarSouth;
	
	private JButton addClient = new JButton("Add new client");
	private JButton deleteClient = new JButton("Delete client");
	
	private JTextField clientNameTextField = new JTextField();
	private JTextField clientZipCodeTextField = new JTextField();
	private JTextField clientStreetTextField = new JTextField();
	private JTextField clientCityTextField = new JTextField();
	private JTextField clientCountryTextField = new JTextField();
	private JTextField clientNipTextField = new JTextField();
	private JTextField clientPhoneNumberTextField = new JTextField();
	
	private JLabel clientNameLabel = new JLabel(" Name: ");
	private JLabel clientZipCodeLabel = new JLabel(" Zip code: ");
	private JLabel clientStreetLabel = new JLabel(" Street: ");
	private JLabel clientCityLabel = new JLabel(" City: ");
	private JLabel clientCountryLabel = new JLabel(" Country: ");
	private JLabel clientNipLabel = new JLabel(" NIP: ");
	private JLabel clientPhoneNumberLabel = new JLabel(" Phone number: ");

	public ManageClients() {
		
		initComponents();
	}
	
	public ManageClients(User user) {
		this.user = user;
		
		initComponents();
	}
		
	private void initComponents() {
		panel = this;
		panel.setLayout(new BorderLayout());
		addClient.setForeground(Color.BLACK); 			 addClient.setFont(new Font("Tahoma", Font.BOLD, 13));
		addClient.setIcon(new ImageIcon("img/Apply.png"));
		deleteClient.setForeground(Color.GREEN); 			 deleteClient.setFont(new Font("Tahoma", Font.PLAIN, 13));
		deleteClient.setIcon(new ImageIcon("img/Delete.png"));
		initializePanel();
		actionForComponents();
	}
	
	private void actionForComponents() {
		
		addClient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean flag = true;
				for(int i=0; i < panelSouthUp.getComponentCount(); i++) {
					Class compClass = panelSouthUp.getComponent(i).getClass();
					if(compClass == JTextField.class) {
						if(((JTextField)panelSouthUp.getComponent(i)).getText().length() == 0) {
							flag = false;										
						}
					}
				}
				
				if(flag) {
					try (Connection conn = DB.getConnection()) {
						PreparedStatement statement = conn.prepareStatement("INSERT INTO clients (name, street, zip_code, city, country, NIP, phone_number) VALUES (?,?,?,?,?,?,?)");
						statement.setString(1, clientNameTextField.getText());
						statement.setString(2, clientStreetTextField.getText());
						statement.setString(3, clientZipCodeTextField.getText());
						statement.setString(4, clientCityTextField.getText());
						statement.setString(5, clientCountryTextField.getText());
						statement.setString(6, clientNipTextField.getText());
						statement.setString(7, clientPhoneNumberTextField.getText());
						
						statement.executeUpdate();
						
						refreshTable();
						JOptionPane.showMessageDialog(panel, "New client added to library.", "Success", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e2) {						
						e2.printStackTrace();
					}			
				} else {
					JOptionPane.showMessageDialog(panel, "Fill all textfields. Client must be full described.", "Blanck textfields", JOptionPane.ERROR_MESSAGE);
				}					
			}
		});	
		
		deleteClient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tableCenter.getSelectedRow();
				if (selectedRow != -1 ) {
					int idColumn = tableCenter.getColumnModel().getColumnIndex("id");
					String clientID = String.valueOf(tableCenter.getValueAt(selectedRow, idColumn));
					System.out.println("Client id: " + clientID);
					
					try(Connection conn = DB.getConnection()){
						PreparedStatement statement = conn.prepareStatement("DELETE FROM clients WHERE id = ?");
						statement.setString(1, clientID);
						statement.executeUpdate();
						JOptionPane.showMessageDialog(panel, "Client successfully deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
						
						refreshTable();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(panel, "Selct client which you want to delete.", "Client not selected", JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
		
	}

	private void initializePanel() {		
		
		tableCenter = createTable();
		scrollNorth = new JScrollPane(tableCenter);		
		
		panelSouth = new JPanel();
		panelSouth.setLayout(new BorderLayout());
		
		panelSouthUp = new JPanel();
		panelSouthUp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		panelSouthUp.add(clientNameLabel);
		panelSouthUp.add(clientNameTextField);
		panelSouthUp.add(clientStreetLabel);
		panelSouthUp.add(clientStreetTextField);
		panelSouthUp.add(clientZipCodeLabel);
		panelSouthUp.add(clientZipCodeTextField);
		panelSouthUp.add(clientCityLabel);
		panelSouthUp.add(clientCityTextField);
		panelSouthUp.add(clientCountryLabel);
		panelSouthUp.add(clientCountryTextField);
		panelSouthUp.add(clientNipLabel);
		panelSouthUp.add(clientNipTextField);
		panelSouthUp.add(clientPhoneNumberLabel);
		panelSouthUp.add(clientPhoneNumberTextField);
		
		for(int i=0; i < panelSouthUp.getComponentCount(); i++) {
			Class compClass = panelSouthUp.getComponent(i).getClass();
			if(compClass == JTextField.class) {
				((JTextField)panelSouthUp.getComponent(i)).setPreferredSize(new Dimension(80, addClient.getPreferredSize().height));
			}
		}
		
		panelSouthUp.add(addClient);
		
		menuBarSouth = new JMenuBar();
		menuBarSouth.add(deleteClient);
		
		panelSouth.add(panelSouthUp, BorderLayout.NORTH);
		panelSouth.add(menuBarSouth, BorderLayout.CENTER);
		
		panel.add(scrollNorth, BorderLayout.CENTER);
		panel.add(panelSouth,BorderLayout.SOUTH);
		
	}

	private JTable createTable() {
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("no.");
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		try(Connection conn = DB.getConnection()){
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM clients");
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				columnNames.add(metaData.getColumnName(i));
			}
			int j = 1;
			while (rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				vector.add(j++);
				for(int i = 1; i <= metaData.getColumnCount(); i++) {
					vector.add(rs.getObject(i));
				}
				data.add(vector);
			}
			
			DefaultTableModel model = new DefaultTableModel(data, columnNames) {
				
				@Override
				public boolean isCellEditable(int row, int column) {					
					return column == 1 || column == 0 ? false : true;
				}
				
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					Class retVal = Object.class;

			        if(getRowCount() > 0)
			            retVal =  getValueAt(0, columnIndex).getClass();

			        return retVal;
				}
			};
			
			tableCenter = new JTable(model);
			
			tableCenter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
			tableCenter.setAutoCreateRowSorter(true);
			tableCenter.getTableHeader().setReorderingAllowed(false);			
			
			DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
			DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
			cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
			
			int no = tableCenter.getColumnModel().getColumnIndex("no.");			
			int id = tableCenter.getColumnModel().getColumnIndex("id");			
			
			tableCenter.getColumnModel().getColumn(no).setMaxWidth(40);
			tableCenter.getColumnModel().getColumn(no).setCellRenderer(cellRenderer);
			tableCenter.getColumnModel().getColumn(id).setMaxWidth(40);
			tableCenter.getColumnModel().getColumn(id).setCellRenderer(cellRenderer);
			
			tableCenter.getModel().addTableModelListener(new TableModelListener() {
				
				@Override
				public void tableChanged(TableModelEvent e) {
					DB.updateDbFromTableCell(tableCenter, "clients", e);
					refreshTable();									
				}
			});
			
			return tableCenter;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableCenter;
	}

	private void refreshTable() {
		panel.remove(scrollNorth);
		tableCenter = createTable();
		scrollNorth = new JScrollPane(tableCenter);
		panel.add(scrollNorth, BorderLayout.CENTER);
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
