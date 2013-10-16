package asema.entities;

import asema.exceptions.SemanticException;

public class ReturnNode extends SentenceNode {

	public ExpressionNode Expression;
	
	public ReturnNode(ExpressionNode exp){
		this.Expression = exp;
	}
	
	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub
		
	}

}
