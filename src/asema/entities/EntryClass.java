package asema.entities;

import java.util.HashMap;
import java.util.Map;

import enums.ModifierMethodType;
import alex.Token;
import asema.exceptions.SemanticErrorException;

/**
 * Entry Class
 * @author jcaramello, nechegoyen
 *
 */
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
	
	public void addAttribute(String name) throws SemanticErrorException{
		if(this.Attributes.containsKey(name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya que contiene un atributo %s.", this.Name, name));
		else this.Attributes.put(name, new EntryVar(null, name));		
	}
	
	public EntryVar getAttribute(String name){
		return this.Attributes.get(name);
	}
	
	public boolean containsAttribute(String name){	
		return this.Attributes.containsKey(name);
	}
	
	public void addMethod(String name, Type returnType, ModifierMethodType modifierType) throws SemanticErrorException{
		// ver como hacer para controlar repetidos. Hay que mirar modificadores tipo de retornos o solo name?
		if(this.Methods.containsKey(name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya que contiene un metodo %s.", this.Name, name));
		else this.Methods.put(name, new EntryMethod(name, modifierType, returnType));		
	}
	
	public EntryMethod getMethod(String name){
		
		// habria que ver si puede haaber 2 metodos con el mismo nombre pero con distinto modificador
		// no recuerdo si eso era valido, en caso de ser asi habria q agregar un parametro mas
		
		return this.Methods.get(name);
	}
	
	public Iterable<EntryMethod> getMethods(){
		return this.Methods.values();
	}
	
	public boolean containsMethod(String name){		
		return this.Methods.containsKey(name);
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
