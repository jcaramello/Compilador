package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Code generator
 * 
 * Forma de uso:
 * 	La applicacion tiene que tener seteado un name, que es el que usa para ponerle el nombre al archivo ([ApplicationName].ceivm)
 * 
 * 	CodeGenerator.Gen(Instruction.LOAD, "2") // Para el caso en q reciba un parametro
 * 	CodeGenerator.Gen(Instruction.ADD) // Para el caso en q no reciba ningun parametro
 * 
 * Una vez que se termine de usar el code generator, es necesario invocar al metodo close, que es el cierra el buffer y escribe en el archivo.
 * 
 * CodeGenerator.close();
 * 
 * NOTA: notar que cree un clase Instructions que tiene constantes string con las instruccion, esto lo hice para evitar tener
 * que andar hardcodeando los string por toda la edt y asi evitar posibles errores de tipo o que falte algun espacio o alguna 
 * pavada asi, que por lo gral despues cuesta darse cuenta.
 * 
 * @author jcaramello, nechegoyen
 *
 */
public class CodeGenerator {

	
	private static final String NEW_LINE = "\n"; 
	private FileWriter fstream;
	private BufferedWriter bufferedWriter;
	
	/**
	 * Formato de instruccion [Instruccion Parametro]
	 */
	private static final String INSTRUCTION_FORMAT = "%s %s"+NEW_LINE;
	
	/**
	 * Singleton Instance
	 */
	private static CodeGenerator current;
	
	/**
	 * Private default constructor
	 */
	private CodeGenerator() throws IOException
	{
		String currentDir = System.getProperty("user.dir").replace("\\", System.getProperty("file.separator"));
		String filePath = String.format("%s%s%s.%s",currentDir, System.getProperty("file.separator"), Application.Name, Application.CompiledFileExtension);
		File logFile = new File(filePath);			
		fstream = new FileWriter(logFile, false);
		bufferedWriter = new BufferedWriter(fstream);	
	}	
	
	/**
	 * Get a Singleton Instance
	 */
	private static CodeGenerator getCurrent(){
		try {
			if(current == null)
				current = new CodeGenerator();				
		} catch (IOException e) {
			if(Application.isVerbose){
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		return current;
	}
	
	/**
	 * Log a simple message
	 * @param msg
	 */
	public static void gen(String inst)
	{
		gen(inst, null);
	}

	/**
	 * Log a simple message
	 * @param msg
	 */
	public static void gen(String inst, String Parameter)
	{
		try {			
			String instruction = null;
			if(Parameter != null)
				instruction = String.format(INSTRUCTION_FORMAT, inst, Parameter);
			else instruction = String.format(INSTRUCTION_FORMAT, inst, "");
			
			getCurrent().bufferedWriter.write(instruction);							
			
		} catch (IOException e) {
			if(Application.isVerbose){
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
		
	
	/**
	 * Close the buffer writer
	 */
	public static void close(){
				
		try {
			getCurrent().bufferedWriter.close();
		} catch (IOException e) {
			if(Application.isVerbose){
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}
