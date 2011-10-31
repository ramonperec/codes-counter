package jgserv.core;

import jgserv.JGServer;
import jgserv.errors.JGError;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 28.10.11
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class Logic implements JGServer.JGServEvent{

	private JGServer.JGServerAgent agent;

	public final void registerAgent(JGServer.JGServerAgent JGServerAgent){
		if(this.agent !=null){
			throw new NullPointerException("serverProxy is nulled");
		}
		this.agent = JGServerAgent;
	}

	protected final JGServer.JGServerAgent agent(){
		return agent;
	}

	public void onServerStarted(JGServerInfo info) {
		log("Server started");
	}

	public void onClientConnected(ConnectionInfo connectionInfo) {
		log("Client: csid="+connectionInfo.getCSID()+" Connected");
	}

	public void onClientDisconnected(ConnectionInfo connectionInfo) {
		log("Client: csid="+connectionInfo.getCSID()+" Disconnected");
	}

	public void onReceivedData(ConnectionInfo connectionInfo, byte[] data) {
		//count+=1;
		//log("Client:" + count +
		//		" csid="+connectionInfo.getCSID()+" recv="+new String(data));
	}

	public void onClientError(JGError error, ConnectionInfo connectionInfo) {
		log("Client: csid="+connectionInfo.getCSID()+" Error");
	}

	public void onServerError(JGError error, JGServerInfo info) {
		log("Server error");
	}

	protected void log(String s){
		System.out.println(s);
	}
}
