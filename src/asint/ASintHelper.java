package asint;

import enums.TokenType;
import alex.Token;

public class ASintHelper {

	public static boolean isFollowExpressionQ(Token t){
		return t.getTokenType() == TokenType.SemicolonSymbol;
	}
	
	public static boolean isFollowExpression(Token t){
		return t.getTokenType() == TokenType.ClosedParenthesisSymbol ||
			   isFollowExpressionQ(t);
	}
	
	public static boolean isFollowExpressionAux(Token t){
		return isFollowExpression(t) ||
			   t.getTokenType() == TokenType.OpenParenthesisSymbol;
	}
	
	public static boolean isFollowExpressionOrAux(Token t){
		return t.getTokenType() == TokenType.OrOperator ||
			   isFollowExpressionAux(t);
	}
	
	public static boolean isFollowExpressionAndAux(Token t){
		return t.getTokenType() == TokenType.AndOperator ||
			   isFollowExpressionOrAux(t);
	}
	
	public static boolean isFollowExpressionCompAux(Token t){
		return t.getTokenType() == TokenType.DistinctOperator ||
			   t.getTokenType() == TokenType.EqualOperator ||
			   isFollowExpressionAndAux(t);
	}
	
	public static boolean isFollowExpressionSRAux(Token t){
		return t.getTokenType() == TokenType.GratherOperator ||
			   t.getTokenType() == TokenType.GratherOrEqualOperator ||
			   t.getTokenType() == TokenType.LessOperator ||
			   t.getTokenType() == TokenType.LessOrEqualOperator ||
			   isFollowExpressionCompAux(t);
	}
	
	public static boolean isFollowTermino(Token t){
		return t.getTokenType() == TokenType.PlusOperator ||
			   t.getTokenType() == TokenType.RestOperator ||
			   isFollowExpressionSRAux(t);
	}
	
	public static boolean isFollowFactor(Token t){
		return t.getTokenType() == TokenType.MultiplierOperator ||
				   t.getTokenType() == TokenType.DivisionOperator ||
				   t.getTokenType() == TokenType.ModOperator ||
				   isFollowTermino(t);
	}
			
	public static boolean isFollowLlamadaStar(Token t){
		return t.getTokenType() == TokenType.MultiplierOperator ||
			   t.getTokenType() == TokenType.DivisionOperator ||
			   t.getTokenType() == TokenType.ModOperator ||
			   isFollowTermino(t);
	}
	
	public static boolean isFollowTerminoAux(Token t){
		return isFollowTermino(t);
		}
	
	public static boolean isFollowListaExpsFact(Token t){
		return t.getTokenType() == TokenType.ClosedParenthesisSymbol;
	}
	
	public static boolean isLiteral(Token t){
		TokenType type = t.getTokenType();
		return type == TokenType.NullKeyword || 
			   type == TokenType.BooleanLiteral ||
			   type == TokenType.IntigerLiteral ||
			   type == TokenType.CharLiteral ||
			   type == TokenType.StringLiteral;
	}
	
	
	public static boolean isFirstListaExps(Token t){
		return t.getTokenType() == TokenType.DistinctOperator ||
			   t.getTokenType() == TokenType.PlusOperator ||
			   t.getTokenType() == TokenType.RestOperator ||
			   isFirstPrimario(t);
	}
	
	public static boolean isFirstPrimario(Token t){
		return t.getTokenType() == TokenType.ThisKeyword ||
			   isLiteral(t) ||
			   t.getTokenType() == TokenType.OpenParenthesisSymbol ||
			   t.getTokenType() == TokenType.Identifier ||
			   t.getTokenType() == TokenType.NewKeyword ||
			   t.getTokenType() == TokenType.DotSymbol;
	}
}
