package alex.exceptions;

public class InvalidCharacterException extends ALexException {
	private static final long serialVersionUID = 1L;

	private static String message = "%s no es un caracter v�lido. L�nea: %s";
	private static String messageWithAdditionalInfo = "%s no es un caracter v�lido. %s :: L�nea: %s";
	
	public InvalidCharacterException(char character, int line) {
		super(String.format(message, character, line));
	}
	
	public InvalidCharacterException(String lexema, String additionalInfo,int line) {
		super(String.format(message, lexema, additionalInfo,line));
	}
}
