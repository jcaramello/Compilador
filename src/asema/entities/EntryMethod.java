package asema.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.CodeGenerator;
import common.CommonHelper;
import common.Instructions;

import asema.TS;
import asema.exceptions.SemanticErrorException;

import enums.ModifierMethodType;
import enums.OriginType;

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
	public int Offset;
	
	/*
	 * Private Members
	 */
	private Map<String, EntryVar> LocalVars;
	private Map<String, EntryVar> FormalArgs;
	private List<EntryVar> FormalArgsByIndex;
	private List<EntryVar> LocalVarsByIndex;
	private BlockNode AST;
	private EntryClass ContainerClass;
	private boolean isMain;

	
	/*
	 * Contructor
	 */			
	public EntryMethod(String _name, ModifierMethodType _modifier, Type _returnType, EntryClass containerClass){
		
		this.Name = _name;
		this.Modifier = _modifier;
		this.ReturnType = _returnType;
		this.LocalVars = new HashMap<String, EntryVar>();
		this.FormalArgs = new HashMap<String, EntryVar>();
		this.FormalArgsByIndex = new LinkedList<EntryVar>();
		this.LocalVarsByIndex = new LinkedList<EntryVar>();
		this.ContainerClass  = containerClass;
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
			else {
				this.FormalArgs.put(var.Name, var);
				this.FormalArgsByIndex.add(var);
			}
		}
	}
	
	/**
	 * Agrega las parametros formales, chequeando que no hayan parametros formales con el mismo nombre
	 * @param vars
	 * @throws SemanticErrorException
	 */
	public void addFormalArgs(Type t, String name) throws SemanticErrorException{		
		
		EntryVar var = new EntryVar(t, name);
		if(this.FormalArgs.containsKey(var.Name))
			throw new SemanticErrorException(String.format("Error! - El parametro formal %s se encuentra repetido dentro de la lista de parametros formales", var.Name));
		else {
			this.FormalArgs.put(var.Name, var);
			this.FormalArgsByIndex.add(var);
		}
	}
	
	/**
	 * Agrega las variables locales, chequeando que no hayan variables locales con el mismo nombre
	 * @param vars
	 * @throws SemanticErrorException
	 */
	public void addLocalVars(List<EntryVar> vars) throws SemanticErrorException{
		for (EntryVar var : vars) {
			if(this.LocalVars.containsKey(var.Name))
				throw new SemanticErrorException(String.format("Error! - La variable local %s se encuentra repetida dentro de la lista de variables locales", var.Name));
			else{
				this.LocalVars.put(var.Name, var);
				this.LocalVarsByIndex.add(var);
			}
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
	 *  Retorna la cantidad de variables locales
	 */
	public int getLocalVarsCant() {
		return this.LocalVars.size();
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
	 *  Retorna la cantidad de parámetros formales
	 */
	public int getFormalArgsCant() {
		return this.FormalArgs.size();
	}
	
	/**
	 * Retorna el i-ésimo parámetro formal
	 */
	public EntryVar getFormalArgByIndex(int i) {
		return this.FormalArgsByIndex.get(i);
	}
	
	/**
	 * Retorna el i-ésimo parámetro formal
	 */
	public List<EntryVar> getFormalArgs() {
		return this.FormalArgsByIndex;
	}
	
	
	/**
	 * Retorna el i-ésimo parámetro formal
	 */
	public EntryVar getLocalVarByIndex(int i) {
		return this.LocalVarsByIndex.get(i);
	}
	
	/**
	 * Retorna el i-ésimo parámetro formal
	 */
	public List<EntryVar> getLocalVars() {
		return this.LocalVarsByIndex;
	}
	
	/**
	 * Valida si existe algun parametro formal con el mismo nombre de alguna variable local
	 * @throws SemanticErrorException
	 */
	public void validateNames() throws SemanticErrorException{
		for (EntryVar fa : this.FormalArgs.values()) {
			if(this.LocalVars.containsKey(fa.Name))
				throw new SemanticErrorException(String.format("Error! - El parametro formal %s es ambiguo. intente renombralo", fa.Name));
		}
	}
	
	/**
	 * Valida si el metodo es un metodo Main bien formado
	 * @throws SemanticErrorException
	 */
	public boolean isValidMain() throws SemanticErrorException{
		isMain = false;
		if(this.Name.toLowerCase().equals("main"))
		{
			isMain = true;
			if(!this.FormalArgs.values().isEmpty())
				throw new SemanticErrorException("Error! - El metodo Main no puede contener parametros formales.");
		}
		
		return isMain;
	}
	
	/**
	 * Determina si una variable es visible o no dentro del metodo
	 * @param name
	 * @return
	 */
	public boolean isVisibleVariable(String name){		
		
		return this.LocalVars.containsValue(name)  ||
			   this.FormalArgs.containsValue(name) ||
			   this.ContainerClass.containsAttribute(name);
	}
	
	/**
	 * Determina si un metodo es visible o no dentro del metodo
	 * @param name
	 * @return
	 */
	public boolean isVisibleMethod(String name){		
		
		return false;
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
	

	/** 
	 *  Obtiene la clase a la cual pertenece
	 */
	public EntryClass getContainerClass() {
		return this.ContainerClass;
	}
	
	public void generate() throws SemanticErrorException{
		
		if(isMain)
			CodeGenerator.gen("Main__method: NOP");
		else	
			CodeGenerator.gen(String.format(Instructions.LABEL, this.ContainerClass.Name, this.Name), Instructions.NOP, true);
		
			
		CodeGenerator.gen(Instructions.LOADFP);
		CodeGenerator.gen(Instructions.LOADSP);
		CodeGenerator.gen(Instructions.STOREFP);
		
		String cantVars = Integer.toString(this.LocalVars.values().size());
		
		CodeGenerator.gen(Instructions.RMEM, cantVars);
		
		this.ContainerClass.setCurrentMethod(this);
		if(this.AST != null)
			this.AST.check();
		else generateBodyPredef();
		
		// Return por default
		
		int toFree = this.getFormalArgsCant();
		
		if(this.Modifier.equals(ModifierMethodType.Dynamic))
			toFree++;	
		if(!this.ReturnType.equals(VoidType.VoidType))
			toFree++;
		
		CodeGenerator.gen(Instructions.FMEM, ""+this.getLocalVarsCant());
		CodeGenerator.gen(Instructions.STOREFP);

		CodeGenerator.gen(Instructions.RET, ""+toFree);
		
	}
	
	/**
	 * Generate defult body
	 */
	private void generateBodyPredef(){
		if(this.Name.equals("read")) {
			CodeGenerator.gen(Instructions.FMEM, 1); 
			CodeGenerator.gen(Instructions.READ);
		} else if(this.Name.equals("println")) { 
			CodeGenerator.gen(Instructions.PRNLN);
		} else if(this.Name.equals("printB")) {
			CodeGenerator.gen(Instructions.LOAD, 3); 
			CodeGenerator.gen(Instructions.BPRINT);
		} else if(this.Name.equals("printC")) {
			CodeGenerator.gen(Instructions.LOAD, 3); 
			CodeGenerator.gen(Instructions.CPRINT); 
		} else if(this.Name.equals("printI")) {
			CodeGenerator.gen(Instructions.LOAD, 3); 
			CodeGenerator.gen(Instructions.IPRINT); 
		} else if(this.Name.equals("printS")) {
			CodeGenerator.gen(Instructions.LOAD, 3); 
			CodeGenerator.gen(Instructions.SPRINT); 
		} else if(this.Name.equals("printBln")) {
			CodeGenerator.gen(Instructions.LOAD, 3); 
			CodeGenerator.gen(Instructions.BPRINT);
			CodeGenerator.gen(Instructions.PRNLN); 
		} else if(this.Name.equals("printCln")) {
			CodeGenerator.gen(Instructions.LOAD, 3); 
			CodeGenerator.gen(Instructions.CPRINT); 
			CodeGenerator.gen(Instructions.PRNLN); 
		} else if(this.Name.equals("printIln")) {
			CodeGenerator.gen(Instructions.LOAD, 3); 
			CodeGenerator.gen(Instructions.IPRINT); 
			CodeGenerator.gen(Instructions.PRNLN); 
		} else if(this.Name.equals("printSln")) {
			CodeGenerator.gen(Instructions.LOAD, 3); 
			CodeGenerator.gen(Instructions.SPRINT);
			CodeGenerator.gen(Instructions.PRNLN); 
		}
	}
	
	/**
	 * Calcula Offset
	 * @return
	 */
	public void calcOffsets(){		

		int cantArgs = this.FormalArgs.values().size();
		for(int i = 1; i <= cantArgs; i++) {
			this.getFormalArgByIndex(i-1).Offset = cantArgs + 3 - i;
			if(this.Modifier == ModifierMethodType.Dynamic)
				this.getFormalArgByIndex(i-1).Offset++; // deja lugar para this
		}

		int cantVars = this.LocalVars.values().size();
		for(int i = 0; i < cantVars; i++) {
			this.getLocalVarByIndex(i-1).Offset = -i;
		}
		
	}
	
	/**
	 * Check declarations
	 * @throws SemanticErrorException 
	 */
	public void checkDeclarations() throws SemanticErrorException{
		for(EntryVar ev : this.LocalVars.values())
			if(!CommonHelper.isPrimitiveType(ev.Type.Name) && TS.getClass(ev.Type.Name) == null)
				throw new SemanticErrorException(String.format("Error(!). Tipo indefinido %s", ev.Type.Name));
			else ev.Origin = OriginType.Local;
	
		for(EntryVar ev : this.FormalArgs.values())
			if(!CommonHelper.isPrimitiveType(ev.Type.Name) && TS.getClass(ev.Type.Name) == null)
				throw new SemanticErrorException(String.format("Error(!). Tipo indefinido %s", ev.Type.Name));
			else ev.Origin = OriginType.Param;
	
		// Para el tipo de retorno
		if(!CommonHelper.isPrimitiveType(this.ReturnType.Name) && TS.getClass(this.ReturnType.Name) == null)
			throw new SemanticErrorException(String.format("Error(!). Tipo indefinido %s", this.ReturnType.Name));		


	}
}
