package asema.entities;

public class EntryVar extends EntryBase{

	/**
	 * Public Members
	 */
	public Type Type;
	public String Identifier;
	
	public EntryVar(Type t, String id){
		
		this.Type = t;
		this.Identifier = id;
	}
	
}
