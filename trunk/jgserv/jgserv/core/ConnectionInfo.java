package jgserv.core;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 30.10.11
 * Time: 22:37
 */
public class ConnectionInfo {

	private Connection handler;

	public ConnectionInfo(Connection connection){
		handler = connection;
	}

	public String getCSID(){
		return handler.getCSID();
	}

}
