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
		
		try {
			curr = alex.getToken();
		} catch (ALexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (curr==null)
			curr = new Token(TokenType.NotSet, "", alex.getLineN()); // Pseudotoken como alternativa a excepcion.
			//throw new UnexpectedEOFException();
		
		Logger.log("Encontrado token "+ curr.getTokenType() + ": " + curr.getLexema() +" en linea "+ curr.getLinea());
	}
	
	
	// Métodos correspondientes a los no-terminales de la gramática.
	
	private boolean inicial() {	
		depth++;
		Logger.log(depth + "-> Iniciando <Inicial>");
		
		clase();
		inicialPlus();
		
		Logger.log("<-" + depth + " Fin <Inicial>");	
		Logger.log("*** ANÁLISIS SINTÁCTICO FINALIZADO EXITOSAMENTE ***");
		
		return true;
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
		// TODO Auto-generated method stub
		
	}
	
	
	private void herenciaQ() {
		// TODO Auto-generated method stub
		
	}


	private void miembroStar() {
		// TODO Auto-generated method stub
		
	}

	
	
	public static void main(String args[])
	{		
		Application.Initialize(args);
		
		new ASint("C:\\prueba.txt");
	}
}
