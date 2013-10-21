package asema;

import java.util.HashMap;
import java.util.Map;

import common.CommonHelper;

import alex.Token;
import asema.entities.EntryClass;
import asema.entities.EntryMethod;
import asema.exceptions.SemanticErrorException;

/***
 * Representa la tabla de simbolos
 * @author jcaramello, nechegoyen
 *
 */
public class TS {
		
	
	/**
	 * Internal static properties
	 */
	
	private static EntryClass CurrentClass;
	
	private static Map<String, EntryClass> Classes;

	/**
	 * Public Methods
	 */
	
	public static void addClass(String classIdentifier) throws SemanticErrorException{				
		if(TS.containsClass(classIdentifier))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya existe en la TS", classIdentifier));
		EntryClass newClass = new EntryClass(classIdentifier, TS.getClass("Object"));
		TS.Classes.put(classIdentifier, newClass);
	}
	
	public static void addClass(String classIdentifier, EntryClass father)throws SemanticErrorException{				
		if(TS.containsClass(classIdentifier))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya existe en la TS", classIdentifier));
		if(!TS.containsClass(father.Name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya existe en la TS", classIdentifier));
		EntryClass newClass = new EntryClass(classIdentifier, father);
		TS.Classes.put(classIdentifier, newClass);
	}
	
	public static Iterable<EntryClass> getClasses(){
		return TS.Classes.values();	
	}
	
	public static EntryClass getClass(String name){
		if(!CommonHelper.isNullOrEmpty(name))
			return TS.Classes.get(name);
		else return null;
	}
	
	public static void setCurrentClass(String identifier)throws SemanticErrorException{
		EntryClass current = TS.Classes.get(identifier);
		if(!CommonHelper.isNullOrEmpty(identifier) && current != null){
			TS.CurrentClass = current;		
		}
		else throw new SemanticErrorException(String.format("Error! - La clase %s no es una clase valida o todavia no ha sido cargada", identifier));
	}
	
	public static boolean containsClass(String identifier){
		return TS.Classes.containsKey(identifier);	
	}
	
	public static void checkCircularInheritance() throws SemanticErrorException{
		
	}	
	
	public static void applyInheritances() throws SemanticErrorException{
		
	}
	
	public static void checkDeclarations() throws SemanticErrorException{
		
		
	}
	
	public static void validate() throws SemanticErrorException{
		for (EntryClass ec : TS.getClasses()) {
			for (EntryMethod em : ec.getMethods()) {
				em.validateNames();
				em.isValidMain();
				em.getAST().check();
			}
			
			ec.getConstructor().validateNames();
			ec.getConstructor().getAST().check();
		}
	}
	
	/**
	 * Inicializa las estructuras basicas de la clase
	 * Debe ser invocado antes de utilizar cualquier otro metodo
	 */
	public static void initialize(){
		TS.Classes = new HashMap<String, EntryClass>();
		TS.initializeObjectClass();
		TS.initializeSystemClass();
	}
	
	
	/*
	 * Private Methods
	 */
	
	private static void initializeObjectClass()
	{
		// TODO: ver de agregar los metdos y cosas default que tiene la clase object
		EntryClass objectClass = new EntryClass("Object", null);
		TS.Classes.put("Object", objectClass);
	}
	
	private static void initializeSystemClass()
	{
		// TODO: ver de agregar los metdos y cosas default que tiene la clase System
		EntryClass systemClass = new EntryClass("System", TS.getClass("Object"));
		TS.Classes.put("System", systemClass);
	}
}
