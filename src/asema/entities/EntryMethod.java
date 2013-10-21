package asema.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asema.exceptions.SemanticErrorException;

import enums.ModifierMethodType;

/**
 * Entry Method
 * @author jcaramello, nechegoyen
 *
 */
public class EntryMethod extends EntryBase {
	
	/*
	 * Public Members
	 */
	public String Name;
	public ModifierMethodType Modifier;
	public Type ReturnType;	
	public boolean IsContructor;
	
	/*
	 * Private Members
	 */
	private Map<String, EntryVar> LocalVars;
	private Map<String, EntryVar> FormalArgs;
	private BlockNode AST;
	
	/*
	 * Contructor
	 */			
	public EntryMethod(String _name, ModifierMethodType _modifier, Type _returnType){
		
		this.Name = _name;
		this.Modifier = _modifier;
		this.ReturnType = _returnType;
		this.LocalVars = new HashMap<String, EntryVar>();
		this.FormalArgs = new HashMap<String, EntryVar>();	
	}	
	
	/*
	 * Public Methods
	 */
	
	
	/**
	 * Agrega las parametros formales, chequeando que no hayan parametros formales con el mismo nombre
	 * @param vars
	 * @throws SemanticErrorException
	 */
	public void addFormalArgs(List<EntryVar> args) throws SemanticErrorException{
		for (EntryVar var : args) {
			if(this.FormalArgs.containsKey(var.Name))
				throw new SemanticErrorException(String.format("Error! - El parametro formal %s se encuentra repetido dentro de la lista de parametros formales", var.Name));
			else this.FormalArgs.put(var.Name, var);
		}
	}
	
	/**
	 * Agrega las variables locales, chequeando que no hayan variables locales con el mismo nombre
	 * @param vars
	 * @throws SemanticErrorException
	 */
	public void addLocalVars(List<EntryVar> vars) throws SemanticErrorException{
		for (EntryVar var : vars) {
			if(this.FormalArgs.containsKey(var.Name))
				throw new SemanticErrorException(String.format("Error! - La variable local %s se encuentra repetida dentro de la lista de variables locales", var.Name));
			else this.FormalArgs.put(var.Name, var);
		}
	}
	
	/**
	 * retorna las variables locales
	 * @param name
	 * @return
	 */
	public EntryVar getLocalVar(String name){
		return this.LocalVars.get(name);
	}
	
	/**
	 * Retorna los parametros formales
	 * @param name
	 * @return
	 */
	public EntryVar getFormalArg(String name){
		return this.FormalArgs.get(name);
	}
	
	/**
	 * Valida si existe algun parametro formal con el mismo nombre de alguna variable local
	 * @throws SemanticErrorException
	 */
	public void validateNames() throws SemanticErrorException{
		for (EntryVar fa : this.FormalArgs.values()) {
			if(this.LocalVars.containsKey(fa.Name));
				throw new SemanticErrorException(String.format("Error! - El parametro formal %s es ambiguo. intente renombralo", fa.Name));
		}
	}
	
	/**
	 * Valida si el metodo es un metodo Main bien formado
	 * @throws SemanticErrorException
	 */
	public void isValidMain() throws SemanticErrorException{
		if(this.Name.toLowerCase().equals("main"))
		{
			if(!this.FormalArgs.values().isEmpty())
				throw new SemanticErrorException("Error! - El metodo Main no puede contener parametros formales.");
		}
	}
	
	/**
	 * Agrega el nodo bloque del AST
	 * @param node
	 */
	public void addAST(BlockNode node){
		if(node != null)
			this.AST = node;
	}
	
	/**
	 * Obtiene el nodo Bloque
	 * @return
	 */
	public BlockNode getAST(){
		return this.AST;
	}
}
