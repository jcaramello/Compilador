package asema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import common.CodeGenerator;
import common.CommonHelper;
import common.Instructions;
import enums.ModifierMethodType;

import alex.Token;
import asema.entities.EntryClass;
import asema.entities.EntryMethod;
import asema.entities.EntryVar;
import asema.entities.PrimitiveType;
import asema.entities.VoidType;
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

	private static int controlLabel;
	
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
	 * Inicializa las classes default y demas cosas de la TS,
	 * Ademas Genera los offset de cada clase y por ultimo el codigo intermedio
	 * @throws SemanticErrorException 
	 */
	public static void generate() throws SemanticErrorException
	{
		initialize();
		
		for (EntryClass c : TS.getClasses()) {
			c.generate();
		}
		
		CodeGenerator.gen(Instructions.HALT);
	}
	
	/**
	 * Chequea si existe herencia circular en la TS
	 * @throws SemanticErrorException
	 */
	public static void checkCircularInheritance() throws SemanticErrorException{
		
	}	
	
	/**
	 * applyInheritances
	 * @throws SemanticErrorException
	 */
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
			}			
			ec.getConstructor().validateNames();			
		}
	}
	
	/**
	 * Inicializa las estructuras basicas de la clase
	 * Debe ser invocado antes de utilizar cualquier otro metodo
	 * @throws SemanticErrorException 
	 */
	public static void initialize() throws SemanticErrorException{
		TS.Classes = new HashMap<String, EntryClass>();
		// Object class debe inicializarse antes que System
		TS.initializeObjectClass();
		TS.initializeSystemClass();
		controlLabel = 0;
	}
	
	/**
	 * Incrementa el número global de label de control (para if, while, for) y devuelve el nuevo a usar 
	 */
	public static int getNewLabelID() {
		controlLabel++;
		return controlLabel;
	}

	public static EntryVar findVar(String identificador) throws SemanticErrorException
	{		
		EntryClass currentClass = TS.getCurrentClass();
		EntryMethod currentMethod = currentClass.getCurrentMethod();
		
		EntryVar var = currentMethod.getLocalVar(identificador);
		if(var == null) 
			var = currentMethod.getFormalArg(identificador);		
		if(var == null)
			var = currentClass.getAttribute(identificador);
		if(var == null)
			throw new SemanticErrorException(String.format("Error(!). %s no es un identificador valido.", identificador));
		return var;	
	}
	
	/*
	 * Private Methods
	 */
	
	/**
	 * Initializa la clase Object
	 */
	private static void initializeObjectClass()
	{
		// TODO: ver de agregar los metdos y cosas default que tiene la clase object
		EntryClass objectClass = new EntryClass("Object", null);
		objectClass.inheritsFrom = null;
		objectClass.isInheritanceApplied = true;
		objectClass.fatherClass = null;				
		TS.Classes.put("Object", objectClass);
	}
	
	/**
	 * Initializa la clase System
	 */
	private static void initializeSystemClass() throws SemanticErrorException
	{
		// TODO: ver de agregar los metdos y cosas default que tiene la clase System
		EntryClass systemClass = new EntryClass("System", TS.getClass("Object"));
		EntryClass objectClass = TS.Classes.get("Object");
		systemClass.inheritsFrom = objectClass.Name;
		systemClass.isInheritanceApplied = true;
		systemClass.fatherClass = objectClass;					
	
		systemClass.addMethod("read", new PrimitiveType("int"), ModifierMethodType.Static);
		systemClass.addMethod("printB", new VoidType(), ModifierMethodType.Static).addFormalArgs(new PrimitiveType("boolean"), "b");
		systemClass.addMethod("printC", new VoidType(), ModifierMethodType.Static).addFormalArgs(new PrimitiveType("char"), "c");
		systemClass.addMethod("printI", new VoidType(), ModifierMethodType.Static).addFormalArgs(new PrimitiveType("int"), "i");
		systemClass.addMethod("printS", new VoidType(), ModifierMethodType.Static).addFormalArgs(new PrimitiveType("String"), "s");
		systemClass.addMethod("println", new VoidType(), ModifierMethodType.Static);
		systemClass.addMethod("printBln", new VoidType(), ModifierMethodType.Static).addFormalArgs(new PrimitiveType("boolean"), "b");
		systemClass.addMethod("printCln", new VoidType(), ModifierMethodType.Static).addFormalArgs(new PrimitiveType("char"), "c");
		systemClass.addMethod("printIln", new VoidType(), ModifierMethodType.Static).addFormalArgs(new PrimitiveType("int"), "i");
		systemClass.addMethod("printSln", new VoidType(), ModifierMethodType.Static).addFormalArgs(new PrimitiveType("String"), "s");		
		
		TS.Classes.put("System", systemClass);
	}
	

	/**
	 * Calcula los offsets
	 */
	private static void calcOffsets(){
		for (EntryClass ec : TS.getClasses()) {
			if(!ec.OffsetCalculated)
				ec.calcOffsets();
		}
	}
}
