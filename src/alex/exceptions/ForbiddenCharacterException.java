package alex.exceptions;

public class ForbiddenCharacterException extends ALexException {
	private static final long serialVersionUID = 1L;

	private static String message = "El caracter (%s) no es válido en este contexto :: Linea %d"; 
	public ForbiddenCharacterException(char character, int line) {
		super(String.format(message, character, line));
	}
}
