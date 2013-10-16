package asema.entities;

import asema.exceptions.SemanticException;

public class WhileNode extends SentenceNode {

	public ExpressionNode LoopCondition;
	//En realidad no estoy seguro si el tipo deberia ser BaseNode o BlockNode
	// Si estoy seguro de que no es SentenceNode como esta en el uml
	public BlockNode Body;
	
	
	public WhileNode(ExpressionNode exp, BlockNode body){
		this.LoopCondition = exp;
		this.Body = body;
	}
	
	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub

	}

}
