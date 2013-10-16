package asema.entities;

import java.util.UUID;
import asema.exceptions.SemanticException;

public abstract class Type {
	
	public String Name;
	public UUID UUID;
	
	public Type(){
		this.UUID = java.util.UUID.randomUUID();
	}
	
	public abstract void check() throws SemanticException;
	public abstract boolean conforms(Type t);
}
