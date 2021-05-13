package menu;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import dbConnectionAndMethods.DB;
import user.User;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

public class ManageEmployees extends JPanel {
	
	private JPanel panel;
	private JPanel panelSouth;
	protected User user;
	
	private JTable tableCenter;

	private JScrollPane scrollNorth;
	
	private JPanel panelSouthUp;
	private JMenuBar menuBarSouth;
	
	private JButton addUser = new JButton("Add new user");
	private JButton deleteUser = new JButton("Delete user");
	
	private JTextField userNameTextField = new JTextField();
	private JTextField userLastNameTextField = new JTextField();
	private JTextField userLoginTextField = new JTextField();
	private JTextField userPasswordTextField = new JTextField();	
	
	private JLabel userNameLabel = new JLabel(" Name: ");
	private JLabel userLastNameLabel = new JLabel(" Lastname: ");
	private JLabel userLoginLabel = new JLabel(" Login: ");
	private JLabel userPasswordLabel = new JLabel(" Password: ");
	private JLabel accessLegend = new JLabel(" Permissions: ");
	
	private JComboBox<String> accessCombobox = new JComboBox<String>();
	private ArrayList<Integer> permissionsID;
	
	
	public ManageEmployees() {
		
		initComponents();
	}
	
	public ManageEmployees(User user) {
		this.user = user;
		
		initComponents();
	}
		
	private void initComponents() {
		panel = this;
		panel.setLayout(new BorderLayout());
		addUser.setForeground(Color.BLACK); 			 addUser.setFont(new Font("Tahoma", Font.BOLD, 13));
		addUser.setIcon(new ImageIcon("img/Apply.png"));
		deleteUser.setForeground(Color.GREEN); 			 deleteUser.setFont(new Font("Tahoma", Font.PLAIN, 13));
		deleteUser.setIcon(new ImageIcon("img/Delete.png"));
		initializePanel();
		actionForComponents();
	}
	
	private void actionForComponents() {
		
		userPasswordTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					addUserButtonMethod();
				}
			}
		});
		
		addUser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addUserButtonMethod();
			}
		});	
		
		deleteUser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tableCenter.getSelectedRow();
				if (selectedRow != -1 ) {
					int idColumn = tableCenter.getColumnModel().getColumnIndex("id");
					String clientID = String.valueOf(tableCenter.getValueAt(selectedRow, idColumn));
					System.out.println("User id: " + clientID);
					
					try(Connection conn = DB.getConnection()){
						PreparedStatement statement = conn.prepareStatement("DELETE FROM users WHERE id = ?");
						statement.setString(1, clientID);
						statement.executeUpdate();
						JOptionPane.showMessageDialog(panel, "User successfully deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
						
						refreshTable();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(panel, "Selct user which you want to delete.", "user not selected", JOptionPane.WARNING_MESSAGE);
				}				
			}
		});		
	}

	protected void addUserButtonMethod() {
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
				String access = accessCombobox.getSelectedItem().toString().substring(0, 1);
				PreparedStatement statement = conn.prepareStatement("INSERT INTO users (name, lastName, YWN, login, password, access) VALUES (?,?,?,?,?,?)");
				statement.setString(1, userNameTextField.getText());
				statement.setString(2, userLastNameTextField.getText());
				statement.setString(3, String.valueOf(new User().getYWN()));
				statement.setString(4, userLoginTextField.getText());
				statement.setString(5, userPasswordTextField.getText());
				statement.setString(6, access);
	
				
				statement.executeUpdate();
				
				refreshTable();
				JOptionPane.showMessageDialog(panel, "New user added to library.", "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e1) {
//				System.out.println(e1.getMessage());
				JOptionPane.showMessageDialog(panel, e1.getMessage(), "Fail", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e2) {						
				e2.printStackTrace();
			}			
		} else {
			JOptionPane.showMessageDialog(panel, "Fill all textfields. User must be full described.", "Blanck textfields", JOptionPane.ERROR_MESSAGE);
		}					
	}			

	private void initializePanel() {		
		
		tableCenter = createTable();
		scrollNorth = new JScrollPane(tableCenter);		
		
		panelSouth = new JPanel();
		panelSouth.setLayout(new BorderLayout());
		
		panelSouthUp = new JPanel();
		panelSouthUp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		getPermissionsToAccessCombobox();
		
		panelSouthUp.add(userNameLabel);
		panelSouthUp.add(userNameTextField);
		panelSouthUp.add(userLastNameLabel);
		panelSouthUp.add(userLastNameTextField);
		panelSouthUp.add(userLoginLabel);
		panelSouthUp.add(userLoginTextField);		
		panelSouthUp.add(userPasswordLabel);
		panelSouthUp.add(userPasswordTextField);
		panelSouthUp.add(accessCombobox);

		
		for(int i=0; i < panelSouthUp.getComponentCount(); i++) {
			Class compClass = panelSouthUp.getComponent(i).getClass();
			if(compClass == JTextField.class) {
				((JTextField)panelSouthUp.getComponent(i)).setPreferredSize(new Dimension(80, addUser.getPreferredSize().height));
			}
		}
		
		panelSouthUp.add(addUser);
		
		menuBarSouth = new JMenuBar();
		menuBarSouth.add(deleteUser);
		
		panelSouth.add(panelSouthUp, BorderLayout.NORTH);
		panelSouth.add(menuBarSouth, BorderLayout.CENTER);
		
		panel.add(scrollNorth, BorderLayout.CENTER);
		panel.add(panelSouth,BorderLayout.SOUTH);
		panel.add(accessLegend,BorderLayout.PAGE_START);
		
	}

	private void getPermissionsToAccessCombobox() {

		try(Connection conn = DB.getConnection()){
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM permissions ORDER BY `id` ASC");
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			permissionsID = new ArrayList<Integer>();
			
			while (rs.next()) {
				String item = "";
				permissionsID.add(rs.getInt("id"));
				for(int i = 1; i <= metaData.getColumnCount(); i++ ) {
					if(i > 1) {
						item += " - " + rs.getString(i);						
					} else
						item += rs.getString(i);
				}
				accessCombobox.addItem(item);
				accessLegend.setText(accessLegend.getText() + item + " | ");
			}
			accessCombobox.setSelectedItem("4 - worker");
			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}

	private JTable createTable() {
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("no.");
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		try(Connection conn = DB.getConnection()){
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM users");
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
					int no = columnNames.indexOf("no.");
					int id = columnNames.indexOf("id");
					int ywn = columnNames.indexOf("YWN");
					return column == no || column == id || column == ywn ? false : true;
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
					int column = e.getColumn();
					if(!tableCenter.getColumnName(column).equals("access")) {
						DB.updateDbFromTableCell(tableCenter, "users", e);
						refreshTable();									
					} else {
						int row = e.getFirstRow();
						Object data = tableCenter.getValueAt(row, column);

						if (permissionsID.contains((int)data)) {
							DB.updateDbFromTableCell(tableCenter, "users", e);
							refreshTable();	
						} else {
							JOptionPane.showMessageDialog(panel, "Wrong access number typed. Choose from existing permissions.", "Fail", JOptionPane.ERROR_MESSAGE);
							refreshTable();	
						}									
					}
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
					map.put("name", "Pawe³");
					map.put("lastName", "D¹browski");
					map.put("id", "4");
					map.put("login", "d");
					map.put("YWN", "20205999");
					map.put("password", "d");
					map.put("access", "admin");
					
					Menu menu = new Menu(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
