package asema.entities;

import asema.exceptions.SemanticErrorException;

public class PrimaryExpressionNode extends ExpressionNode {

	public PrimaryNode Value;
	
	public PrimaryExpressionNode(PrimaryNode value){
		this.Value = value; 
	}	
	
	@Override
	public Type check() throws SemanticErrorException {
		
		return PrimaryNode.check();

	}

}
