package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import alex.Token;
import asema.exceptions.SemanticErrorException;

public class LiteralNode extends PrimaryNode {

	public Token Value;
	
	public Type Type;
	
	public LiteralNode(Type t, Token tk){		
		this.Type = t;
		this.Value = tk;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		if(Type.equals(PrimitiveType.Boolean)) {
			if(Value.getLexema().equals("true")) CodeGenerator.gen(Instructions.PUSH, 1);
			if(Value.getLexema().equals("false")) CodeGenerator.gen(Instructions.PUSH, 0);			
		}
		else if(Type.equals(PrimitiveType.String)) {
			CodeGenerator.gen(Instructions.RMEM, 1);
			CodeGenerator.gen(Instructions.PUSH, Value.getLexema().length() + 1);
			CodeGenerator.gen(Instructions.PUSH, "LMALLOC");
			CodeGenerator.gen(Instructions.CALL);
			
			for(int i = 0; i < Value.getLexema().length(); i++) {
				CodeGenerator.gen(Instructions.DUP);
				CodeGenerator.gen(Instructions.PUSH, Value.getLexema().charAt(i));
				CodeGenerator.gen(Instructions.STOREREF, i);
			}
			
			CodeGenerator.gen(Instructions.DUP);
			CodeGenerator.gen(Instructions.PUSH, 0);
			CodeGenerator.gen(Instructions.STOREREF, Value.getLexema().length());
		}
		else if(Type.equals("null")) {
			// Qué hacer en este caso? Esto es C(Object)
		}
		else CodeGenerator.gen(Instructions.PUSH, Value.getLexema());
		
		return Type;
	}

}
