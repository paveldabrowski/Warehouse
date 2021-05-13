package menu;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StockForOrder {
	
	public JTable stockTable;
	
	
	public StockForOrder() {
		
		
		String[] columnNames = {"#", "Number"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0)
		{
		    //  Returning the Class of each column will allow different
		    //  renderers and editors to be used based on Class
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				// TODO Auto-generated method stub
				return columnIndex == 0 ? Boolean.class : Integer.class;
			}
//			
//		    public Class getColumnClass(int column)
//		    {
//		        return column == 0 ? Boolean.class : Integer.class;
//		    }
		};
		
		for (Integer i = 1; i < 6; i++) 
		{
		    Object[] row = {Boolean.FALSE, i};
		    model.addRow( row);
		} 
		stockTable = new JTable(model);
		stockTable.getTableHeader().setReorderingAllowed(false);
		
	
		
//		JFrame frame = new JFrame();
//		frame.setBounds(0, 0, 1000, 1000);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
//		
//		
//		JScrollPane scroll = new JScrollPane(stockTable);
//		frame.add(scroll);
		
	}
	public static void main(String[] args) {
//		new StockForOrder();
	}
}