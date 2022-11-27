package projectGui;

import java.awt.Color;

import javax.swing.JFrame;


public class GUI {

	private static JFrame mainFrame = new JFrame();
	private static displayFrame mFrame;

	public static void main(String[] args) {
		mFrame = new displayFrame(mainFrame);
		
	}
	
	

	
}