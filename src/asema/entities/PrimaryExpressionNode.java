package asema.entities;

import asema.exceptions.SemanticException;

public class PrimaryExpressionNode extends ExpressionNode {

	public PrimaryNode Value;
	
	public PrimaryExpressionNode(PrimaryNode value){
		this.Value = value; 
	}	
	
	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub

	}

}
