package asema.entities;

import alex.Token;
import asema.exceptions.SemanticErrorException;

public class LiteralNode extends PrimaryNode {

	public Token Value;
	
	public Type Type;
	
	public LiteralNode(Type t, Token tk){		
		this.Type = t;
		this.Value = tk;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
