package asema.entities;

public class EntryVar extends EntryBase{

	/**
	 * Public Members
	 */
	public Type Type;
	public String Name;
	public String Origin; 
	public int Offset;
	
	public EntryVar(Type t, String id){
		
		this.Type = t;
		this.Name = id;
	}
	
	public boolean esParametroLocal() {
		return Origin.equals("param");
	}
	
	public boolean esVariableLocal() {
		return Origin.equals("local");
	}
	
}
