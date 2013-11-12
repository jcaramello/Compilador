package asema.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import asema.exceptions.SemanticErrorException;

public class CallNode extends PrimaryNode {

	/* Se me habia plateando la duda de si el contexto era siempre un nodo primario
	 * dado que se podria tener algo de esta forma (3*5).toString();
	 * Creo que esto queda resulto por que el tipo resultado de la expression deberia ser 
	 * un LiteralNode el cual es un PrimaryNode. viendolo ahora parece bastante trivial, pero por alguna razon 
	 * me genera la duda de que en ese caso el contexto fuera una expression y no un primaryNode.
	 * Idem para (new Sarasa).sarasa(); en este caso creo q el resultado de la expresion es un IDNode.
	 * Notar que para este caso se deberia crear alguna especie de "identificador" para el objeto anonimo
	 * En fin, cualquier caso creo que el contexto, termina siendo siempre un primaryNode.
	 */
	public PrimaryNode Context;	
	public List<ExpressionNode> ActualsParameters;	
	public IDNode OperationName;
	
	public CallNode(PrimaryNode context, IDNode operationName, List<ExpressionNode> actualsParameters){
		this.Context = context;
		this.OperationName = operationName;
		this.ActualsParameters = actualsParameters;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
