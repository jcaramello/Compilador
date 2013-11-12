package asema.entities;

import java.util.List;

import alex.Token;
import asema.exceptions.SemanticErrorException;

public class NewNode extends CallNode {

	public NewNode(Token tkn, CallNode call, List<ExpressionNode> args){
		super(call, null, null);		
		
		this.OperationName = new IDNode(tkn);
		this.ActualsParameters = args;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
