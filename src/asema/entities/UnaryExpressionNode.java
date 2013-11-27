package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import enums.TokenType;
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
	public PrimitiveType check() throws SemanticErrorException {
		
		if(Operator.getTokenType() == TokenType.PlusOperator || Operator.getTokenType() == TokenType.RestOperator)
		{
			if(!Operand.check().equals(PrimitiveType.Int))
				throw new SemanticErrorException("El tipo del operando de + o - debe ser un entero, en línea " + Operator.getLinea()) ;
			else
			{
				if(Operator.getTokenType() == TokenType.RestOperator)
				{
					CodeGenerator.gen(Instructions.NEG);
					return PrimitiveType.Int;
				}
			}
		}
		
		if(Operator.getTokenType() == TokenType.NotOperator) {
			if(!Operand.check().equals(PrimitiveType.Boolean))
				throw new SemanticErrorException("El tipo del operando de ! debe ser un boolean, en línea " + Operator.getLinea());
			else {
				CodeGenerator.gen(Instructions.NOT);
				return PrimitiveType.Boolean;
			}
		}
		
		return null; 
	}
	
	

}
