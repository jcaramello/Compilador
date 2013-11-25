package asema.entities;

import java.util.UUID;

import alex.Token;

public abstract class EntryBase {

	public UUID UUID;
	public Token Token;
	
	public EntryBase(){
		
		this.UUID = java.util.UUID.randomUUID();
	}
}
