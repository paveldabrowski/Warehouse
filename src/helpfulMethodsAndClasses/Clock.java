package helpfulMethodsAndClasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Clock extends JFrame implements ActionListener{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JLabel clock = new JLabel(getTime());
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		clock.setText(getTime());
	}	

	public static String getTime() {
		GregorianCalendar calendar = new GregorianCalendar();
		String hour =  "" + calendar.get(Calendar.HOUR_OF_DAY);
		String min = "" + calendar.get(Calendar.MINUTE);
		String sec = "" + calendar.get(Calendar.SECOND);
		
		if (Integer.parseInt(hour) < 10)
			hour = "0" + hour;
		if (Integer.parseInt(min) < 10)
			min = "0" + min;
		if (Integer.parseInt(sec) < 10)
			sec = "0" + sec;
		return ""+ hour + " : " + min + " : " + sec + "  ";
	}
	
	public JLabel getClock () {
		return clock;
	}
}
