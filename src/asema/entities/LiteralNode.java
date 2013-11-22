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
		
		//CodeGenerator.gen("# LiteralNode start");
		
		if(Type.equals(PrimitiveType.Boolean)) {
			if(Value.getLexema().equals("true")) CodeGenerator.gen(Instructions.PUSH, 1);
			if(Value.getLexema().equals("false")) CodeGenerator.gen(Instructions.PUSH, 0);			
		}
		else if(Type.equals(PrimitiveType.String)) {
			CodeGenerator.gen(Instructions.RMEM, 1);
			CodeGenerator.gen(Instructions.PUSH, Value.getLexema().length() + 1);
			CodeGenerator.gen(Instructions.PUSH, "LMALLOC");
			CodeGenerator.gen(Instructions.CALL);
			
			// El lexema incluye las comillas!
			for(int i = 1; i < Value.getLexema().length() - 1; i++) {
				CodeGenerator.gen(Instructions.DUP);
				CodeGenerator.gen(Instructions.PUSH, Value.getLexema().charAt(i));
				CodeGenerator.gen(Instructions.STOREREF, i - 1);
			}
			
			CodeGenerator.gen(Instructions.DUP);
			CodeGenerator.gen(Instructions.PUSH, 0);
			CodeGenerator.gen(Instructions.STOREREF, Value.getLexema().length() - 2);
		}
		else if(Type.equals("null")) {
			// Qué hacer en este caso? Esto es C(Object)
		}
		else CodeGenerator.gen(Instructions.PUSH, Value.getLexema());
		
		return Type;
	}

}
