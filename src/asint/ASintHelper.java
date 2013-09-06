package asint;

import enums.TokenType;
import alex.Token;

public class ASintHelper {

	public static boolean isFollowExpressionQ(Token t){
		return t.getTokenType() == TokenType.SemicolonSymbol;
	}
	
	public static boolean isFollowExpressionAux(Token t){
		return isFollowExpressionQ(t) ||
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
	
	
}
