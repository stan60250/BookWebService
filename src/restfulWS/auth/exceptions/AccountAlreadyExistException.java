package restfulWS.auth.exceptions;

public class AccountAlreadyExistException extends Exception {
	public AccountAlreadyExistException() {
		super("error.accountalreadyexist");
	}
}
