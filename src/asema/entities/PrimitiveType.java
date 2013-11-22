package asema.entities;

import alex.Token;
import asema.exceptions.SemanticErrorException;

public class PrimitiveType extends Type {

	public static PrimitiveType Int = new PrimitiveType("int");
	public static PrimitiveType String = new PrimitiveType("string");
	public static PrimitiveType Boolean = new PrimitiveType("boolean");
	public static PrimitiveType Char = new PrimitiveType("char");	
	
	private PrimitiveType(String name)
	{
		this.Name = name;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean conforms(Type t) {
		return this.Name.equals(t.Name);
	}

	
	public static PrimitiveType get(Token tkn) throws SemanticErrorException{
		
		String name = tkn.getLexema();
		if(name.equals("int"))
			return PrimitiveType.Int;
		if(name.equals("string"))
			return PrimitiveType.String;
		if(name.equals("boolena"))
			return PrimitiveType.Boolean;
		if(name.equals("char"))
			return PrimitiveType.Char;
				
		throw new SemanticErrorException(java.lang.String.format("Error(!). Tipo primitivo invalido: %s. Linea %d", name, tkn.getLinea()));
	}
	

}
