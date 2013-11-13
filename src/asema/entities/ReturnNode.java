package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import enums.ModifierMethodType;
import asema.TS;
import asema.exceptions.SemanticErrorException;

public class ReturnNode extends SentenceNode {

	public ExpressionNode Expression;
	
	public ReturnNode(ExpressionNode exp){
		this.Expression = exp;
	}
	
	@Override
	public Type check() throws SemanticErrorException {		
		
		EntryMethod met = TS.getCurrentClass().getCurrentMethod();
		Type t = met.ReturnType;
		
		if(!met.ReturnType.equals(VoidType.VoidType) && !met.IsContructor) {
			if(!Expression.check().conforms(met.ReturnType))
				throw new SemanticErrorException("El tipo de la expresi�n de retorno de un m�todo debe conformar al tipo del m�todo.");
			
			CodeGenerator.gen(Instructions.STORE, "" + (met.getFormalArgsCant() + 3)); // esto est� bien?
		}	
		
		int toFree = met.getFormalArgsCant();
		
		if(met.Modifier.equals(ModifierMethodType.Dynamic))
			toFree++;
		
		CodeGenerator.gen(Instructions.FMEM, ""+met.getLocalVarsCant());
		CodeGenerator.gen(Instructions.STOREFP);
		
		if(!met.ReturnType.equals(VoidType.VoidType))
			toFree++;
		
		CodeGenerator.gen(Instructions.RET, ""+toFree);
		
		return met.ReturnType;	
	}
	

}
