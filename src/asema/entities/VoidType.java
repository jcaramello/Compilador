package asema.entities;

import asema.exceptions.SemanticException;

public class VoidType extends Type {

	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean conforms(Type t) {
		// TODO Auto-generated method stub
		return false;
	}

}
