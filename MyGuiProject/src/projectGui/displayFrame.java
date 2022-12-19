package projectGui;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

//import javax.swing.JButton;
//import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class displayFrame 
{
	
	private JFrame mainFrame;
	private JTextField cmnd_Field;
	private JPanel Graph_panel;
	private JTextPane tout_textPane;
	private Timer DispUpdate_Timer;
	static int Downl_Cnt, Pb_NValues;
	static boolean Pb_Ready;
	private SimpleAttributeSet TextSet = new SimpleAttributeSet();
	static final int NTRACES = 4;
	static ITrace2D mtraces[] = new ITrace2D[4];
	static Chart2D chart;
	  
	
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
		Cmnd_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		FlowLayout flowLayout = (FlowLayout) Cmnd_panel.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		springLayout.putConstraint(SpringLayout.NORTH, Cmnd_panel, -45, SpringLayout.SOUTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, Cmnd_panel, -10, SpringLayout.EAST, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, Cmnd_panel, 10, SpringLayout.WEST, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, Cmnd_panel, -10, SpringLayout.SOUTH, mainFrame.getContentPane());
		mainFrame.getContentPane().add(Cmnd_panel);
		
		JPanel Readings_panel = new JPanel();
		Readings_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		FlowLayout flowLayout1 = (FlowLayout) Readings_panel.getLayout();
		flowLayout1.setHgap(10);
		flowLayout1.setAlignment(FlowLayout.LEFT);
		springLayout.putConstraint(SpringLayout.NORTH, Readings_panel, 10, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, Readings_panel, 40, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, Readings_panel, 10, SpringLayout.WEST, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, Readings_panel, -10, SpringLayout.EAST, mainFrame.getContentPane());
		mainFrame.getContentPane().add(Readings_panel);
		
			
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, Cmnd_panel);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 250, SpringLayout.WEST, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 50, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, mainFrame.getContentPane());
		
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
		mainFrame.getContentPane().add(scrollPane);
		
		
		Graph_panel = new JPanel();
		Graph_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, Graph_panel, 50, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, Graph_panel, 10, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, Graph_panel, -10, SpringLayout.NORTH, Cmnd_panel);
		springLayout.putConstraint(SpringLayout.EAST, Graph_panel, -10, SpringLayout.EAST, mainFrame.getContentPane());
		
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
		
		JMenuItem mnNewMenuItem = new JMenuItem("New");
		JMenuItem mnNewMenuItem1 = new JMenuItem("Save");
		mnNewMenu.add(mnNewMenuItem);
		mnNewMenu.add(mnNewMenuItem1);
		
		
		JMenuItem mnNewMenu1Item = new JMenuItem("Connect");
		JMenuItem mnNewMenu1Item1 = new JMenuItem("Disconnect");
		JMenuItem mnNewMenu1Item2 = new JMenuItem("Exit");
		mnNewMenu1.add(mnNewMenu1Item);
		mnNewMenu1.add(mnNewMenu1Item1);
		mnNewMenu1.add(mnNewMenu1Item2);
		mnNewMenu1Item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dev_name;
				dev_name = GUI.getConName();
				if (dev_name != null) {
					GUI.spDisconn(dev_name);
				}
		        DispUpdate_Timer.stop();
		        System.exit(0);
			}
		});
		
		
		
		
		CreateChart();
        DispUpdate_Timer = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateDynamic();
                DispUpdate_Timer.restart();
            }
        });
		
		
       // DispUpdate_Timer.start();
        Downl_Cnt = 0;
        Pb_NValues = 0;
        Pb_Ready = false;
	
	
		}
	
	  private void PrintTextWin(String tWstr, int tWstyle, boolean newline) {
	        try {
	            Document doc = tout_textPane.getStyledDocument();
	            StyleConstants.setItalic(TextSet, false);
	            StyleConstants.setBold(TextSet, false);
	            StyleConstants.setForeground(TextSet, Color.BLACK);
	            switch (tWstyle) {
	                case 0:
	                    StyleConstants.setBold(TextSet, true);
	                    StyleConstants.setForeground(TextSet, Color.DARK_GRAY);
	                    break;
	                case 1: StyleConstants.setForeground(TextSet, Color.BLUE);
	                    break;
	                case 2: StyleConstants.setForeground(TextSet, Color.BLACK);
	                    break;
	                case 3: StyleConstants.setForeground(TextSet, Color.RED);
	                	break;
	                case 4: StyleConstants.setForeground(TextSet, Color.GREEN);
	                	break;
	                default:
	                    doc.remove(0, doc.getLength());
	            }
	            if (tWstyle >= 0) {
	            	tout_textPane.setCharacterAttributes(TextSet, true);
	            	if (newline) {
	                    doc.insertString(doc.getLength(), tWstr+"\n", TextSet);            		
	            	} else {
	                    doc.insertString(doc.getLength(), tWstr, TextSet);
	            	}
	            }
	        } catch (BadLocationException ex) {
	            System.out.println(ex.toString());
	        }
	    }


		private void ClearChart()
		{
			int  k;
			for (k = 0; k < NTRACES; k++) {
				mtraces[k].removeAllPoints();
				mtraces[k].addPoint(0.0, 0.0);
			}
		}



		private void CreateChart()
		{
			int  k;

			chart = new Chart2D();
			for (k = 0; k < NTRACES; k++) {
				mtraces[k] = new Trace2DSimple();
		        chart.addTrace(mtraces[k]);
			}
	        mtraces[0].setColor(Color.blue);     mtraces[0].setName("trace 0");
	        mtraces[1].setColor(Color.red);      mtraces[1].setName("trace 1");
	        mtraces[2].setColor(Color.green);    mtraces[2].setName("trace 2");
	        mtraces[3].setColor(Color.magenta);  mtraces[3].setName("trace 3");
	        Graph_panel.setLayout(new BorderLayout(0, 0));
	        
	        Graph_panel.add(chart);
	        Graph_panel.setSize(100,200);
	        chart.setVisible(true);
	        Graph_panel.setVisible(true);
	        Graph_panel.repaint();	
		}


		
		
	}
		
		
		
		
	

			 
	

	
		

