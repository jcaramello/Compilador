package asema.entities;

import asema.exceptions.SemanticErrorException;

public class PrimaryExpressionNode extends ExpressionNode {

	public PrimaryNode Value;
	
	public PrimaryExpressionNode(PrimaryNode value){
		this.Value = value; 
	}	
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
