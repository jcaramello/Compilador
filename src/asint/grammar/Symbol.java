package asint.grammar;

import enums.SymbolType;

/**
 * Symbol Class
 * @author jcaramello, nechegoyen
 *
 */
public class Symbol {


	/**
	 * Symbol Type
	 */
	public SymbolType Type;
	
	/**
	 * Value
	 */
	public String Value;

	/**
	 * Default Constructor
	 * @param type
	 * @param value
	 */
	public Symbol(SymbolType type, String value){
		this.Type = type;
		this.Value = value;
	}
}
