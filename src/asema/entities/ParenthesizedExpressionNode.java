package asema.entities;

import asema.exceptions.SemanticErrorException;

public class ParenthesizedExpressionNode extends PrimaryNode {

	private ExpressionNode expression;
	
	public ParenthesizedExpressionNode(ExpressionNode e){
		this.expression = e;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		

		return expression.check();

	}

}
