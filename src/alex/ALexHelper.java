/**
 * 
 */
package alex;

/**
 * Clase estatica que provee metodos helpers para el analizador lexico
 * @author jcaramello, nechegoyen
 *
 */
public class ALexHelper {
	
	/**
	 * Determina si un caracter es una letra
	 * @param caracter
	 * @return true si es una letra
	 */
	public static boolean isLetter(int caracter){
		return (caracter>=65 && caracter<=90)||(caracter>=97 && caracter<=122)||caracter==95;
	}

	/**
	 * Determina si un caracter es un digito
	 * @param caracter
	 * @return
	 */
	public static boolean isDigit(int caracter){
		return caracter>=48 && caracter<=57;
	}
	
	/**
	 * Determina si es un operador prohibido (~|?|:)
	 * @param caracter
	 * @return
	 */
	public static boolean isForbiddenOperator(int caracter){
		return caracter==126 || caracter==63 || caracter==58 || caracter==94;
	}
	
	/**
	 * Retorna true para los caracteres que estando fuera del lenguaje, igualmente se aceptan en el archivo para
	 * mejorar su legibilidad
	 * @param caracter
	 * @return
	 */
	public static boolean esCaracterIgnorado(int caracter) {
		return (caracter==' ' || caracter=='\n' || caracter=='\t' || caracter=='\r');
	}
	
	/**
	 * Retorna true para ,|;|.|(|)|[|]|{|}
	 * @param caracter
	 * @return
	 */
	public static boolean isPunctuationSymbol(int caracter){
		return caracter==44 || caracter==59 || caracter==46 || caracter==40 || caracter==41 || caracter==91 || caracter==93 || caracter==123 || caracter==125;
	}
}
