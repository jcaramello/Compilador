package asema.entities;

import asema.exceptions.SemanticErrorException;

public class PrimitiveType extends Type {

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
		// TODO Auto-generated method stub
		return false;
	}

}
