package jgserv.core;

import jgserv.errors.JGError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class Connection {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ConnectionEvent event;

	private LinkedList<byte[]> packetsToSend;

    private String CSID;
	private int bufferSize;
	private long lastTime;
	private long pingLastTime;
	private long timeoutLimit;

	private ConnectionInfo connectionInfo;
	private long pingTimeoutLimit;

	/**
	 * Constructor of Connection
	 * @param CSID JGServer's internal Connection Socket ID
	 * @param bufferSize buffer size reading from input stream
	 * @param timeoutLimit timeout limit for disconnect(if nothing to receive)
	 * @param socket Socket descriptor from AcceptWaiterEvent.onSocketConnected
	 * @throws IOException throws when exist problem with input and output streams
	 */
    public  Connection(Integer CSID, int bufferSize, long timeoutLimit, long pingTimeOutLimit, Socket socket) throws IOException{

		lastTime = getCurrentTime();
		pingLastTime = getCurrentTime();
		packetsToSend = new LinkedList<byte[]>();

		this.pingTimeoutLimit = pingTimeOutLimit;
		this.bufferSize = bufferSize;
        this.socket = socket;
        this.CSID = String.valueOf(CSID);
		this.timeoutLimit = timeoutLimit;

		connectionInfo = new ConnectionInfo(this);

        try{
			inputStream = socket.getInputStream();
		}catch (IOException e){
			throw e;
		}

		try{
			outputStream = socket.getOutputStream();
		}catch(IOException e){
			throw e;
		}

    }

	/**
	 * Sending data in input socket
	 * @param bytes raw data to send. Length must be <= bufferSize
	 * @throws IOException when bytes length over bufferSize
	 */
	public void send(byte[] bytes) throws IOException, NullPointerException{
		byte[] sending = bytes;

		if(bytes.length>bufferSize){
			throw new IOException("Input byte's length over buffer size!");
		}

		if(bytes.length<bufferSize){
			sending = getNulledPacket();
			for(int i=0; i<bytes.length; i++){
				sending[i]=bytes[i];
			}
		}

		synchronized (packetsToSend){
			packetsToSend.push(bytes);
		}
	}

	private long timeShifting=0;

	/**
	 * Checking socket for input or/and output data, and timeout checking
	 */
    public void checkSocket(long timeShift){
		timeShifting = timeShift;
        if(checkTimeoutIsOver()){
			dispatchDisconnected();
			return;
		}

		readingFromSocket();

		if((getCurrentTime() - pingLastTime)>=pingTimeoutLimit){
			try{
				send(getOneBytePacket(InternalCommandCode.PING));
			}catch (Exception e){};

			pingLastTime = getCurrentTime();

		}

        writingFromSocket();
    }

	/**
	 * Sending data from stack
	 */
    private void writingFromSocket() {
		if(packetsToSend.size() == 0)return;

		try{
			outputStream.write(packetsToSend.remove(0));
			outputStream.flush();
		}catch (IOException e){
			dispatchDisconnected();
		}
    }

	/**
	 * Reading data from input socket, and check received data for internal command
	 */
    private void readingFromSocket() {

		try{
			if(inputStream.available() <= 0)return;
		}catch(IOException e){
			dispatchDisconnected();
		}

        int readed = -1;
        byte[] buffer = getNulledPacket();

        try{
			readed = inputStream.read(buffer);
        }catch (IOException ex){
			//TODO ERROR DICLARE!
            dispatchError(null);
            return;
        }

        if(readed > 0){
			if(checkForInternalCommand(buffer)){
				//internal
				byte firstByte = buffer[0];
				switch (firstByte){
					case InternalCommandCode.PING:
						try{
							send(getOneBytePacket(InternalCommandCode.PONG));
						}catch (IOException e){
							//TODO what to do?!
							//Event?
						}
						break;
					case InternalCommandCode.PONG:
						break;

					case InternalCommandCode.ESC:
						//TODO do the react!
						break;
				}

			}else{
				//not internal
				dispatchData(buffer);
			}
			//System.out.println("RECV");
			pingLastTime = getCurrentTime();
			lastTime = getCurrentTime();
        }
    }

	/**
	 * Check buffer for contains internal command
	 * @param buffer
	 * @return true is contains
	 */
	private boolean checkForInternalCommand(byte[] buffer){
		if(buffer==null)return false;

		boolean result;

		byte b = buffer[0];
		//byte[] packet=null;
		//packet = getOneBytePacket(InternalCommandCode.PONG);
		switch (b){
			case InternalCommandCode.PING:
			case InternalCommandCode.PONG:
			case InternalCommandCode.ESC:
				result = true;
				break;
			default:
				result=false;
				break;
		}


		return result;
	}

	/**
	 * Check for timeout
	 * @return true means that time is out!
	 */
	private boolean checkTimeoutIsOver(){
		if(timeoutLimit==0)return false;
		return (getCurrentTime() - (lastTime+timeShifting)) > timeoutLimit;
	}

	/**
	 * Return byte[bufferSize] packet with bt in first place
	 * @param bt
	 * @return raw packet
	 */
	private byte[] getOneBytePacket(byte bt){
		byte[] result = getNulledPacket();
		result[0]=bt;
		return result;
	}

	/**
	 * Return nulled packet with size = bufferSize
	 * @return
	 */
	private byte[] getNulledPacket(){
		byte[] result= new byte[bufferSize];
		for(int i=0; i<result.length; i++)result[i]=(byte)0;
		return result;
	}

	/**
	 * Dispatched Event about received data
	 * @param buffer recv data
	 */
    private  void dispatchData(byte[] buffer){
        if(event!=null)event.onConnectionData(connectionInfo, buffer);
    }

	/**
	 * Dispatched when Socket is disconnected
	 */
	private  void dispatchDisconnected(){
		if(event!=null)event.onConnectionDisconnected(connectionInfo);
	}

	/**
	 * Dispatched event about error in Connection
	 */
    private void dispatchError(JGError error){
        if(event!=null){
			event.onConnectionError(error, connectionInfo);
		}
    }

	/**
	 * Get current time in miliseconds
	 * @return
	 */
	private long getCurrentTime(){
		return System.currentTimeMillis();
	}

	/**
	 * Get CSID
	 * @return
	 */
    public String getCSID() {
        return CSID;
    }

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	/**
	 * Settind event listener for Connection Events
	 * @param event
	 */
    public void setConnectionEventListener(ConnectionEvent event) {
        this.event = event;
    }

	/**
	 * Return Connection event listner handler
	 * @return  Event handler
	 */
	public ConnectionEvent getConnectionEventListener(){
		return event;
	}

	/**
	 * Closing connection
	 */
	public void close() {
		try{outputStream.close();}catch (Exception exp){}
		try{inputStream.close();}catch (Exception exp){}
		try{socket.close();}catch (Exception exp){}

		setConnectionEventListener(null);
		outputStream = null;
		inputStream = null;
		socket = null;

		packetsToSend.clear();
		packetsToSend = null;
	}

	/**
	 * Kit with internal command constants
	 */
	private class InternalCommandCode{

		public static final byte PING = (byte)0x1;
		public static final byte PONG = (byte)0x2;
		public static final byte ESC =  (byte)0x3;

	}

	/**
	 * Interface for ConnectionEvent
	 */
	public static interface ConnectionEvent{

        void onConnectionData(ConnectionInfo connection, byte[] data);

		void onConnectionDisconnected(ConnectionInfo connection);

        void onConnectionError(JGError error,ConnectionInfo connection);

    }

}
