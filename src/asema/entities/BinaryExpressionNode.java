package asema.entities;

import alex.Token;
import asema.exceptions.SemanticErrorException;

public class BinaryExpressionNode extends ExpressionNode {

	public Token Operator;
	public ExpressionNode RigthOperand;
	public ExpressionNode LeftOperand;
	
	public BinaryExpressionNode(ExpressionNode rigthOperand, ExpressionNode leftOperand, Token operator){
		this.Operator = operator;
		this.RigthOperand = rigthOperand;
		this.LeftOperand = leftOperand;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}