package projectGui;


import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class displayFrame 
{
	
	private JFrame mainFrame;
	private JTextField cmnd_Field;
	private JPanel Graph_panel, Readings_panel;
	private JTextPane tout_textPane;

	
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					displayFrame window = new displayFrame();
					window.mainFrame.setVisible(true);
				}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
			
	});
	}
		
	public displayFrame() {
		initialize();
	}
	
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setTitle("Pulseoximeter GUI");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(100, 100, 850, 600);
		SpringLayout springLayout =  new SpringLayout();
		mainFrame.getContentPane().setLayout(springLayout);
		
		JPanel Cmnd_panel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, Cmnd_panel, -45, SpringLayout.SOUTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, Cmnd_panel, -10, SpringLayout.EAST, mainFrame.getContentPane());
		Cmnd_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		FlowLayout flowLayout = (FlowLayout) Cmnd_panel.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		springLayout.putConstraint(SpringLayout.WEST, Cmnd_panel, 10, SpringLayout.WEST, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, Cmnd_panel, -10, SpringLayout.SOUTH, mainFrame.getContentPane());
		mainFrame.getContentPane().add(Cmnd_panel);
		
		/*Readings_panel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, Readings_panel, 45, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, Readings_panel, -10, SpringLayout.EAST, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, Readings_panel, 10, SpringLayout.WEST, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, Readings_panel, 10, SpringLayout.NORTH, Graph_panel);
		Readings_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		FlowLayout flowLayout1 = (FlowLayout) Readings_panel.getLayout();
		flowLayout1.setHgap(10);
		flowLayout1.setAlignment(FlowLayout.LEFT);
		mainFrame.getContentPane().add(Readings_panel);*/
		
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, Cmnd_panel);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 250, SpringLayout.WEST, mainFrame.getContentPane());
		
		JLabel lblNewLabel = new JLabel("Command: ");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		Cmnd_panel.add(lblNewLabel);
		
		cmnd_Field = new JTextField();
		cmnd_Field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//CommandHandler(cmnd_Field.getText());
				cmnd_Field.setText("");
			}
		});
				
		Cmnd_panel.add(cmnd_Field);
		cmnd_Field.setColumns(60);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, mainFrame.getContentPane());
		mainFrame.getContentPane().add(scrollPane);
		
		
		Graph_panel = new JPanel();
		Graph_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, Graph_panel, 10, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, Graph_panel, 10, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, Graph_panel, -10, SpringLayout.NORTH, Cmnd_panel);
		springLayout.putConstraint(SpringLayout.EAST, Graph_panel, -30, SpringLayout.EAST, mainFrame.getContentPane());
		
		tout_textPane = new JTextPane();
		tout_textPane.setEditable(false);
		scrollPane.setViewportView(tout_textPane);
		mainFrame.getContentPane().add(Graph_panel);
		
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
	
		JMenu mnNewMenu1 = new JMenu("Options");
		menuBar.add(mnNewMenu1);
		
		JMenu mnNewMenu2 = new JMenu("Help");
		menuBar.add(mnNewMenu2);
		
		
		
	}

		
		
	
		
		
		
		
	

			 
	

	
		
}
