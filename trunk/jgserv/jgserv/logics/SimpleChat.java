package jgserv.logics;

import jgserv.core.ConnectionInfo;
import jgserv.core.JGServerInfo;
import jgserv.errors.JGError;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 31.10.11
 * Time: 7:29
 */
public class SimpleChat extends FlashServerLogic{

	private Hashtable<String, ConnectionInfo> connections;

	public SimpleChat(String policyFileContains) {
		super(policyFileContains);
		connections = new Hashtable<String, ConnectionInfo>();
	}

	@Override
	protected void onReceivedData_logic(ConnectionInfo info, byte[] data) {
		Enumeration<String> keys = connections.keys();
		while (keys.hasMoreElements()){
			ConnectionInfo connection =  connections.get(keys.nextElement());
			agent().send(connection, data);
		}
	}

	@Override
	public void onClientConnected(ConnectionInfo connectionInfo) {
		connections.put(connectionInfo.getCSID(), connectionInfo);
	}

	@Override
	public void onClientDisconnected(ConnectionInfo connectionInfo) {
		connections.remove(connectionInfo.getCSID());
	}
}
