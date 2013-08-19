package alex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import alex.exceptions.*;


public class ALex {

	protected BufferedReader inputStream;
	protected String lexema = ""; // El lexema "en construcci�n"
	protected int last = 0; // El �ltimo caracter le�do. Lo mantengo como int porque necesito encontrar el -1 que devuelve BufferedReader.read() en EOF.
	protected int lineN = 1, startLineN = 1; // Numero de l�nea actual, y n�mero de l�nea al comenzar a buscar un token (para el caso "comentario sin cerrar")
	
	// Variables utilizadas en t�cnicas para facilitar el testing.
	protected boolean testing = false; // False: se lee desde un archivo (operatoria normal); true: se lee desde un String, para testear m�s c�modamente. 
	protected int caracterActual = 0; // Caracter a leer en la cadena de entrada.
	protected String toTest; // Cadena a testear.
	
	protected boolean verbose = false; // "True" activa los mensajes de informaci�n.
	
	protected final String[] keywordsList = {"boolean", "char", "class", "classDef", "else", "extends", "for", "if", 
			"int", "new", "null", "return", "String", "super", "this", "void", "while"};
	protected final String[] forbiddenWordsList = {"abstract", "break", "byte", "byvalue", "case", "cast", "catch", 
			"const", "continue", "default", "do", "double", "final", "finally", "future", "generic", "goto", 
			"implements", "import", "inner", "instanceof", "interface", "long", "native", "none", "operator", "outer",
			"package", "private", "protected", "public", "rest", "short", "static", "switch", "synchronized", "throw",
			"throws", "transient", "try", "var", "volatile"};
	
	
	public ALex(String archivo, boolean verbose) throws InvalidIdentifierException, InvalidCharacterException, ForbiddenWordException, InvalidStringException, ForbiddenOperatorException, OutOfAlphabetException, UnclosedCommentException	{
		try {
			this.verbose = verbose;
			inputStream = new BufferedReader(new FileReader(archivo)); 
		 	
		}
		catch (FileNotFoundException e) {
			System.err.println("Archivo no encontrado.");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("Excepci�n de E/S.");
			e.printStackTrace();
		}
	}
	
	
	// Constructor alternativo que lee directamente sobre un String en vez de sobre un archivo, utilizado para testing.
	public ALex(String archivo, String entrada) throws InvalidIdentifierException, InvalidCharacterException, ForbiddenWordException, InvalidStringException, ForbiddenOperatorException, OutOfAlphabetException, UnclosedCommentException {
		if(archivo.equals("test")) {
			toTest = entrada;
			testing = true;
			verbose = true;
		}	
	}
	

	/***  Estado inicial ***/
	
