package alex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import common.Application;
import common.Logger;
import enums.TokenType;
import alex.exceptions.*;

/**
 * Analizador Lexico
 * 
 * @author jcaramello, nechegoyen
 * 
 */
public class ALex {

	protected BufferedReader inputStream;
	// El lexema "en construcción"
	protected String lexema = "";
	// El último caracter leído. Lo mantengo como int porque necesito encontrar
	// el -1 que devuelve BufferedReader.read() en EOF.
	protected int last = 0;
	// Numero de línea actual, y número de línea al comenzar a buscar un token
	// (para el caso "comentario sin cerrar")
	protected int lineN = 1, startLineN = 1;

	/**
	 * Variables utilizadas en técnicas para facilitar el testing.
	 */
	// Caracter a leer en la cadena de entrada.
	protected int caracterActual = 0;
	// Cadena a testear.
	protected String toTest;

	protected static final String[] keywordsList = { "boolean", "char",
			"class", "else", "extends", "for", "if", "int", "new", "null",
			"return", "String", "this", "void", "while", "var", "static",
			"dynamic" };

	protected static final String[] forbiddenWordsList = { "abstract", "break",
			"byte", "byvalue", "case", "cast", "catch", "const", "continue",
			"default", "do", "double", "final", "finally", "future", "generic",
			"goto", "implements", "import", "inner", "instanceof", "interface",
			"long", "native", "none", "operator", "outer", "package",
			"private", "protected", "public", "rest", "short", "switch",
			"synchronized", "throw", "throws", "transient", "try", "volatile" };

	/**
	 * Default Constructor
	 * 
	 * @param input
	 * @param verbose
	 * @throws InvalidIdentifierException
	 * @throws InvalidCharacterException
	 * @throws ForbiddenWordException
	 * @throws InvalidStringException
	 * @throws ForbiddenOperatorException
	 * @throws ForbiddenCharacterException
	 * @throws UnclosedCommentException
	 */
	public ALex(String input) throws InvalidIdentifierException,
			InvalidCharacterException, ForbiddenWordException,
			InvalidStringException, ForbiddenOperatorException,
			ForbiddenCharacterException, UnclosedCommentException {
		try {

			if (Application.isTesting)
				toTest = input;
			else
				inputStream = new BufferedReader(new FileReader(input));

		} catch (FileNotFoundException e) {
			Logger.log("Archivo no encontrado.");
			Logger.verbose(e);
		} catch (Exception e) {
			Logger.verbose(e);
		}
	}

	/*** Estado inicial ***/

	/**
	 * Obtiene el proximo token
	 * @return El proximo token en el archivo fuente
	 * @throws IOException
	 * @throws InvalidIdentifierException
	 * @throws InvalidCharacterException
	 * @throws ForbiddenWordException
	 * @throws InvalidStringException
	 * @throws ForbiddenOperatorException
	 * @throws ForbiddenCharacterException
	 * @throws UnclosedCommentException
	 */
	@org.junit.Test
	public Token getToken() throws IOException, InvalidIdentifierException,
			InvalidCharacterException, ForbiddenWordException,
			InvalidStringException, ForbiddenOperatorException,
			ForbiddenCharacterException, UnclosedCommentException {

		if (last == 0)
			last = getNextCharacter(); // Para la primera invocación.

		while (last != -1) { // no EOF
			startLineN = lineN;
			if (ALexHelper.esCaracterIgnorado(last)) {
				last = getNextCharacter();
				continue;
			}

			// [a..z][A..Z]|_
			if (ALexHelper.isLetterOrUnderscore(last))
				return e1();
			// [0..9]
			if (ALexHelper.isDigit(last))
				return e2();
			// '
			if (last == 39)
				return e3();
			// "
			if (last == 34)
				return e6();
			// &
			if (last == 38)
				return e8();
			// |
			if (last == 124)
				return e9();
			// >
			if (last == 62)
				return e10();
			// <
			if (last == 60)
				return e11();
			// =
			if (last == 61)
				return e12();
			// %|*|^
			if (last == 37 || last == 42)
				return e13();
			// +|-
			if (last == 43 || last == 45)
				return e14();
			// /
			if (last == 47)
				return e15();
			// ,|;|.|(|)|[|]|{|}
			if (ALexHelper.isPunctuationSymbol(last))
				return e16();
			// !
			if (last == 33)
				return e17();

			// (~|?|:)
			if (ALexHelper.isForbiddenOperator(last))
				throw new ForbiddenOperatorException((char) last + "", lineN);

			// Si llega acá, el caracter leído está fuera del alfabeto y se debe
			// lanzar la excepción apropiada.
			throw new ForbiddenCharacterException((char) last, lineN);
		}

		return null; // No hay más tokens.
	}

