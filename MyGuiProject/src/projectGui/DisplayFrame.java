package projectGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Locale;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
	private JButton Start, Connect, R, Disconnect, plotGraph, clearGraph, save;
	private JPanel graphPanel;
	private JTextPane toutTextPane;
	private Timer displayUpdateTimer;
	public int var = 1;
	public int i = 0;
	static Chart2D chart;
	private ITrace2D trace;
    private int numTraces = 50;
    private SimpleAttributeSet TextSet = new SimpleAttributeSet();
    static int Downl_Cnt;
    static boolean Pb_Ready;
    static final int NROWS = 50, NCOLS = 3;
    static double Plot_Buffer[][] = new double[NROWS][NCOLS];
    static int Pb_NValues;
    private String file = "OutputFile";
	
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
		mainFrame.setTitle("PulseOximeter 0.0.1");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(100, 100, 930, 600);
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
		addCommPorts();
		
		// adding button to refresh the available comm ports
		R = new JButton(" R ");
		R.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			portList.removeAllItems();
			addCommPorts();

		}
			});
		R.setFocusable(false);
		R.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		R.setBackground(Color.LIGHT_GRAY);
		
		Connect = new JButton("Connect ");
		Connect.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			actionConnect();
		}
			});
		Connect.setFocusable(false);
		Connect.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		Readings_panel.add(portList);
		Readings_panel.add(R);
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
		Disconnect.setEnabled(false);
		Readings_panel.add(Disconnect);

		
		// adding Start button
		Start = new JButton();
		Start.setText("Start ");
		Start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actionStart();
			}
		});
		Start.setFocusable(false);
		Start.setEnabled(false);
		Start.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		Readings_panel.add(Start);		
				
		// adding readings label and title
		JLabel readingLbl1 = new JLabel("SpO2(%):");
		Readings_panel.add(readingLbl1);
		spo2Field = new JTextField();
		Readings_panel.add(spo2Field);
		spo2Field.setColumns(5);
		spo2Field.setEditable(false);
		spo2Field.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		spo2Field.setBackground(Color.WHITE);
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
		
		// adding graph button
		plotGraph = new JButton();
		plotGraph.setText("Plot Graph ");
		plotGraph.setHorizontalAlignment(SwingConstants.RIGHT);
		plotGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actionPlotGraph();
			}
		});
		plotGraph.setFocusable(false);
		plotGraph.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		plotGraph.setEnabled(false);
		Readings_panel.add(plotGraph);
		
		clearGraph = new JButton();
		clearGraph.setText("Clear Graph ");
		clearGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actionClearGraph();
								
			}
		});
		clearGraph.setFocusable(false);
		clearGraph.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		clearGraph.setEnabled(false);
		Readings_panel.add(clearGraph);

		save = new JButton();
		save.setText("Save ");
		save.setHorizontalAlignment(SwingConstants.RIGHT);
		save.setEnabled(false);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveChart();
				saveFile();
			}
		});
		save.setFocusable(false);
		save.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		Readings_panel.add(save);
		
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
		
		JMenuItem mnNewMenuItem1 = new JMenuItem("Save");
		mnNewMenu.add(mnNewMenuItem1);
		mnNewMenuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(save.isEnabled()) {
					saveChart();
					saveFile();
				} else {
					printTextWin(" \n *** Cannot Save Data ***", 3, true);
				}
				
			}
		});
		
		JMenuItem mnNewMenu1Item1 = new JMenuItem("Exit");
		mnNewMenu1.add(mnNewMenu1Item1);
		mnNewMenu1Item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SerialNetwork.disconnectPort();
				displayUpdateTimer.stop();
		        System.exit(0);
			}
		});
		
		createChart();
		
		displayUpdateTimer =  new Timer(250, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDynamic();
				displayUpdateTimer.restart();
			}
		});
		displayUpdateTimer.start();
		Downl_Cnt = 0;
        Pb_NValues = 0;
        Pb_Ready = false;
	}

	public void addCommPorts() {
		String[] ports = SerialNetwork.getCommPorts() ;
		for (int p =0; p < ports.length; p++) {
			portList.addItem(ports[p]);
		}
	}
	
	//making function for displaying value
	private void displayReading(int spo2, int bpm, int ppg) {
	
		spo2Field.setText(Integer.toString(spo2));
		bpmField.setText(Integer.toString(bpm));
		ppgField.setText(Integer.toString(ppg));
	}
	
	private void createChart() {
		
		chart = new Chart2D();
		trace = new Trace2DLtd(numTraces);
		trace.setColor(Color.RED);
		IAxis axisX = chart.getAxisX();
	    axisX.setPaintGrid(true);
	    axisX.getAxisTitle().setTitle("Time (s)");
	    IAxis axisY = chart.getAxisY();
	    axisY.setPaintGrid(true);
	    axisY.getAxisTitle().setTitle("Heart Rate");

		chart.addTrace(trace);
		trace.setName("Graph for Heart Rate");
		
		graphPanel.setLayout(new BorderLayout(0, 0));
		graphPanel.add(chart);
		chart.setVisible(true);
		chart.setSize(600, 500);
		graphPanel.setVisible(true);
		graphPanel.repaint();
		
	}
	
	private void plotChart(int xvalue, double yvalue) {	
		trace.addPoint(xvalue, yvalue);
	}
	
	private void clearChart() {
		trace.removeAllPoints();
		trace.addPoint(0,0);
		save.setEnabled(false);
		Pb_Ready = false;
		Start.setEnabled(true);
	}
	
	private void saveChart() {
		 Thread t = new Thread() {
	            public void run() {
	                // save the chart to a file
	                try {
	                    BufferedImage bi = chart.snapShot();
	                    ImageIO.write(bi, "JPEG", new File("Graph.jpg"));
	                    JOptionPane.showMessageDialog(mainFrame, "Graph and Data Saved");
	                    printTextWin("\n Graph and Data Saved ", 1, true);
	                    System.out.println("\n Graph Saved  \n ");
	                    // other possible file formats are PNG and BMP
	                } catch (Exception ex) {
	                    System.err.println("Error saving Graph to File: "+ex.getMessage());
	                }
	            }
	        };
	        t.start();
		}
	
	private void actionConnect() {
		boolean checkConnect = SerialNetwork.connectPort(portList.getSelectedItem().toString());
		if (checkConnect) {
			String c = "Connected to " + SerialNetwork.getConnectionName();
			printTextWin(c,1,true);
			Disconnect.setEnabled(true);
			Connect.setEnabled(false);
			R.setEnabled(false);
			portList.setEnabled(false);
			Start.setEnabled(true);
		}
		else {
			String f = "Connection failed" ;
			printTextWin(f,3,true); 
		}
	}
	
	private void actionDisconnect() {
		String name = SerialNetwork.getConnectionName();
		SerialNetwork.disconnectPort();
		String d = "\n" + name + " Disconnected";
		printTextWin(d,1,true);
		Connect.setEnabled(true);
		Disconnect.setEnabled(false);
		plotGraph.setEnabled(false);
		clearGraph.setEnabled(false);
		Start.setEnabled(false);
		R.setEnabled(true);
		portList.setEnabled(true);
		spo2Field.setText("");
		bpmField.setText("");
		ppgField.setText("");
		clearChart();
	}
	
	private void actionStart() {
		
		Start.setEnabled(false);
		sendPacket(SerialNetwork.pType_start);
	}
	
	private void actionPlotGraph() {
		for(int i=0; i<NROWS; i++) {
			plotChart(i,Plot_Buffer[i][1]);
		}
		save.setEnabled(true);
		clearGraph.setEnabled(true);
		Pb_Ready = false;
		plotGraph.setEnabled(false);
		
	}
	
	private void actionClearGraph() {
		clearChart();
		clearGraph.setEnabled(false);
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
		String command = cmds, dev_name;
		if (command.equals("clc")) {
			toutTextPane.setText("");
		} else if (command.equals("clg")) {
			clearChart();
		} else if (command.equals("help")) {
			printTextWin("FPGA Control Help:", 1, true);
			printTextWin("    clc - clear text window", 1, true);	
			printTextWin("    connect - connect", 1, true);			
			printTextWin("    disconnect - disconnect", 1, true);
			printTextWin("    plot - plots the graph", 1, true);	
			printTextWin("    start - starts the sensor", 1, true);
			printTextWin("    saveGr - saves the graph", 1, true);
			printTextWin("    saveFile - saves the data to file", 1, true);
			printTextWin("    clearGr - clear chart window", 1, true);		
			printTextWin("    exit - exit", 1, true);	
		} else if (command.startsWith("connect")) {
			actionConnect();
		} else if (command.equals("disconnect")) {
			actionDisconnect();
		} else if (command.equals("exit")) {
			dev_name = SerialNetwork.getConnectionName();
			if (dev_name != null) {
				SerialNetwork.disconnectPort();
			}
			displayUpdateTimer.stop();
	        System.exit(0);
		} else if (command.equals("start")) {
			if(SerialNetwork.isConnected) {
				actionStart();
			}else {
				printTextWin("\n  *** Device not connected***", 3, true);
			}
		}else if (command.startsWith("plot")) {
			if(plotGraph.isEnabled()) {
				actionPlotGraph();
			}else {
				printTextWin("\n  *** Values not received***", 3, true);
			}
		}else if (command.startsWith("saveGr")) {
			if(save.isEnabled()) {
					saveChart();
			}else {
					printTextWin("\n  *** All Values not received***", 3, true);
				}
		}else if (command.startsWith("clearGr")) {
			if(clearGraph.isEnabled()) {
				actionClearGraph();
			}else {
				printTextWin("\n  *** Complete Graph not plotted***", 3, true);
			}
		}else if (command.startsWith("saveFile")) {
				if(save.isEnabled()) {
					saveFile();
				}else {
					printTextWin("\n  *** All Values not received***", 3, true);
				}
		}else if (command.startsWith("clc")) {
			toutTextPane.setText("");
		}else if (command.length() > 0) {
			printTextWin("\n   *** command???: \"" + command + "\"", 3, true);
		}
	}
	
	public void updateDynamic() {
		byte[] rData ;
    	while ((rData = SerialNetwork.recvSerial()) != SerialNetwork.error) {
				System.out.println("["+ rData + "]");
			try {
				byte pType = rData[0];
				switch(pType) 
				{
				case SerialNetwork.pType_startSuccess:
					sendPacket(SerialNetwork.pType_sendData);
					break;
					
				case SerialNetwork.pType_startFail:
					sendPacket(SerialNetwork.pType_start);
					break;

				case SerialNetwork.pType_data:
					System.out.println("PBNVALUE: " + Pb_NValues);
					printTextWin("+", 1, false);
					processData(rData);
					Downl_Cnt++;
					if (Downl_Cnt>25) {
						Downl_Cnt = 0;
						printTextWin(" ", 1, true);
					}
					break;

				case SerialNetwork.pType_error:
					break;
				}
			}
			catch  (NumberFormatException e) {
		        System.out.println(e.toString());
		    }
    	}
   	}

	private void processData(byte[] data) {
		if(data.length <= NCOLS) {
    		System.out.printf("processData: Data Insufficient");
			return;
		}
		int spo2 = (int)data[1];
		int bpm = (int)data[2];
		int ppg = (int)data[3];
		
		displayReading(spo2, bpm, ppg);
		System.out.printf("Values: %d %d %d\n", spo2,bpm,ppg);
		if(!Pb_Ready) {
			Plot_Buffer[Pb_NValues][0] = (double)spo2;
			Plot_Buffer[Pb_NValues][1] = (double)bpm;
			Plot_Buffer[Pb_NValues][2] = (double)ppg;
		}
		if(Pb_NValues >= NROWS-1) {
			printTextWin("\n Download finished.", 1, true);
			plotGraph.setEnabled(true);
    		Pb_Ready = true;
    		Pb_NValues = 0;	
		}
		else {
			Pb_NValues++;
		}
	}
	
	private void saveFile() {
		Thread t = new Thread() {
            public void run() {
                // save the data to a file
                try {
                   Writer out = new OutputStreamWriter(new FileOutputStream(file));
					for (int k = 0; k < NROWS; k++) {
	                    out.write(String.format(Locale.ENGLISH, "%f %f %f \n",
	                    	Plot_Buffer[k][0], Plot_Buffer[k][1],Plot_Buffer[k][2]));
					}
					out.close();
				}
				catch (Exception ex) {
                    System.err.println("Error saving Data to File: "+ex.getMessage());
                }
            }
        };
        t.start();
		
		
	}

	static void sendPacket(byte pType) {

		byte[] sendData = new byte[5];
		int sendDatalength = 1;
		byte crc = 0x00;
		
		sendData[0] = SerialNetwork.startByte;
		sendData[1] = (byte)sendDatalength;
		sendData[2] = pType;
		sendData[3] = (byte) (crc^pType);
		sendData[4] = SerialNetwork.stopByte;
		SerialNetwork.sendData(sendData, sendData.length);
		}

} 
		

	

	
		

