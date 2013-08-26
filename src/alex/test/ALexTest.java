package alex.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.Application;

import enums.LogType;
import enums.TokenType;
import alex.ALex;
import alex.Token;
import alex.exceptions.ForbiddenOperatorException;
import alex.exceptions.ForbiddenWordException;
import alex.exceptions.InvalidCharacterException;
import alex.exceptions.InvalidIdentifierException;
import alex.exceptions.InvalidStringException;
import alex.exceptions.OutOfAlphabetException;
import alex.exceptions.UnclosedCommentException;


public class ALexTest {

	/**
     * Sets up the test fixture. 
     * (Called before every test case method.)
     */
    @Before
    public void setUp() {
        Application.isTesting = true;
        Application.logType = LogType.Console;
    }

    /**
     * Tears down the test fixture. 
     * (Called after every test case method.)
     */
    @After
    public void tearDown() {
        
    }
    
	@Test
	public void testIdentifierValido1() throws Exception {
		ALex tester = new ALex("asd");
		Token t = tester.obtenerToken();
		assertEquals("Result", TokenType.Identifier, t.getTokenType());
		assertEquals("Result", "asd", t.getLexema());		
	}
	
	@Test
	public void testIdentifierValido2() throws Exception {
		ALex tester = new ALex("_a2sd");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.Identifier, t.getTokenType());
		assertEquals("Result", "_a2sd", t.getLexema());	
	}
	
	@Test
	public void testIdentifierValido3() throws Exception {
		ALex tester = new ALex("a_s1d_3{");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.Identifier, t.getTokenType());
		assertEquals("Result", "a_s1d_3", t.getLexema());	
	}
	
	@Test(expected = InvalidIdentifierException.class)  
	public void testIdentifierInvalido() throws Exception {
		ALex tester = new ALex("3asd");
		Token t = tester.obtenerToken();		
	}
	
	@Test
	public void testPalabraReservada() throws Exception {
		ALex tester = new ALex("classDef");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.ClassDef, t.getTokenType());
		assertEquals("Result", "classDef", t.getLexema());	
	}
	
	@Test(expected = ForbiddenWordException.class)  
	public void testPalabraProhibida() throws Exception {
		ALex tester = new ALex("byvalue");
		Token t = tester.obtenerToken();		
	}
	
	@Test
	public void testIntLiteral() throws Exception {
		ALex tester = new ALex("123456");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.IntLiteral, t.getTokenType());
		assertEquals("Result", "123456", t.getLexema());	
	}
	
	@Test
	public void testCharLiteral1() throws Exception {
		ALex tester = new ALex("'a'");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.CharLiteral, t.getTokenType());
		assertEquals("Result", "'a'", t.getLexema());	
	}
	
	@Test
	public void testCharLiteral2() throws Exception {
		ALex tester = new ALex("'\t'");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.CharLiteral, t.getTokenType());
		assertEquals("Result", "'\t'", t.getLexema());	
	}
	
	@Test(expected = InvalidCharacterException.class)
	public void testInvalidCharLiteral1() throws Exception {
		ALex tester = new ALex("'\'");
		Token t = tester.obtenerToken();		
	}
	
	@Test(expected = InvalidCharacterException.class)
	public void testInvalidCharLiteral2() throws Exception {
		ALex tester = new ALex("'\n'");
		Token t = tester.obtenerToken();		
	}

	@Test(expected = InvalidCharacterException.class)
	public void testInvalidCharLiteral3() throws Exception {
		ALex tester = new ALex("'ab'");
		Token t = tester.obtenerToken();		
	}
	
	@Test(expected = InvalidCharacterException.class)
	public void testInvalidCharLiteral4() throws Exception {
		ALex tester = new ALex("''");
		Token t = tester.obtenerToken();		
	}	
	
	@Test
	public void testStringLiteral1() throws Exception {
		ALex tester = new ALex("\"qwerty\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result",TokenType.StringLiteral, t.getTokenType());
		assertEquals("Result", "\"qwerty\"", t.getLexema());	
	}

	@Test
	public void testStringLiteral2() throws Exception {
		ALex tester = new ALex("\"\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.StringLiteral, t.getTokenType());
		assertEquals("Result", "\"\"", t.getLexema());	
	}

	@Test
	public void testStringLiteral3() throws Exception {
		ALex tester = new ALex("\"class\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.StringLiteral, t.getTokenType());
		assertEquals("Result", "\"class\"", t.getLexema());	
	}
	
	// Esto está bien definido. Entrada (literalmente): "\\", lexema: "\\" 
	@Test
	public void testStringLiteral4() throws Exception {
		ALex tester = new ALex("\"\\\\\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.StringLiteral, t.getTokenType());
		assertEquals("Result", "\"\\\"", t.getLexema());	
	}
	
	@Test
	public void testStringLiteral5() throws Exception {
		ALex tester = new ALex("\"ñ\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", TokenType.StringLiteral, t.getTokenType());
		assertEquals("Result", "\"ñ\"", t.getLexema());	
	}

	@Test(expected = InvalidStringException.class)
	public void testInvalidStringLiteral1() throws Exception {
		ALex tester = new ALex("\"qwe\nrty\"");
		Token t = tester.obtenerToken();		
	}
	
	@Test(expected = InvalidStringException.class)
	public void testInvalidStringLiteral2() throws Exception {
		ALex tester = new ALex("\"\\\"");
		Token t = tester.obtenerToken();		
	}	
	
	@Test
	public void testOperator1() throws Exception {
		ALex tester = new ALex("||");
		Token t = tester.obtenerToken();	
		assertEquals("Result", TokenType.OrOperator, t.getTokenType());
		assertEquals("Result", "||", t.getLexema());	
	}
	
	@Test
	public void testOperator2() throws Exception {
		ALex tester = new ALex("*");
		Token t = tester.obtenerToken();	
		assertEquals("Result", TokenType.MultiplierOperator, t.getTokenType());
		assertEquals("Result", "*", t.getLexema());	
	}
	
	@Test
	public void testOperator3() throws Exception {
		ALex tester = new ALex("<qwe");
		Token t = tester.obtenerToken();	
		assertEquals("Result", TokenType.LessOperator, t.getTokenType());
		assertEquals("Result", "<", t.getLexema());	
	}
	
	@Test
	public void testOperator4() throws Exception {
		ALex tester = new ALex(">=3");
		Token t = tester.obtenerToken();	
		assertEquals("Result", TokenType.GratherOrEqualOperator, t.getTokenType());
		assertEquals("Result", ">=", t.getLexema());	
	}
	
	@Test
	public void testOperator5() throws Exception {
		ALex tester = new ALex("==asd");
		Token t = tester.obtenerToken();	
		assertEquals("Result", TokenType.EqualOperator, t.getTokenType());
		assertEquals("Result", "==", t.getLexema());	
	}

	@Test
	public void testOperator6() throws Exception {
		ALex tester = new ALex("/");
		Token t = tester.obtenerToken();	
		assertEquals("Result", TokenType.DivisionOperator, t.getTokenType());
		assertEquals("Result", "/", t.getLexema());	
	}
	
	@Test
	public void testOperator7() throws Exception {
		ALex tester = new ALex("!(asd)");
		Token t = tester.obtenerToken();	
		assertEquals("Result", TokenType.NotOperator, t.getTokenType());
		assertEquals("Result", "!", t.getLexema());	
	}
	
	@Test
	public void testOperator8() throws Exception {
		ALex tester = new ALex("!=b");
		Token t = tester.obtenerToken();	
		assertEquals("Result", TokenType.DistinctOperator, t.getTokenType());
		assertEquals("Result", "!=", t.getLexema());	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator1() throws Exception {
		ALex tester = new ALex("&");
		Token t = tester.obtenerToken();	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator2() throws Exception {
		ALex tester = new ALex("+=");
		Token t = tester.obtenerToken();	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator3() throws Exception {
		ALex tester = new ALex("?");
		Token t = tester.obtenerToken();	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator4() throws Exception {
		ALex tester = new ALex("|=");
		Token t = tester.obtenerToken();	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator5() throws Exception {
		ALex tester = new ALex("^");
		Token t = tester.obtenerToken();	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator6() throws Exception {
		ALex tester = new ALex("^=");
		Token t = tester.obtenerToken();	
	}
	
	@Test
	public void testComment1() throws Exception {
		ALex tester = new ALex("//asdasd");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);
	}
	
	@Test
	public void testComment2() throws Exception {
		ALex tester = new ALex("//asdasd\na");
		Token t = tester.obtenerToken();
		assertEquals("Result", TokenType.Identifier, t.getTokenType());
		assertEquals("Result", "a", t.getLexema());	
	}
	
	@Test
	public void testComment3() throws Exception {
		ALex tester = new ALex("//as/////asdna");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);	
	}
	
	@Test
	public void testComment4() throws Exception {
		ALex tester = new ALex("/*asdasd*/");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);
	}
	
	@Test
	public void testComment5() throws Exception {
		ALex tester = new ALex("/*asd//asd*/");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);
	}
	
	@Test
	public void testComment6() throws Exception {
		ALex tester = new ALex("/*asdasd*/a");
		Token t = tester.obtenerToken();
		assertEquals("Result", TokenType.Identifier, t.getTokenType());
		assertEquals("Result", "a", t.getLexema());	
	}
	
	@Test(expected = UnclosedCommentException.class)
	public void testInvalidComment1() throws Exception {
		ALex tester = new ALex("/*asd");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);
	}
	
	@Test(expected = OutOfAlphabetException.class)
	public void testInvalidInput() throws Exception {
		ALex tester = new ALex("ñ");
		Token t = tester.obtenerToken();	
	}
	

}
