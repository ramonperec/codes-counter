package jgserv;

import jgserv.core.*;
import jgserv.errors.JGError;
import jgserv.exceptions.StartStatusException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 23.10.11
 * Time: 6:54
 * To change this template use File | Settings | File Templates.
 */
public final class JGServer {

	private Hashtable<String, Connection> connections;
	private ServerSocket socket;
	private AcceptWaiter acceptWaiter;
	private Thread accepterThread;

	private Integer nextSUID=-1;
	private boolean isStarted = false;

	private AcceptWaiter.AcceptWaiterEvent acceptWaiterEventHandler;
	private Connection.ConnectionEvent connectionEventHandler;
	private JGServerAgent serverAgent;

	private JGServerInitData serverParameters;
	private Logic logicHandler;

	public JGServer(){
		acceptWaiterEventHandler = new AcceptWaiter.AcceptWaiterEvent() {
			public void onSocketConnected(Socket s) {
				onASocketConnected(s);
			}

			public void onSocketAcceptError(JGError error) {
				onASocketAcceptError(error);
			}
		};
		connectionEventHandler = new Connection.ConnectionEvent() {
			public void onConnectionData(ConnectionInfo connection, byte[] data) {
				onCData(connection, data);
			}

			public void onConnectionDisconnected(ConnectionInfo connection) {
				onCDisconnected(connection);
			}

			public void onConnectionError(JGError error, ConnectionInfo connection) {
				onCError(error, connection);
			}
		};
		serverAgent = new JGServerAgent() {
			public void send(ConnectionInfo info, byte[] buffer) {
				sendToConnection(info, buffer);
			}

			public void close(ConnectionInfo connectionInfo) {
				closeConnection(connectionInfo);
			}
		};
	}

	private void closeConnection(ConnectionInfo connectionInfo){
		onCDisconnected(connectionInfo);
	}

	public void start(JGServerInitData startParameters) throws IOException, StartStatusException, NullPointerException{
		if(isStarted)throw new StartStatusException();
		if(startParameters==null)throw new NullPointerException("Start parameters is nulled");

		serverParameters = startParameters;
		logicHandler = startParameters.getLogic();
		logicHandler.registerAgent(serverAgent);

		connections = new Hashtable();

		try{
			socket = new ServerSocket(startParameters.getPort());
		}catch (IOException e){
			throw e;
		}

		acceptWaiter = new AcceptWaiter(socket);
		acceptWaiter.setAcceptWaiterEventListener(acceptWaiterEventHandler);

		accepterThread = new Thread(acceptWaiter);
		accepterThread.setPriority(Thread.MIN_PRIORITY);
		accepterThread.start();
		isStarted=true;

		try{
			logicHandler.onServerStarted(getServerInfo());
		}catch (Exception e){
			//todo log this
		}

	}

	private JGServerInfo getServerInfo(){
		return  new JGServerInfo();
	}

	private void sendToConnection(ConnectionInfo info, byte[] buffer) {
		Connection connection;
		if(connections.containsKey(info.getCSID())){
			connection = connections.get(info.getCSID());
			try{
				connection.send(buffer);
			}catch (IOException e){
				//TODO what to do? Event!
			}
		}
	}

	public void checkSockets() throws StartStatusException{
		if(!isStarted)throw new StartStatusException();
		long timeStart = System.currentTimeMillis();
		Enumeration<String> keys = connections.keys();
		while (keys.hasMoreElements()){
			Connection connection =  connections.get(keys.nextElement());
			connection.checkSocket(System.currentTimeMillis()-timeStart);
		}
		//TODO in JGServInfo inject time of last checking
		//System.out.println(System.currentTimeMillis()-timeStart);
    }

	//-----------------------------------------------------------------------------
	//AcceptWaiterEvent's handlers-----------------------------------------------
    private void onASocketConnected(Socket s) {
        Connection connection;
        try{
            connection = new Connection(getNextCSID(),
										serverParameters.getBufferSize(),
										serverParameters.getTimeoutLimit(),
										serverParameters.getPingFromServerTimeOut(),
										s);

        }catch (IOException ex){
			//TODO WHATS THE FUCK!
            return;
        }

        synchronized (connections){
            connections.put(connection.getCSID(), connection);
			System.out.println("Connections: "+connections.size());
        }
		connection.setConnectionEventListener(connectionEventHandler);

		try{
			logicHandler.onClientConnected(connection.getConnectionInfo());
		}catch (Exception e){
			//TODO log this
		}

	}

    private void onASocketAcceptError(JGError error) {
		try{
			logicHandler.onServerError(error, getServerInfo());
		}catch (Exception e){
			//TODO log this
		}
    }

	//-----------------------------------------------------------------------------



	//---------------------------------------------------------------------------
	//ConnectionEvent handlers--------------------------------------------
    private void onCData(ConnectionInfo info, byte[] buffer_data) {
		try{
			logicHandler.onReceivedData(info, buffer_data);
		}catch (Exception e){
			//TODO log this
		}
    }

	private void onCDisconnected(ConnectionInfo connection) {
		//System.out.print("sdfsdfsddfsdf");
		if(connections.containsKey(connection.getCSID())){
			Connection conn = connections.remove(connection.getCSID());
			//closeConnection(connection);
			conn.close();
			try{
				logicHandler.onClientDisconnected(connection);
			}catch (Exception e){
				//TODO log this
			}
		}
	}

	private void onCError(JGError error, ConnectionInfo connection) {
		try{
			logicHandler.onClientError(error, connection);
		}catch (Exception e){
			//TODO log this
		}
	}
	//--------------------------------------------------------------------------------

	private Integer getNextCSID() {
		if(nextSUID == Integer.MAX_VALUE) nextSUID = 0;
		nextSUID++;

		return nextSUID;
	}

	public boolean serverIsStarted(){
		return isStarted;
	}

	//---------------------------------------------------------------------
	//JGServEvents---------------------------------------------------------

	public static interface JGServEvent{
		void onServerStarted(JGServerInfo info);
		void onServerError(JGError error, JGServerInfo info);

		void onClientConnected(ConnectionInfo connectionInfo);
		void onClientDisconnected(ConnectionInfo connectionInfo);
		void onReceivedData(ConnectionInfo connectionInfo, byte[] data);
		void onClientError(JGError error, ConnectionInfo connectionInfo);
	}

	//---------------------------------------------------------------------

	public static interface JGServerAgent {
		void send(ConnectionInfo connectionInfo, byte[] buffer);
		void close(ConnectionInfo connectionInfo);
	}

}
