package asint;

public class UnexpectedTokenException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UnexpectedTokenException(String msg) {
		super(msg);
	}
}
