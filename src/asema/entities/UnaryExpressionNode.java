package asema.entities;

import alex.Token;
import asema.exceptions.SemanticException;

public class UnaryExpressionNode extends ExpressionNode {

	public Token Operator;
	public ExpressionNode Operand;
	
	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub

	}

}
