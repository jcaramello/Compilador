package alex;

public class Token {
	private String token;
	private String lexema;
	private int linea;
	
	public String getToken() {
		return token;
	}

	public String getLexema() {
		return lexema;
	}

	public int getLinea() {
		return linea;
	}

	public Token(String token, String lexema, int linea) {
		super();
		this.token = token;
		this.lexema = lexema;
		this.linea = linea;
	}
}

