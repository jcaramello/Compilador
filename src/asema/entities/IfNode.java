package asema.entities;

import asema.exceptions.SemanticException;

public class IfNode extends SentenceNode {
	
	public ExpressionNode ConditionalExpression;
	
	// Notar que tanto el nodo then como el nodo else, son de tipo Base node, 
	// por que basicamente pueden ser cualquier cosa, un bloque o una sentencia 
	
	// En realidad no estoy seguro si el tipo deberia ser BaseNode o BlockNode
	// Si estoy seguro de que no es SentenceNode como esta en uml
	
	public BaseNode ThenExpression;
	public BaseNode ElseExpression;

	public IfNode(ExpressionNode cond, BaseNode thenExp, BaseNode elseExp){
		this.ConditionalExpression = cond;
		this.ThenExpression = thenExp;
		this.ElseExpression = elseExp;
	}
	
	@Override
	public void check() throws SemanticException {
		// TODO Auto-generated method stub

	}

}
