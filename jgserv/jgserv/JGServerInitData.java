package jgserv;

import jgserv.core.Logic;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 28.10.11
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class JGServerInitData {

	private int port = 2305;
	private InetAddress inetAddress;
	private int BufferSize = 512;
	private Logic logic;
	private long timeoutLimit = 5000;
	private long pingFromServerTimeOut = 2500;


	public JGServerInitData(){
		logic = new Logic();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	public int getBufferSize() {
		return BufferSize;
	}

	public void setBufferSize(int bufferSize) {
		BufferSize = bufferSize;
	}

	public Logic getLogic() {
		return logic;
	}

	public void setLogic(Logic logic) {
		this.logic = logic;
	}

	public long getTimeoutLimit() {
		return timeoutLimit;
	}

	public void setTimeoutLimit(long timeoutLimit) {
		this.timeoutLimit = timeoutLimit;
	}

	public void setPingFromServerTimeOut(long timout) {
		pingFromServerTimeOut = timout;
	}

	public long getPingFromServerTimeOut() {
		return pingFromServerTimeOut;
	}
}
