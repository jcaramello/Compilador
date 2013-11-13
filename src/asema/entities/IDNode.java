package asema.entities;

import common.CodeGenerator;

import alex.Token;
import asema.TS;
import asema.exceptions.SemanticErrorException;

public class IDNode extends PrimaryNode {

	public Token Identifier;
	
	public IDNode(Token id){
		this.Identifier = id;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		EntryVar ev = TS.findVar(Identifier.getLexema());
		
		if(ev.esParametroLocal() || ev.esVariableLocal()) {
			CodeGenerator.gen("LOAD " + ev.Offset);
		} else {
			if(TS.getCurrentClass().getCurrentMethod().Modifier.equals("static"))
				throw new SemanticErrorException("No se puede acceder a una variable de instancia desde un método static.");
			
			CodeGenerator.gen("LOAD 3");
			CodeGenerator.gen("LOADREF " + ev.Offset);
		}
		
		return ev.Type;
	}

}
