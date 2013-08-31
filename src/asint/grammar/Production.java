package asint.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import common.Logger;
import enums.SymbolType;

/**
 * Production Class 
 * @author jcaramello, nechegoyen
 *
 */
public class Production {

	// Public Members;	
	
	/**
	 * Production's Head
	 */
	public String Head;
	
	/**
	 * Production's body
	 */
	public List<Symbol> Body;
	
	/**
	 * Default Constructor
	 * @param Head
	 * @param bodyMembers
	 */
	public Production(String Head, Object[]... bodyMembers){
		this.Head = Head;
		this.Body = new ArrayList(bodyMembers.length);
				
		for (Object[] pair : bodyMembers) {
			Logger.verbose("Creando Produccion %s -> %s", Head, (String)pair[1]);
			this.Body.add(new Symbol((SymbolType)pair[0], (String)pair[1]));
		}			
	}
}
