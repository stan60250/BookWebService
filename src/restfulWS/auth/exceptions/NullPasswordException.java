package restfulWS.auth.exceptions;

public class NullPasswordException extends Exception {
	
	public NullPasswordException() {
		super("error.nullpassword");
	}
}
