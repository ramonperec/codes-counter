package jgserv.logics;

import jgserv.core.ConnectionInfo;
import jgserv.core.Logic;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 31.10.11
 * Time: 2:47
 */
public class FlashServerLogic extends Logic{
	private String REQUEST = "<policy-file-request/>";
	private String policyFileContains;
	private byte[] policyFileBinary;

	public FlashServerLogic(String policyFileContains){
		this.policyFileContains = policyFileContains;
		policyFileBinary = policyFileContains.getBytes();
	}

	@Override
	public final void onReceivedData(ConnectionInfo connectionInfo, byte[] data) {
		String inp = new String(data);
		//System.out.println(inp);
		if(inp == REQUEST){
			agent().send(connectionInfo, policyFileBinary);
		}else{
			onReceivedData_logic(connectionInfo, data);
		}

	}

	@Override
	public void onClientConnected(ConnectionInfo connectionInfo) {

	}

	protected void onReceivedData_logic(ConnectionInfo info, byte[] data) {

	}

	public static String getPolicyFileAnswer(){
		//TODO norm realization
		return "<cross-domain-policy>\n" +
				"   <allow-access-from domain=\"*\" to-ports=\"2305\"/>\n" +
				"</cross-domain-policy>";
	}

}