	/*** Métodos auxiliares ***/

	/***
	 * Encapsula BufferedReader.read() para contar las líneas del archivo.
	 * @return
	 * @throws IOException
	 */
	protected int getNextCharacter() throws IOException {
		if (Application.isTesting) { // Modo testing
			if (caracterActual >= toTest.length())
				return -1;
			if (toTest.charAt(caracterActual) == '\n')
				lineN++;
			return (int) toTest.charAt(caracterActual++);
		} else // Operatoria normal
		{
			int toRet = inputStream.read();
			if (toRet == '\n')
				lineN++;

			Logger.verbose("Leído: %s", (char) toRet + "");
			return toRet;
		}
	}

	/**
	 * 
	 * Acá empiezan los métodos correspondientes a los distintos estados del A.F.
	 *
	 **/

	/**
	 * Identifier State
	 * @return
	 * @throws IOException
	 * @throws ForbiddenWordException
	 */
	protected Token e1() throws IOException, ForbiddenWordException {
		Logger.verbose("Buscando: identifier");

		lexema += (char) last;
		last = getNextCharacter();

		if (ALexHelper.isLetterOrUnderscore(last) || ALexHelper.isDigit(last)) {
			return e1();
		} else {
			TokenType tokenType = TokenType.Identifier;

			/** Optimizaciones **/

			// Si es una palabra clave, cambio el tipo de token.
			for (int i = 0; i < keywordsList.length; i++)
				if (keywordsList[i].equals(lexema))
					tokenType = TokenType.getValue(keywordsList[i]);

			for (int i = 0; i < forbiddenWordsList.length; i++)
				// Si es una palabra prohibida, cambio el tipo de token.
				if (forbiddenWordsList[i].equals(lexema))
					throw new ForbiddenWordException(lexema, lineN);

			// Si es un literal booleano, cambio el tipo de token.
			if (lexema.equals("true") || lexema.equals("false"))
				tokenType = TokenType.BooleanLiteral;

			Token toRet = new Token(tokenType, lexema, lineN);
			lexema = "";
			return toRet;
		}
	}

	/**
	 * intLiteral State
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidIdentifierException
	 */
	protected Token e2() throws IOException, InvalidIdentifierException {
		Logger.verbose("Buscando: intLiteral");

		lexema += (char) last;
		last = getNextCharacter();

		if (ALexHelper.isDigit(last))
			return e2();
		else {
			if (ALexHelper.isLetterOrUnderscore(last))
				throw new InvalidIdentifierException(lexema + (char) last,
						lineN);

			Token toRet = new Token(TokenType.IntigerLiteral, lexema, lineN);
			lexema = "";
			return toRet;
		}
	}

	/**
	 * charLiteral State
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidCharacterException
	 */
	protected Token e3() throws IOException, InvalidCharacterException {
		Logger.verbose("Buscando: charLiteral");

		lexema += (char) last;
		last = getNextCharacter();

		// ^[\\|\n|\r|']
		if (last != 39 && last != 92 && last != 13 && last != 10)
			return e4();
		else if (last == 92)
			return e5(); // Caso en que se encontró una "\"
		else
			throw new InvalidCharacterException((char) last, lineN);
	}

