package alex.exceptions;

public class ForbiddenWordException extends ALexException {
	private static final long serialVersionUID = 1L;
	
	private static String message = "La palabra %s está prohibida en el lenguaje. Puede tratarse de una palabra Java prohibida por compatibilidad, o de una palabra reservada para futuras extensiones :: Línea: %d";

	public ForbiddenWordException(String lexema, int line) {
		super(String.format(message, lexema, line));
	}
}

