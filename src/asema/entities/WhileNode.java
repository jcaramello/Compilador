package asema.entities;

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
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
