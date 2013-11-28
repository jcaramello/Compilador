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
		
		CodeGenerator.gen("# CallNode start");
		
		if(Context == null)
			Context = new ThisNode();
		
		EntryClass clase =  TS.getClass(Context.check().Name);

		EntryMethod met = clase.getMethod(OperationName.Identifier.getLexema());
		
		if(met == null) throw new SemanticErrorException("Error(!). El m�todo " + OperationName.Identifier.getLexema() + " no existe en la clase " + clase.Name + ".");
		
		if(ActualsParameters.size() != met.getFormalArgsCant())
			throw new SemanticErrorException("Error(!). La cantidad de par�metros actuales debe coincidir con la cantidad de par�metros formales en l�nea " + OperationName.Identifier.getLinea() + "." );
		
		if(met.Modifier == ModifierMethodType.Static) {
			CodeGenerator.gen("POP");  // elimino el this apilado
			
			for(int i = 0; i < ActualsParameters.size(); i++) {
				if(!ActualsParameters.get(i).check().conforms(met.getFormalArgByIndex(i).Type))
						throw new SemanticErrorException("Error(!). Los tipos de los par�metros actuales deben conformar a los tipos de los par�metros formales en par�metro " + i + ", en l�nea "+ OperationName.Identifier.getLinea() + ".");
			}
			
			if(!met.ReturnType.equals(VoidType.VoidType))
				CodeGenerator.gen(Instructions.RMEM, 1);
				
			CodeGenerator.gen(Instructions.PUSH, met.getContainerClass().Name + "_" + met.Name);
			CodeGenerator.gen(Instructions.CALL);
		}
		else {
			if(!met.ReturnType.equals(VoidType.VoidType)) {
				CodeGenerator.gen(Instructions.RMEM, 1);
				CodeGenerator.gen(Instructions.SWAP);
			}
			
			for(int i = 0; i < ActualsParameters.size(); i++) {
				if(!ActualsParameters.get(i).check().conforms(met.getFormalArgByIndex(i).Type))
						throw new SemanticErrorException("Error(!). Los tipos de los par�metros actuales deben conformar a los tipos de los par�metros formales en par�metro " + i + ", en l�nea "+ OperationName.Identifier.getLinea() + ".");				
				CodeGenerator.gen(Instructions.SWAP);
			}
			
			CodeGenerator.gen(Instructions.DUP);
			CodeGenerator.gen(Instructions.LOADREF, 0); // offset de la VTable
			CodeGenerator.gen(Instructions.LOADREF, met.Offset + 1); // Saltar el NOP
			CodeGenerator.gen(Instructions.CALL);		
		}
		
		CodeGenerator.gen("# CallNode end");
	
		return met.ReturnType;
	}

}
