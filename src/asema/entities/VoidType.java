package asema.entities;

import asema.exceptions.SemanticErrorException;

public class VoidType extends Type {

	public static VoidType VoidType = new VoidType();
	
	private VoidType() {
		this.Name = "void";
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
