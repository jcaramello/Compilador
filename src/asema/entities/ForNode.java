package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import asema.TS;
import asema.exceptions.SemanticErrorException;

public class ForNode extends SentenceNode {

	public AssignmentNode InitializationExpression;	
	public ExpressionNode LoopCondition;
	public ExpressionNode IncrementExpression;
	public BaseNode Body;
	
	public ForNode(AssignmentNode a, ExpressionNode l, ExpressionNode i, BaseNode b){
		this.InitializationExpression = a;
		this.LoopCondition = l;
		this.IncrementExpression = i;
		this.Body = b;
	}
	
	
	@Override
	public Type check() throws SemanticErrorException {
		
		int l1 = TS.getNewLabelID();
		int l2 = TS.getNewLabelID();
		
		String label1 = String.format("L%d", l1);
		String label2 = String.format("L%d", l2);
		
		InitializationExpression.check();
		
		CodeGenerator.gen(label1+ ": NOP", true);
		
		if(!LoopCondition.check().equals(PrimitiveType.Boolean))
			throw new SemanticErrorException("El tipo de la expresión condicional del for debe ser boolean.");
		
		CodeGenerator.gen(Instructions.BF, label2);
		
		Body.check();		
		
		if(!IncrementExpression.check().conforms(InitializationExpression.leftSide.Type))
			throw new SemanticErrorException("El tipo de la expresión de incremento en el for debe conformar con el de la variable de iteración.");
		
		CodeGenerator.gen(Instructions.STORE, InitializationExpression.leftSide.Offset);
		CodeGenerator.gen(Instructions.JUMP, label1);
		CodeGenerator.gen(label2 + ": NOP");
	
		
		return null; // ??

	}

}
