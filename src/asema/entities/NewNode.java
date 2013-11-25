package asema.entities;

import java.util.List;

import common.CodeGenerator;
import common.Instructions;

import alex.Token;
import asema.TS;
import asema.exceptions.SemanticErrorException;

public class NewNode extends PrimaryNode {

	public Token ClassName;
	public List<ExpressionNode> ActualsParameters;	
	
	public NewNode(Token tkn, List<ExpressionNode> args){	
		this.ClassName = tkn;
		this.ActualsParameters = args;
	}
	
	@Override
	public ClassType check() throws SemanticErrorException {
		
		EntryClass cl = TS.getClass(ClassName.getLexema());
		
		if(cl == null)
			throw new SemanticErrorException(String.format("Error(!). La clase %s no existe. Linea %d", ClassName.getLexema(), ClassName.getLinea()));
				
		CodeGenerator.gen(Instructions.RMEM, 1);
		CodeGenerator.gen(Instructions.PUSH, cl.getCantAttributes() + 1);
		CodeGenerator.gen(Instructions.PUSH, "LMALLOC");
		CodeGenerator.gen(Instructions.CALL);
		CodeGenerator.gen(Instructions.DUP);
		CodeGenerator.gen(Instructions.PUSH, "VT_" + cl.Name);
		CodeGenerator.gen(Instructions.STOREREF, "0");
		CodeGenerator.gen(Instructions.DUP);
		
		if(ActualsParameters.size() != cl.getConstructor().getFormalArgsCant())
			throw new SemanticErrorException("Error(!). La cantidad de parámetros actuales del constructor debe coincidir con la cantidad de parámetros formales. Linea: "+ ClassName.getLinea());
		
		for(int i = 0; i < ActualsParameters.size(); i++) {
			if(!ActualsParameters.get(i).check().conforms(cl.getConstructor().getFormalArgByIndex(i).Type))
					throw new SemanticErrorException("Error(!). Los tipos de los parámetros actuales del constructor deben conformar a los tipos de los parámetros formales. Linea: "+ ClassName.getLinea());
			else CodeGenerator.gen(Instructions.SWAP);
		}		
		
		CodeGenerator.gen(Instructions.PUSH, cl.Name + "_" + cl.getConstructor().Name);
		CodeGenerator.gen(Instructions.CALL);

		return new ClassType(cl);
	}

}
