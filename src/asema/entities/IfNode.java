package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import asema.TS;
import asema.exceptions.SemanticErrorException;

public class IfNode extends SentenceNode {
	
	public ExpressionNode ConditionalExpression;
	
	// Notar que tanto el nodo then como el nodo else, son de tipo Base node, 
	// por que basicamente pueden ser cualquier cosa, un bloque o una sentencia 
	
	// En realidad no estoy seguro si el tipo deberia ser BaseNode o BlockNode
	// Si estoy seguro de que no es SentenceNode como esta en uml
	
	public BaseNode ThenNode;
	public BaseNode ElseNode;

	public IfNode(ExpressionNode cond, BaseNode thenExp, BaseNode elseSentence){
		this.ConditionalExpression = cond;
		this.ThenNode = thenExp;
		this.ElseNode = elseSentence;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		int l1 = TS.getNewLabelID();
		int l2 = TS.getNewLabelID();

		if(!ConditionalExpression.check().equals(PrimitiveType.Boolean))
			throw new SemanticErrorException("El tipo de la expresión condicional del if debe ser boolean.");
		
		CodeGenerator.gen(Instructions.BF, l1);
		
		ThenNode.check();
		
		CodeGenerator.gen(Instructions.JUMP, l2);
		CodeGenerator.gen("L" + l1 + ": NOP", true);
		
		if(ElseNode != null) ElseNode.check();
		
		CodeGenerator.gen("L" + l2 + ": NOP", true);
		
		
		return null; // ??
	}

}
