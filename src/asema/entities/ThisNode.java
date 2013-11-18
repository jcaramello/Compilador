package asema.entities;

import common.CodeGenerator;
import common.Instructions;
import enums.ModifierMethodType;

import asema.TS;
import asema.exceptions.SemanticErrorException;

public class ThisNode extends PrimaryNode {

	public Type Type;
	
	public ThisNode(Type t){
		this.Type = t;
	}
	
	public ThisNode() {
		this.Type = new ClassType(TS.getCurrentClass());
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		if(TS.getCurrentClass().getCurrentMethod().Modifier == ModifierMethodType.Static)
			throw new SemanticErrorException("No se puede acceder a this desde un método estático.");
		
		CodeGenerator.gen(Instructions.LOAD, 3);
		
		return Type;
	}

}
