package asema.entities;

import asema.exceptions.SemanticErrorException;

public class ClassType extends Type {

	private EntryClass ClassRef;
	
	public ClassType(EntryClass classRef){
		this.ClassRef = classRef;
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