package asema.entities;

import asema.exceptions.SemanticErrorException;

public class SimpleSentenceNode extends SentenceNode {

	public ExpressionNode Expression;
	
	
	public SimpleSentenceNode(ExpressionNode exp){
		this.Expression = exp;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub
		
	}

}
