package alex.exceptions;



public class InvalidIdentifierException extends ALexException {
	private static final long serialVersionUID = 1L;

	public InvalidIdentifierException(String msg) {
		super(msg);
	}
}