package enums;

/**
 * Token Type
 * @author jcaramello, nechegoyen
 *
 */
public enum TokenType {

	//Special Case
	NotSet,
	//Identifier
	Identifier,
	//Reserved Words
	Boolean,
	Char, 
	Class, 
	ClassDef, 
	Else, 
	Extends, 
	For,
	If, 
	Int, 
	New,
	Null, 
	Return, 
	String, 
	Super, 
	This, 
	Void, 
	While,
	Var,
	Static,
	Dynamic,
	//Literals
	BoolLiteral,
	IntLiteral,
	CharLiteral,
	StringLiteral,
	//Operators
	AndOperator,
	OrOperator,
	GratherOrEqualOperator,
	GratherOperator,
	LessOrEqualOperator,
	LessOperator,
	EqualOperator,
	AssignOperator,
	DivisionOperator,
	MultiplierOperator,
	PlusOperator,
	RestOperator,
	DistinctOperator,
	NotOperator,
	ModOperator,
	//Symbols
	ComaSymbol,
	SemicolonSymbol,
	PointSymbol,
	OpenClaspSymbol,
	ClosedClaspSymbol,
	OpenKeySymbol,
	ClosedKeySymbol,
	OpenParenthesisSymbol,
	ClosedParenthesisSymbol,	
	;
	
	@Override
    public String toString() {
		String enumValue = super.toString();
		// Hack for a ALex's optimization 
		if(this == TokenType.AndOperator)
			enumValue = "&&";
		else if(this == TokenType.OrOperator)
			enumValue = "||";
		else if(this == TokenType.GratherOperator)
			enumValue = ">";
		else if(this == TokenType.GratherOrEqualOperator)
			enumValue = ">=";
		else if(this == TokenType.LessOperator)
			enumValue = "<";
		else if(this == TokenType.LessOrEqualOperator)
			enumValue = "<=";
		else if(this == TokenType.EqualOperator)
			enumValue = "==";
		else if(this == TokenType.AssignOperator)
			enumValue = "=";
		else if(this == TokenType.DivisionOperator)
			enumValue = "/";
		else if(this == TokenType.MultiplierOperator)
			enumValue = "*";
		else if(this == TokenType.PlusOperator)
			enumValue = "+";
		else if(this == TokenType.RestOperator)
			enumValue = "-";
		else if(this == TokenType.DistinctOperator)
			enumValue = "!=";
		else if(this == TokenType.NotOperator)					
			enumValue = "!";
		else if(this == TokenType.ModOperator)
			enumValue = "%";
		else if(this == TokenType.ComaSymbol)
			enumValue = ",";
		else if(this == TokenType.SemicolonSymbol)
			enumValue = ";";
		else if(this == TokenType.PointSymbol)
			enumValue = ".";
		else if(this == TokenType.OpenClaspSymbol)
			enumValue = "[";
		else if(this == TokenType.ClosedClaspSymbol)
			enumValue = "]";
		else if(this == TokenType.OpenKeySymbol)
			enumValue = "{";
		else if(this == TokenType.ClosedKeySymbol)
			enumValue = "}";
		else if(this == TokenType.OpenParenthesisSymbol)
			enumValue = "(";
		else if(this == TokenType.ClosedParenthesisSymbol)
			enumValue = ")";	
		else if(this != TokenType.String){        	
			enumValue = enumValue.substring(0, 1).toLowerCase() + enumValue.substring(1);
        }
				
        
		return enumValue;
    }
	
	public static TokenType getValue(String value){
		TokenType type = TokenType.NotSet;
		
		if(value.equals("/"))
			type = TokenType.DivisionOperator;
		else if(value.equals("*"))
			type = TokenType.MultiplierOperator;
		else if(value.equals("+"))
			type = TokenType.PlusOperator;
		else if(value.equals("-"))
			type = TokenType.RestOperator;	
		else if(value.equals("%"))
			type = TokenType.ModOperator;	
		else if(value.equals(","))
			type = TokenType.ComaSymbol;
		else if(value.equals(";"))
			type = TokenType.SemicolonSymbol;
		else if(value.equals("."))
			type = TokenType.PointSymbol;
		else if(value.equals("["))
			type = TokenType.OpenClaspSymbol;
		else if(value.equals("]"))
			type = TokenType.ClosedClaspSymbol;
		else if(value.equals("{"))
			type = TokenType.OpenKeySymbol;
		else if(value.equals("}"))
			type = TokenType.ClosedKeySymbol;
		else if(value.equals("("))
			type = TokenType.OpenParenthesisSymbol;
		else if(value.equals(")"))
			type = TokenType.ClosedParenthesisSymbol;
		else {
			// if the value is a reserved word
			String enumStringValue = value.substring(0, 1).toUpperCase() + value.substring(1);
			type = TokenType.valueOf(TokenType.class, enumStringValue);
		}
		return type;
	}
}
