package asema.entities;

import java.util.List;

import enums.ModifierMethodType;

import alex.Token;
import asema.exceptions.SemanticException;

public class EntryClass {

	/**
	 * Public Members
	 */
	
	public Token identificador;
	
	public EntryClass fatherClass;
	
	public boolean isInheritanceApplied;
	
	public void addAttribute(Token identifier) throws SemanticException{
		
	}
	
	public EntryVar getAttribute(String identifier){
		return null;
	}
	
	public boolean containsAttribute(String identifier){
	
		return false;
	}
	
	public void addMethod(Token identifier, Type returnType, ModifierMethodType modifierType){
		
	}
	
	public EntryMethod getMethod(String identifier){
		
		// habria que ver si puede haaber 2 metodos con el mismo nombre pero con distinto modificador
		// no recuerdo si eso era valido, en caso de ser asi habria q agregar un parametro mas
		
		return null;
	}
	
	
	public boolean containsMethod(String identifier){
		
		return false;
	}
	
	public void addConstructor(Token identifier){
		
	}
	
	public void getConstructor(){
		
	}
	
	public void applyInheritance() throws SemanticException{
		
	}
	
	/**
	 * Protected Members
	 */
	
	protected List<EntryVar> attributes;
	
	protected List<EntryMethod> methods;
	
	protected List<EntryVar> instances;
	
	protected EntryMethod constructor;
}
