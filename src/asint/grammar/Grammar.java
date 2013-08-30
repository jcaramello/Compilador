package asint.grammar;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import enums.SymbolType;

/**
 * Grammar class
 * @author jcaramello, enechegoyen
 *
 */
public class Grammar {	

	//Private Properties
	private static Grammar current;

	/**
	 * First Set
	 */
	private Dictionary<String, List<String>> Firsts;
	
	/**
	 * Follows Set
	 */
	private Dictionary<String, List<String>> Follows;
	
	/**
	 * Grammar's Productions
	 */
	private List<Production> Productions;
	
	/**
	 * Private Constructor - Singleton Instance
	 */
	private Grammar(){
		initialize();
	}
	
	// Private Methods
	
	/**
	 * Initialize the Grammar
	 */
	private void initialize(){
		getCurrent().CreateGrammarProductions();
		getCurrent().CalculateFirsts();
		getCurrent().CalculateFollows();
	}
	
	/**
	 * Get the current instance of grammar
	 * @return
	 */
	private static Grammar getCurrent(){
		if(current == null)
			current = new Grammar();
		
		return current;
	}
	
	/**
	 * Create the grammar's productions
	 */
	private void CreateGrammarProductions(){
		Grammar grammar = getCurrent();
		
		grammar.Productions.add(new Production("StmtFor", new Object[][]{
														{SymbolType.Terminal,"for"},
														{SymbolType.Terminal, "("},
														{SymbolType.NonTerminal, "Expr"},
														{SymbolType.Terminal, ";"},
														{SymbolType.NonTerminal, "Expr"},
														{SymbolType.Terminal, ";"},
														{SymbolType.NonTerminal, "Expr"},
														{SymbolType.Terminal, ";"},
														{SymbolType.Terminal, ")"},
														{SymbolType.Terminal, "{"},
														{SymbolType.NonTerminal, "Stmt"},
														{SymbolType.Terminal, "}"}														
												}));
		
		// El resto de las producciones de la gramaticas
	}
	
	/**
	 * Calculate the Firsts Sets for the Symbols of the current grammar
	 */
	private void CalculateFirsts(){
		// Ver algoritmo en el libro
	}
	
	/**
	 * Calculate the Follow Set for the Symbols of the current grammar
	 */
	private void CalculateFollows(){
		// Ver algoritmo en el libro
	}
	
	// Public Static Members
	
	/**
	 * Get the First set of the parameter passed in
	 * @param x
	 */
	public static List<String> Firsts(String x){
		return new ArrayList<String>();
	}
	
	/**
	 * Get the Follow set of the parameter passed in
	 * @param x
	 */
	public static List<String> Follows(String x){
		return new ArrayList<String>();	
	}
	
	/**
	 * Check if x Match with some of the grammar's productions
	 * @param x
	 * @return
	 */
	public static boolean match(String x){
		return true;		
	}
	
	/**
	 * Get the grammar's productions
	 * @return
	 */
	public static List<Production> getProductions(){
		return getCurrent().Productions;
	}
	
}
