package asema.entities;

import asema.exceptions.SemanticErrorException;

public class ReturnNode extends SentenceNode {

	public ExpressionNode Expression;
	
	public ReturnNode(ExpressionNode exp){
		this.Expression = exp;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub
		
	}

}
