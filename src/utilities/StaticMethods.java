package utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JButton;

public interface StaticMethods {

	/**
	 * Set font for buttons from ArrayList
	 * @param componentList
	 * @param fontName
	 * @param fontStyle
	 * @param fontSize
	 */
	static void setButtonsFont(ArrayList<JButton> componentList, String fontName, int fontStyle, int fontSize) {
		ArrayList<JButton> result = componentList;
		for (int i = 0; i < componentList.size(); i++)
			componentList.get(i).setFont(new Font(fontName, fontStyle, fontSize));		
	}
	
	/**
	 * Set sizefont for buttons from ArrayList
	 * @param componentList
	 * @param width
	 * @param height
	 */
	static void setButtonsSize(ArrayList<JButton> componentList, int width, int height) {
		ArrayList<JButton> result = componentList;
		for(int i = 0; i < componentList.size(); i++ )
			componentList.get(i).setPreferredSize(new Dimension(width, height));		
	}
	
	/**
	 * Set background color for buttons from ArrayList
	 * @param componentList
	 * @param componentColor
	 */
	static void setButtonsColor(ArrayList<JButton> componentList, Color componentColor) {
		ArrayList<JButton> result = componentList;
		for(int i = 0; i < componentList.size(); i++ ) {			
				componentList.get(i).setBackground(componentColor);			
		}		
	}
	
	/**
	 * Set font color for in buttons from ArrayList
	 * @param componentList
	 * @param textColor
	 */
	static void setButtonsTextColor(ArrayList<JButton> componentList, Color textColor) {
		ArrayList<JButton> result = componentList;
		for(int i = 0; i < componentList.size(); i++ ) {
			componentList.get(i).setForeground(textColor);			
		}		
	}
}
