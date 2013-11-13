package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import asema.TS;
import asema.exceptions.SemanticErrorException;

public class WhileNode extends SentenceNode {

	public ExpressionNode LoopCondition;
	//En realidad no estoy seguro si el tipo deberia ser BaseNode o BlockNode
	// Si estoy seguro de que no es SentenceNode como esta en el uml
	public SentenceNode Body;
	
	
	public WhileNode(ExpressionNode exp, SentenceNode body){
		this.LoopCondition = exp;
		this.Body = body;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		int l1 = TS.getNewLabelID();
		int l2 = TS.getNewLabelID();

		CodeGenerator.gen("L" + l1 + ": NOP");
		
		if(!LoopCondition.check().equals(PrimitiveType.Boolean))
			throw new SemanticErrorException("El tipo de la expresión condicional del while debe ser boolean.");
		
		CodeGenerator.gen(Instructions.BF, ""+l2);
		
		Body.check();
		
		CodeGenerator.gen(Instructions.JUMP, ""+l1);
		CodeGenerator.gen("L" + l2 + ": NOP");
	
		
		return null; // ??

	}

}
