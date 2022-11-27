package projectGui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.Border;

public class displayFrame {
	// instance of  frame 
	JFrame mFrame;
	
	// create all buttons required for GUI
	JButton bStart = new JButton();
	JButton bStop = new JButton();
	JButton bConnect = new JButton();
	

	private String title = "Welcome to PulseOximeter";
	private Color backgroundColor = Color.GRAY;
	public displayFrame(JFrame frame) {
		mFrame = frame;
		initFrame(mFrame);
		initButton(bConnect,20,100,"CONNECT");
		initButton(bStart,20,170,"START");
		initButton(bStop,20,240,"STOP");
		
	}
	
	public void initFrame(JFrame frame) {
		/*creates the frame of required size and colour
		 * returns frame as o/p*/
		frame.setVisible(true);
		frame.setTitle(title);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setSize(750,750 );
		frame.setLayout(null);
		frame.getContentPane().setBackground(backgroundColor);
	
		return;
		
	}

	public void initButton(JButton b, int x, int y, String bname ) {
		// modifies a button
		Border bBorder = BorderFactory.createLineBorder(Color.BLACK,1);
		mFrame.add(b);
		b.setBounds(x, y, 100, 50);
		b.setFocusable(false);
		b.setText(bname);
		b.setBackground(Color.LIGHT_GRAY);
		b.setBorder(bBorder);
	}
}
