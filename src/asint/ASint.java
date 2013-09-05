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
	
	
	public ASint(String archivo)
	{
		depth = 0;
		
		try {
			alex = new ALex(archivo);
			
			inicial();			
			
		} // Atrapo las excepciones previamente definidas en el analizador lexico
		  catch (ALexException e) {
			Logger.log("\n"+e.getMessage());
		} // De aqui en adelante, excepciones correspondientes al analizador sintactico
		  catch (Exception e) {
			//  e.printStackTrace();
			System.out.println("\n"+e.getMessage());
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
	
	
	// Métodos correspondientes a los no-terminales de la gramática.
	
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
			throw new UnexpectedTokenException("(!) Error, se esperaba class en línea " + curr.getLinea());
			
		}
		
		getToken(); // identificador
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en línea " + curr.getLinea());
		}
		
		herenciaQ();
		
		getToken(); // {
		if(curr.getTokenType() != TokenType.OpenKeySymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba { en línea " + curr.getLinea());
		}
		
		miembroStar();			
		
		getToken(); // }
		if(curr.getTokenType() != TokenType.ClosedKeySymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba } en línea " + curr.getLinea());
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
				throw new UnexpectedTokenException("(!) Error, se esperaba extends o { en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba atributo, constructor, método o } en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba var, identificador o modificador de método (static o dynamic) en línea " + curr.getLinea());
		}
		
		Logger.log("<-" + depth + " Fin <Miembro>");	
	    depth--;
	}


	private void metodo() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Metodo>");
		
		getToken();
		reuseToken();
		if(curr.getTokenType() != TokenType.StaticKeyword &&
				curr.getTokenType() != TokenType.DynamicKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba modificador de método (static o dynamic) en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba tipo de método: void, boolean, int, char, String o identificador de tipo en línea " + curr.getLinea());
		}
		else tipoMetodo();
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de método), en línea " + curr.getLinea());
		}
		
		argsFormales(); // entiendo que en estos no obtengo ningún beneficio informando más temprano del error.
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
			throw new UnexpectedTokenException("(!) Error, se esperaba ( abriendo lista de argumentos formales en línea " + curr.getLinea());
		}
		
		listaArgsFormalesQ();
		
		getToken();
		if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ) cerrando lista de argumentos formales en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba boolean, int, char, String o identificador como tipo de argumento formal, o bien cierre de paréntesis, en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba ',' (parámetro formal adicional) o cierre de paréntesis en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba boolean, int, char, String o identificador como tipo de argumento formal en línea " + curr.getLinea());
		}
		else tipo();
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de argumento formal), en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba var (para variables locales) o { para apertura de bloque en línea " + curr.getLinea());		
		} 
		else if(curr.getTokenType() == TokenType.VarKeyword) {
			tipo();
			listaDecVars();
			
			getToken(); // ;
			if(curr.getTokenType() != TokenType.SemicolonSymbol) { 
				throw new UnexpectedTokenException("(!) Error, se esperaba ; para cerrar declaración de variable local en línea " + curr.getLinea());		
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
			throw new UnexpectedTokenException("(!) Error, se esperaba { para apertura de bloque en línea " + curr.getLinea());		
		}
		
		sentenciaStar();
		
		getToken(); 
		if(curr.getTokenType() != TokenType.ClosedKeySymbol) { 
			throw new UnexpectedTokenException("(!) Error, se esperaba } para cierre de bloque en línea " + curr.getLinea());		
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
			throw new UnexpectedTokenException("(!) Error, se esperaba var en línea " + curr.getLinea());		
		}

		getToken(); 
		reuseToken();
		if(curr.getTokenType() != TokenType.Identifier &&
				curr.getTokenType() != TokenType.BooleanKeyword &&
				curr.getTokenType() != TokenType.CharKeyword &&
				curr.getTokenType() != TokenType.IntKeyword &&
				curr.getTokenType() != TokenType.StringKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba boolean, int, char, String o identificador de tipo después de var, en línea " + curr.getLinea());
		}
		else tipo();
		
		getToken();
		reuseToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de variable), en línea " + curr.getLinea());
		}
		else listaDecVars();
		
		getToken();
		if(curr.getTokenType() != TokenType.SemicolonSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ; en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba boolean, int, char, String o identificador de tipo en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba tipo primitivo: boolean, int, char, String en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de variable) en línea " + curr.getLinea());
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
			throw new UnexpectedTokenException("(!) Error, se esperaba , o ; en línea " + curr.getLinea());
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
				throw new UnexpectedTokenException("(!) Error, se esperaba ; después de asignación en línea " + curr.getLinea());			
			}
		}
		else if(curr.getTokenType() == TokenType.OpenParenthesisSymbol) {
			reuseToken();
			sentenciaSimple();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; después de sentencia simple en línea " + curr.getLinea());			
			}
		}
		else if(curr.getTokenType() == TokenType.IfKeyword) {
			getToken();
			if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ( en condición de if en línea " + curr.getLinea());			
			}
			
			expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en condición de if en línea " + curr.getLinea());			
			}
			
			sentencia();
			sentenciaFact();
		}
		else if(curr.getTokenType() == TokenType.WhileKeyword) {
			getToken();
			if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ( en condición de while en línea " + curr.getLinea());			
			}
			
			expression();			
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en condición de while en línea " + curr.getLinea());			
			}
			
			sentencia();
		}
		else if(curr.getTokenType() == TokenType.ForKeyword) {
			getToken();
			if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ( en for en línea " + curr.getLinea());			
			}
			
			getToken();
			reuseToken();
			if(curr.getTokenType() != TokenType.Identifier) {
				throw new UnexpectedTokenException("(!) Error, se esperaba identificador para asignación en for, en línea " + curr.getLinea());			
			} 
			else asignacion();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; entre asignación y condición de corte en for, en línea " + curr.getLinea());			
			}
			
			expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; entre condición de corte y expresión de incremento en for, en línea " + curr.getLinea());			
			}
			
			expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en for en línea " + curr.getLinea());			
			}
			
			sentencia();
		}
		else if(curr.getTokenType() == TokenType.OpenKeySymbol) {
			sentenciaStar();
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedKeySymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba } (cierre de bloque) en línea " + curr.getLinea());			
			}
		}
		else if(curr.getTokenType() == TokenType.ReturnKeyword) {
			expressionQ();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; en return, en línea " + curr.getLinea());			
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
		else reuseToken(); // lambda (acá hay margen para captar errores anticipadamente con follow(SentenciaFact)
		
		Logger.log("<-" + depth + " Fin <SentenciaFact>");	
	    depth--;			
	}

	
	private void asignacion() throws UnexpectedTokenException {
		depth++;
		Logger.log(depth + "-> Iniciando <Asignacion>");
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en asignación, en línea " + curr.getLinea());					
		}
		
		getToken();
		if(curr.getTokenType() != TokenType.EqualOperator) {
			throw new UnexpectedTokenException("(!) Error, se esperaba = en asignación, en línea " + curr.getLinea());					
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
			throw new UnexpectedTokenException("(!) Error, se esperaba ( para apertura de sentencia simple, en línea " + curr.getLinea());					
		}
		
		expression();
		
		getToken();
		if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ) para cierre de sentencia simple, en línea " + curr.getLinea());					
		}
		
		Logger.log("<-" + depth + " Fin <SentenciaSimple>");	
	    depth--;
	}

	
	private void expressionQ() {
		// TODO Auto-generated method stub
		
	}

	private void expression() {
		// TODO Auto-generated method stub
		
	}



	public static void main(String args[])
	{		
		Application.Initialize(args);
		
		new ASint("C:\\prueba.txt");
	}
}
