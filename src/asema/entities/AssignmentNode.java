package asema.entities;

import asema.exceptions.SemanticErrorException;

public class AssignmentNode extends SentenceNode {

	private EntryVar leftSide;
	private ExpressionNode  rigthSide;
	
	public AssignmentNode(EntryVar left, ExpressionNode rigth)
	{
		this.leftSide = left;
		this.rigthSide = rigth;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
