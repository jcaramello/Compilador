package asema;

import java.util.List;

import common.AttributeManager;
import enums.AttributeType;
import enums.ModifierMethodType;

import alex.Token;
import asema.entities.*;
import asema.exceptions.SemanticErrorException;

/***
 * Proporciona metodos helper para la construccion de los ast y para realizar
 * los chequeos correspondientes en la primera pasada que se realiza en el asint
 * @author jcaramello, enchegoyen
 *
 */
public class EDTHelper {

	/**
	 * Agrega una clase chequeando si existe o no. En caso de que exista dispara un excepcion
	 * @throws SemanticErrorException 
	 */
	public static void addClass(Token tkn) throws SemanticErrorException{
		if(TS.containsClass(tkn.getLexema()))
			throw new SemanticErrorException(String.format("Error (!). La clase %s ya existe. Linea %s", tkn.getLexema(), Integer.toString(tkn.getLinea())));
		else {
			TS.addClass(tkn.getLexema());
			TS.setCurrentClass(tkn.getLexema());
		}
	}
	
	/**
	 * Agrega un metodo chequeando si existe o no. En caso de que exista dispara un excepcion
	 * @throws SemanticErrorException 
	 */
	public static void addMethod(Token tkn) throws SemanticErrorException{
		if(TS.getCurrentClass().containsMethod(tkn.getLexema()))
			throw new SemanticErrorException(String.format("Error (!). El metodo %s ya existe. Linea %s", tkn.getLexema(), Integer.toString(tkn.getLinea())));
		else {			
			Type returnType = (Type)AttributeManager.getCurrent().getSynthesizedAttribute(AttributeType.TypeMethod_Type).Value;
			ModifierMethodType modifierType = (ModifierMethodType) AttributeManager.getCurrent().getSynthesizedAttribute(AttributeType.ModMethod_Mod).Value;
			EntryMethod entryMethod = TS.getCurrentClass().addMethod(tkn.getLexema(), returnType, modifierType);
			TS.getCurrentClass().setCurrentMethod(entryMethod);
		}
	}
	
	/**
	 * Agrega los parametros formales tanto a los metodos como a los constructores
	 * @param method
	 * @throws SemanticErrorException
	 */
	public static void addArgsFormals(EntryMethod method) throws SemanticErrorException
	{
		List<EntryVar> args = (List<EntryVar>) AttributeManager.getCurrent().getSynthesizedAttribute(AttributeType.ArgsFormales_Args).Value;
		method.addFormalArgs(args);			
	}
	
	/**
	 * Agrega los variables locales tanto a los metodos como a los constructores
	 * @param method
	 * @throws SemanticErrorException
	 */
	public static void addLocalVars(EntryMethod method)throws SemanticErrorException
	{
		List<EntryVar> vars = (List<EntryVar>) AttributeManager.getCurrent().getSynthesizedAttribute(AttributeType.VarsLocales_Vars).Value;
		method.addLocalVars(vars);			
	}
	
	/**
	 * Agrega el nodo ast tanto a los metodos como a los constructores
	 * @param method
	 * @throws SemanticErrorException
	 */
	public static void addAST(EntryMethod method)throws SemanticErrorException
	{
		BlockNode node = (BlockNode) AttributeManager.getCurrent().getSynthesizedAttribute(AttributeType.Bloque_AST).Value;
		method.addAST(node);			
	}
	
	/**
	 * Agrega un constructor a la clase actual
	 * @param tkn
	 * @throws SemanticErrorException
	 */
	public static void addConstructor(Token tkn) throws SemanticErrorException
	{
		EntryClass currentClass = TS.getCurrentClass();
		if(currentClass.Name.equals(tkn.getLexema())){
			EntryMethod ctor = new EntryMethod(tkn.getLexema(), ModifierMethodType.Static, new ClassType(currentClass), currentClass);
			currentClass.addConstructor(ctor);
		}
		else throw new SemanticErrorException(String.format("Error (!). El nombre del constructor debe ser identico a la clase. Linea %s",Integer.toString(tkn.getLinea())));
			
	}
	
}
