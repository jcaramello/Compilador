package asema.entities;

public class EntryVar extends EntryBase{

	/**
	 * Public Members
	 */
	public Type Type;
	public String Name;
	
	public EntryVar(Type t, String id){
		
		this.Type = t;
		this.Name = id;
	}
	
}
