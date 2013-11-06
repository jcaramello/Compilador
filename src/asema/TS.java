package asema;

import java.util.HashMap;
import java.util.Map;

import common.CommonHelper;

import alex.Token;
import asema.entities.EntryClass;
import asema.entities.EntryMethod;
import asema.entities.EntryVar;
import asema.exceptions.SemanticErrorException;

/***
 * Representa la tabla de simbolos
 * @author jcaramello, nechegoyen
 *
 */
public class TS {
		
	
	/*
	 * Internal static properties
	 */
	
	private static EntryClass CurrentClass;
	
	private static Map<String, EntryClass> Classes;

	/*
	 * Public Methods
	 */
	
	/**
	 * Agrega una nueva clase a la TS, validando que no exista ninguna otra clase con el mismo nombre y cuya clase padre es Object
	 * @param classIdentifier
	 * @throws SemanticErrorException
	 */
	public static void addClass(String classIdentifier) throws SemanticErrorException{				
		if(TS.containsClass(classIdentifier))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya existe en la TS", classIdentifier));
		EntryClass newClass = new EntryClass(classIdentifier, TS.getClass("Object"));
		TS.Classes.put(classIdentifier, newClass);
	}
	
	/**
	 * Agrega una nueva clase a la TS, validando que no exista ninguna otra clase con el mismo nombre y cuya clase padre es father
	 * @param classIdentifier
	 * @param father
	 * @throws SemanticErrorException
	 */
	public static EntryClass addClass(String classIdentifier, EntryClass father)throws SemanticErrorException{				
		if(TS.containsClass(classIdentifier))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya existe en la TS", classIdentifier));
		if(!TS.containsClass(father.Name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya existe en la TS", classIdentifier));
		EntryClass newClass = new EntryClass(classIdentifier, father);
		TS.Classes.put(classIdentifier, newClass);
		return newClass;
	}
	
	/**
	 * Retorna todas las clases de la TS
	 * @return
	 */
	public static Iterable<EntryClass> getClasses(){
		return TS.Classes.values();	
	}
	
	/**
	 * Retorna la clase de nombre name
	 * @param name
	 * @return
	 */
	public static EntryClass getClass(String name){
		if(!CommonHelper.isNullOrEmpty(name))
			return TS.Classes.get(name);
		else return null;
	}
	
	/**
	 * Setea la clase actual
	 * @param identifier
	 * @throws SemanticErrorException
	 */
	public static void setCurrentClass(String identifier)throws SemanticErrorException{
		EntryClass current = TS.Classes.get(identifier);
		if(!CommonHelper.isNullOrEmpty(identifier) && current != null){
			TS.CurrentClass = current;		
		}
		else throw new SemanticErrorException(String.format("Error! - La clase %s no es una clase valida o todavia no ha sido cargada", identifier));
	}
	
	/**
	 * Retorna la clase actual
	 * @return
	 */
	public static EntryClass getCurrentClass(){
		return TS.CurrentClass;
	}
	
	/**
	 * Determina si la TS contiene o no una clase con el nombre name
	 * @param name
	 * @return
	 */
	public static boolean containsClass(String name){
		return TS.Classes.containsKey(name);	
	}
	
	/**
	 * Chequea si existe herencia circular en la TS
	 * @throws SemanticErrorException
	 */
	public static void checkCircularInheritance() throws SemanticErrorException{
		
	}	
	
	public static void applyInheritances() throws SemanticErrorException{
		
	}
		
	
	/**
	 * Realiza todas las validaciones semanticas de la TS
	 * @throws SemanticErrorException
	 */
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
