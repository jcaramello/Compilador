package common;

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
	 * False: se lee desde un archivo (operatoria normal); true: se lee desde un String, para testear más cómodamente.	
	 */
	public static boolean isTesting = false;  
				
	/**
	 * 	"True" activa los mensajes de información.
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
}
