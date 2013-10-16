package asema.entities;

import java.util.UUID;

public abstract class EntryBase {

	public UUID UUID;
	
	public EntryBase(){
		
		this.UUID = java.util.UUID.randomUUID();
	}
}
