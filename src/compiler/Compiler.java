package compiler;

import alex.exceptions.ALexException;
import asema.TS;
import asema.exceptions.SemanticErrorException;
import asint.ASint;
import asint.UnexpectedTokenException;

import common.Application;
import common.Logger;

public class Compiler {
	/**
	 * Main
	 * @param args
	 */
	public static void main(String args[])
	{					
		Application.Initialize(args);
		
		try {				
			
			if (validateInput(args)) {
				TS.initialize();
				ASint asint = new ASint(args[0]);	
				TS.execute();
				Logger.log("El se compilo exitosamente");
			}
			
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
		}
	
	}

	private static boolean validateInput(String args[]){
		
		boolean valid = true;
		if (args.length == 0) {
			 Logger.log("Debe ingresarse al menos un parámetro.\nModo de uso: cie <Archivo_fuente> [<Archivo_destino>] [-options] \n\ndonde options: \n\t v: Enable verbose mode \n\t t: Enable testing Mode");
				valid = false;
			}
		return valid;
	}	
}
