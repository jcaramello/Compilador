package asema.entities;

import asema.exceptions.SemanticException;

public abstract class Type {
	
	public String Name;
	
	public abstract void check() throws SemanticException;
	public abstract boolean conforms(Type t);
}
