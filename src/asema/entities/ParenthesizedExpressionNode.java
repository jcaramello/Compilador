package asema.entities;

import asema.exceptions.SemanticErrorException;

public class ParenthesizedExpressionNode extends PrimaryNode {

	private ExpressionNode expression;
	
	public ParenthesizedExpressionNode(ExpressionNode e){
		this.expression = e;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
