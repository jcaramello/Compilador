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
	
	private void inicial() {	
		depth++;
		Logger.log(depth + "-> Iniciando <Inicial>");
		
		clase();
		inicialPlus();
		
		Logger.log("<-" + depth + " Fin <Inicial>");		
		depth--;
	}

	
	private void clase() {
		depth++;
		Logger.log(depth + "-> Iniciando <Clase>");
		
		getToken(); // class		
		if(curr.getTokenType() != TokenType.ClassKeyword) {
			Logger.log("(!) Error, se esperaba class en línea " + curr.getLinea());
			// throw new ASintException?
		}
		
		getToken(); // identificador
		if(curr.getTokenType() != TokenType.Identifier) {
			Logger.log("(!) Error, se esperaba identificador en línea " + curr.getLinea());
			// throw new ASintException?
		}
		
		herenciaQ();
		
		getToken(); // {
		if(curr.getTokenType() != TokenType.OpenKeySymbol) {
			Logger.log("(!) Error, se esperaba { en línea " + curr.getLinea());
			// throw new ASintException?
		}
		
		miembroStar();			
		
		getToken(); // }
		if(curr.getTokenType() != TokenType.ClosedKeySymbol) {
			Logger.log("(!) Error, se esperaba } en línea " + curr.getLinea());
			// throw new ASintException?
		}

		Logger.log("<-" + depth + " Fin <Clase>");	
		depth--;
	}


	private void inicialPlus() {
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
	
	
	private void herenciaQ() {
		depth++;
		Logger.log(depth + "-> Iniciando <Herencia?>");
		
		getToken(); // extends
		if(curr.getTokenType() == TokenType.ExtendsKeyword) {
			getToken(); // identificador
			if(curr.getTokenType() != TokenType.Identifier) {
				Logger.log("(!) Error, se esperaba identificador en línea " + curr.getLinea());
				// throw new ASintException?
			}
		}
		else {
			if(curr.getTokenType() != TokenType.OpenKeySymbol) { // i.e. follow(herenciaQ)
				Logger.log("(!) Error, se esperaba extends o { en línea " + curr.getLinea());
				// throw new ASintException?
			}
					
			reuseToken();
		}		
		
		Logger.log("<-" + depth + " Fin <Herencia?>");	
		depth--;
	}



	private void miembroStar() {
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
			Logger.log("(!) Error, se esperaba atributo, constructor, método o } en línea " + curr.getLinea());
			// throw new ASintException?
		}
		
		Logger.log("<-" + depth + " Fin <Miembro*>");	
		depth--;
	}

	
	
	private void miembro() {
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
			modMetodo();
		} else {
			Logger.log("(!) Error, se esperaba var, identificador o modificador de método (static o dynamic) en línea " + curr.getLinea());
			// throw new ASintException?
		}
		
		Logger.log("<-" + depth + " Fin <Miembro>");	
	    depth--;
	}


	private void modMetodo() {
		// TODO Auto-generated method stub
		
	}


	private void ctor() {
		// TODO Auto-generated method stub
		
	}


	private void atributo() {
		depth++;
		Logger.log(depth + "-> Iniciando <Atributo>");
		
		getToken(); // identificador
		if(curr.getTokenType() != TokenType.VarKeyword) {
			Logger.log("(!) Error, se esperaba var en línea " + curr.getLinea());
			// throw new ASintException?
		}

		
		// (sigue...)
		
		Logger.log("<-" + depth + " Fin <Atributo>");	
	    depth--;
	}


	public static void main(String args[])
	{		
		Application.Initialize(args);
		
		new ASint("C:\\prueba.txt");
	}
}
