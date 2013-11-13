package asema.entities;

import common.CodeGenerator;

import asema.TS;
import asema.exceptions.SemanticErrorException;

public class ThisNode extends PrimaryNode {

	public Type Type;
	
	public ThisNode(Type t){
		this.Type = t;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		if(TS.getCurrentClass().getCurrentMethod().Modifier.equals("static"))
			throw new SemanticErrorException("No se puede acceder a this desde un método estático.");
		
		CodeGenerator.gen("LOAD 3");
		
		return Type;
	}

}
