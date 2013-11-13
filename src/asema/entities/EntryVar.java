package asema.entities;

import enums.OriginType;

public class EntryVar extends EntryBase{

	/**
	 * Public Members
	 */
	public Type Type;
	public String Name;
	public OriginType Origin; 
	public int Offset;
	
	public EntryVar(Type t, String id){
		
		this.Offset = -1;
		this.Type = t;
		this.Name = id;
	}
	
	public boolean esParametroLocal() {
		return Origin.equals(OriginType.Local);
	}
	
	public boolean esVariableLocal() {
		return Origin.equals(OriginType.Param);
	}
	
}
