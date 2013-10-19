package asema.entities;

import alex.Token;
import asema.exceptions.SemanticException;

public class IDNode extends PrimaryNode {

	public Token Identifier;
	
	public IDNode(Token id){
		this.Identifier = id;
	}
	
	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub

	}

}
