package asema.entities;

import alex.Token;
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
	
public EntryVar(Token tkn){
		
		this.Offset = -1;
		this.Type = null;
		this.Name = tkn.getLexema();
		this.Token = tkn;
	}
	
	public boolean esParametroLocal() {
		return Origin.equals(OriginType.Local);
	}
	
	public boolean esVariableLocal() {
		return Origin.equals(OriginType.Param);
	}
	
}
