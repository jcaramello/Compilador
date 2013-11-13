package asema.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.CodeGenerator;
import common.CommonHelper;
import common.Instructions;

import enums.ModifierMethodType;
import alex.Token;
import asema.TS;
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
	
	public String inheritsFrom;
	
	public EntryClass fatherClass;
	
	public boolean isInheritanceApplied;	
	
	public boolean OffsetCalculated;
	
	/**
	 * private Members
	 */
	
	private Map<String, EntryVar> Attributes;
	
	private List<EntryVar> OrderedAttributes;
	
	private Map<String, EntryMethod> Methods;
	
	private List<EntryMethod> OrderedMethods;
		
	private Map<String, EntryVar> InstancesVariables;
	
	private EntryMethod CurrentMethod;
	
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
		this.Methods = new HashMap<String, EntryMethod>();
		this.InstancesVariables = new HashMap<String, EntryVar>();
		this.Constructor = new EntryMethod(String.format("Default_%s_Constructor", name), ModifierMethodType.Dynamic, new VoidType(), this);
	}
	
	public void addAttribute(EntryVar a) throws SemanticErrorException{
		if(this.Attributes.containsKey(a.Name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya que contiene un atributo %s.", this.Name, a.Name));
		else {
			this.Attributes.put(a.Name, a);
			this.OrderedAttributes.add(a);
		}
	}
	
	public EntryVar getAttribute(String name){
		return this.Attributes.get(name);
	}	
	
	public List<EntryVar> getAttributes(){
		return this.OrderedAttributes;
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
	
	public EntryMethod addMethod(String name, Type returnType, ModifierMethodType modifierType) throws SemanticErrorException{
		// ver como hacer para controlar repetidos. Hay que mirar modificadores tipo de retornos o solo name?
		EntryMethod entryMethod = null;
		if(this.Methods.containsKey(name))
			throw new SemanticErrorException(String.format("Error! - La clase %s ya que contiene un metodo %s.", this.Name, name));
		else{
			entryMethod = new EntryMethod(name, modifierType, returnType, this);
			this.Methods.put(name, entryMethod);
			this.OrderedMethods.add(entryMethod);
		}
		
		return entryMethod;
	}
	
	public EntryMethod getMethod(String name){
		
		// habria que ver si puede haaber 2 metodos con el mismo nombre pero con distinto modificador
		// no recuerdo si eso era valido, en caso de ser asi habria q agregar un parametro mas
		
		return this.Methods.get(name);
	}
	
	public List<EntryMethod> getMethods(){
		return this.OrderedMethods;
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
	
	public void generate() throws SemanticErrorException{
		CodeGenerator.gen(Instructions.DATA_SECTION);
		CodeGenerator.gen(Instructions.VTLabel, Instructions.NOP);
		
		TS.setCurrentClass(this.Name);		
		String[] mets = new String[this.Methods.size()];
		
		for (EntryMethod em : Methods.values()) {
			if(em.Modifier == ModifierMethodType.Dynamic)
				mets[em.Offset] = em.Name;
		}
		
		for(int i = 0; i < mets.length; i++) {
			CodeGenerator.gen(Instructions.DW, mets[i]);			
		}
		
		CodeGenerator.gen(Instructions.CODE_SECTION);
		
		for (EntryMethod em : Methods.values()) {
			em.generate();
		}

		this.Constructor.generate();
	}
	
	/**
	 * Calcula Offset
	 */
	public void calcOffsets()
	{		
		if(this.fatherClass != null)
			this.fatherClass.calcOffsets();

		// Preservo los offsets del padre de atributos en el CIR (ilustrativo; innecesario)
		for (EntryVar ev : this.fatherClass.getAttributes()) {
			this.getAttribute(ev.Name).Offset = ev.Offset;
		}			

		// Agrego los nuevos a partir de ahí
		int cantVarsAncestro = this.fatherClass.getAttributes().size();
		int cantVars = this.getAttributes().size();
		for(int i = 1; i <= cantVars; i++) {
			if(this.getAttributes().get(i).Offset == -1) 
				this.getAttributes().get(i).Offset = cantVarsAncestro + i;
		}

		// Preservo los offsets del padre de métodos en la VT (ilustrativo, sólo necesario para los redefinidos)
		for(EntryMethod em : this.fatherClass.getMethods()) {
			if(em.Modifier == ModifierMethodType.Dynamic)
				this.getMethod(em.Name).Offset = em.Offset;
		}

		// Agrego los nuevos a partir de ahí
		int cantMetodosAncestro = this.fatherClass.getMethods().size();
		int cantMetodos = this.getMethods().size();
		int off = 0;
		for(int i = 0; i < cantMetodos; i++) {
			if(this.getMethods().get(i).Modifier == ModifierMethodType.Dynamic && this.getMethods().get(i).Offset == -1){
				this.getMethods().get(i).Offset = cantMetodosAncestro + off;
				off++;
			}
			this.getMethods().get(i).calcOffsets();
		}

		this.Constructor.calcOffsets();		
		this.OffsetCalculated = true;
	}
}
