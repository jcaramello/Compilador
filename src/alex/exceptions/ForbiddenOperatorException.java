package alex.exceptions;

//Ver Sintaxis, 2.9
public class ForbiddenOperatorException extends ALexException {
	private static final long serialVersionUID = 1L;

	public ForbiddenOperatorException(String msg) {
		super(msg);
	}
}