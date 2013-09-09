package asint;

import java.io.IOException;

import common.Application;
import common.Logger;
import enums.TokenType;

import alex.ALex;
import alex.Token;
import alex.exceptions.ALexException;
import alex.exceptions.ForbiddenCharacterException;
import alex.exceptions.ForbiddenOperatorException;
import alex.exceptions.ForbiddenWordException;
import alex.exceptions.InvalidCharacterException;
import alex.exceptions.InvalidIdentifierException;
import alex.exceptions.InvalidStringException;
import alex.exceptions.UnclosedCommentException;

/**
 * Analizador Sintactico
 * @author jcaramello, nechegoyen
 *
 */
public class ASint {

	private ALex alex; // Instancia de analizador lexico
	
	private Token curr; // Token actual
	private boolean reusar; // Se debe reusar el token actual? (para transiciones lambda)
	private int depth; // Profundidad de llamadas
	
	
	public ASint(String archivo) throws UnexpectedTokenException
	{
		depth = 0;
		
		try {
			Logger.log("\n\n*** Iniciando " + archivo + " ***");
			
			alex = new ALex(archivo);
			
			inicial();			
			
		} // Atrapo las excepciones previamente definidas en el analizador lexico
		  catch (ALexException e) {
			Logger.log("\n"+e.getMessage());
		} 
	}
	
	
	private void getToken() {
		
		if (reusar) {
			reusar = false;
		} 
		else 
		{
			try {
		
				curr = alex.getToken();
				
				if (curr==null)
					curr = new Token(TokenType.NotSet, "EOF", alex.getLineN()); // Pseudotoken como alternativa a excepcion.
					//throw new UnexpectedEOFException();
				
				Logger.log("Encontrado token "+ curr.getTokenType() + ": " + curr.getLexema() +" en linea "+ curr.getLinea());
			} catch (ALexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	

	private void reuseToken() {
		reusar = true;
	}
	
	
	// M�todos correspondientes a los no-terminales de la gram�tica.
	
	private void inicial() throws UnexpectedTokenException {	
		depth++;
		Logger.log(depth + "-> Iniciando <Inicial>");
		
		clase();
		inicialPlus();
		
		Logger.log("<-" + depth + " Fin <Inicial>");		
		depth--;
	}

	
	private void clase() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Clase>");
		
		getToken(); // class		
		if(curr.getTokenType() != TokenType.ClassKeyword) {
			throw new UnexpectedTokenException("(!) Error, se esperaba class en l�nea " + curr.getLinea());
			
		}
		
		getToken(); // identificador
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en l�nea " + curr.getLinea());
		}
		
		herenciaQ();
		
		getToken(); // {
		if(curr.getTokenType() != TokenType.OpenKeySymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba { en l�nea " + curr.getLinea());
		}
		
		miembroStar();			
		
		getToken(); // }
		if(curr.getTokenType() != TokenType.ClosedKeySymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba } en l�nea " + curr.getLinea());
		}

		Logger.log("<-" + depth + " Fin <Clase>");	
		depth--;
	}


	private void inicialPlus() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Inicial+>");
		
		getToken();
		if(curr.getTokenType() == TokenType.NotSet && curr.getLexema().equals("EOF")) {
			Logger.log("(!) EndOfFile");	
		}	
		else {
			reuseToken();
			inicial();
		}
		
		Logger.log("<-" + depth + " Fin <Inicial+>");	
		depth--;
	}
	
	
	private void herenciaQ() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Herencia?>");
		
		getToken(); // extends
		if(curr.getTokenType() == TokenType.ExtendsKeyword) {
			reuseToken();
			herencia();
		}
		else {
			if(curr.getTokenType() != TokenType.OpenKeySymbol) { // i.e. follow(herenciaQ)
				throw new UnexpectedTokenException("(!) Error, se esperaba extends o { en l�nea " + curr.getLinea());
			}
					
			reuseToken();
		}		
		
