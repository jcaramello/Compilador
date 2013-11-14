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
		
		InitializationExpression.check();
		
		CodeGenerator.gen("L" + l1 + ": NOP");
		
		if(!LoopCondition.check().equals(PrimitiveType.Boolean))
			throw new SemanticErrorException("El tipo de la expresión condicional del for debe ser boolean.");
		
		CodeGenerator.gen(Instructions.BF, ""+l2);
		
		Body.check();
		
		if(!IncrementExpression.check().conforms(InitializationExpression.leftSide.Type))
			throw new SemanticErrorException("El tipo de la expresión de incremento en el for debe conformar con el de la variable de iteración.");
		
		CodeGenerator.gen(Instructions.JUMP, ""+l1);
		CodeGenerator.gen("L" + l2 + ": NOP");
	
		
		return null; // ??

	}

}
