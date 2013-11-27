package asema.entities;

import common.CodeGenerator;

import asema.exceptions.SemanticErrorException;

public class SimpleSentenceNode extends SentenceNode {

	public ExpressionNode Expression;
	
	
	public SimpleSentenceNode(ExpressionNode exp){
		this.Expression = exp;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		Type toRet = Expression.check();
		if(toRet != VoidType.VoidType)
			CodeGenerator.gen("POP");
		
		return toRet;
	}

}
