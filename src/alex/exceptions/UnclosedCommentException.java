package alex.exceptions;

public class UnclosedCommentException extends ALexException {
	private static final long serialVersionUID = 1L;

	private static String message = "Se llegó al final del archivo sin encontrar el cierre de comentario. El comentario fue abierto en la línea %d";
	public UnclosedCommentException(int line) {
		super(String.format(message, line));
	}
}

