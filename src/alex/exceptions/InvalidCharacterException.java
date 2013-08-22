package alex.exceptions;

public class InvalidCharacterException extends ALexException {
	private static final long serialVersionUID = 1L;

	private static String message = "%s no es un caracter válido. Línea: %s";
	private static String messageWithAdditionalInfo = "%s no es un caracter válido. %s :: Línea: %s";
	
	public InvalidCharacterException(char character, int line) {
		super(String.format(message, character, line));
	}
	
	public InvalidCharacterException(String lexema, String additionalInfo,int line) {
		super(String.format(message, lexema, additionalInfo,line));
	}
}
