package jgserv.errors;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 31.10.11
 * Time: 1:26
 */
public class JGError {

	public final  static int NO_ERROR=0;
	public final  static int ACCEPT_IO=1;
	public final  static int ACCEPT_SECURITY=2;
	public final  static int ACCEPT_TIMEOUT=3;
	public final  static int ACCEPT_ILLEGAL_BLOCKING=4;
	public final  static int ACCEPT_RECV_NULL_SOCKET=5;


	private int code;
	private String message;

	private JGError(int code, String message){
		this.code = code;
		this.message = message;
	}

	private static HashMap<Integer, String> errors;

	private static void initErrors(){
		errors = new HashMap<Integer, String>();
		errors.put(NO_ERROR, "No error. Something going wrong =(");
		errors.put(ACCEPT_ILLEGAL_BLOCKING, "this socket has an associated channel, the channel is in non-blocking mode, and there is no connection ready to be accepted");
		errors.put(ACCEPT_IO, "I/O error occurs when waiting for a connection");
		errors.put(ACCEPT_SECURITY, "security manager exists and its checkAccept method doesn't allow the operation");
		errors.put(ACCEPT_TIMEOUT, "timeout was previously set with setSoTimeout and the timeout has been reached");
		errors.put(ACCEPT_RECV_NULL_SOCKET, "After accept socket = null");
	}

	public static JGError getErrorByType(int code){
		if(errors==null)initErrors();
		String errMessage = errors.get(code);
		return new JGError(code, errMessage);
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
