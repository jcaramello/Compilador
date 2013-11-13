package asema.entities;

import asema.exceptions.SemanticErrorException;

public abstract class PrimaryNode extends BaseNode {
	
	public abstract Type check() throws SemanticErrorException;
}