	/**
	 * charLiteral con caracter encontrado.
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidCharacterException
	 */
	protected Token e4() throws IOException, InvalidCharacterException {
		Logger.verbose("Buscando: charLiteral (esperando cierre de comilla)");

		lexema += (char) last;
		last = getNextCharacter();

		// '
		if (last == 39) {
			Token toRet = new Token(TokenType.CharLiteral, lexema + "'", lineN);
			lexema = "";
			// En este caso salgo al leer la "'", no el siguiente caracter, por
			// lo tanto debo indicar que hace falta leer uno nuevo.
			last = 0;
			return toRet;
		} else
			throw new InvalidCharacterException(lexema + (char) last,"Se esperaba cierra de comillas simples", lineN);
	}

	/**
	 * charLiteral habiendo leído barra.
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidCharacterException
	 */
	protected Token e5() throws IOException, InvalidCharacterException {
		Logger.verbose("Buscando: charLiteral (caso caracter especial)");

		// <-- nótese que no agrego al lexema la barra leída anteriormente.
		last = getNextCharacter();

		if (last == 110)
			last = (char) 10; // \n: LF
		if (last == 116)
			last = (char) 9; // \t: TAB
		if (last == 114)
			last = (char) 13; // \r: CR

		return e4();
	}

	/**
	 * stringLiteral
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidStringException
	 */
	protected Token e6() throws IOException, InvalidStringException {
		Logger.verbose("Buscando: stringLiteral");

		lexema += (char) last;
		last = getNextCharacter();

		// NL
		if (last == 10)
			throw new InvalidStringException("El caracter NL (new line) no puede aparecer en un string",lineN);
		// \
		if (last == 92)
			return e7();
		// "
		else if (last == 34) {
			Token toRet = new Token(TokenType.StringLiteral, lexema
					+ (char) last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		} else if (last == -1)
			throw new InvalidStringException("String mal formado", lineN);
		else
			return e6();
	}

	/**
	 * stringLiteral habiendo leído barra.
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidStringException
	 */
	protected Token e7() throws IOException, InvalidStringException {
		Logger.verbose("Buscando: stringLiteral (caso caracter especial)");

		// <-- nótese que no agrego al lexema la barra leída anteriormente.
		last = getNextCharacter();

		if (last == 110)
			last = (char) 10; // \n: LF
		if (last == 116)
			last = (char) 9; // \t: TAB
		if (last == 114)
			last = (char) 13; // \r: CR
		if (last == 34)
			throw new InvalidStringException("Caracter especial mal formado.",
					lineN);
		// De lo contrario, queda asignado a "last" el caracter leído
		// interpretado literalmente, incluyendo otra barra.

		return e6();
	}

	/**
	 * Leído &
	 * 
	 * @return
	 * @throws IOException
	 * @throws ForbiddenOperatorException
	 */
	protected Token e8() throws IOException, InvalidIdentifierException {
		Logger.verbose("Buscando: &&");

		lexema += (char) last;
		last = getNextCharacter();
		// &
		if (last == 38) { 
			Token toRet = new Token(TokenType.AndOperator, lexema + (char) last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		} 
		else throw new InvalidIdentifierException(lexema, lineN);
	}

	/**
	 * Leído |
	 * 
	 * @return
	 * @throws IOException
	 * @throws ForbiddenOperatorException
	 */
	protected Token e9() throws IOException, ForbiddenOperatorException {
		Logger.verbose("Buscando: ||");

		lexema += (char) last;
		last = getNextCharacter();

		if (last == 124) { // |
			Token toRet = new Token(TokenType.OrOperator, lexema + (char) last,
					lineN);
			lexema = "";
			last = 0;
			return toRet;
		} else if (last == 61)
			throw new ForbiddenOperatorException("|=", lineN);
		else
			throw new ForbiddenOperatorException("|", lineN);
	}

	/**
	 * Leído >
	 * 
	 * @return
	 * @throws IOException
	 * @throws ForbiddenOperatorException
	 */
	protected Token e10() throws IOException, ForbiddenOperatorException {
		Logger.verbose("Buscando: > o >=");

		lexema += (char) last;
		last = getNextCharacter();

		if (last == 61) { // =
			Token toRet = new Token(TokenType.GratherOrEqualOperator, lexema
					+ (char) last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		} else if (last == 62) // >
			throw new ForbiddenOperatorException(">>", lineN);
		else {
			Token toRet = new Token(TokenType.GratherOperator, lexema, lineN);
			lexema = "";
			return toRet;
		}
	}

	/**
	 * Leído <
	 * 
	 * @return
	 * @throws IOException
	 * @throws ForbiddenOperatorException
	 */
	protected Token e11() throws IOException, ForbiddenOperatorException {
		Logger.verbose("Buscando: < o <=");

		lexema += (char) last;
		last = getNextCharacter();

		if (last == 61) { // =
			Token toRet = new Token(TokenType.LessOrEqualOperator, lexema
					+ (char) last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		} else if (last == 60) // <
			throw new ForbiddenOperatorException("<<", lineN);
		else {
			Token toRet = new Token(TokenType.LessOperator, lexema, lineN);
			lexema = "";
			return toRet;
		}
	}

	/**
	 * Leído =
	 * 
	 * @return
	 * @throws IOException
	 * @throws ForbiddenOperatorException
	 */
	protected Token e12() throws IOException, ForbiddenOperatorException {
		Logger.verbose("Buscando: = o ==");

		lexema += (char) last;
		last = getNextCharacter();

		if (last == 61) { // =
			Token toRet = new Token(TokenType.EqualOperator, lexema
					+ (char) last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		} else {
			Token toRet = new Token(TokenType.AssignOperator, lexema, lineN);
			lexema = "";
			return toRet;
		}
	}

	/**
	 * Hallado %|* Cada uno de estos operadores define su propio token en este
	 * diseño, pero aprovechando que todos se comportan de forma idéntica,
	 * utilizo este "meta-estado".
	 * 
	 * @return
	 * @throws IOException
	 * @throws ForbiddenOperatorException
	 */
	protected Token e13() throws IOException, ForbiddenOperatorException {
		Logger.verbose("Buscando: % o *");

		lexema += (char) last;
		last = getNextCharacter();		
		Token toRet = new Token(TokenType.getValue(lexema), lexema, lineN);
		lexema = "";
		return toRet;

	}

	/**
	 * Hallado +|- Caso similar a e13()
	 * Separado por legibilidad.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ForbiddenOperatorException
	 */
	protected Token e14() throws IOException, ForbiddenOperatorException {
		Logger.verbose("Buscando: + o -");

		lexema += (char) last;
		last = getNextCharacter();		
		Token toRet = new Token(TokenType.getValue(lexema), lexema, lineN);
		lexema = "";
		return toRet;

	}

	/**
	 * Hallado /
	 * 
	 * @return
	 * @throws IOException
	 * @throws ForbiddenOperatorException
	 * @throws InvalidIdentifierException
	 * @throws InvalidCharacterException
	 * @throws ForbiddenWordException
	 * @throws InvalidStringException
	 * @throws ForbiddenCharacterException
	 * @throws UnclosedCommentException
	 */
	protected Token e15() throws IOException, ForbiddenOperatorException,
			InvalidIdentifierException, InvalidCharacterException,
			ForbiddenWordException, InvalidStringException,
			ForbiddenCharacterException, UnclosedCommentException {
		Logger.verbose("Buscando: / (división), // o /* (comentarios)");

		lexema += (char) last;
		last = getNextCharacter();
		
		// Caso comentario de línea simple: //
		if (last == 47)
			return c1();
		// Caso comentario multilínea: /*
		if (last == 42)
			return c2();
		else {
			Token toRet = new Token(TokenType.DivisionOperator, lexema, lineN);
			lexema = "";
			return toRet;
		}
	}

	/**
	 * Puntuación (ver Sintaxis 2.8). Situación similar a e13(), acá se agrupan
	 * varios estados del autómata que se comportan idénticamente. Lo agrego por
	 * completitud, podría haber sido más prolijo hacer esto directamente en
	 * obtenerToken()
	 * 
	 * @return
	 * @throws IOException
	 */
	protected Token e16() throws IOException {
		Logger.verbose("Buscando: , ; . ( ) [ ] { }");

		lexema += (char) last;
		last = getNextCharacter();
		// Puedo hacer esto por haber tomado la convención de que los tokens con
		// patrón "unitario"
		// utilicen el mismo nombre que su único lexema válido.
		Token toRet = new Token(TokenType.getValue(lexema), lexema, lineN);
		lexema = "";
		return toRet;
	}

	/**
	 * Leído !
	 * 
	 * @return
	 * @throws IOException
	 */
	protected Token e17() throws IOException {
		Logger.verbose("Buscando: ! o !=");

		lexema += (char) last;
		last = getNextCharacter();

		if (last == 61) { // =
			Token toRet = new Token(TokenType.DistinctOperator, lexema
					+ (char) last, lineN);
			lexema = "";
			last = 0;
			return toRet;
		} else {
			Token toRet = new Token(TokenType.NotOperator, lexema, lineN);
			lexema = "";
			return toRet;
		}
	}

	/**
	 * Comentario de línea simple
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidIdentifierException
	 * @throws InvalidCharacterException
	 * @throws ForbiddenWordException
	 * @throws InvalidStringException
	 * @throws ForbiddenOperatorException
	 * @throws ForbiddenCharacterException
	 * @throws UnclosedCommentException
	 */
	protected Token c1() throws IOException, InvalidIdentifierException,
			InvalidCharacterException, ForbiddenWordException,
			InvalidStringException, ForbiddenOperatorException,
			ForbiddenCharacterException, UnclosedCommentException {
		Logger.verbose("Leyendo comentario de línea simple.");

		lexema += (char) last;
		last = getNextCharacter();

		if (last == 10 || last == -1) {
			Logger.verbose("Fin de comentario de línea simple.");
			lexema = "";
			// Se encontró fin de línea (o fin de archivo), i.e. fin del
			// comentario.
			// Como se pide devolver un token pero el comentario se debe
			// ignorar, llamo recursivamente a obtenerToken();
			return getToken();
		} else
			return c1();
	}

	/**
	 * Comentario multilínea
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidIdentifierException
	 * @throws InvalidCharacterException
	 * @throws ForbiddenWordException
	 * @throws InvalidStringException
	 * @throws ForbiddenOperatorException
	 * @throws ForbiddenCharacterException
	 * @throws UnclosedCommentException
	 */
	protected Token c2() throws IOException, InvalidIdentifierException,
			InvalidCharacterException, ForbiddenWordException,
			InvalidStringException, ForbiddenOperatorException,
			ForbiddenCharacterException, UnclosedCommentException {
		Logger.verbose("Leyendo comentario multilínea.");

		lexema += (char) last;
		last = getNextCharacter();

		if (last == 42)
			return c3(); // *
		if (last == -1)
			throw new UnclosedCommentException(startLineN);
		else
			return c2();
	}

	/**
	 * Comentario multilínea habiendo encontrado asterisco (potencial cierre)
	 * 
	 * @return
	 * @throws IOException
	 * @throws InvalidIdentifierException
	 * @throws InvalidCharacterException
	 * @throws ForbiddenWordException
	 * @throws InvalidStringException
	 * @throws ForbiddenOperatorException
	 * @throws ForbiddenCharacterException
	 * @throws UnclosedCommentException
	 */
	protected Token c3() throws IOException, InvalidIdentifierException,
			InvalidCharacterException, ForbiddenWordException,
			InvalidStringException, ForbiddenOperatorException,
			ForbiddenCharacterException, UnclosedCommentException {
		Logger.verbose("Leyendo comentario multilínea (encontrado *).");

		lexema += (char) last;
		last = getNextCharacter();

		if (last == 47) {
			Logger.verbose("Fin de comentario multilínea.");
			lexema = "";
			last = 0;
			// Se encontró fin del comentario.
			// Como se pide devolver un token pero el comentario se debe
			// ignorar, llamo recursivamente a obtenerToken();
			return getToken();
		}
		if (last == -1)
			throw new UnclosedCommentException(startLineN);
		// Falsa alarma, era un asterisco parte del comentario.
		else
			return c2();
	}

}
