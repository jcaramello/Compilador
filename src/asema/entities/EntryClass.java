package asema.entities;

import java.util.HashMap;
import java.util.Map;

import common.CommonHelper;

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
	 * private Members
	 */
	
	private Map<String, EntryVar> Attributes;
	
	private Map<String, EntryMethod> Methods;
		
	private Map<String, EntryVar> InstancesVariables;
	
	private EntryMethod CurrentMethod;
	
	/*
	 * Esto creo que es mejor tomar la convencion de q un constructor es un metodo
	 * ya que en realidad las clases terminan siendo lo mismo. Ademas facilita el dise�o de bloque nodo
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
		this.Methods = new HashMap<String, EntryMethod>();
		this.InstancesVariables = new HashMap<String, EntryVar>();
		this.Constructor = new EntryMethod(String.format("Default_%s_Constructor", name), ModifierMethodType.Dynamic, new VoidType(), this);
	}
	
	public void addAttribute(String name) throws SemanticErrorException{
		if(this.Attributes.containsKey(name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya que contiene un atributo %s.", this.Name, name));
		else this.Attributes.put(name, new EntryVar(null, name));		
	}
	
	public EntryVar getAttribute(String name){
		return this.Attributes.get(name);
	}
	
	public void addInstanceVariable(String name) throws SemanticErrorException{
		if(this.InstancesVariables.containsKey(name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya que contiene una variable de instancia %s.", this.Name, name));
		if(this.Methods.containsKey(name) || name.equals(this.Name))
			throw new SemanticErrorException("Ninguna clase puede definir variables de instancia con el mismo nombre que ella o que alguno de sus metodos.");
		else this.InstancesVariables.put(name, new EntryVar(new ClassType(this), name));		
	}
	
	public EntryVar getInstanceVariable(String name){
		if(!CommonHelper.isNullOrEmpty(name))
			return this.InstancesVariables.get(name);
		else return null;
	}
	
	public Iterable<EntryVar> getInstances(){
		return this.InstancesVariables.values();
	}
	
	public boolean containsAttribute(String name){	
		return this.Attributes.containsKey(name);
	}
	
	public void addMethod(String name, Type returnType, ModifierMethodType modifierType) throws SemanticErrorException{
		// ver como hacer para controlar repetidos. Hay que mirar modificadores tipo de retornos o solo name?
		if(this.Methods.containsKey(name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya que contiene un metodo %s.", this.Name, name));
		else this.Methods.put(name, new EntryMethod(name, modifierType, returnType, this));		
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
	
	public void setCurrentMethod(EntryMethod current){
		if(current != null)
			this.CurrentMethod = current;
	}
	
	public EntryMethod getCurrentMethod(){
		return this.CurrentMethod;
	}
	
	public void applyInheritance() throws SemanticErrorException{
		
	}	
}
