package alex.exceptions;



public class InvalidIdentifierException extends ALexException {
	private static final long serialVersionUID = 1L;
	private static String message = "%s no es un identificador válido :: Línea: %d";
	public InvalidIdentifierException(String lexema, int line) {
		super(String.format(message, lexema, line));
	}
}