		Logger.log("<-" + depth + " Fin <Herencia?>");	
		depth--;
	}



	private void herencia() throws UnexpectedTokenException {
		
		getToken(); // extends, garantizado

		getToken(); // identificador
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en l�nea " + curr.getLinea());
		}	
	}


	private void miembroStar() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Miembro*>");
		
		getToken();
		if(curr.getTokenType() == TokenType.VarKeyword 			  // <Atributo>
			|| curr.getTokenType() == TokenType.Identifier 		  // <Ctor>
			|| curr.getTokenType() == TokenType.StaticKeyword 	  // <ModMetodo>
			|| curr.getTokenType() == TokenType.DynamicKeyword)   // <ModMetodo>
		{
			reuseToken();
			miembro();
			miembroStar();
		}
		else if(curr.getTokenType() == TokenType.ClosedKeySymbol) { // follow(Miembro*)
			reuseToken();
		}
		else {
			throw new UnexpectedTokenException("(!) Error, se esperaba atributo, constructor, m�todo o } en l�nea " + curr.getLinea());
		}
		
		Logger.log("<-" + depth + " Fin <Miembro*>");	
		depth--;
	}

	
	
	private void miembro() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Miembro>");
	
		getToken();
		reuseToken();
		if(curr.getTokenType() == TokenType.VarKeyword) {			  // <Atributo>
			atributo();
		} else if(curr.getTokenType() == TokenType.Identifier) {	  // <Ctor>
			ctor();
		} else if(curr.getTokenType() == TokenType.StaticKeyword 	  // <ModMetodo>
				|| curr.getTokenType() == TokenType.DynamicKeyword) { // <ModMetodo>
			metodo();
		} else {
			throw new UnexpectedTokenException("(!) Error, se esperaba var, identificador o modificador de m�todo (static o dynamic) en l�nea " + curr.getLinea());
		}
		
		Logger.log("<-" + depth + " Fin <Miembro>");	
	    depth--;
	}


	private void metodo() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Metodo>");
		
		getToken();
		reuseToken();
		if(curr.getTokenType() != TokenType.StaticKeyword &&		// Nunca salta
				curr.getTokenType() != TokenType.DynamicKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba modificador de m�todo (static o dynamic) en l�nea " + curr.getLinea());
		} 
		else modMetodo();
		
		getToken();
		reuseToken();
		if(curr.getTokenType() != TokenType.Identifier &&
				curr.getTokenType() != TokenType.BooleanKeyword &&
				curr.getTokenType() != TokenType.CharKeyword &&
				curr.getTokenType() != TokenType.IntKeyword &&
				curr.getTokenType() != TokenType.StringKeyword &&
				curr.getTokenType() != TokenType.VoidKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba tipo de m�todo: void, boolean, int, char, String o identificador de tipo en l�nea " + curr.getLinea());
		}
		else tipoMetodo();
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de m�todo), en l�nea " + curr.getLinea());
		}
		
		argsFormales(); // entiendo que en estos no obtengo ning�n beneficio informando m�s temprano del error.
		varsLocales();
		bloque();
		
		Logger.log("<-" + depth + " Fin <Metodo>");
		depth--;		
	}
	

	private void modMetodo() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <ModMetodo>");
		
		getToken(); // static o dynamic garantizado.

		Logger.log("<-" + depth + " Fin <ModMetodo>");
		depth--;			
	}


	private void tipoMetodo() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <TipoMetodo>");
		
		getToken(); // garantizado void o TipoPrimitivo
		if(curr.getTokenType() != TokenType.VoidKeyword) {
			reuseToken();
			tipo();
		}
		
		Logger.log("<-" + depth + " Fin <TipoMetodo>");
		depth--;	
	}

	
	private void argsFormales() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <ArgsFormales>");
		
		getToken();
		if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ( abriendo lista de argumentos formales en l�nea " + curr.getLinea());
		}
		
		listaArgsFormalesQ();
		
		getToken();
		if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ) cerrando lista de argumentos formales en l�nea " + curr.getLinea());
		}
		
		Logger.log("<-" + depth + " Fin <ArgsFormales>");
		depth--;		
	}

	
	private void listaArgsFormalesQ() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <ListaArgsFormales?>");
		
		getToken(); 
		reuseToken();
		if(curr.getTokenType() != TokenType.Identifier &&
				curr.getTokenType() != TokenType.BooleanKeyword &&
				curr.getTokenType() != TokenType.CharKeyword &&
				curr.getTokenType() != TokenType.IntKeyword &&
				curr.getTokenType() != TokenType.StringKeyword &&
				curr.getTokenType() != TokenType.ClosedParenthesisSymbol)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba boolean, int, char, String o identificador como tipo de argumento formal, o bien cierre de par�ntesis, en l�nea " + curr.getLinea());
		}
		else if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol)
			listaArgsFormales();
		
		Logger.log("<-" + depth + " Fin <ListaArgsFormales?>");
		depth--;	
	}


	private void listaArgsFormales() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <ListaArgsFormales>");
		
		argFormal();
		listaArgsFormalesFact();
		
		Logger.log("<-" + depth + " Fin <ListaArgsFormales>");
		depth--;			
	}

	
	private void listaArgsFormalesFact() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <ListaArgsFormalesFact>");
		
		getToken(); 
		if(curr.getTokenType() != TokenType.ComaSymbol &&
				curr.getTokenType() != TokenType.ClosedParenthesisSymbol)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba ',' (par�metro formal adicional) o cierre de par�ntesis en l�nea " + curr.getLinea());
		} 
		else if(curr.getTokenType() == TokenType.ClosedParenthesisSymbol)
		{
			reuseToken(); // lambda
		}
		else if(curr.getTokenType() == TokenType.ComaSymbol)
		{
			listaArgsFormales();
		}
		
		Logger.log("<-" + depth + " Fin <ListaArgsFormalesFact>");
		depth--;			
	}

	
	private void argFormal() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <ArgFormal>");

		getToken(); 
		reuseToken();
		if(curr.getTokenType() != TokenType.Identifier &&
				curr.getTokenType() != TokenType.BooleanKeyword &&
				curr.getTokenType() != TokenType.CharKeyword &&
				curr.getTokenType() != TokenType.IntKeyword &&
				curr.getTokenType() != TokenType.StringKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba boolean, int, char, String o identificador como tipo de argumento formal en l�nea " + curr.getLinea());
		}
		else tipo();
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de argumento formal), en l�nea " + curr.getLinea());
		}
		
		Logger.log("<-" + depth + " Fin <ArgFormal>");
		depth--;
	}


	private void varsLocales() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <VarsLocales>");

		getToken(); // var
		if(curr.getTokenType() != TokenType.VarKeyword &&
				curr.getTokenType() != TokenType.OpenKeySymbol) { // Follow(VarsLocales) 
			throw new UnexpectedTokenException("(!) Error, se esperaba var (para variables locales) o { para apertura de bloque en l�nea " + curr.getLinea());		
		} 
		else if(curr.getTokenType() == TokenType.VarKeyword) {
			tipo();
			listaDecVars();
			
			getToken(); // ;
			if(curr.getTokenType() != TokenType.SemicolonSymbol) { 
				throw new UnexpectedTokenException("(!) Error, se esperaba ; para cerrar declaraci�n de variable local en l�nea " + curr.getLinea());		
			}
			
			atributoStar();
		}
		else if(curr.getTokenType() == TokenType.OpenKeySymbol) {
			reuseToken(); // lambda
		}
		
		Logger.log("<-" + depth + " Fin <VarsLocales>");
		depth--;
	}


	private void bloque() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Bloque>");
		
		getToken(); 
		if(curr.getTokenType() != TokenType.OpenKeySymbol) { 
			throw new UnexpectedTokenException("(!) Error, se esperaba { para apertura de bloque en l�nea " + curr.getLinea());		
		}
		
		sentenciaStar();
		
		getToken(); 
		if(curr.getTokenType() != TokenType.ClosedKeySymbol) { 
			throw new UnexpectedTokenException("(!) Error, se esperaba } para cierre de bloque en l�nea " + curr.getLinea());		
		}
		
		Logger.log("<-" + depth + " Fin <Bloque>");	
	    depth--;
	}


	private void ctor() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Ctor>");
		
		getToken(); // identifier garantizado

		argsFormales();
		varsLocales();
		bloque();
		
		Logger.log("<-" + depth + " Fin <Ctor>");	
	    depth--;
	}


	private void atributoStar() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Atributo*>");
		
		getToken(); 
		reuseToken();
		if(curr.getTokenType() != TokenType.OpenKeySymbol) { // Follow(AtributoStar)
			atributo();
			atributoStar();
		}
		// else lambda
		
		Logger.log("<-" + depth + " Fin <Atributo*>");	
	    depth--;
	}

	
	private void atributo() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Atributo>");
		
		getToken(); // var
		if(curr.getTokenType() != TokenType.VarKeyword) {
			throw new UnexpectedTokenException("(!) Error, se esperaba var en l�nea " + curr.getLinea());		
		}

		getToken(); 
		reuseToken();
		if(curr.getTokenType() != TokenType.Identifier &&
				curr.getTokenType() != TokenType.BooleanKeyword &&
				curr.getTokenType() != TokenType.CharKeyword &&
				curr.getTokenType() != TokenType.IntKeyword &&
				curr.getTokenType() != TokenType.StringKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba boolean, int, char, String o identificador de tipo despu�s de var, en l�nea " + curr.getLinea());
		}
		else tipo();
		
		getToken();
		reuseToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de variable), en l�nea " + curr.getLinea());
		}
		else listaDecVars();
		
		getToken();
		if(curr.getTokenType() != TokenType.SemicolonSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ; en l�nea " + curr.getLinea());
		}
		
		Logger.log("<-" + depth + " Fin <Atributo>");	
	    depth--;
	}


	private void tipo() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Tipo>");

		getToken();
		if(curr.getTokenType() != TokenType.Identifier &&
				curr.getTokenType() != TokenType.BooleanKeyword &&
				curr.getTokenType() != TokenType.CharKeyword &&
				curr.getTokenType() != TokenType.IntKeyword &&
				curr.getTokenType() != TokenType.StringKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba boolean, int, char, String o identificador de tipo en l�nea " + curr.getLinea());
		}
		
		if(curr.getTokenType() != TokenType.Identifier) {
			reuseToken();
			tipoPrimitivo();
		}
		
		Logger.log("<-" + depth + " Fin <Tipo>");	
	    depth--;
	}


	private void tipoPrimitivo() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <TipoPrimitivo>");
		
		getToken();
		if(curr.getTokenType() != TokenType.BooleanKeyword &&
				curr.getTokenType() != TokenType.CharKeyword &&
				curr.getTokenType() != TokenType.IntKeyword &&
				curr.getTokenType() != TokenType.StringKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba tipo primitivo: boolean, int, char, String en l�nea " + curr.getLinea());
		}
		
		Logger.log("<-" + depth + " Fin <TipoPrimitivo>");	
	    depth--;
	}


	private void listaDecVars() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <ListaDecVars>");
		
		getToken(); // identifier
		if(curr.getTokenType() != TokenType.Identifier)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de variable) en l�nea " + curr.getLinea());
		}
		
		listaDecVarsFact();
		
		Logger.log("<-" + depth + " Fin <ListaDecVars>");	
	    depth--;	
	}

	private void listaDecVarsFact() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <ListaDecVarsFact>");
		
		getToken(); 
		if(curr.getTokenType() == TokenType.ComaSymbol)
			listaDecVars();
		else if(curr.getTokenType() != TokenType.SemicolonSymbol) { // follow(ListaDecVars)
			throw new UnexpectedTokenException("(!) Error, se esperaba , o ; en l�nea " + curr.getLinea());
		} 
		else reuseToken(); // lambda
		
		Logger.log("<-" + depth + " Fin <ListaDecVarsFact>");	
	    depth--;			
	}
	

	private void sentenciaStar() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Sentencia*>");
		
		getToken(); 
		reuseToken();
		if(curr.getTokenType() != TokenType.ClosedKeySymbol) { // Follow(Sentencia*)
			sentencia();
			sentenciaStar();
		}
		
		Logger.log("<-" + depth + " Fin <Sentencia*>");	
	    depth--;	
	}


	private void sentencia() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Sentencia>");
		
		getToken(); 

		if(curr.getTokenType() == TokenType.SemicolonSymbol) {

		}
		else if(curr.getTokenType() == TokenType.Identifier) {
			reuseToken();
			asignacion();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; despu�s de asignaci�n en l�nea " + curr.getLinea());			
			}
		}
		else if(curr.getTokenType() == TokenType.OpenParenthesisSymbol) {
			reuseToken();
			sentenciaSimple();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; despu�s de sentencia simple en l�nea " + curr.getLinea());			
			}
		}
		else if(curr.getTokenType() == TokenType.IfKeyword) {
			getToken();
			if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ( en condici�n de if en l�nea " + curr.getLinea());			
			}
			
			expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en condici�n de if en l�nea " + curr.getLinea());			
			}
			
			sentencia();
			sentenciaFact();
		}
		else if(curr.getTokenType() == TokenType.WhileKeyword) {
			getToken();
			if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ( en condici�n de while en l�nea " + curr.getLinea());			
			}
			
			expression();			
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en condici�n de while en l�nea " + curr.getLinea());			
			}
			
			sentencia();
		}
		else if(curr.getTokenType() == TokenType.ForKeyword) {
			getToken();
			if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ( en for en l�nea " + curr.getLinea());			
			}
			
			getToken();
			reuseToken();
			if(curr.getTokenType() != TokenType.Identifier) {
				throw new UnexpectedTokenException("(!) Error, se esperaba identificador para asignaci�n en for, en l�nea " + curr.getLinea());			
			} 
			else asignacion();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; entre asignaci�n y condici�n de corte en for, en l�nea " + curr.getLinea());			
			}
			
			expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; entre condici�n de corte y expresi�n de incremento en for, en l�nea " + curr.getLinea());			
			}
			
			expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en for en l�nea " + curr.getLinea());			
			}
			
			sentencia();
		}
		else if(curr.getTokenType() == TokenType.OpenKeySymbol) {
			sentenciaStar();
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedKeySymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba } (cierre de bloque) en l�nea " + curr.getLinea());			
			}
		}
		else if(curr.getTokenType() == TokenType.ReturnKeyword) {
			expressionQ();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; en return, en l�nea " + curr.getLinea());			
			}
		}
		
		Logger.log("<-" + depth + " Fin <Sentencia>");	
	    depth--;			
	}


	private void sentenciaFact() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <SentenciaFact>");
		
		getToken();
		if(curr.getTokenType() == TokenType.ElseKeyword) {
			sentencia();
		}
		else reuseToken(); // lambda (ac� hay margen para captar errores anticipadamente con follow(SentenciaFact)
		
		Logger.log("<-" + depth + " Fin <SentenciaFact>");	
	    depth--;			
	}

	
	private void asignacion() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Asignacion>");
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en asignaci�n, en l�nea " + curr.getLinea());					
		}
		
		getToken();
		if(curr.getTokenType() != TokenType.AssignOperator) {
			throw new UnexpectedTokenException("(!) Error, se esperaba = en asignaci�n, en l�nea " + curr.getLinea());					
		}
		
		expression();
		
		Logger.log("<-" + depth + " Fin <Asignacion>");	
	    depth--;
	}
	
	
	private void sentenciaSimple() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <SentenciaSimple>");
		
		getToken();
		if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ( para apertura de sentencia simple, en l�nea " + curr.getLinea());					
		}
		
		expression();
		
		getToken();
		if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ) para cierre de sentencia simple, en l�nea " + curr.getLinea());					
		}
		
		Logger.log("<-" + depth + " Fin <SentenciaSimple>");	
	    depth--;
	}

	
	private void expressionQ() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <Expression?>");			
				
		getToken();		
		
		if(!ASintHelper.isFollowExpressionQ(curr)){					
			reuseToken();
			expression();				
		}
		
		Logger.log("<-" + depth + " Fin <Expression?>");	
	    depth--;
	}
	
	private void expression() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <Expression?>");			
				
		expressionOr();
		expressionAux();		
		
		Logger.log("<-" + depth + " Fin <Expression?>");	
	    depth--;
	}
	
	private void expressionAux() throws UnexpectedTokenException{
		depth++	;
		Logger.log(depth + "-> Iniciando <ExpressionAux>");
		 
		getToken();			
		
		if(curr.getTokenType() == TokenType.OrOperator){
			expressionOr();
			expressionAux();
		}else if(!ASintHelper.isFollowExpressionAux(curr))	{	
				throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());
		}else reuseToken();
		
		Logger.log("<-" + depth + " Fin <ExpressionAux>");	
	    depth--;
	}
	
	private void expressionOr() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <ExpressionAux>");
			
		expressionAnd();
		expressionOrAux();	
		
		Logger.log("<-" + depth + " Fin <ExpressionAux>");	
	    depth--;
	}
	
	private void expressionOrAux()throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <ExpressionOrAux>");
		 
		getToken();
		
		if(curr.getTokenType() == TokenType.AndOperator){
			expressionAnd();
			expressionOrAux();
		}
		else if(!ASintHelper.isFollowExpressionOrAux(curr)){
			throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());
		}else reuseToken();
		
		Logger.log("<-" + depth + " Fin <ExpressionOrAux>");	
	    depth--;
	}
	
	private void expressionAnd()throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <ExpressionAnd>");
		 	
		expressionComp();
		expressionAndAux();		
		
		Logger.log("<-" + depth + " Fin <ExpressionAnd>");	
	    depth--;
	}	
	
	private void expressionAndAux() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <ExpressionAndAux>");
		 	
		getToken();
		if(curr.getTokenType() == TokenType.DistinctOperator || curr.getTokenType() == TokenType.EqualOperator ){
			expressionComp();
			expressionAndAux();
		}else if(!ASintHelper.isFollowExpressionAndAux(curr)){
			throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());
		}else reuseToken();
		
		Logger.log("<-" + depth + " Fin <ExpressionAndAux>");	
	    depth--;
	}
	
	private void expressionComp()throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <ExpressionComp>");
		
		expressionSR();
		expressionCompAux();
		
		Logger.log("<-" + depth + " Fin <ExpressionComp>");	
	    depth--;
	}
	
	private void expressionCompAux() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <ExpressionCompAux>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.GratherOrEqualOperator ||
		   curr.getTokenType() == TokenType.LessOrEqualOperator ||
		   curr.getTokenType() == TokenType.GratherOperator ||
		   curr.getTokenType() == TokenType.LessOperator){
			
			expressionSR();
			expressionCompAux();
		}else if(!ASintHelper.isFollowExpressionCompAux(curr)){
			throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());
		}else reuseToken();
		
		Logger.log("<-" + depth + " Fin <ExpressionCompAux>");	
	    depth--;
	}		
	
	private void expressionSR()throws UnexpectedTokenException{		
		depth++;
		Logger.log(depth + "-> Iniciando <ExpressionSR>");
	
		termino();
		expressionSRAux();
		
		Logger.log("<-" + depth + " Fin <ExpressionSR>");	
	    depth--;
	}
	
	private void expressionSRAux()throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <ExpressionSRAux>");
	
		getToken();
		if(curr.getTokenType() == TokenType.PlusOperator ||
		   curr.getTokenType() == TokenType.RestOperator){
			termino();
			expressionSRAux();
		}else if(!ASintHelper.isFollowExpressionSRAux(curr)){	
			throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());
		}else reuseToken();
		
		Logger.log("<-" + depth + " Fin <ExpressionSRAux>");	
	    depth--;
	}
	
	private void termino()throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <Termino>");
	
		factor();
		terminoAux();
		
		Logger.log("<-" + depth + " Fin <Termino>");	
	    depth--;
	}

	private void terminoAux()throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <TerminoAux>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.MultiplierOperator ||
		   curr.getTokenType() == TokenType.DivisionOperator ||
		   curr.getTokenType() == TokenType.ModOperator){
			
			factor();
			terminoAux();
		}else if(!ASintHelper.isFollowTermino(curr)){
			throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());
		}else reuseToken();
		
		Logger.log("<-" + depth + " Fin <TerminoAux>");	
	    depth--;
	}
	
	private void factor()throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <Factor>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.NotOperator ||
		   curr.getTokenType() == TokenType.PlusOperator ||
		   curr.getTokenType() == TokenType.RestOperator){
			factor();
			
		}else {
			reuseToken();
			primario();
		}
		
		Logger.log("<-" + depth + " Fin <Factor>");	
	    depth--;
	}
	
	private void primario() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <Primario>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.ThisKeyword || ASintHelper.isLiteral(curr)){
			
		}else if(curr.getTokenType() == TokenType.OpenParenthesisSymbol){
			
			expression();
			getToken();
			if(curr.getTokenType() == TokenType.ClosedParenthesisSymbol)
				llamadaStar();
			else throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());
			
		}else if(curr.getTokenType() == TokenType.Identifier){			
			primarioFact();			
		}else if(curr.getTokenType() == TokenType.NewKeyword){
			getToken();
			if(curr.getTokenType() == TokenType.Identifier){
				argsActuales();
				llamadaStar();
			}
		}else throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());		
		
		Logger.log("<-" + depth + " Fin <Primario>");	
	    depth--;
	}
	
	
	private void primarioFact() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <PrimarioFact>");
	
		getToken();
		reuseToken();
		
		if(curr.getTokenType() == TokenType.DotSymbol){				
			llamadaStar();	
		}else if(curr.getTokenType() == TokenType.OpenParenthesisSymbol){			
			argsActuales();
			llamadaStar();
		} 
		else throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());		
		
		Logger.log("<-" + depth + " Fin <PrimarioFact>");	
	    depth--;
	}
	
	private void llamadaStar() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <Llamada*>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.DotSymbol){
			reuseToken();
			llamada();
			llamadaStar();
		
		}else if(!ASintHelper.isFollowLlamadaStar(curr))
			throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());		
		
		Logger.log("<-" + depth + " Fin <Llamada*>");	
	    depth--;
	}

	private void llamada() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <Llamada>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.DotSymbol){
			getToken();
			if(curr.getTokenType() == TokenType.Identifier){
				argsActuales();
			}throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());
		
		}else throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());		
		
		Logger.log("<-" + depth + " Fin <Llamada>");	
	    depth--;
	}
	
	private void argsActuales() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <ArgsActuales>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.OpenParenthesisSymbol){
			listaExpsQ();
		
		}else throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());		
		
		Logger.log("<-" + depth + " Fin <ArgsActuales>");	
	    depth--;
	}
	
	private void listaExpsQ() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <listaExps?>");
	
		getToken();
		
		if(ASintHelper.isFirstListaExps(curr)){
			reuseToken();
			ListaExps();
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol)
				throw new UnexpectedTokenException("(!) Error, Se esperaba "+ curr.getLexema() +" en l�nea " + curr.getLinea());
				
		}else if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol)
			throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());		
		
		Logger.log("<-" + depth + " Fin <listaExps?>");	
	    depth--;
	}
	
	private void listaExps() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <listaExps>");
	
		getToken();
		
		if(ASintHelper.isFirstListaExps(curr)){
			reuseToken();
			expressionOr();
			expressionAux();
			listaExpsFact();
				
		}else throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());		
		
		Logger.log("<-" + depth + " Fin <listaExps>");	
	    depth--;
	}
	
	private void listaExpsFact() throws UnexpectedTokenException{
		depth++;
		Logger.log(depth + "-> Iniciando <listaExpsFact>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.ComaSymbol){			
			listaExps();
				
		}else if(!ASintHelper.isFollowListaExpsFact(curr)) 
			throw new UnexpectedTokenException("(!) Error, token invalido "+ curr.getLexema() +" en l�nea " + curr.getLinea());		
		
		Logger.log("<-" + depth + " Fin <listaExpsFact>");	
	    depth--;
	}
	
	public static void main(String args[])
	{		
		Application.Initialize(args);
		
		try {
			new ASint("C:\\prueba.txt");
		} catch (UnexpectedTokenException e) {
			Logger.log(e.getMessage());
			e.printStackTrace();
		}
	}
}
