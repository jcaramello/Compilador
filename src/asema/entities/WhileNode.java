package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import asema.TS;
import asema.exceptions.SemanticErrorException;

public class WhileNode extends SentenceNode {

	public ExpressionNode LoopCondition;
	//En realidad no estoy seguro si el tipo deberia ser BaseNode o BlockNode
	// Si estoy seguro de que no es SentenceNode como esta en el uml
	public BaseNode Body;
	
	
	public WhileNode(ExpressionNode exp, BaseNode body){
		this.LoopCondition = exp;
		this.Body = body;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		int l1 = TS.getNewLabelID();
		int l2 = TS.getNewLabelID();

		String label1 = String.format("L%d", l1);
		String label2 = String.format("L%d", l2);
		
		CodeGenerator.gen(label1 + ": NOP", true);
		
		if(!LoopCondition.check().equals(PrimitiveType.Boolean))
			throw new SemanticErrorException("El tipo de la expresión condicional del while debe ser boolean.");
		
		CodeGenerator.gen(Instructions.BF, label2);
		
		Body.check();
		
		CodeGenerator.gen(Instructions.JUMP, label1);
		CodeGenerator.gen(label2 + ": NOP", true);
	
		
		return null; // ??

	}

}
