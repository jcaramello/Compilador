package asema.entities;

import alex.Token;
import asema.exceptions.SemanticErrorException;

public class UnaryExpressionNode extends ExpressionNode {

	public Token Operator;
	public ExpressionNode Operand;
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
