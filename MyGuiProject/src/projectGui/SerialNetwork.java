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
	
	static String ReadString() {
    	String  recv_str;
        byte[] d_arr = new byte[32];
        int idx = 0;
        int chunkSize = DisplayFrame.NCOLS * 2 + 1;
        recv_str = null;
        if (isConnected == true) {
	        try {
	            if ((mPort.bytesAvailable()) >= chunkSize) {
	            	byte[] sof = new byte[1];
	            	mPort.readBytes(sof, 1);
	            	if(sof[0] != '$') {
	            		return null;
	            	}
	            	d_arr[idx] = sof[0];
	            	idx++;
	            	mPort.readBytes(sof, 1);
	            	if(sof[0] != ',') {
	            		return null;
	            	}
	            	d_arr[idx] = sof[0];
	            	idx++;
	            	for(int i = 0;i<DisplayFrame.NCOLS; i++) {
	            		mPort.readBytes(sof, 1);
	            		while(sof[0] != ',') {
		            		d_arr[idx] = sof[0];
		            		idx++;
		            		mPort.readBytes(sof, 1);
		            	}
		            	d_arr[idx] = ',';
		            	idx++;
	            	}
	            	
	            	for(int i = idx; i<32; i++) {
	            		d_arr[idx] = 0x0;
	            	}
	            	//c_arr = new byte[chunkSize];
	            	//mPort.readBytes(c_arr, chunkSize);
	            	recv_str = new String(d_arr);
		            System.out.printf("ReadString(): [%s] %d\n", d_arr, chunkSize);
		            return recv_str;
	            }
	        } catch (Exception ex) {
	        	System.out.println(ex.toString());
	        }
        }
        return recv_str;
    }
}