	@org.junit.Test
	public Token obtenerToken() throws IOException, InvalidIdentifierException, InvalidCharacterException, ForbiddenWordException, InvalidStringException, ForbiddenOperatorException, OutOfAlphabetException, UnclosedCommentException {
		
		if (last == 0) last = obtenerCaracter(); // Para la primera invocaci�n.
		
		while(last != -1) { // no EOF
			startLineN = lineN; 
			if(esCaracterIgnorado((char)last)) {
				last = obtenerCaracter();
				continue;
			}
			if((last>=65 && last<=90)||(last>=97 && last<=122)||last==95) return e1(); 	// [a..z]|[A..Z]|_
			if(last>=48 && last<=57) return e2(); 							 			// [0..9]
			if(last==39) return e3();													// '
			if(last==34) return e6();													// "
			if(last==38) return e8();													// &
			if(last==124) return e9();													// |
			if(last==62) return e10();													// >
			if(last==60) return e11();													// <
			if(last==61) return e12();													// =
			if(last==37 || last==42 || last==94) return e13();							// %|*|^
			if(last==43 || last==45) return e14();										// +|- 
			if(last==47) return e15();													// /
			if(last==44 || last==59 || last==46 || last==40 || last==41 || 
				last==91 || last==93 || last==123 || last==125)
					return e16();														// ,|;|.|(|)|[|]|{|}
			if(last==33) return e17();													// !
			if(last==126 || last==63 || last==58)										// ~|?|:
				throw new ForbiddenOperatorException((char)last+" no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN);
			
			throw new OutOfAlphabetException("El caracter le�do no es v�lido en el alfabeto del lenguaje."); // Si llega ac�, el caracter le�do est� fuera del alfabeto y se debe lanzar la excepci�n apropiada.
		}
		
		return null; // No hay m�s tokens.
	}
	
	/*** M�todos auxiliares ***/
	
	// Encapsula BufferedReader.read() para contar las l�neas del archivo.
	protected int obtenerCaracter() throws IOException
	{
		if(testing) { // Modo testing
			if (caracterActual >= toTest.length()) return -1;
			if (toTest.charAt(caracterActual) == '\n') lineN++;
			return (int)toTest.charAt(caracterActual++);
		}
		else // Operatoria normal
		{
			int toRet = inputStream.read();
			if (toRet=='\n') lineN++;
			
			if(verbose) System.out.println("Le�do: "+(char)toRet);
			return toRet;
		}
	}
	
	/* Retorna true para los caracteres que estando fuera del lenguaje, igualmente se aceptan en el archivo para
		mejorar su legibilidad */
	private boolean esCaracterIgnorado(char cual) {
		return (cual==' ' || cual=='\n' || cual=='\t' || cual=='\r');
	}

	
	/*** Ac� empiezan los m�todos correspondientes a los distintos estados del A.F. ***/

	// identifier
	protected Token e1() throws IOException, ForbiddenWordException {
		if(verbose) System.out.println("Buscando: identifier");
		
		lexema += (char)last;
		last = obtenerCaracter();
			
		// [a..z]|[A..Z]|[0..9]|_ - Mientras haya m�s, sigue leyendo.
		if((last>=65 && last<=90)||
		   (last>=97 && last<=122)||
		   (last>=48 && last<=57)||
		   last==95) {
			return e1();
		}
		else {
			String tokenType = "identifier"; 
			
			// Optimizaciones
			
			for(int i=0; i<keywordsList.length; i++) // Si es una palabra clave, cambio el tipo de token.
				if(keywordsList[i].equals(lexema)) tokenType = keywordsList[i];
			
			for(int i=0; i<forbiddenWordsList.length; i++) // Si es una palabra prohibida, cambio el tipo de token.
				if(forbiddenWordsList[i].equals(lexema)) throw new ForbiddenWordException("La palabra "+lexema+ " est� prohibida en el lenguaje. Puede "+
						"tratarse de una palabra Java prohibida por compatibilidad, o de una palabra reservada para futuras extensiones. L�nea: "+lineN);
				
			if(lexema.equals("true") || lexema.equals("false")) tokenType = "boolLiteral"; // Si es un literal booleano, cambio el tipo de token.
			
			Token toRet = new Token(tokenType, lexema, lineN);
			lexema = "";
			return toRet;
		}
	}
	
	// intLiteral
	protected Token e2() throws IOException, InvalidIdentifierException {
		if(verbose) System.out.println("Buscando: intLiteral");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last>=48 && last<=57) // [0..9]
			return e2();
		else {
			if((last>=65 && last<=90)||(last>=97 && last<=122))  // Caso de error [a..z][A..Z], ej. 3asdf
				throw new InvalidIdentifierException((lexema+(char)last) + " no es un identificador v�lido. L�nea: "+lineN);
			
			Token toRet = new Token("intLiteral", lexema, lineN);
			lexema = "";
			return toRet;
		}		
	}
	
