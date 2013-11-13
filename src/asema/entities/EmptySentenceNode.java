package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import asema.exceptions.SemanticErrorException;

public class EmptySentenceNode extends SentenceNode {

	@Override
	public Type check() throws SemanticErrorException {
		
		CodeGenerator.gen(Instructions.NOP);
		return null;
	}

}
