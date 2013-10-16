package asema.entities;

import asema.exceptions.SemanticException;

public class SimpleSentenceNode extends SentenceNode {

	public ExpressionNode Expression;
	
	
	public SimpleSentenceNode(ExpressionNode exp){
		this.Expression = exp;
	}
	
	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub
		
	}

}
