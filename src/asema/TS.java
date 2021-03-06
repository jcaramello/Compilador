package asema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
	
	private static Map<UUID, EntryVar> InstancesVariables;

	private static int controlLabel;
	
	public static EntryClass ObjectClass;
	
	/*
	 * Public Methods
	 */
	
	/**
	 * Agrega una nueva clase a la TS, validando que no exista ninguna otra clase con el mismo nombre y cuya clase padre es Object
	 * @param classIdentifier
	 * @throws SemanticErrorException
	 */
	public static void addClass(Token tkn) throws SemanticErrorException{				
		String classIdentifier = tkn.getLexema();
		if(TS.containsClass(classIdentifier))
			throw new SemanticErrorException(String.format("Error(!) - La clase %s ya existe en la TS. Linea %d", classIdentifier, tkn.getLinea()));
		EntryClass newClass = new EntryClass(tkn, TS.getClass("Object"));
		TS.Classes.put(classIdentifier, newClass);
	}
	
	/**
	 * Agrega una nueva clase a la TS, validando que no exista ninguna otra clase con el mismo nombre y cuya clase padre es father
	 * @param classIdentifier
	 * @param father
	 * @throws SemanticErrorException
	 */
	public static EntryClass addClass(Token tkn, EntryClass father)throws SemanticErrorException{				
		String classIdentifier = tkn.getLexema();
		if(TS.containsClass(classIdentifier))
			throw new SemanticErrorException(String.format("Error(!) - La clase %s ya existe en la TS. Linea %d", classIdentifier, tkn.getLinea()));
		if(!TS.containsClass(father.Name))
			throw new SemanticErrorException(String.format("Error(!) - La clase %s ya existe en la TS. Linea %d", classIdentifier, tkn.getLinea()));
		EntryClass newClass = new EntryClass(tkn, father);
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
	 * Execute semantics controls, generate code
	 * @throws SemanticErrorException 
	 */
	public static void  execute() throws SemanticErrorException
	{				
		TS.checkDeclarations();
		TS.validate();
		TS.checkCircularInheritance();
		TS.applyInheritances();
		TS.calcOffsets();
		TS.generate();
		// Este queda al final por si acaso.
		CodeGenerator.gen(Instructions.HALT);
	}
	
	/**
	 * Genera el codigo para cada una de las clases
	 * junto con los offset 
	 * @throws SemanticErrorException 
	 */
	public static void generate() throws SemanticErrorException
	{		
		for (EntryClass c : TS.getClasses()) {
			c.generate();
		}			
	}
	
	/**
	 * Chequea si existe herencia circular en la TS
	 * @throws SemanticErrorException
	 */
	public static void checkCircularInheritance() throws SemanticErrorException{
		for(EntryClass ec: TS.getClasses()) {
			EntryClass c = ec.fatherClass;
			while(c!= null) {
				if(c.Name.equals(ec.Name))
					throw new SemanticErrorException("Existe herencia circular en " + ec.Name);
				c = c.fatherClass;
			}			
		}
	}	
	
	/**
	 * applyInheritances
	 * @throws SemanticErrorException
	 */
	public static void applyInheritances() throws SemanticErrorException{		
		for(EntryClass ec : TS.getClasses())
			if(!CommonHelper.isPredefinedClass(ec.Name))
				ec.applyInheritance();
	}	
	
	/**
	 * Check Declarations
	 * @throws SemanticErrorException 
	 */
	public static void checkDeclarations() throws SemanticErrorException{
		for(EntryClass ec : TS.getClasses())
			if(!CommonHelper.isPredefinedClass(ec.Name))
				ec.checkDeclarations();
	}
	
	
	/**
	 * Realiza todas las validaciones semanticas de la TS
	 * @throws SemanticErrorException
	 */
	public static void validate() throws SemanticErrorException{
		boolean existsMain = false;
		int totalNumberOfMainMethods = 0;
		for (EntryClass ec : TS.getClasses()) {
			for (EntryMethod em : ec.getMethods()) {
				em.validateNames();
				boolean isValidMain = em.isValidMain();
				if(isValidMain)
					totalNumberOfMainMethods++;
				existsMain = existsMain || isValidMain;
			}			
			ec.getConstructor().validateNames();			
		}
		
		if(!existsMain)
			throw new SemanticErrorException("Error(!). Alguna clase debe contener al menos un metodo main sin parametros"); 
		if(totalNumberOfMainMethods > 1)
			throw new SemanticErrorException("Error(!). No puede haber mas de un metodo main.");
	}
	
	/**
	 * Inicializa las estructuras basicas de la clase
	 * Debe ser invocado antes de utilizar cualquier otro metodo
	 * 
	 * NOTA: DEBE LLAMARSE ANTES DE PROCESSAR LA EDT. Esto es asi por que es necesario
	 * que esten inicializadas las estructuras basicas antes de comenzar a processar la edt
	 * @throws SemanticErrorException 
	 */
	public static void initialize() throws SemanticErrorException{
		TS.Classes = new HashMap<String, EntryClass>();
		TS.InstancesVariables = new HashMap<UUID, EntryVar>();
		// Object class debe inicializarse antes que System
		TS.jumpToMain();
		TS.initializePredefRoutines();
		TS.initializeObjectClass();
		TS.initializeSystemClass();
		controlLabel = 0;
	}
	
	private static void jumpToMain() {
		
		CodeGenerator.gen(Instructions.CODE_SECTION, true);
		CodeGenerator.gen(Instructions.RMEM, "1");
		CodeGenerator.gen(Instructions.PUSH, "Main__method");
		CodeGenerator.gen(Instructions.CALL);
		CodeGenerator.gen(Instructions.HALT);
	}
	
	private static void initializePredefRoutines() {
		
		// Rutina malloc
		
		CodeGenerator.gen(Instructions.CODE_SECTION, true);
		CodeGenerator.gen("LMALLOC: NOP", true);
		CodeGenerator.gen(Instructions.LOADFP);
		CodeGenerator.gen(Instructions.LOADSP);
		CodeGenerator.gen(Instructions.STOREFP);
		CodeGenerator.gen("LOADHL");
		CodeGenerator.gen(Instructions.DUP);
		CodeGenerator.gen(Instructions.PUSH, "1");
		CodeGenerator.gen(Instructions.ADD);
		CodeGenerator.gen(Instructions.STORE, "4");
		CodeGenerator.gen(Instructions.LOAD, "3");
		CodeGenerator.gen(Instructions.ADD);
		CodeGenerator.gen("STOREHL");
		CodeGenerator.gen(Instructions.STOREFP);
		CodeGenerator.gen(Instructions.RET, "1");
	}

	/**
	 * Incrementa el n�mero global de label de control (para if, while, for) y devuelve el nuevo a usar 
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
		
		/*
		if(var == null && currentClass.fatherClass != null){
			var = TS.findInheritedAttribute(identificador, currentClass.fatherClass); 
		}*/
		
		// Se decidio que se el llamador quien debe verificar que si identificador no representa nada, entonces debe producirse un error semantico  		
		// identificador podria ser alguna otra cosa, como por ej, un identificador de clase en una llamada calificada y yo desde aca no lo se
		// el que llama a var tendra que seguir buscando y decidir cuando no se ha encontrado nada y corresponde disparar la excepcion
		
		return var;	
	}
	
	
	public static EntryVar findInheritedAttribute(String name, EntryClass clase){
		
		EntryVar var = clase.getAttribute(name);
		if(var == null && clase.fatherClass != null)
			var = TS.findInheritedAttribute(name, clase.fatherClass);
		
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
		EntryClass objectClass = new EntryClass(new Token("Object"), null);
		objectClass.inheritsFrom = null;
		objectClass.isInheritanceApplied = true;
		objectClass.fatherClass = null;			
		TS.ObjectClass = objectClass;
	
		TS.Classes.put("Object", objectClass);
	}
	
	/**
	 * Initializa la clase System
	 */
	private static void initializeSystemClass() throws SemanticErrorException
	{		
		EntryClass systemClass = new EntryClass(new Token("System"), TS.getClass("Object"));
		EntryClass objectClass = TS.Classes.get("Object");
		systemClass.inheritsFrom = objectClass.Name;
		systemClass.isInheritanceApplied = true;
		systemClass.fatherClass = objectClass;					
	
		systemClass.addMethod(new Token("read"), PrimitiveType.Int, ModifierMethodType.Static);
		systemClass.addMethod(new Token("printB"), VoidType.VoidType, ModifierMethodType.Static).addFormalArgs(PrimitiveType.Boolean, "b");
		systemClass.addMethod(new Token("printC"), VoidType.VoidType, ModifierMethodType.Static).addFormalArgs(PrimitiveType.Char, "c");
		systemClass.addMethod(new Token("printI"), VoidType.VoidType, ModifierMethodType.Static).addFormalArgs(PrimitiveType.Int, "i");
		systemClass.addMethod(new Token("printS"), VoidType.VoidType, ModifierMethodType.Static).addFormalArgs(PrimitiveType.String, "s");
		systemClass.addMethod(new Token("println"), VoidType.VoidType, ModifierMethodType.Static);
		systemClass.addMethod(new Token("printBln"), VoidType.VoidType, ModifierMethodType.Static).addFormalArgs(PrimitiveType.Boolean, "b");
		systemClass.addMethod(new Token("printCln"), VoidType.VoidType, ModifierMethodType.Static).addFormalArgs(PrimitiveType.Char, "c");
		systemClass.addMethod(new Token("printIln"), VoidType.VoidType, ModifierMethodType.Static).addFormalArgs(PrimitiveType.Int, "i");
		systemClass.addMethod(new Token("printSln"), VoidType.VoidType, ModifierMethodType.Static).addFormalArgs(PrimitiveType.String, "s");		
		
		TS.Classes.put("System", systemClass);
	}	

	/**
	 * Calcula los offsets
	 */
	private static void calcOffsets(){
		for (EntryClass ec : TS.getClasses()) {
			if(!ec.OffsetCalculated && ec != TS.ObjectClass)
				ec.calcOffsets();
		}
	}
}
