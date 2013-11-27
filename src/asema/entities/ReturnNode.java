package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import enums.ModifierMethodType;
import asema.TS;
import asema.exceptions.SemanticErrorException;

public class ReturnNode extends SentenceNode {
	
	public alex.Token Token;
	public ExpressionNode Expression;
	public EmptySentenceNode EmptyReturn;
	
	public ReturnNode(ExpressionNode exp, alex.Token tkn){
		this.Expression = exp;
		this.Token = tkn;
	}
	
	public ReturnNode(EmptySentenceNode exp, alex.Token tkn){
		this.EmptyReturn = exp;
		this.Token = tkn;
	}
	
	@Override
	public Type check() throws SemanticErrorException {		
		
		EntryMethod met = TS.getCurrentClass().getCurrentMethod();
		Type t = met.ReturnType;
		
		if(met.ReturnType == VoidType.VoidType && this.Expression != null && this.EmptyReturn == null) 
			throw new SemanticErrorException(String.format("Error(!) - Sentencia return invalida. El tipo de retorno del metodo es void. Linea %d", this.Token.getLinea()));
		
		if(!met.ReturnType.equals(VoidType.VoidType) && !met.IsDefaultContructor) {
			if(!Expression.check().conforms(met.ReturnType))
				throw new SemanticErrorException("El tipo de la expresión de retorno de un método debe conformar al tipo del método");
			
			CodeGenerator.gen(Instructions.STORE, met.getFormalArgsCant() + 4); // esto está bien?
		}	
		
		int toFree = met.getFormalArgsCant();
		
		if(met.Modifier.equals(ModifierMethodType.Dynamic))
			toFree++;
		
		CodeGenerator.gen(Instructions.FMEM, met.getLocalVarsCant());
		CodeGenerator.gen(Instructions.STOREFP);
		
		// Simplemente no se reservó ese espacio antes
		//if(!met.ReturnType.equals(VoidType.VoidType))
		//	toFree++;
		
		CodeGenerator.gen(Instructions.RET, toFree);
		
		return met.ReturnType;	
	}
	

}
