package jgserv.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: BigLittleBox
 * Date: 31.10.11
 * Time: 1:05
 */
public class StartStatusException extends Exception {
	public StartStatusException(){
		super("Instance of JGServer is already started");
	}
}
