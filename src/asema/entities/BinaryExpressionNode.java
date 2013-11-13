package asema.entities;

import common.CodeGenerator;

import enums.TokenType;
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
	public PrimitiveType check() throws SemanticErrorException {
		
		PrimitiveType toRet = null;
		Type ti = LeftOperand.check();
		Type td = RigthOperand.check();
		
		if(Operator.getTokenType() == TokenType.PlusOperator || Operator.getTokenType() == TokenType.RestOperator
				|| Operator.getTokenType() == TokenType.MultiplierOperator || Operator.getTokenType() == TokenType.DivisionOperator
				|| Operator.getTokenType() == TokenType.ModOperator)
		{
			if(!ti.equals(PrimitiveType.Int) || !td.equals(PrimitiveType.Int))
				throw new SemanticErrorException("El tipo de los operandos +, -, *, / o % debe ser entero.");
			else toRet = new PrimitiveType("Int");	
		}
		
		if(Operator.getTokenType() == TokenType.AndOperator || Operator.getTokenType() == TokenType.OrOperator) {
			if(!ti.equals(PrimitiveType.Boolean) || !td.equals(PrimitiveType.Boolean))
				throw new SemanticErrorException("El tipo de los operandos && o || debe ser boolean.");
			else toRet = new PrimitiveType("Boolean");	
		}
				
		if(Operator.getTokenType() == TokenType.LessOperator || Operator.getTokenType() == TokenType.LessOrEqualOperator
				|| Operator.getTokenType() == TokenType.GratherOperator || Operator.getTokenType() == TokenType.GratherOrEqualOperator)
		{
			if(!ti.equals(PrimitiveType.Int) || !td.equals(PrimitiveType.Int))
				throw new SemanticErrorException("El tipo de los operandos >, >=, <, <= debe ser entero.");
			else toRet = new PrimitiveType("Boolean");	
		}
		
		if(Operator.getTokenType() == TokenType.EqualOperator || Operator.getTokenType() == TokenType.DistinctOperator) {
			if(!ti.conforms(td) && !td.conforms(ti))
				throw new SemanticErrorException("El tipo de los operandos == o != debe conformar.");
			else toRet = new PrimitiveType("Boolean");	
		}
		
		if(Operator.getTokenType() == TokenType.PlusOperator) CodeGenerator.gen("ADD");
		if(Operator.getTokenType() == TokenType.RestOperator) CodeGenerator.gen("SUB");
		if(Operator.getTokenType() == TokenType.MultiplierOperator) CodeGenerator.gen("MUL");
		if(Operator.getTokenType() == TokenType.DivisionOperator) CodeGenerator.gen("DIV");
		if(Operator.getTokenType() == TokenType.AndOperator) CodeGenerator.gen("AND");
		if(Operator.getTokenType() == TokenType.OrOperator) CodeGenerator.gen("OR");
		if(Operator.getTokenType() == TokenType.GratherOperator) CodeGenerator.gen("GT");
		if(Operator.getTokenType() == TokenType.GratherOrEqualOperator) CodeGenerator.gen("GE");
		if(Operator.getTokenType() == TokenType.LessOperator) CodeGenerator.gen("LT");
		if(Operator.getTokenType() == TokenType.LessOrEqualOperator) CodeGenerator.gen("LE");
		if(Operator.getTokenType() == TokenType.EqualOperator) CodeGenerator.gen("EQ");
		if(Operator.getTokenType() == TokenType.DistinctOperator) CodeGenerator.gen("NE");
		if(Operator.getTokenType() == TokenType.ModOperator) CodeGenerator.gen("MOD");
		
		return toRet;
	}

}
