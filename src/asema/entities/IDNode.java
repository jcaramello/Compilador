package asema.entities;

import common.CodeGenerator;
import common.Instructions;

import alex.Token;
import asema.TS;
import asema.exceptions.SemanticErrorException;

public class IDNode extends PrimaryNode {

	public Token Identifier;
	
	public IDNode(Token id){
		this.Identifier = id;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		CodeGenerator.gen("# IDNode start");
		
		Type type = null;
		EntryVar ev = TS.findVar(Identifier.getLexema());		
		
		// Aca no estamos diferenciando entre si es una variable de tipo primitivo o una variable de tipo clase
		// no se si eso hace falta o no.
		if(ev != null){
			if(ev.esParametroLocal() || ev.esVariableLocal()) {
				CodeGenerator.gen(Instructions.LOAD, ev.Offset);
			} else {
				if(TS.getCurrentClass().getCurrentMethod().Modifier.equals("static"))
					throw new SemanticErrorException("Error(!). No se puede acceder a una variable de instancia desde un método static. Linea: "+Identifier.getLinea());
				
				CodeGenerator.gen(Instructions.LOAD, 3);
				CodeGenerator.gen(Instructions.LOADREF, ev.Offset);
			}
			
			type = ev.Type;
		}
		
		// Identificador de clase tipo System.print();
		EntryClass ec = TS.getClass(Identifier.getLexema());
		if(ec != null){
			// Esto es lo que después se desapila cuando es static.
			CodeGenerator.gen(Instructions.PUSH, "VT_" + ec.Name);
			
			// Esto modela el hecho de que es un LITERAL Clase, pero no es tipo clase.
			// i.e. A a = A; va a fallar porque no conforma, pero vamos a mantener la información del nombre para las llamadas estáticas.
			type = new PrimitiveType("C_" + ec.Name); 
		}
		
		if(type == null)
			throw new SemanticErrorException(String.format("Error (!). El identificador %s no puede ser resuelto. Linea %d", Identifier.getLexema(), Identifier.getLinea()));
		
		CodeGenerator.gen("# IDNode end");
		return type;
	}

}
