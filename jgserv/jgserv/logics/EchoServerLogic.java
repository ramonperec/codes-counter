package jgserv.logics;

import jgserv.core.ConnectionInfo;
import jgserv.core.Logic;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBoxy-Pride
 * Date: 27.10.11
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
public class EchoServerLogic extends Logic{
	@Override
	public void onReceivedData(ConnectionInfo connectionInfo, byte[] data) {
		String s = new String(data);

		s= "Echo:"+s;
		agent().send(connectionInfo, s.getBytes());
	}
}
