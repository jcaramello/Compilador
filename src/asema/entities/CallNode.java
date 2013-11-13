package asema.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.CodeGenerator;
import common.Instructions;
import enums.ModifierMethodType;

import asema.TS;
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
	public Type check() throws SemanticErrorException {
		
		if(Context == null)
			Context = new ThisNode();
		
		EntryMethod met = TS.getClass(Context.check().Name).getMethod(OperationName.Identifier.getLexema());
		
		if(ActualsParameters.size() != met.getFormalArgsCant())
			throw new SemanticErrorException("La cantidad de parámetros actuales debe coincidir con la cantidad de parámetros formales.");
		
		if(met.Modifier == ModifierMethodType.Static) {
			CodeGenerator.gen("POP");  // elimino el this apilado
			
			for(int i = 0; i < ActualsParameters.size(); i++) {
				if(!ActualsParameters.get(i).check().conforms(met.getFormalArgByIndex(i).Type))
						throw new SemanticErrorException("Los tipos de los parámetros actuales deben conformar a los tipos de los parámetros formales.");
			}
			
			if(!met.ReturnType.equals(VoidType.VoidType))
				CodeGenerator.gen(Instructions.RMEM, "1");
				
			CodeGenerator.gen(Instructions.PUSH, met.getContainerClass().Name + "_" + met.Name);
			CodeGenerator.gen(Instructions.CALL);
		}
		else {
			if(!met.ReturnType.equals(VoidType.VoidType)) {
				CodeGenerator.gen(Instructions.RMEM, "1");
				CodeGenerator.gen(Instructions.SWAP);
			}
			
			for(int i = 0; i < ActualsParameters.size(); i++) {
				if(!ActualsParameters.get(i).check().conforms(met.getFormalArgByIndex(i).Type))
						throw new SemanticErrorException("Los tipos de los parámetros actuales deben conformar a los tipos de los parámetros formales.");
				CodeGenerator.gen(Instructions.SWAP);
			}
			
			CodeGenerator.gen(Instructions.DUP);
			CodeGenerator.gen(Instructions.LOADREF, "0");
			CodeGenerator.gen(Instructions.LOADREF, ""+ met.Offset);
			CodeGenerator.gen(Instructions.CALL);
		}
	
		return met.ReturnType;
	}

}
