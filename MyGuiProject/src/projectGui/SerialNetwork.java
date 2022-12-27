package projectGui;

import com.fazecast.jSerialComm.SerialPort;

public class SerialNetwork {
	
	static final int nPortsMax = 10;
	private int nPorts = 0;
	static private boolean isConnected = false;
	
	
	
	static void initSerialNet () {
		
		
	}

	static String[] getCommPorts() {
		SerialPort commPorts[] = SerialPort.getCommPorts();
		int tPorts = commPorts.length;
		String[] portNames = new String[tPorts];
		for (int i=0; i< tPorts; i++ ) {
			portNames[i] = commPorts[i].getSystemPortName();
		}
		return portNames;
		
	}
}
