package asema.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asema.exceptions.SemanticErrorException;

import enums.ModifierMethodType;

public class EntryMethod extends EntryBase {
	
	/**
	 * Public Members
	 */
	public String Name;
	public ModifierMethodType Modifier;
	public Type ReturnType;	
	public boolean IsContructor;
	
	/**
	 * Private Members
	 */
	private Map<String, EntryVar> LocalVars;
	private Map<String, EntryVar> FormalArgs;
	private BlockNode AST;
	
	/**
	 * Contructor
	 */			
	public EntryMethod(String _name, ModifierMethodType _modifier, Type _returnType){
		
		this.Name = _name;
		this.Modifier = _modifier;
		this.ReturnType = _returnType;
		this.LocalVars = new HashMap<String, EntryVar>();
		this.FormalArgs = new HashMap<String, EntryVar>();	
	}	
	
	/**
	 * Public Methods
	 */
	
	public void addFormalArgs(List<EntryVar> args) throws SemanticErrorException{
		for (EntryVar var : args) {
			if(this.FormalArgs.containsKey(var.Name))
				throw new SemanticErrorException(String.format("Error! - El parametro formal %s se encuentra repetido dentro de la lista de parametros formales", var.Name));
			else this.FormalArgs.put(var.Name, var);
		}
	}
	
	public void addLocalVars(List<EntryVar> vars) throws SemanticErrorException{
		for (EntryVar var : vars) {
			if(this.FormalArgs.containsKey(var.Name))
				throw new SemanticErrorException(String.format("Error! - La variable local %s se encuentra repetida dentro de la lista de variables locales", var.Name));
			else this.FormalArgs.put(var.Name, var);
		}
	}
	
	public EntryVar getLocalVar(String name){
		return this.LocalVars.get(name);
	}
	
	public EntryVar getFormalArg(String name){
		return this.FormalArgs.get(name);
	}
	
	public void validateNames() throws SemanticErrorException{
		for (EntryVar fa : this.FormalArgs.values()) {
			if(this.LocalVars.containsKey(fa.Name));
				throw new SemanticErrorException(String.format("Error! - El parametro formal %s es ambiguo. intente renombralo", fa.Name));
		}
	}
	
	public void isValidMain() throws SemanticErrorException{
		if(this.Name.toLowerCase().equals("main"))
		{
			if(!this.FormalArgs.values().isEmpty())
				throw new SemanticErrorException("Error! - El metodo Main no puede contener parametros formales.");
		}
	}
	
	public void addAST(BlockNode node){
		if(node != null)
			this.AST = node;
	}
	
	public BlockNode getAST(){
		return this.AST;
	}
}
