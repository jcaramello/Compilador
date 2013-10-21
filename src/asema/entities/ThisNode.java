package asema.entities;

import asema.exceptions.SemanticErrorException;

public class ThisNode extends PrimaryNode {

	public Type Type;
	
	public ThisNode(Type t){
		this.Type = t;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
