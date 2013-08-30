package alex;

import alex.exceptions.ALexException;
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
				Logger.log("+--------+-------------------------+--------------------------------------------------+");
				Logger.log("|%-8s|%-25s|%-50s|","LINEA", "TOKEN TYPE", "LEXEMA");
				Logger.log("+--------+-------------------------+--------------------------------------------------+");
				
				ALex alex = new ALex(args[0]);													
				Token token = alex.getToken();
				while(token != null)
				{	
					Logger.log("|%-8s|%-25s|%-50s|", token.getLinea()+"",token.getTokenType().toString(), token.getLexema());
					token = alex.getToken();														
				} 
				Logger.log("+--------+-------------------------+--------------------------------------------------+");				
			}			
		}catch(ALexException al){
			Logger.log(al.getMessage());
		}catch (Exception e) {			
			Logger.verbose(e);									
		}
		finally{		
			Logger.close();
		}
	
	}

	private static boolean validateInput(String args[]){
		
		boolean valid = true;
		if (args.length == 0) {
			 Logger.log("Debe ingresarse al menos un parámetro.\nModo de uso: ALex <Archivo_fuente> [<Archivo_destino>] [-options] \n\ndonde options: \n\t v: Enable verbose mode \n\t t: Enable testing Mode");
				valid = false;
			}
		return valid;
	}	
}
