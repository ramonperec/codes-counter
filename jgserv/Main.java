import jgserv.JGServer;
import jgserv.JGServerInitData;
import jgserv.exceptions.StartStatusException;
import jgserv.logics.FlashServerLogic;
import jgserv.logics.SimpleChat;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 23.10.11
 * Time: 6:49
 * To change this template use File | Settings | File Templates.
 */
public class Main {

	public static void main (String args []){
	    //System.out.print("Hello world");

		JGServer s = new JGServer();
		JGServerInitData serverInitData = new JGServerInitData();

		serverInitData.setLogic(new SimpleChat(FlashServerLogic.getPolicyFileAnswer()));
		serverInitData.setTimeoutLimit(20000);
		serverInitData.setPingFromServerTimeOut(11000);

		try{
		    s.start(serverInitData);
		}catch (StartStatusException stEx){
			System.out.print("JGS:start: "+stEx.getMessage());
		}catch (NullPointerException npEx){
			System.out.print("JGS:start: "+npEx.getMessage());
		}catch (IOException ex){
			System.out.print("JGS:start: "+ex.getMessage());
		}

		while(true){

			try{s.checkSockets();}catch (Exception e){}
		}
	}

}
