package jgserv.core;

import jgserv.errors.JGError;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 25.10.11
 * Time: 6:10
 * To change this template use File | Settings | File Templates.
 */
public class AcceptWaiter implements Runnable{

	private ServerSocket socket;
	private AcceptWaiterEvent event;

	public AcceptWaiter(ServerSocket s){
		socket = s;

	}

	public void run() {
		Socket s = null;
		JGError error = null;
		while (!socket.isClosed()){
			try{
				s = socket.accept();
			}
			catch (SecurityException secEx){
				error = JGError.getErrorByType(JGError.ACCEPT_SECURITY);
			}
			catch (SocketTimeoutException socTEx){
				error = JGError.getErrorByType(JGError.ACCEPT_TIMEOUT);
			}
			catch (IllegalBlockingModeException illEx){
				error = JGError.getErrorByType(JGError.ACCEPT_ILLEGAL_BLOCKING);
			}
			catch (IOException ioEx){
				error = JGError.getErrorByType(JGError.ACCEPT_IO);
			}

			if(error!=null){
				dispatchError(error);
				error = null;
				break;
			}

			if(s!=null){
				dispatchEvent(s);
			}else{
				dispatchError(JGError.getErrorByType(JGError.ACCEPT_RECV_NULL_SOCKET));
			}
		}
	}

	private void dispatchEvent(Socket socket) {
		if(event!=null){
			event.onSocketConnected(socket);
		}
	}

    private void dispatchError(JGError error) {
		if(event!=null){
			event.onSocketAcceptError(error);
		}
	}


	public AcceptWaiterEvent getAcceptWaiterEventListener() {
		return event;
	}

	public void setAcceptWaiterEventListener(AcceptWaiterEvent event) {
		this.event = event;
	}

	public static interface AcceptWaiterEvent {

		void onSocketConnected(Socket s);
        void onSocketAcceptError(JGError error);

	}

}



