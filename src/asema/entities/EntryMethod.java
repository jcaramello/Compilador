package asema.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asema.exceptions.SemanticException;

import enums.ModifierMethodType;

public class EntryMethod extends EntryBase {
	
	/**
	 * Public Members
	 */
	public String Identifier;
	public ModifierMethodType Modifier;
	public Type ReturnType;	
	public boolean IsContructor;
	
	/**
	 * Private Members
	 */
	private Map<String, EntryVar> LocalVars;
	private Map<String, EntryVar> FormalArgs;
	
	/**
	 * Contructor
	 */		
	
	public EntryMethod(String _identifier, ModifierMethodType _modifier, Type _returnType){
		
		this.Identifier = _identifier;
		this.Modifier = _modifier;
		this.ReturnType = _returnType;
		this.LocalVars = new HashMap<String, EntryVar>();
		this.FormalArgs = new HashMap<String, EntryVar>();
	
	}
	
	
	/**
	 * Public Methods
	 */
	
	public void addFormalArgs(List<EntryVar> args){
		
	}
	
	public void addLocalVars(List<EntryVar> vars){
			
	}
	
	public EntryVar getLocalVar(String identifier){
		return new EntryVar(null, identifier);
	}
	
	public void checkNames() throws SemanticException{
		
	}
	
	public void addAST(BlockNode nodo){
		
	}
}
