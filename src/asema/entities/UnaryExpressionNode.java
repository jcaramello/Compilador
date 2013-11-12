package asema.entities;

import alex.Token;
import asema.exceptions.SemanticErrorException;

public class UnaryExpressionNode extends ExpressionNode {

	public Token Operator;
	public ExpressionNode Operand;
	
	public UnaryExpressionNode(ExpressionNode op, Token tkn){
		this.Operand = op;
		this.Operator = tkn;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
