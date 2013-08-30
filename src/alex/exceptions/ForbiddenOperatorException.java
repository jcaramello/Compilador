package alex.exceptions;

//Ver Sintaxis, 2.9
public class ForbiddenOperatorException extends ALexException {
	private static final long serialVersionUID = 1L;

	private static String message = "%s no es un operador valido (ver Sintaxis 2.9) :: Linea %d";
	public ForbiddenOperatorException(String operator, int linea) {
		super(String.format(message, operator, linea));
	}
}