package common;

import java.io.File;
import java.io.FileReader;

import enums.LogType;

/**
 * Application
 * @author jcaramello, nechegoyen
 *
 */
public class Application {
	
	/**
	 * Parameter used for indicate a testing mode operation
	 */
	public static final String TESTING_PARAMETER = "-t";
	
	/**
	 * Parameter used for indicate a verbose mode operation
	 */
	public static final String VERBOSE_PARAMETER = "-v";
	
	/**
	 * Application Name
	 */
	public static String Name;
	
	/**
	 * False: se lee desde un archivo (operatoria normal); true: se lee desde un String, para testear m�s c�modamente.	
	 */
	public static boolean isTesting = false;  
				
	/**
	 * 	"True" activa los mensajes de informaci�n.
	 */
	public static boolean isVerbose = false; 
	
	/**
	 * Log Type
	 */
	public static LogType logType;
	
	/**
	 * Log File Path 
	 */
	public static String logFilePath;
	
	
	/**
	 * Compiled file Extension 
	 */
	public static String CompiledFileExtension = "ceivm";
	
	/**
	 * Initialize global application's variables
	 * @param args
	 */
	public static void Initialize(String args[]){
		Application.Name = (new File(args[0])).getName();
		Application.isTesting = isTestingEnabled(args);
		Application.isVerbose = isVerboseEnabled(args);
		Application.logType = (args.length > 1 && !args[1].equals(Application.VERBOSE_PARAMETER) && !args[1].equals(Application.TESTING_PARAMETER)) ? LogType.File : LogType.Console;
		Application.logFilePath = (Application.logType == LogType.File) ? args[1] : null;
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
