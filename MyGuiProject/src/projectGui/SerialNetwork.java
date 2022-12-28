package projectGui;

import com.fazecast.jSerialComm.SerialPort;

public class SerialNetwork {
	
	static final int nPortsMax = 10;
	private int nPorts = 0;
	static private boolean isConnected = false;
	static SerialPort mPort;
	static private String mPortName = null;
	
	
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
	
	static boolean connectPort(String portName) {
		try {
			mPort = SerialPort.getCommPort(portName);
			mPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY, false);
        	if (mPort.openPort() == false) {
        		System.out.printf("*** error open %s\n", portName);
        		return false;
        	}
        	else {
        	     isConnected = true;
                 mPortName = portName;
        	}
        } catch (Exception ex) {
        	System.out.println(ex.toString());
        
		}
		return isConnected;
	}
	 
	static void SendString(String tstr) {
	        if (isConnected == true) {
	            mPort.writeBytes(tstr.getBytes(), tstr.length());
	        }
	    }

	
	static void disconnectPort() {
		if (isConnected == true) {
			mPort.closePort();
			isConnected = false;
			mPortName = null;
		}
	}
	
	static String getConnectionName() {
		return mPortName;
	}
	
}
