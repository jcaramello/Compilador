package asint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import common.Logger;
import enums.ModifierMethodType;
import enums.TokenType;

import alex.ALex;
import alex.Token;
import alex.exceptions.ALexException;
import asema.TS;
import asema.entities.*;
import asema.exceptions.SemanticErrorException;

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
	
	
	public ASint(String archivo) throws UnexpectedTokenException, SemanticErrorException
	{
		depth = 0;
		
		try {
			Logger.verbose("\n\n*** Iniciando " + archivo + " ***");
			
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
				
				Logger.verbose("Encontrado token "+ curr.getTokenType() + ": " + curr.getLexema() +" en linea "+ curr.getLinea());
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
	
	private void inicial() throws UnexpectedTokenException, SemanticErrorException {	
		depth++;
		Logger.verbose(depth + "-> Iniciando <Inicial>");
		
		clase();		
		inicialPlus();
		
		Logger.verbose("<-" + depth + " Fin <Inicial>");		
		depth--;
	}

	
	private void clase() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Clase>");
		
		getToken(); // class		
		if(curr.getTokenType() != TokenType.ClassKeyword) {
			throw new UnexpectedTokenException("(!) Error, se esperaba class en línea " + curr.getLinea());
			
		}
		
		getToken(); // identificador
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en línea " + curr.getLinea());
		}			
		
		TS.addClass(curr.getLexema());
		TS.setCurrentClass(curr.getLexema());
		
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

		Logger.verbose("<-" + depth + " Fin <Clase>");	
		depth--;
	}


	private void inicialPlus() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Inicial+>");
		
		getToken();
		if(curr.getTokenType() == TokenType.NotSet && curr.getLexema().equals("EOF")) {
			Logger.verbose("(!) EndOfFile");	
		}	
		else {
			reuseToken();
			inicial();
		}
		
		Logger.verbose("<-" + depth + " Fin <Inicial+>");	
		depth--;
	}
	
	
	private void herenciaQ() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Herencia?>");
		
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
		
		Logger.verbose("<-" + depth + " Fin <Herencia?>");	
		depth--;
	}



	private void herencia() throws UnexpectedTokenException {
		
		getToken(); // extends, garantizado

		getToken(); // identificador
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en línea " + curr.getLinea());
		}
		
		// Agrege un nuevo atributo inheritsFrom por que el Father class lo implementamos comod e tipo
		// EntryClass y es para mantener la ref a la clase padre. la cual puede que todavia no haya procesado.
		TS.getCurrentClass().inheritsFrom = curr.getLexema();
	}


	private void miembroStar() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Miembro*>");
		
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
			throw new UnexpectedTokenException("(!) Error, definicion del miembro de clase no valido. se esperaba: var, identificador o modificador de método (static o dynamic) y no "+curr.getLexema()+ ", en línea " + curr.getLinea());
		}
		
		Logger.verbose("<-" + depth + " Fin <Miembro*>");	
		depth--;
	}

	
	
	private void miembro() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Miembro>");
	
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
		
		Logger.verbose("<-" + depth + " Fin <Miembro>");	
	    depth--;
	}


	private void metodo() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Metodo>");
		Type returnType = null;
		ModifierMethodType modifierType = null;
		
		getToken();
		reuseToken();
		if(curr.getTokenType() != TokenType.StaticKeyword && curr.getTokenType() != TokenType.DynamicKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba modificador de método (static o dynamic) en línea " + curr.getLinea());
		} 
		else{			
			modifierType = modMetodo();						
		}
		
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
		else{			
			returnType = tipoMetodo();								
		}
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de método), en línea " + curr.getLinea());
		}
						
		EntryMethod entryMethod = TS.getCurrentClass().addMethod(curr.getLexema(), returnType, modifierType);
		TS.getCurrentClass().setCurrentMethod(entryMethod);
				
		List<EntryVar> args = argsFormales(); 				
		TS.getCurrentClass().getCurrentMethod().addFormalArgs(args);		
		
		List<EntryVar> vars = varsLocales();				
		TS.getCurrentClass().getCurrentMethod().addLocalVars(vars);
		
		BlockNode node = bloque();		
		TS.getCurrentClass().getCurrentMethod().addAST(node);
		
		Logger.verbose("<-" + depth + " Fin <Metodo>");
		depth--;		
	}
	

	private ModifierMethodType modMetodo() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <ModMetodo>");
		
		ModifierMethodType modifier;
		
		getToken(); // static o dynamic garantizado.

		if(curr.getTokenType() == TokenType.StaticKeyword)
			modifier = ModifierMethodType.Static;
		else modifier = ModifierMethodType.Dynamic;
		
		Logger.verbose("<-" + depth + " Fin <ModMetodo>");
		depth--;		
		
		return modifier;
	}


	private Type tipoMetodo() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <TipoMetodo>");
		Type type = new VoidType();
		
		getToken(); // garantizado void o TipoPrimitivo
		if(curr.getTokenType() != TokenType.VoidKeyword) {
			reuseToken();
			type = tipo();
		}
		
		Logger.verbose("<-" + depth + " Fin <TipoMetodo>");
		depth--;	
		return type;
	}

	
	private List<EntryVar> argsFormales() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <ArgsFormales>");
		
		List<EntryVar> argsFormales_Args = null;
		
		getToken();
		if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ( abriendo lista de argumentos formales en línea " + curr.getLinea());
		}
				
		argsFormales_Args = listaArgsFormalesQ();						
		getToken();
		if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ) cerrando lista de argumentos formales en línea " + curr.getLinea());
		}
		
		Logger.verbose("<-" + depth + " Fin <ArgsFormales>");
		depth--;
		
		return argsFormales_Args;
	}

	
	private List<EntryVar> listaArgsFormalesQ() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <ListaArgsFormales?>");
		
		List<EntryVar> listaArgsFormalesQ_Args = new ArrayList<EntryVar>();
		
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
		else if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol){						
			listaArgsFormalesQ_Args = listaArgsFormales();					
		}
		
		Logger.verbose("<-" + depth + " Fin <ListaArgsFormales?>");
		depth--;
		return listaArgsFormalesQ_Args;
	}


	private List<EntryVar> listaArgsFormales() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <ListaArgsFormales>");		
		
		EntryVar arg = argFormal();						
		List<EntryVar> listaArgsFormalesFact = listaArgsFormalesFact();
		
		
		for (EntryVar entryVar : listaArgsFormalesFact) {
			if(entryVar.Name.equals(arg.Name))
				throw new SemanticErrorException(String.format("El nombre del argumento esta repetido. Linea %s", Integer.toString(curr.getLinea())));
		}
		
		//Inserto al principio para que al final de la recursion la lista quede ordenada
		listaArgsFormalesFact.add(0, arg);		
		
		Logger.verbose("<-" + depth + " Fin <ListaArgsFormales>");
		depth--;	
		return listaArgsFormalesFact;
	}

	
	private List<EntryVar> listaArgsFormalesFact() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <ListaArgsFormalesFact>");
		List<EntryVar> listaArgsFormalesFact = new ArrayList<EntryVar>();
		
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
			listaArgsFormalesFact = listaArgsFormales();					
		}
		
		Logger.verbose("<-" + depth + " Fin <ListaArgsFormalesFact>");
		depth--;
		
		return listaArgsFormalesFact;
	}

	
	private EntryVar argFormal() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <ArgFormal>");
		Type type;
		
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
		else type = tipo();
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de argumento formal), en línea " + curr.getLinea());
		}			
		
		Logger.verbose("<-" + depth + " Fin <ArgFormal>");
		depth--;
		
		return new EntryVar(type, curr.getLexema());
	}


	private List<EntryVar> varsLocales() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <VarsLocales>");
		List<EntryVar> variables = new ArrayList<EntryVar>();;
		
		getToken(); // var
		if(curr.getTokenType() != TokenType.VarKeyword &&
				curr.getTokenType() != TokenType.OpenKeySymbol) { // Follow(VarsLocales) 
			throw new UnexpectedTokenException("(!) Error, se esperaba var (para variables locales) o { para apertura de bloque en línea " + curr.getLinea());		
		} 
		else if(curr.getTokenType() == TokenType.VarKeyword) {
			Type expectedType = tipo();
			variables = listaDecVars(expectedType);
			
			getToken(); // ;
			if(curr.getTokenType() != TokenType.SemicolonSymbol) { 
				throw new UnexpectedTokenException("(!) Error, se esperaba ; para cerrar declaración de variable local en línea " + curr.getLinea());		
			}
			
			atributoStar();
		}
		else if(curr.getTokenType() == TokenType.OpenKeySymbol) {
			reuseToken(); // lambda				
		}
		
		Logger.verbose("<-" + depth + " Fin <VarsLocales>");
		depth--;
		
		return variables;
	}


	private BlockNode bloque() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Bloque>");
		BlockNode node;
		
		getToken(); 
		if(curr.getTokenType() != TokenType.OpenKeySymbol) { 
			throw new UnexpectedTokenException("(!) Error, se esperaba { para apertura de bloque en línea " + curr.getLinea());		
		}
		
		List<SentenceNode> sentences  = sentenciaStar();
		node = new BlockNode(sentences);
		
		getToken(); 
		if(curr.getTokenType() != TokenType.ClosedKeySymbol) { 
			throw new UnexpectedTokenException("(!) Error, se esperaba } para cierre de bloque en línea " + curr.getLinea());		
		}
		
		Logger.verbose("<-" + depth + " Fin <Bloque>");	
	    depth--;
	    
	    return node;
	}


	private void ctor() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Ctor>");
		
		getToken(); // identifier garantizado
			
		if(!curr.getLexema().equals(TS.getCurrentClass().Name))
			throw new SemanticErrorException(String.format("El nombre del constructor debe coincidir con el nombre de la clase. Linea %s.", Integer.toString(curr.getLinea())));
		
		EntryMethod ctor = 
				new EntryMethod(curr.getLexema(), ModifierMethodType.Dynamic, new ClassType(TS.getCurrentClass()), TS.getCurrentClass());
		
		List<EntryVar> args = argsFormales();					
		ctor.addFormalArgs(args);
		
		List<EntryVar> vars = varsLocales();
		ctor.addLocalVars(vars);
		
		BlockNode node = bloque();
		ctor.addAST(node);
		
		TS.getCurrentClass().addConstructor(ctor);
		
		Logger.verbose("<-" + depth + " Fin <Ctor>");	
	    depth--;
	}


	private void atributoStar() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Atributo*>");
		
		getToken(); 
		reuseToken();
		if(curr.getTokenType() != TokenType.OpenKeySymbol) { // Follow(AtributoStar)
			atributo();
			atributoStar();
		}
		// else lambda
		
		Logger.verbose("<-" + depth + " Fin <Atributo*>");	
	    depth--;
	}

	
	private void atributo() throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Atributo>");
		
		Type type = null;
		List<EntryVar> vars = null;
		
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
		else {			
			type = tipo();								
		}
		
		getToken();
		reuseToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de variable), en línea " + curr.getLinea());
		}
		else{ 			
			vars = listaDecVars(type);	
			
			for (EntryVar var : vars) {
				TS.getCurrentClass().addAttribute(var);
			}
		}
		getToken();
		if(curr.getTokenType() != TokenType.SemicolonSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ; en línea " + curr.getLinea());
		}
		
		Logger.verbose("<-" + depth + " Fin <Atributo>");	
	    depth--;
	}


	private Type tipo() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Tipo>");

		Type type;
		
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
			type = tipoPrimitivo();
		}
		else{
			type = new ClassType(curr);
		}
		Logger.verbose("<-" + depth + " Fin <Tipo>");	
	    depth--;
	    
	    return type;
	}


	private Type tipoPrimitivo() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <TipoPrimitivo>");
		
		getToken();
		if(curr.getTokenType() != TokenType.BooleanKeyword &&
				curr.getTokenType() != TokenType.CharKeyword &&
				curr.getTokenType() != TokenType.IntKeyword &&
				curr.getTokenType() != TokenType.StringKeyword)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba tipo primitivo: boolean, int, char, String en línea " + curr.getLinea());
		}
		
		Type type = new PrimitiveType(curr.getLexema());
		
		Logger.verbose("<-" + depth + " Fin <TipoPrimitivo>");	
	    depth--;
	    
	    return type;
	}


	private List<EntryVar> listaDecVars(Type expectedType) throws UnexpectedTokenException, SemanticErrorException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <ListaDecVars>");
				
		getToken(); // identifier
		if(curr.getTokenType() != TokenType.Identifier)
		{
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador (nombre de variable) en línea " + curr.getLinea());
		}
		
		List<EntryVar> variables = listaDecVarsFact(expectedType);
		
		for (EntryVar entryVar : variables) {
			if(entryVar.Name.equals(curr.getLexema()))
				throw new SemanticErrorException(String.format("Error(!). La variable %s esta repetida. Linea %s", curr.getLexema(), Integer.toString(curr.getLinea())));
		}
		
		variables.add(0, new EntryVar(expectedType, curr.getLexema()));
		
		Logger.verbose("<-" + depth + " Fin <ListaDecVars>");	
	    depth--;	
	    
	    return variables;
	}

	private List<EntryVar> listaDecVarsFact(Type expectedType) throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <ListaDecVarsFact>");
		
		List<EntryVar> variables = new ArrayList<EntryVar>();
		
		getToken(); 
		if(curr.getTokenType() == TokenType.ComaSymbol)
			variables = listaDecVars(expectedType);
		else if(curr.getTokenType() != TokenType.SemicolonSymbol) { // follow(ListaDecVars)
			throw new UnexpectedTokenException("(!) Error, se esperaba , o ; en línea " + curr.getLinea());
		} 
		else reuseToken(); // lambda
		
		Logger.verbose("<-" + depth + " Fin <ListaDecVarsFact>");	
	    depth--;	
	    
	    return variables;
	}
	

	private List<SentenceNode> sentenciaStar() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Sentencia*>");
		List<SentenceNode> sentences = new ArrayList<SentenceNode>();
		
		getToken(); 
		reuseToken();
		if(curr.getTokenType() != TokenType.ClosedKeySymbol) { // Follow(Sentencia*)
			SentenceNode sentence = sentencia();
			sentences = sentenciaStar();
			sentences.add(sentence); // no estoy seguro si es un add o un add(0)
		}
		
		Logger.verbose("<-" + depth + " Fin <Sentencia*>");	
	    depth--;
	    
	    return sentences;
	}


	private SentenceNode sentencia() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Sentencia>");
		SentenceNode sentence;
		
		getToken(); 
		
		if(curr.getTokenType() == TokenType.SemicolonSymbol) {
			sentence = new EmptySentenceNode();
		}		
		else if(curr.getTokenType() == TokenType.Identifier) {
			reuseToken();
			sentence = asignacion();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; después de asignación en línea " + curr.getLinea());			
			}
		} 
		else if(curr.getTokenType() == TokenType.OpenParenthesisSymbol) {
			reuseToken();
			sentence = sentenciaSimple();
			
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
			
			ExpressionNode cond = expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en condición de if en línea " + curr.getLinea());			
			}
			
			SentenceNode thenSentence = sentencia();
			SentenceNode elseSentence = sentenciaFact();
			
			sentence = new IfNode(cond, thenSentence, elseSentence);
		}
		else if(curr.getTokenType() == TokenType.WhileKeyword) {
			getToken();
			if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ( en condición de while en línea " + curr.getLinea());			
			}
			
			ExpressionNode loopCond = expression();			
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en condición de while en línea " + curr.getLinea());			
			}
			
			SentenceNode body = sentencia();
			
			sentence = new WhileNode(loopCond, body);
		}
		else if(curr.getTokenType() == TokenType.ForKeyword) {
			getToken();
			if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ( en for en línea " + curr.getLinea());			
			}
			
			AssignmentNode initAssing;			
			
			getToken();
			reuseToken();
			if(curr.getTokenType() != TokenType.Identifier) {
				throw new UnexpectedTokenException("(!) Error, se esperaba identificador para asignación en for, en línea " + curr.getLinea());			
			} 
			else initAssing = asignacion();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; entre asignación y condición de corte en for, en línea " + curr.getLinea());			
			}
			
			ExpressionNode loopCond = expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; entre condición de corte y expresión de incremento en for, en línea " + curr.getLinea());			
			}
			
			ExpressionNode incrementExp = expression();
			
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ) en for en línea " + curr.getLinea());			
			}
			
			SentenceNode body = sentencia();
			
			sentence = new ForNode(initAssing, loopCond, incrementExp, body);
		}
		else if(curr.getTokenType() == TokenType.OpenKeySymbol) {
			List<SentenceNode> sentences = sentenciaStar();
			sentence = new BlockNode(sentences);
			getToken();
			if(curr.getTokenType() != TokenType.ClosedKeySymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba } (cierre de bloque) en línea " + curr.getLinea());			
			}
		}
		else if(curr.getTokenType() == TokenType.ReturnKeyword) {
			
			ExpressionNode returnExp = expressionQ();
			if(returnExp == null)
				throw new SemanticErrorException(String.format("Error(!). La expression de retorno no puede ser vacia. Linea %s", Integer.toString(curr.getLinea())));
			sentence = new ReturnNode(returnExp);
			
			getToken();
			if(curr.getTokenType() != TokenType.SemicolonSymbol) {
				throw new UnexpectedTokenException("(!) Error, se esperaba ; en return, en línea " + curr.getLinea());			
			}
		}else throw new UnexpectedTokenException("(!) Error, el token " + curr.getLexema()+" no se corresponde con el comienzo de una sentencia valida, en línea " + curr.getLinea());
		
		Logger.verbose("<-" + depth + " Fin <Sentencia>");	
	    depth--;
	    
	    return sentence;
	}


	private SentenceNode sentenciaFact() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <SentenciaFact>");
		SentenceNode sentence = null;
		
		getToken();
		if(curr.getTokenType() == TokenType.ElseKeyword) {
			sentence = sentencia();
		}
		else reuseToken(); // lambda (acá hay margen para captar errores anticipadamente con follow(SentenciaFact)
		
		Logger.verbose("<-" + depth + " Fin <SentenciaFact>");	
	    depth--;	
	    
	    return sentence;
	}

	
	private AssignmentNode asignacion() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <Asignacion>");		
		
		getToken();
		if(curr.getTokenType() != TokenType.Identifier) {
			throw new UnexpectedTokenException("(!) Error, se esperaba identificador en asignación, en línea " + curr.getLinea());					
		}
		
		getToken();
		if(curr.getTokenType() != TokenType.AssignOperator) {
			throw new UnexpectedTokenException("(!) Error, se esperaba = en asignación, en línea " + curr.getLinea());					
		}
		
		EntryVar leftRigth = TS.findVar(curr.getLexema());
		ExpressionNode rigthSide = expression();
				
		AssignmentNode assignmentNode = new AssignmentNode(leftRigth, rigthSide);
		Logger.verbose("<-" + depth + " Fin <Asignacion>");	
	    depth--;
	}
	
	
	private SimpleSentenceNode sentenciaSimple() throws UnexpectedTokenException {
		depth++;
		Logger.verbose(depth + "-> Iniciando <SentenciaSimple>");
		
		getToken();
		if(curr.getTokenType() != TokenType.OpenParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ( para apertura de sentencia simple, en línea " + curr.getLinea());					
		}
		
		ExpressionNode exp = expression();
		
		getToken();
		if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol) {
			throw new UnexpectedTokenException("(!) Error, se esperaba ) para cierre de sentencia simple, en línea " + curr.getLinea());					
		}
		
		Logger.verbose("<-" + depth + " Fin <SentenciaSimple>");	
	    depth--;
	    
	    return new SimpleSentenceNode(exp);
	}

	
	private ExpressionNode expressionQ() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <Expression?>");			
		
		ExpressionNode exp = null;
		
		getToken();		
		reuseToken();
		
		if(!ASintHelper.isFollowExpressionQ(curr)){										
			exp = expression();				
		}
		
		Logger.verbose("<-" + depth + " Fin <Expression?>");	
	    depth--;
	    
	    return exp;
	}
	
	private ExpressionNode expression() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <Expression?>");			
				
		expressionOr();
		expressionAux();		
		
		Logger.verbose("<-" + depth + " Fin <Expression?>");	
	    depth--;
	}
	
	private void expressionAux() throws UnexpectedTokenException{
		depth++	;
		Logger.verbose(depth + "-> Iniciando <ExpressionAux>");
		 
		getToken();			
		
		if(curr.getTokenType() == TokenType.OrOperator){
			expressionOr();
			expressionAux();
		}else if(!ASintHelper.isFollowExpressionAux(curr))	{	
				throw new UnexpectedTokenException("(!) Error, Expression mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());
		}else reuseToken();
		
		Logger.verbose("<-" + depth + " Fin <ExpressionAux>");	
	    depth--;
	}
	
	private void expressionOr() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <ExpressionAux>");
			
		expressionAnd();
		expressionOrAux();	
		
		Logger.verbose("<-" + depth + " Fin <ExpressionAux>");	
	    depth--;
	}
	
	private void expressionOrAux()throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <ExpressionOrAux>");
		 
		getToken();
		
		if(curr.getTokenType() == TokenType.AndOperator){
			expressionAnd();
			expressionOrAux();
		}
		else if(!ASintHelper.isFollowExpressionOrAux(curr)){
			throw new UnexpectedTokenException("(!) Error, Expression mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());
		}else reuseToken();
		
		Logger.verbose("<-" + depth + " Fin <ExpressionOrAux>");	
	    depth--;
	}
	
	private void expressionAnd()throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <ExpressionAnd>");
		 	
		expressionComp();
		expressionAndAux();		
		
		Logger.verbose("<-" + depth + " Fin <ExpressionAnd>");	
	    depth--;
	}	
	
	private void expressionAndAux() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <ExpressionAndAux>");
		 	
		getToken();
		if(curr.getTokenType() == TokenType.DistinctOperator || curr.getTokenType() == TokenType.EqualOperator ){
			expressionComp();
			expressionAndAux();
		}else if(!ASintHelper.isFollowExpressionAndAux(curr)){
			throw new UnexpectedTokenException("(!) Error, Expression mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());
		}else reuseToken();
		
		Logger.verbose("<-" + depth + " Fin <ExpressionAndAux>");	
	    depth--;
	}
	
	private void expressionComp()throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <ExpressionComp>");
		
		expressionSR();
		expressionCompAux();
		
		Logger.verbose("<-" + depth + " Fin <ExpressionComp>");	
	    depth--;
	}
	
	private void expressionCompAux() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <ExpressionCompAux>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.GratherOrEqualOperator ||
		   curr.getTokenType() == TokenType.LessOrEqualOperator ||
		   curr.getTokenType() == TokenType.GratherOperator ||
		   curr.getTokenType() == TokenType.LessOperator){
			
			expressionSR();
			//expressionCompAux(); // Esta linea fue comentada para corregir el defecto de  a = a > b < d; ya que si esta, toma como valida esa expresion y no deberia hacerlo.
			
		}else if(!ASintHelper.isFollowExpressionCompAux(curr)){
			throw new UnexpectedTokenException("(!) Error, Expression mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());
		}else reuseToken();
		
		Logger.verbose("<-" + depth + " Fin <ExpressionCompAux>");	
	    depth--;
	}		
	
	private void expressionSR()throws UnexpectedTokenException{		
		depth++;
		Logger.verbose(depth + "-> Iniciando <ExpressionSR>");
	
		termino();
		expressionSRAux();
		
		Logger.verbose("<-" + depth + " Fin <ExpressionSR>");	
	    depth--;
	}
	
	private void expressionSRAux()throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <ExpressionSRAux>");
	
		getToken();
		if(curr.getTokenType() == TokenType.PlusOperator ||
		   curr.getTokenType() == TokenType.RestOperator){
			termino();
			expressionSRAux();
		}else if(!ASintHelper.isFollowExpressionSRAux(curr)){	
			throw new UnexpectedTokenException("(!) Error, Expression mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());
		}else reuseToken();
		
		Logger.verbose("<-" + depth + " Fin <ExpressionSRAux>");	
	    depth--;
	}
	
	private void termino()throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <Termino>");
	
		factor();
		terminoAux();
		
		Logger.verbose("<-" + depth + " Fin <Termino>");	
	    depth--;
	}

	private void terminoAux()throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <TerminoAux>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.MultiplierOperator ||
		   curr.getTokenType() == TokenType.DivisionOperator ||
		   curr.getTokenType() == TokenType.ModOperator){
			
			factor();
			terminoAux();
		}else if(!ASintHelper.isFollowTerminoAux(curr)){
			throw new UnexpectedTokenException("(!) Error, Expresion mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());
		}else reuseToken();
		
		Logger.verbose("<-" + depth + " Fin <TerminoAux>");	
	    depth--;
	}
	
	private void factor()throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <Factor>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.NotOperator ||
		   curr.getTokenType() == TokenType.PlusOperator ||
		   curr.getTokenType() == TokenType.RestOperator){
			factor();
			
		}else {
			reuseToken();
			primario();
		}
		
		Logger.verbose("<-" + depth + " Fin <Factor>");	
	    depth--;
	}
	
	private void primario() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <Primario>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.ThisKeyword || ASintHelper.isLiteral(curr)){
			
		}else if(curr.getTokenType() == TokenType.OpenParenthesisSymbol){
			
			expression();
			getToken();
			if(curr.getTokenType() == TokenType.ClosedParenthesisSymbol)
				llamadaStar();
			else throw new UnexpectedTokenException("(!) Error, la llamada no es correcta. Se esperaba ), el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());
			
		}else if(curr.getTokenType() == TokenType.Identifier){			
			primarioFact();			
		}else if(curr.getTokenType() == TokenType.NewKeyword){
			getToken();
			if(curr.getTokenType() == TokenType.Identifier){
				argsActuales();
				llamadaStar();
			}
		}else throw new UnexpectedTokenException("(!) Error, Expresion mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());		
		
		Logger.verbose("<-" + depth + " Fin <Primario>");	
	    depth--;
	}
	
	
	private void primarioFact() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <PrimarioFact>");
	
		getToken();
		reuseToken();
		
		if(curr.getTokenType() == TokenType.DotSymbol){				
			llamadaStar();	
		}else if(curr.getTokenType() == TokenType.OpenParenthesisSymbol){			
			argsActuales();
			llamadaStar();
		} 				
		else if(!ASintHelper.isFollowFactor(curr))
			throw new UnexpectedTokenException("(!) Error, Expresion mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());		
				
		Logger.verbose("<-" + depth + " Fin <PrimarioFact>");	
	    depth--;
	}
	
	private void llamadaStar() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <Llamada*>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.DotSymbol){			
			reuseToken();
			llamada();
			llamadaStar();
		
		}else if(!ASintHelper.isFollowLlamadaStar(curr))
			throw new UnexpectedTokenException("(!) Error, Expresion mal formada, el token "+ curr.getLexema() +" no es valido, en línea " + curr.getLinea());		
		else reuseToken();
		
		Logger.verbose("<-" + depth + " Fin <Llamada*>");	
	    depth--;
	}

	private void llamada() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <Llamada>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.DotSymbol){
			getToken();
			if(curr.getTokenType() == TokenType.Identifier){				
				argsActuales();
			}else throw new UnexpectedTokenException("(!) Error, Expresion mal formada, se esperaba un identificador. Token invalido "+ curr.getLexema() +" en línea " + curr.getLinea());
		
		}else if(curr.getTokenType() != TokenType.DotSymbol)
			throw new UnexpectedTokenException("(!) Error, Expresion mal formada, se esperaba un '.' (Punto). Token invalido "+ curr.getLexema() +" en línea " + curr.getLinea());
		
		Logger.verbose("<-" + depth + " Fin <Llamada>");	
	    depth--;	    
	}
	
	private void argsActuales() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <ArgsActuales>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.OpenParenthesisSymbol){
			listaExpsQ();
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol)
				throw new UnexpectedTokenException("(!) Error, Expresion mal formada, se esperaba ')'. Token invalido "+ curr.getLexema() +" en línea " + curr.getLinea() + ". Se esperaba )");
			
		}else throw new UnexpectedTokenException("(!) Error, Expresion mal formada, se esperaba '('. Token invalido "+ curr.getLexema() +" en línea " + curr.getLinea());		
		
		Logger.verbose("<-" + depth + " Fin <ArgsActuales>");	
	    depth--;
	}
	
	private void listaExpsQ() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <listaExps?>");
	
		getToken();
		reuseToken();
		
		if(ASintHelper.isFirstListaExps(curr)){			
			listaExps();
			getToken();
			if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol)
				throw new UnexpectedTokenException("(!) Error, Lista de expressiones mal formada. Se esperaba ')'. Token invalido "+ curr.getLexema() +" en línea " + curr.getLinea());
			else reuseToken();
			
		}else if(curr.getTokenType() != TokenType.ClosedParenthesisSymbol)
			throw new UnexpectedTokenException("(!) Error,Lista de expressiones mal formada. Se esperaba ')'. Token invalido "+ curr.getLexema() +" en línea " + curr.getLinea());		
		
		Logger.verbose("<-" + depth + " Fin <listaExps?>");	
	    depth--;
	}
	
	private void listaExps() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <listaExps>");
	
		getToken();
		
		if(ASintHelper.isFirstListaExps(curr)){
			reuseToken();
			expressionOr();
			expressionAux();
			listaExpsFact();
				
		}else throw new UnexpectedTokenException("(!) Error, Lista de expresiones mal formada. Token invalido "+ curr.getLexema() +" en línea " + curr.getLinea());		
		
		Logger.verbose("<-" + depth + " Fin <listaExps>");	
	    depth--;
	}
	
	private void listaExpsFact() throws UnexpectedTokenException{
		depth++;
		Logger.verbose(depth + "-> Iniciando <listaExpsFact>");
	
		getToken();
		
		if(curr.getTokenType() == TokenType.ComaSymbol){			
			listaExps();
				
		}else if(!ASintHelper.isFollowListaExpsFact(curr)) 
			throw new UnexpectedTokenException("(!) Error, Lista de expressiones mal formada. Token invalido "+ curr.getLexema() +" en línea " + curr.getLinea());		
		else reuseToken();
		
		Logger.verbose("<-" + depth + " Fin <listaExpsFact>");	
	    depth--;
	}	
}
