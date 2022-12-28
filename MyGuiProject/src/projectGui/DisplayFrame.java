package projectGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Writer;

import javax.swing.Timer;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;

public class DisplayFrame 
{
	
	private JFrame mainFrame;
	private JTextField cmnd_Field, spo2Field,bpmField, ppgField;
	private JComboBox<String> portList ;
	private JButton Connect, Disconnect, plotGraph, clearGraph;
	private JPanel graphPanel;
	private JTextPane toutTextPane;
	private Timer rTimer;
	private Timer pTimer;
	public int var = 1;
	public int i = 0;
	static Chart2D chart;
	private ITrace2D trace;
    int numTraces = 50;
    private SimpleAttributeSet TextSet = new SimpleAttributeSet();

	
	  
	
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DisplayFrame window = new DisplayFrame();
					window.mainFrame.setVisible(true);
				}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
			
	});
	}
		
	public DisplayFrame() {
		initialize();
	}
	
	private void initialize() {
		
		//Making the frame for display, adding buttons and panels
		
		mainFrame = new JFrame();
		mainFrame.setTitle("Pulseoximeter GUI");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(100, 100, 850, 600);
		SpringLayout springLayout =  new SpringLayout();
		mainFrame.getContentPane().setLayout(springLayout);
		
		// adding command panel
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
		
		// adding reading panel
		JPanel Readings_panel = new JPanel();
		Readings_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		FlowLayout flowLayout1 = (FlowLayout) Readings_panel.getLayout();
		flowLayout1.setHgap(10);
		flowLayout1.setVgap(2);
		flowLayout1.setAlignment(FlowLayout.LEFT);
		springLayout.putConstraint(SpringLayout.NORTH, Readings_panel, 10, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, Readings_panel, 45, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, Readings_panel, 10, SpringLayout.WEST, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, Readings_panel, -10, SpringLayout.EAST, mainFrame.getContentPane());
		
		// adding connect button and drop down for selecting port
		portList = new JComboBox<String>();
		String[] ports = SerialNetwork.getCommPorts() ;
		for (int p =0; p < ports.length; p++) {
			portList.addItem(ports[p]);
		}
				
		Connect = new JButton("Connect ");
				
		Connect.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			actionConnect();
		}
				
				});
		Connect.setFocusable(false);
		Connect.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		Dimension d = new Dimension ();
		d.setSize(500, 200);
		Connect.setMinimumSize(d);
				
		Readings_panel.add(portList);
		Readings_panel.add(Connect);
				
		
		// adding disconnect button
		Disconnect = new JButton();
		Disconnect.setText("Disconnect ");
		Disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actionDisconnect();
			
			}
		});
		Disconnect.setFocusable(false);
		Disconnect.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		Readings_panel.add(Disconnect);

		
				
		// adding readings label and title
		JLabel readingLbl1 = new JLabel("SpO2(%):");
		Readings_panel.add(readingLbl1);
		spo2Field = new JTextField();
		Readings_panel.add(spo2Field);
		spo2Field.setColumns(5);
		spo2Field.setEditable(false);
		spo2Field.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		spo2Field.setBackground(Color.WHITE);
		
		rTimer = new Timer(1000,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	displayReading(96+var, 88+var, 1+var);
            	var++;
            	rTimer.start();
              
            }
        });
		rTimer.start();
		
		

		Readings_panel.add(spo2Field);

		JLabel readingLbl2 = new JLabel("BPM:");
		Readings_panel.add(readingLbl2);
		bpmField = new JTextField();
		Readings_panel.add(bpmField);
		bpmField.setColumns(5);
		bpmField.setEditable(false);
		bpmField.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		bpmField.setBackground(Color.WHITE);

		JLabel readingLbl3 = new JLabel("PPG:");
		Readings_panel.add(readingLbl3);
		ppgField = new JTextField();
		Readings_panel.add(ppgField);
		ppgField.setColumns(5);
		ppgField.setEditable(false);
		ppgField.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		ppgField.setBackground(Color.WHITE);
		pTimer = new Timer(1000,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	plotChart(i,5+1);
            	System.out.printf("Plotted\n");
            	i++;
            	pTimer.restart();
              
            }
        });
		// adding graph button
		plotGraph = new JButton();
		plotGraph.setText("Plot Graph ");
		plotGraph.setHorizontalAlignment(SwingConstants.RIGHT);
		plotGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(pTimer.isRunning()) {
					return;
				}
				else
					pTimer.start();
				
			}
		});
		plotGraph.setFocusable(false);
		plotGraph.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		Readings_panel.add(plotGraph);
		
		clearGraph = new JButton();
		clearGraph.setText("Clear Graph ");
		clearGraph.setHorizontalAlignment(SwingConstants.RIGHT);
		clearGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(pTimer.isRunning()) {
					pTimer.stop();
				}
				clearChart();
								
			}
		});
		clearGraph.setFocusable(false);
		clearGraph.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		Readings_panel.add(clearGraph);

		mainFrame.getContentPane().add(Readings_panel);
		
		//adding scroll pane
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
				CommandHandler(cmnd_Field.getText());
				cmnd_Field.setText("");
			}
		});
				
		Cmnd_panel.add(cmnd_Field);
		cmnd_Field.setColumns(60);
		mainFrame.getContentPane().add(scrollPane);
		
		graphPanel = new JPanel();
		graphPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, graphPanel, 50, SpringLayout.NORTH, mainFrame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, graphPanel, 10, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, graphPanel, -10, SpringLayout.NORTH, Cmnd_panel);
		springLayout.putConstraint(SpringLayout.EAST, graphPanel, -10, SpringLayout.EAST, mainFrame.getContentPane());
		
		toutTextPane = new JTextPane();
		toutTextPane.setEditable(false);
		scrollPane.setViewportView(toutTextPane);
		mainFrame.getContentPane().add(graphPanel);
		
		// adding menu bar and items 
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
		
		
		JMenuItem mnNewMenu1Item2 = new JMenuItem("Exit");
		mnNewMenu1.add(mnNewMenu1Item2);
		mnNewMenu1Item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SerialNetwork.disconnectPort();
		      //  DispUpdate_Timer.stop();
		        System.exit(0);
			}
		});
		
		createChart();	
		}
	

	
	 
	//making function for displaying value
	public void displayReading(int spo2, int bpm, int ppg) {
	
		spo2Field.setText(Integer.toString(spo2));
		bpmField.setText(Integer.toString(bpm));
		ppgField.setText(Integer.toString(ppg));

	}

		
	//a method for updating all values of GUI
	public void updateDynamic() {
		
		
	}
	
	private void createChart() {
		
		chart = new Chart2D();
		trace = new Trace2DLtd(numTraces);
		trace.setColor(Color.RED);
		IAxis axisX = chart.getAxisX();
	    axisX.setPaintGrid(true);
	    IAxis axisY = chart.getAxisY();
	    axisY.setPaintGrid(true);
		chart.addTrace(trace);
		
		graphPanel.setLayout(new BorderLayout(0, 0));
		graphPanel.add(chart);
		chart.setVisible(true);
		chart.setSize(600, 500);
		graphPanel.setVisible(true);
		graphPanel.repaint();
		
	}
	private void plotChart(int xaxis, int yaxis) {	
		trace.addPoint(xaxis, yaxis);
	}
	
	private void clearChart() {
		i=0;
		trace.removeAllPoints();
		//trace.addPoint(0, 0);
		
	}
	
	private void actionConnect() {
		boolean checkConnect = SerialNetwork.connectPort(portList.getSelectedItem().toString());
		if (checkConnect) {
			String c = "Connected to " + SerialNetwork.getConnectionName();
			printTextWin(c,1,true);
		}
		else {
			String f = "Connection failed" ;
			printTextWin(f,3,true); 
		}
	}
	
	private void actionDisconnect() {
		String name = SerialNetwork.getConnectionName();
		SerialNetwork.disconnectPort();
		String d = name + " Disconnected";
		printTextWin(d,1,true);
	}
	
	private void printTextWin(String t, int tstyle, boolean newline) {
		try {
			Document doc = toutTextPane.getStyledDocument();
			StyleConstants.setItalic(TextSet, false);
            StyleConstants.setBold(TextSet, false);
            StyleConstants.setForeground(TextSet, Color.BLACK);
            switch (tstyle) {
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
        if (tstyle >= 0) {
        	toutTextPane.setCharacterAttributes(TextSet, true);
        	if (newline) {
                doc.insertString(doc.getLength(), t+"\n", TextSet);            		
        	} else {
                doc.insertString(doc.getLength(), t, TextSet);
        	}
        }
	}
		catch(BadLocationException ex) {
            System.out.println(ex.toString());
			
		}
	}
	
	private void CommandHandler(String cmds) {
		String command = cmds, dev_name, fname;
		int  k, q, stat;
		Writer out = null;

		if (command.equals("clc")) {
			toutTextPane.setText("");
		} else if (command.equals("clg")) {
			clearChart();
		} else if (command.equals("help")) {
			printTextWin("FPGA Control Help:", 1, true);
			printTextWin("    clc - clear text window", 1, true);	
			printTextWin("    clg - clear chart window", 1, true);				
			printTextWin("    conn {comX} - connect", 1, true);			
			printTextWin("    disconn - disconnect", 1, true);			
			printTextWin("    .{sendstring}", 1, true);
		} else if (command.startsWith("conn")) {
			actionConnect();
		} else if (command.equals("disconn")) {
			actionDisconnect();
		} else if (command.equals("exit")) {
			/*dev_name = SerialNetw.getConName();
			if (dev_name != null) {
				SerialNetw.spDisconn(dev_name);
			}
	        DispUpdate_Timer.stop();
	        System.exit(0);*/
		} else if (command.equals("downl")) {
			
		} else if (command.startsWith("plot")) {
			
		} else if (command.startsWith(".")) {
			if (SerialNetwork.getConnectionName() != null) {
				SerialNetwork.SendString(command.substring(1) + "\n");
			} else {
				printTextWin("*** no connected device", 3, true);				
			}
		} else if (command.length() > 0) {
			printTextWin("*** command???: \"" + command + "\"", 3, true);
		}
	}


} 

	

	
		

