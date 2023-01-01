package projectGui;

import com.fazecast.jSerialComm.SerialPort;

public class SerialNetwork {
	
	static final int nPortsMax = 10;
	static private boolean isConnected = false;
	static SerialPort mPort;
	static private String mPortName = null;
	
	 //defining protocol constants
	public static byte startByte = 0x24;             // $ for start
	public static byte stopByte = 0x23;			 // # for stop
	public static final byte pType_start = 0x25;
	public static final byte pType_startSuccess = 0x26;
	public static final byte pType_startFail = 0x27;
	public static final byte pType_sendData = 0x28;
	public static final byte pType_data = 0x29;
	public static final byte pType_stopData = 0x30;
	public static final byte pType_stopOk = 0x31;
	public static final byte pType_error = 0x32;
	public static final byte[] error = {(byte) 0xff};
	
	
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
	
	static byte[] recvSerial() {
		
		if(!isConnected) {
			return error;
		}
		
		int minBytes = 5;
		long bytesToRead = 1;
		byte[] temp = new byte[1];
		byte[] crc = new byte[1];
		crc[0] = 0x00;
		
		/* check if min bytes are available for reading*/
		if(mPort.bytesAvailable() <= minBytes) {
			return error; 
		}
		
		/*read the first byte, check if it is startByte*/
		mPort.readBytes(temp,bytesToRead); 
		if (temp[0] != startByte) {
			return error;						/*if first byte is not startByte, return error*/
		}
		
		mPort.readBytes(temp,bytesToRead); 
		bytesToRead = temp[0];
		byte[] data = new byte[(int) bytesToRead];
		
		mPort.readBytes(data, bytesToRead);
		for (int i = 0; i < data.length; i++) {
		crc[0] = (byte) (crc[0] ^ data[i]);
		}
		
		bytesToRead = 1;
		mPort.readBytes(temp, bytesToRead);
		if (temp[0] != crc[0]) {
			return error;						/*if first byte is not startByte, return error*/
		}
		
		mPort.readBytes(temp, bytesToRead);
		if (temp[0] != stopByte) {
			return error;						/*if first byte is not startByte, return error*/
		}
		
		return data;
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

	public static void sendData(byte[] sendData, int length) {
		// TODO Auto-generated method stub
		if(isConnected) {
			mPort.writeBytes(sendData, length);
		}
	}
}
