package asema.entities;

import common.CodeGenerator;
import common.Instructions;
import enums.ModifierMethodType;

import asema.TS;
import asema.exceptions.SemanticErrorException;

public class AssignmentNode extends SentenceNode {

	public EntryVar leftSide;
	private ExpressionNode  rigthSide;
	
	public AssignmentNode(EntryVar left, ExpressionNode rigth)
	{
		this.leftSide = left;
		this.rigthSide = rigth;
	}
	
	@Override
	public Type check() throws SemanticErrorException {
		
		CodeGenerator.gen("# AssignmentNode Start");

		int numL = leftSide.Token.getLinea();
		String name = leftSide.Name;

		leftSide = TS.findVar(leftSide.Name); 
		if(leftSide == null)
			throw new SemanticErrorException("Error(!). " + name + " no es un identificador valido, en línea " + numL);

		Type t = leftSide.Type;			
		
		if(!rigthSide.check().conforms(t))
			throw new SemanticErrorException("Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea " + leftSide.Token.getLinea() + ".");
		
		if(leftSide.esParametroLocal() || leftSide.esVariableLocal()) {
			CodeGenerator.gen(Instructions.STORE, ""+ leftSide.Offset);
		}
		else {
			if(TS.getCurrentClass().getCurrentMethod().Modifier == ModifierMethodType.Static)
				throw new SemanticErrorException("Error(!). No se puede asignar un valor a una variable de instancia desde un método estático, en línea " + leftSide.Token.getLinea() + ".");
			CodeGenerator.gen(Instructions.LOAD, "3");
			CodeGenerator.gen(Instructions.SWAP);
			CodeGenerator.gen(Instructions.STOREREF, ""+ leftSide.Offset);
		}
			
		CodeGenerator.gen("# AssignmentNode End");
		
		return t;
	}

}
