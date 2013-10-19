package asema.entities;

import alex.Token;
import asema.exceptions.SemanticException;

public class LiteralNode extends PrimaryNode {

	public Token Value;
	
	public Type Type;
	
	public LiteralNode(Type t, Token tk){		
		this.Type = t;
		this.Value = tk;
	}
	
	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub

	}

}
