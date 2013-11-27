package compiler;

import alex.exceptions.ALexException;
import asema.TS;
import asema.exceptions.SemanticErrorException;
import asint.ASint;
import asint.UnexpectedTokenException;

import common.Application;
import common.CodeGenerator;
import common.Logger;

public class Compiler {
	/**
	 * Main
	 * @param args
	 */
	public static void main(String args[])
	{		
		if (validateInput(args)) {			
			Application.Initialize(args);
			
			try {									
				TS.initialize();
				ASint asint = new ASint(args[0]);	
				TS.execute();
				Logger.log("Se compiló exitosamente");						
			}
			catch(UnexpectedTokenException u){
				Logger.log(u.getMessage());			
			}catch(SemanticErrorException u){
				Logger.log(u.getMessage());				
			}catch (Exception e) {
				Logger.log("Error!!");
				Logger.verbose(e);									
			}
			finally{		
				Logger.close();
				CodeGenerator.close();
			}
		}
	}

	private static boolean validateInput(String args[]){
		
		boolean valid = true;
		if (args.length == 0) {			 
			 System.out.print("Debe ingresarse al menos un parámetro.\nModo de uso: java -jar minijava.jar <IN_FILE> [<OUT_FILE>] [-options] \n\ndonde options: \n\t v: Enable verbose mode \n\t t: Enable testing Mode");
				valid = false;
			}
		return valid;
	}	
}
