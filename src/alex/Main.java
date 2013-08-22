package alex;

import common.Application;
import common.Logger;
import enums.LogType;

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
		// Initialize global application variables
		Application.Name = "ALex";
		Application.isTesting = isTestingEnabled(args);
		Application.isVerbose = isVerboseEnabled(args);
		Application.logType = (args.length > 1 && !args[1].equals(Application.VERBOSE_PARAMETER) && !args[1].equals(Application.TESTING_PARAMETER)) ? LogType.File : LogType.Console;
		Application.logFilePath = (Application.logType == LogType.File) ? args[1] : null;
		
		try {				
			
			if (validateInput(args)) {							
				Logger.log("%-16s | %-32s | %-8s", "TOKEN", "LEXEMA", "LINEA");
				Logger.log("-------------------------------------------------------------");
				
				ALex alex = new ALex(args[0]);									
				
				Token t = alex.obtenerToken();
				while(t != null)
				{	
					Logger.log("%-16s | %-32s | %-8s", t.getTokenType().toString(), t.getLexema(), t.getLinea()+"");
					t = alex.obtenerToken();														
				} 
			}			
		} catch (Exception e) {
			Logger.log(e.getMessage());
		}
		finally{		
			Logger.close();
		}
	
	}

	private static boolean validateInput(String args[]){
		
		boolean valid = true;
		if (args.length == 0) {
			 Logger.log("Debe ingresarse al menos un parámetro. Modo de uso: ALex <Archivo_fuente> [<Archivo_destino>]");
				valid = false;
			}
		return valid;
	}
	
	private static boolean isTestingEnabled(String args[]){
		boolean isTesting = false;
		
		for (int i = 1; i < args.length; i++) {
			if(args[i].equals(Application.TESTING_PARAMETER)){
				isTesting = true;				
				break;
			}						
		}
		
		return isTesting;		
	}
	
	private static boolean isVerboseEnabled(String args[]){
		boolean isVerbose = false;
		
		for (int i = 1; i < args.length; i++) {
			if(args[i].equals(Application.VERBOSE_PARAMETER)){
				isVerbose = true;
				break;
			}						
		}
		
		return isVerbose;		
	}
}