	// charLiteral
	protected Token e3() throws IOException, InvalidCharacterException {
		if(verbose) System.out.println("Buscando: charLiteral");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last!=39 && last!=92 && last!=13 && last!=10) return e4(); // ^[\\|\n|\r|']
		else if(last==92) return e5(); // Caso en que se encontr� una "\"
		else throw new InvalidCharacterException((char)last + " no es un caracter v�lido. L�nea: "+lineN);
	}
	
	// charLiteral con caracter encontrado.
	protected Token e4() throws IOException, InvalidCharacterException {
		if(verbose) System.out.println("Buscando: charLiteral (esperando cierre de comilla)");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==39) { // '
			Token toRet = new Token("charLiteral", lexema+"'", lineN);
			lexema = "";
			last = 0; // En este caso salgo al leer la "'", no el siguiente caracter, por lo tanto debo indicar que hace falta leer uno nuevo.
			return toRet;
		}
		else throw new InvalidCharacterException((lexema+(char)last)+ " no forma un caracter v�lido. Se esperaba cierra de comillas simples. L�nea: "+lineN);
	}
		
	// charLiteral habiendo le�do barra.
	protected Token e5() throws IOException, InvalidCharacterException {
		if(verbose) System.out.println("Buscando: charLiteral (caso caracter especial)");

			// <-- n�tese que no agrego al lexema la barra le�da anteriormente.
		last = obtenerCaracter();
			
		if(last==110) last=(char)10; // \n: LF
		if(last==116) last=(char)9;  // \t: TAB
		if(last==114) last=(char)13; // \r: CR
		if(last==39) throw new InvalidCharacterException("Caracter especial mal formado en l�nea "+lineN);
	    // De lo contrario, queda asignado a "last" el caracter le�do interpretado literalmente, incluyendo otra barra.
		
		return e4();
	}
	
	// stringLiteral
	protected Token e6() throws IOException, InvalidStringException {
		if(verbose) System.out.println("Buscando: stringLiteral");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==10) throw new InvalidStringException("El caracter NL (new line) no puede aparecer en un string (ver Sintaxis 2.7.4.1). L�nea: "+lineN); // NL
		if(last==92) return e7(); // \
		else if(last==34) { // "
			Token toRet = new Token("stringLiteral", lexema+(char)last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		}
		else return e6();
	}
	
	// stringLiteral habiendo le�do barra.
	protected Token e7() throws IOException, InvalidStringException {
		if(verbose) System.out.println("Buscando: stringLiteral (caso caracter especial)");

			// <-- n�tese que no agrego al lexema la barra le�da anteriormente.
		last = obtenerCaracter();
		
		if(last==110) last=(char)10; // \n: LF
		if(last==116) last=(char)9;  // \t: TAB
		if(last==114) last=(char)13; // \r: CR
		if(last==34) throw new InvalidStringException("Caracter especial mal formado en l�nea "+lineN);
	    // De lo contrario, queda asignado a "last" el caracter le�do interpretado literalmente, incluyendo otra barra.
		
		return e6();	
	}
	
	// Le�do &
	protected Token e8() throws IOException, ForbiddenOperatorException {
		if(verbose) System.out.println("Buscando: &&");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==38) { // &
			Token toRet = new Token("&&", lexema+(char)last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		}
		else if(last==61) throw new ForbiddenOperatorException("&= no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN);
		else throw new ForbiddenOperatorException("& no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN);
	}
	
	// Le�do |
	protected Token e9() throws IOException, ForbiddenOperatorException {
		if(verbose) System.out.println("Buscando: ||");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==124) { // |
			Token toRet = new Token("||", lexema+(char)last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		}
		else if(last==61) throw new ForbiddenOperatorException("|= no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN);
		else throw new ForbiddenOperatorException("| no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN);
	}
	
	// Le�do >
	protected Token e10() throws IOException, ForbiddenOperatorException {
		if(verbose) System.out.println("Buscando: > o >=");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==61) { // =
			Token toRet = new Token(">=", lexema+(char)last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		}
		else if(last==62) // > 
			throw new ForbiddenOperatorException(">> no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN);
		else {
			Token toRet = new Token(">", lexema, lineN);
			lexema = "";
			return toRet;
		}
	}
	
	// Le�do <
	protected Token e11() throws IOException, ForbiddenOperatorException {
		if(verbose) System.out.println("Buscando: < o <=");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==61) { // =
			Token toRet = new Token("<=", lexema+(char)last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		}
		else if(last==60) // < 
			throw new ForbiddenOperatorException("<< no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN);
		else {
			Token toRet = new Token("<", lexema, lineN);
			lexema = "";
			return toRet;
		}
	}
	
	// Le�do =
	protected Token e12() throws IOException, ForbiddenOperatorException {
		if(verbose) System.out.println("Buscando: = o ==");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==61) { // =
			Token toRet = new Token("==", lexema+(char)last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		}
		else {
			Token toRet = new Token("=", lexema, lineN);
			lexema = "";
			return toRet;
		}	
	}
	
	// Hallado %|*|^
	// Cada uno de estos operadores define su propio token en este dise�o, pero aprovechando que todos se comportan de forma id�ntica, utilizo este "meta-estado".
	protected Token e13() throws IOException, ForbiddenOperatorException {
		if(verbose) System.out.println("Buscando: %, * o ^");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==61) throw new ForbiddenOperatorException(lexema+"= no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN); // Casos %=, *=, ^=
		else {
			Token toRet = new Token(lexema, lexema, lineN); // Puedo hacer esto por haber tomado la convenci�n de que los tokens con patr�n "unitario" utilicen el mismo nombre que su �nico lexema v�lido.
			lexema = "";
			return toRet;
		}	
	}
	
	// Hallado +|-
	// Caso similar a e13(), pero tambi�n debo evitar ++|--. Separado por legibilidad.
	protected Token e14() throws IOException, ForbiddenOperatorException {
		if(verbose) System.out.println("Buscando: + o -");
			
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==61) throw new ForbiddenOperatorException(lexema+"= no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN); // Casos +=, -=
		if(lexema.equals(""+(char)last)) throw new ForbiddenOperatorException(lexema+lexema+" no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN); // Casos ++, --
		else {
			Token toRet = new Token(lexema, lexema, lineN); // Puedo hacer esto por haber tomado la convenci�n de que los tokens con patr�n "unitario" utilicen el mismo nombre que su �nico lexema v�lido.
			lexema = "";
			return toRet;
		}	
	}
	
	// Hallado /
	protected Token e15() throws IOException, ForbiddenOperatorException, InvalidIdentifierException, InvalidCharacterException, ForbiddenWordException, InvalidStringException, OutOfAlphabetException, UnclosedCommentException {
		if(verbose) System.out.println("Buscando: / (divisi�n), // o /* (comentarios)");
			
		lexema += (char)last;
		last = obtenerCaracter();
			
		if(last==61) throw new ForbiddenOperatorException("/= no es un operador valido de Decaf (ver Sintaxis 2.9). L�nea: "+lineN); // Caso /=
		if(last==47) return c1(); // Caso comentario de l�nea simple: //
		if(last==42) return c2(); // Caso comentario multil�nea: /*
		else {
			Token toRet = new Token("/", lexema, lineN);
			lexema = "";
			return toRet;
		}	
	}

	// Puntuaci�n (ver Sintaxis 2.8). Situaci�n similar a e13(), ac� se agrupan varios estados del aut�mata que se comportan id�nticamente. Lo agrego por completitud, podr�a haber sido m�s prolijo hacer esto directamente en obtenerToken() 
	protected Token e16() throws IOException {
		if(verbose) System.out.println("Buscando: , ; . ( ) [ ] { }");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		Token toRet = new Token(lexema, lexema, lineN); // Puedo hacer esto por haber tomado la convenci�n de que los tokens con patr�n "unitario" utilicen el mismo nombre que su �nico lexema v�lido.
		lexema = "";
		return toRet;
	}
	
	// Le�do !
	protected Token e17() throws IOException {
		if(verbose) System.out.println("Buscando: ! o !=");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==61) { // =
			Token toRet = new Token("!=", lexema+(char)last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		}
		else {
			Token toRet = new Token("!", lexema, lineN);
			lexema = "";
			return toRet;
		}	
	}
	
	// Comentario de l�nea simple 
	protected Token c1() throws IOException, InvalidIdentifierException, InvalidCharacterException, ForbiddenWordException, InvalidStringException, ForbiddenOperatorException, OutOfAlphabetException, UnclosedCommentException {
		if(verbose) System.out.println("Leyendo comentario de l�nea simple.");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==10 || last==-1) {
			if(verbose) System.out.println("Fin de comentario de l�nea simple.");
			lexema = "";
			return obtenerToken(); // Se encontr� fin de l�nea (o fin de archivo), i.e. fin del comentario. Como se pide devolver un token pero el comentario se debe ignorar, llamo recursivamente a obtenerToken();
		}
		else return c1();
	}
	
	// Comentario multil�nea
	protected Token c2() throws IOException, InvalidIdentifierException, InvalidCharacterException, ForbiddenWordException, InvalidStringException, ForbiddenOperatorException, OutOfAlphabetException, UnclosedCommentException {
		if(verbose) System.out.println("Leyendo comentario multil�nea.");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==42) return c3(); // *
		if(last==-1) throw new UnclosedCommentException("Se lleg� al final del archivo sin encontrar el cierre de comentario. El comentario fue abierto en la l�nea "+startLineN);
		else return c2();
	}
	
	// Comentario multil�nea habiendo encontrado asterisco (potencial cierre)
	protected Token c3() throws IOException, InvalidIdentifierException, InvalidCharacterException, ForbiddenWordException, InvalidStringException, ForbiddenOperatorException, OutOfAlphabetException, UnclosedCommentException {
		if(verbose) System.out.println("Leyendo comentario multil�nea (encontrado *).");
		
		lexema += (char)last;
		last = obtenerCaracter();
		
		if(last==47) {
			if(verbose) System.out.println("Fin de comentario multil�nea.");
			lexema = "";
			last = 0;
			return obtenerToken(); // Se encontr� fin del comentario. Como se pide devolver un token pero el comentario se debe ignorar, llamo recursivamente a obtenerToken();
		}
		if(last==-1) throw new UnclosedCommentException("Se lleg� al final del archivo sin encontrar el cierre de comentario. El comentario fue abierto en la l�nea "+startLineN);
		else return c2(); // Falsa alarma, era un asterisco parte del comentario.		
	}
	
}
	

