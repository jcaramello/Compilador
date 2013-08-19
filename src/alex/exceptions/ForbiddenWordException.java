package alex.exceptions;

public class ForbiddenWordException extends ALexException {
	private static final long serialVersionUID = 1L;

	public ForbiddenWordException(String msg) {
		super(msg);
	}
}

