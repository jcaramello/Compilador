package asema.entities;

import alex.Token;
import asema.TS;
import asema.exceptions.SemanticErrorException;

public class ClassType extends Type {

	private EntryClass ClassRef;
	
	// Este lo guardo por que se puede dar el caso que haga referencia a un tipo clase todavia no procesado
	// entonces luego voy a tener que chequear esto y en caso de error voy a necesitar saber la linea 
	// donde hice referencia al tipo
	private Token Token;
	
	public ClassType(EntryClass classRef){
		this.ClassRef = classRef;
		this.Name = classRef.Name;
	}
	
	public ClassType(Token tkn){
		this.Name = tkn.getLexema();
		this.Token = tkn;
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean conforms(Type t) {
		EntryClass c = TS.getClass(t.Name);
		
		while(c!= null) {
			if(c.Name.equals(t.Name))
				return true;
			c = c.fatherClass;
		}			
		
		return false;
	}

}
