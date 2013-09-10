package asint;

import common.Application;
import common.Logger;

/**
 * Main
 * @author jcaramello, nechegoyen
 *
 */
public class Main {
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String args[])
	{					
		Application.Initialize(args);
		
		try {				
			
			if (validateInput(args)) {
				ASint asint = new ASint(args[0]);	
				Logger.log("El analisis se completo exitosamente");
			}
			
		}catch(UnexpectedTokenException u){
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
			 Logger.log("Debe ingresarse al menos un parámetro.\nModo de uso: ASint <Archivo_fuente> [<Archivo_destino>] [-options] \n\ndonde options: \n\t v: Enable verbose mode \n\t t: Enable testing Mode");
				valid = false;
			}
		return valid;
	}	
}