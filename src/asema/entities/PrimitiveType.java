package asema.entities;

import asema.exceptions.SemanticErrorException;

public class PrimitiveType extends Type {

	public static PrimitiveType Int = new PrimitiveType("Int");
	public static PrimitiveType String = new PrimitiveType("String");
	public static PrimitiveType Boolean = new PrimitiveType("Boolean");
	public static PrimitiveType Char = new PrimitiveType("Char");	
	
	public PrimitiveType(String name)
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

	

}
