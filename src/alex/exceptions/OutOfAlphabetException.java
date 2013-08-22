package alex.exceptions;

public class OutOfAlphabetException extends ALexException {
	private static final long serialVersionUID = 1L;

	private static String message = "El caracter (%s) no es válido en el alfabeto del lenguaje :: Linea %s"; 
	public OutOfAlphabetException(char character, int line) {
		super(String.format(message, character, line));
	}
}
