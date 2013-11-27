package alex;

import enums.TokenType;

/**
 * Token
 * @author jcaramello, nechegoyen
 *
 */
public class Token {
	
	public Token(){}
	
	public Token(String lex){
		this.lexema = lex;
		this.tokenType = TokenType.NotSet;
		this.linea = -1;
	}
	
	/**
	 * Token Type
	 */
	private TokenType tokenType;
	
	/**
	 * Lexema
	 */
	private String lexema;
	
	/**
	 * linea
	 */
	private int linea;
	
	
	/** Setter/Getter **/
	
	public TokenType getTokenType() {
		return tokenType;
	}

	public String getLexema() {
		return lexema;
	}

	public int getLinea() {
		return linea;
	}

	/**
	 * Default Constructor
	 * @param token
	 * @param lexema
	 * @param linea
	 */
	public Token(TokenType token, String lexema, int linea) {
		super();
		this.tokenType = token;
		this.lexema = lexema;
		this.linea = linea;
	}
}

