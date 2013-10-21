package asema.entities;

import java.util.HashMap;
import java.util.Map;

import enums.ModifierMethodType;
import alex.Token;
import asema.exceptions.SemanticErrorException;

public class EntryClass extends EntryBase{

	/**
	 * Public Members
	 */
	
	public String Name;	
	
	public EntryClass fatherClass;
	
	public boolean isInheritanceApplied;
	
	
	/**
	 * Protected Members
	 */
	
	protected Map<String, EntryVar> Attributes;
	
	protected Map<String, EntryMethod> Methods;
	
	protected Map<String, EntryVar> Instances;
	
	/*
	 * Esto creo que es mejor tomar la convencion de q un constructor es un metodo
	 * ya que en realidad las clases terminan siendo lo mismo. Ademas facilita el diseño de bloque nodo
	 * ya que sino, hay q tener q diferenciar en bloque nodo si el ast pertenece a un constructor o un metodo.
	 * Agrege un flag boolean en la clase de entry method para poder diferenciarlo en caso de
	 * que lo necesitemos mas adelante.
	 */
	protected EntryMethod Constructor;
	
	/**
	 * Constructor
	 */
	public EntryClass(String name, EntryClass father){
		
		this.Name = name;
		this.Attributes = new HashMap<String, EntryVar>();
		this.Instances = new HashMap<String, EntryVar>();
		this.Methods = new HashMap<String, EntryMethod>();
		this.Constructor = new EntryMethod(String.format("Default_%s_Constructor", name), ModifierMethodType.Dynamic, new VoidType());
	}
	
	public void addAttribute(Token identifier) throws SemanticErrorException{
		
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
	
	public Iterable<EntryMethod> getMethods(){
		return this.Methods.values();
	}
	
	public boolean containsMethod(String identifier){
		
		return false;
	}
	
	public void addConstructor(EntryMethod constructor){
		if(constructor != null)
			this.Constructor = constructor;
	}
	
	public EntryMethod getConstructor(){
		return this.Constructor;
	}
	
	public void applyInheritance() throws SemanticErrorException{
		
	}	
}
