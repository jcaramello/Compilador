package alex.exceptions;

public class InvalidStringException extends ALexException {
	private static final long serialVersionUID = 1L;
	
	private static String message = "String invalido. %s. Linea :: %d";
	public InvalidStringException(String addtionalInfo, int linea) {
		super(String.format(message, addtionalInfo, linea));
	}
}
