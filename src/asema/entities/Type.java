package asema.entities;

import java.util.UUID;
import asema.exceptions.SemanticErrorException;

public abstract class Type {
	
	public String Name;
	public UUID UUID;
	
	public Type(){
		this.UUID = java.util.UUID.randomUUID();
	}
	
	public abstract void check() throws SemanticErrorException;
	public abstract boolean conforms(Type t);
}
