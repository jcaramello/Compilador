package asema;

import java.util.Map;

import alex.Token;
import asema.entities.EntryClass;
import asema.exceptions.SemanticException;

/***
 * Representa la tabla de simbolos
 * @author jcaramello, nechegoyen
 *
 */
public class TS {
		
	
	/**
	 * Internal static properties
	 */
	
	private static EntryClass currentClass;

	/**
	 * Public Methods
	 */
	
	public static void addClass(Token clase){
		// recibe el token por el tema del numero de linea, q nose si servira para algo
		// pero por si las moscas creo q deberiamos guardarlo
	}
	
	public static Map<String, EntryClass> getClasses(){
		return null;	
	}
	
	public static void setCurrentClass(String identificador){
		
	}
	
	public static boolean contains(String identificador){
		return true;	
	}
	
	public static void checkCircularInheritance() throws SemanticException{
		
	}
	
	public static void checkMain() throws SemanticException{
		
	}
	
	
	public static void applyInheritances() throws SemanticException{
		
	}
}
