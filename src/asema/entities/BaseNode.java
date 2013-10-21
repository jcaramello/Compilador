package asema.entities;

import java.util.UUID;

import asema.exceptions.SemanticErrorException;

public abstract class BaseNode {

	public UUID UUID;
	
	public BaseNode(){
		
		this.UUID = java.util.UUID.randomUUID();
	}
	
	public abstract void check() throws SemanticErrorException;
}
