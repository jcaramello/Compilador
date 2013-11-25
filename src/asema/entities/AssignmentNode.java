package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import asema.TS;
import asema.exceptions.SemanticErrorException;

public class AssignmentNode extends SentenceNode {

	public EntryVar leftSide;
	private ExpressionNode  rigthSide;
	
	public AssignmentNode(EntryVar left, ExpressionNode rigth)
	{
		this.leftSide = left;
		this.rigthSide = rigth;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		CodeGenerator.gen("# AssignmentNode Start");
				
		Type t = leftSide.Type;
		
		if(!rigthSide.check().conforms(t))
			throw new SemanticErrorException("El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo.");
		
		if(leftSide.esParametroLocal() || leftSide.esVariableLocal()) {
			CodeGenerator.gen(Instructions.STORE, ""+ leftSide.Offset);
		}
		else {
			CodeGenerator.gen(Instructions.LOAD, "3");
			CodeGenerator.gen(Instructions.SWAP);
			CodeGenerator.gen(Instructions.STOREREF, ""+ leftSide.Offset);
		}
			
		CodeGenerator.gen("# AssignmentNode End");
		
		return t;
	}

}
