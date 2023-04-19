/**
* The SerialNetwork program implements a code for connecting the GUI
* with a Micro controller using UART communication protocol.
*
* @author  Vaishnavi Shah
* @version 1.0
* @since   02-01-2023 
*/
package projectGui;

import com.fazecast.jSerialComm.SerialPort;

public class SerialNetwork {
	
	static final int nPortsMax = 10;
	static public boolean isConnected = false;
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
//	public static final byte pType_stopData = 0x30;
//	public static final byte pType_stopOk = 0x31;
	public static final byte pType_error = 0x32;
	public static final byte pType_consoleText = 0x33;
	public static final byte[] error = {(byte) 0xff};
		
	// function to get available serial ports and return the names
	static String[] getCommPorts() {
		SerialPort commPorts[] = SerialPort.getCommPorts();
		int tPorts = commPorts.length;
		String[] portNames = new String[tPorts];
		for (int i=0; i< tPorts; i++ ) {
			portNames[i] = commPorts[i].getSystemPortName();
		}
		return portNames;
	}
	
	//function to connect a given port to GUI 
	static boolean connectPort(String portName) {
		try {
			mPort = SerialPort.getCommPort(portName);
			mPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY, false);
        	if (mPort.openPort() == false) {
        		if(DisplayFrame.DEBUG){
        		System.out.printf("*** error open %s\n", portName);
        		}
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

	//function to disconnect the port
	static void disconnectPort() {
		if (isConnected == true) {
			mPort.closePort();
			isConnected = false;
			mPortName = null;
		}
	}
	
	//function to return the name of connected port
	static String getConnectionName() {
		return mPortName;
	}
	
	// function for action when command is received on port and return data array containing packet type and readings 
	static byte[] recvSerial() {
			if(!isConnected) {
			return error;
		}
		int minBytes = 5;
		long bytesToRead = 1;
		byte[] start = new byte[1];
		byte[] temp = new byte[1];
		byte[] crc = new byte[1];
		crc[0] = 0x00;
		
		
		/* check if min bytes are available for reading*/
		if(mPort.bytesAvailable() < minBytes) {
			System.out.printf("minBytes not available\n");
			return error; 		
		}
		
		/*read the first byte, check if it is startByte or consoleText Byte*/
		mPort.readBytes(temp,bytesToRead); 
		start[0] = temp[0];
		if (temp[0] != startByte) {
			if(DisplayFrame.DEBUG){
			System.out.printf("ErrorByte:[0x%X] \n", temp[0]);
			}
			return error;						/*if first byte is not startByte, return error*/
		}
		
		mPort.readBytes(temp,bytesToRead); 
		bytesToRead = temp[0];
		crc[0] = (byte) (start[0] ^ temp[0]);
		byte[] data = new byte[(int) bytesToRead];
		
		mPort.readBytes(data, bytesToRead);
		System.out.printf("data:");
		for (int i = 0; i < bytesToRead; i++) {
			if(DisplayFrame.DEBUG){
			System.out.printf("0x%X,", data[i]);
			}
			crc[0] = (byte) (crc[0] ^ data[i]);
		}
		
		bytesToRead = 1;
		mPort.readBytes(temp, bytesToRead);
		if (temp[0] != crc[0]) {
			if(DisplayFrame.DEBUG){
				System.out.printf("CRC value", crc)	;
			System.out.printf("CRC check failed \n");
			}
			return error;						/*if first byte is not startByte, return error*/
		}
		
		mPort.readBytes(temp, bytesToRead);
		if (temp[0] != stopByte) {
			if(DisplayFrame.DEBUG){
			System.out.printf("Stopbyte check failed \n");
			}
			return error;						/*if first byte is not startByte, return error*/
		}
		
		return data;
	}
	
	//function to write command on serial port.
	// we have to pass the command and length of command to function as parameters
	public static void sendData(byte[] sendData, int length) {
		if(isConnected) {
			mPort.writeBytes(sendData, length);
		}
	}
}
