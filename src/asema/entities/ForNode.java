package asema.entities;

import asema.exceptions.SemanticErrorException;

public class ForNode extends SentenceNode {

	public AssignmentNode InitializationExpression;	
	public ExpressionNode LoopCondition;
	public ExpressionNode IncrementExpression;
	public SentenceNode Body;
	
	public ForNode(AssignmentNode a, ExpressionNode l, ExpressionNode i, SentenceNode b){
		this.InitializationExpression = a;
		this.LoopCondition = l;
		this.IncrementExpression = i;
		this.Body = b;
	}
	
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
