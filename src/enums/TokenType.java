package enums;

import common.Logger;

/**
 * Token Type
 * 
 * @author jcaramello, nechegoyen
 * 
 */
public enum TokenType {

	// Special Case
	NotSet,
	// Identifier
	Identifier,
	// Reserved Words
	BooleanKeyword, CharKeyword, ClassKeyword, ElseKeyword, ExtendsKeyword, ForKeyword, IfKeyword, IntKeyword, NewKeyword, 
	ReturnKeyword, StringKeyword, ThisKeyword, VoidKeyword, WhileKeyword, VarKeyword, StaticKeyword, DynamicKeyword,NullKeyword,
	// Literals
	BooleanLiteral, IntigerLiteral, CharLiteral, StringLiteral,
	// Operators
	AndOperator, OrOperator, GratherOrEqualOperator, GratherOperator, LessOrEqualOperator, LessOperator, EqualOperator, 
	AssignOperator, DivisionOperator, MultiplierOperator, PlusOperator, RestOperator, DistinctOperator, NotOperator, ModOperator,
	// Symbols
	ComaSymbol, SemicolonSymbol, DotSymbol, OpenClaspSymbol, ClosedClaspSymbol, OpenKeySymbol, ClosedKeySymbol, 
	OpenParenthesisSymbol, ClosedParenthesisSymbol;

	public static TokenType getValue(String value) {
		TokenType type = TokenType.NotSet;
		if (value.equals("/"))
			type = TokenType.DivisionOperator;
		else if (value.equals("*"))
			type = TokenType.MultiplierOperator;
		else if (value.equals("+"))
			type = TokenType.PlusOperator;
		else if (value.equals("-"))
			type = TokenType.RestOperator;
		else if (value.equals("%"))
			type = TokenType.ModOperator;
		else if (value.equals(","))
			type = TokenType.ComaSymbol;
		else if (value.equals(";"))
			type = TokenType.SemicolonSymbol;
		else if (value.equals("."))
			type = TokenType.DotSymbol;
		else if (value.equals("["))
			type = TokenType.OpenClaspSymbol;
		else if (value.equals("]"))
			type = TokenType.ClosedClaspSymbol;
		else if (value.equals("{"))
			type = TokenType.OpenKeySymbol;
		else if (value.equals("}"))
			type = TokenType.ClosedKeySymbol;
		else if (value.equals("("))
			type = TokenType.OpenParenthesisSymbol;
		else if (value.equals(")"))
			type = TokenType.ClosedParenthesisSymbol;
		else {

			// if the value is a reserved word
			
				String enumStringValue = value.substring(0, 1).toUpperCase() + value.substring(1) + "Keyword";
				try {
					type = TokenType.valueOf(TokenType.class, enumStringValue);
				} catch (IllegalArgumentException e) {
					try {
						type = TokenType.valueOf(value);
					} catch (Exception e1) {
						Logger.verbose(e1.getMessage());
					}
				} catch (Exception e) {
					Logger.verbose(e.getMessage());
				}
			
		}
		return type;
	}
}
