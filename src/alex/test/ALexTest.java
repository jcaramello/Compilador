package alex.test;

import static org.junit.Assert.*;
import org.junit.Test;
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

	@Test
	public void testIdentifierValido1() throws Exception {
		ALex tester = new ALex("test", "asd");
		Token t = tester.obtenerToken();
		assertEquals("Result", "identifier", t.getToken());
		assertEquals("Result", "asd", t.getLexema());		
	}
	
	@Test
	public void testIdentifierValido2() throws Exception {
		ALex tester = new ALex("test", "_a2sd");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "identifier", t.getToken());
		assertEquals("Result", "_a2sd", t.getLexema());	
	}
	
	@Test
	public void testIdentifierValido3() throws Exception {
		ALex tester = new ALex("test", "a_s1d_3{");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "identifier", t.getToken());
		assertEquals("Result", "a_s1d_3", t.getLexema());	
	}
	
	@Test(expected = InvalidIdentifierException.class)  
	public void testIdentifierInvalido() throws Exception {
		ALex tester = new ALex("test", "3asd");
		Token t = tester.obtenerToken();		
	}
	
	@Test
	public void testPalabraReservada() throws Exception {
		ALex tester = new ALex("test", "classDef");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "classDef", t.getToken());
		assertEquals("Result", "classDef", t.getLexema());	
	}
	
	@Test(expected = ForbiddenWordException.class)  
	public void testPalabraProhibida() throws Exception {
		ALex tester = new ALex("test", "byvalue");
		Token t = tester.obtenerToken();		
	}
	
	@Test
	public void testIntLiteral() throws Exception {
		ALex tester = new ALex("test", "123456");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "intLiteral", t.getToken());
		assertEquals("Result", "123456", t.getLexema());	
	}
	
	@Test
	public void testCharLiteral1() throws Exception {
		ALex tester = new ALex("test", "'a'");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "charLiteral", t.getToken());
		assertEquals("Result", "'a'", t.getLexema());	
	}
	
	@Test
	public void testCharLiteral2() throws Exception {
		ALex tester = new ALex("test", "'\t'");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "charLiteral", t.getToken());
		assertEquals("Result", "'\t'", t.getLexema());	
	}
	
	@Test(expected = InvalidCharacterException.class)
	public void testInvalidCharLiteral1() throws Exception {
		ALex tester = new ALex("test", "'\'");
		Token t = tester.obtenerToken();		
	}
	
	@Test(expected = InvalidCharacterException.class)
	public void testInvalidCharLiteral2() throws Exception {
		ALex tester = new ALex("test", "'\n'");
		Token t = tester.obtenerToken();		
	}

	@Test(expected = InvalidCharacterException.class)
	public void testInvalidCharLiteral3() throws Exception {
		ALex tester = new ALex("test", "'ab'");
		Token t = tester.obtenerToken();		
	}
	
	@Test(expected = InvalidCharacterException.class)
	public void testInvalidCharLiteral4() throws Exception {
		ALex tester = new ALex("test", "''");
		Token t = tester.obtenerToken();		
	}	
	
	@Test
	public void testStringLiteral1() throws Exception {
		ALex tester = new ALex("test", "\"qwerty\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "stringLiteral", t.getToken());
		assertEquals("Result", "\"qwerty\"", t.getLexema());	
	}

	@Test
	public void testStringLiteral2() throws Exception {
		ALex tester = new ALex("test", "\"\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "stringLiteral", t.getToken());
		assertEquals("Result", "\"\"", t.getLexema());	
	}

	@Test
	public void testStringLiteral3() throws Exception {
		ALex tester = new ALex("test", "\"class\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "stringLiteral", t.getToken());
		assertEquals("Result", "\"class\"", t.getLexema());	
	}
	
	// Esto está bien definido. Entrada (literalmente): "\\", lexema: "\\" 
	@Test
	public void testStringLiteral4() throws Exception {
		ALex tester = new ALex("test", "\"\\\\\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "stringLiteral", t.getToken());
		assertEquals("Result", "\"\\\"", t.getLexema());	
	}
	
	@Test
	public void testStringLiteral5() throws Exception {
		ALex tester = new ALex("test", "\"ñ\"");
		Token t = tester.obtenerToken();		
		assertEquals("Result", "stringLiteral", t.getToken());
		assertEquals("Result", "\"ñ\"", t.getLexema());	
	}

	@Test(expected = InvalidStringException.class)
	public void testInvalidStringLiteral1() throws Exception {
		ALex tester = new ALex("test", "\"qwe\nrty\"");
		Token t = tester.obtenerToken();		
	}
	
	@Test(expected = InvalidStringException.class)
	public void testInvalidStringLiteral2() throws Exception {
		ALex tester = new ALex("test", "\"\\\"");
		Token t = tester.obtenerToken();		
	}	
	
	@Test
	public void testOperator1() throws Exception {
		ALex tester = new ALex("test", "||");
		Token t = tester.obtenerToken();	
		assertEquals("Result", "||", t.getToken());
		assertEquals("Result", "||", t.getLexema());	
	}
	
	@Test
	public void testOperator2() throws Exception {
		ALex tester = new ALex("test", "*");
		Token t = tester.obtenerToken();	
		assertEquals("Result", "*", t.getToken());
		assertEquals("Result", "*", t.getLexema());	
	}
	
	@Test
	public void testOperator3() throws Exception {
		ALex tester = new ALex("test", "<qwe");
		Token t = tester.obtenerToken();	
		assertEquals("Result", "<", t.getToken());
		assertEquals("Result", "<", t.getLexema());	
	}
	
	@Test
	public void testOperator4() throws Exception {
		ALex tester = new ALex("test", ">=3");
		Token t = tester.obtenerToken();	
		assertEquals("Result", ">=", t.getToken());
		assertEquals("Result", ">=", t.getLexema());	
	}
	
	@Test
	public void testOperator5() throws Exception {
		ALex tester = new ALex("test", "==asd");
		Token t = tester.obtenerToken();	
		assertEquals("Result", "==", t.getToken());
		assertEquals("Result", "==", t.getLexema());	
	}

	@Test
	public void testOperator6() throws Exception {
		ALex tester = new ALex("test", "/");
		Token t = tester.obtenerToken();	
		assertEquals("Result", "/", t.getToken());
		assertEquals("Result", "/", t.getLexema());	
	}
	
	@Test
	public void testOperator7() throws Exception {
		ALex tester = new ALex("test", "!(asd)");
		Token t = tester.obtenerToken();	
		assertEquals("Result", "!", t.getToken());
		assertEquals("Result", "!", t.getLexema());	
	}
	
	@Test
	public void testOperator8() throws Exception {
		ALex tester = new ALex("test", "!=b");
		Token t = tester.obtenerToken();	
		assertEquals("Result", "!=", t.getToken());
		assertEquals("Result", "!=", t.getLexema());	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator1() throws Exception {
		ALex tester = new ALex("test", "&");
		Token t = tester.obtenerToken();	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator2() throws Exception {
		ALex tester = new ALex("test", "+=");
		Token t = tester.obtenerToken();	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator3() throws Exception {
		ALex tester = new ALex("test", "?");
		Token t = tester.obtenerToken();	
	}
	
	@Test(expected = ForbiddenOperatorException.class)
	public void testInvalidOperator4() throws Exception {
		ALex tester = new ALex("test", "|=");
		Token t = tester.obtenerToken();	
	}
	
	@Test
	public void testComment1() throws Exception {
		ALex tester = new ALex("test", "//asdasd");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);
	}
	
	@Test
	public void testComment2() throws Exception {
		ALex tester = new ALex("test", "//asdasd\na");
		Token t = tester.obtenerToken();
		assertEquals("Result", "identifier", t.getToken());
		assertEquals("Result", "a", t.getLexema());	
	}
	
	@Test
	public void testComment3() throws Exception {
		ALex tester = new ALex("test", "//as/////asdna");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);	
	}
	
	@Test
	public void testComment4() throws Exception {
		ALex tester = new ALex("test", "/*asdasd*/");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);
	}
	
	@Test
	public void testComment5() throws Exception {
		ALex tester = new ALex("test", "/*asd//asd*/");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);
	}
	
	@Test
	public void testComment6() throws Exception {
		ALex tester = new ALex("test", "/*asdasd*/a");
		Token t = tester.obtenerToken();
		assertEquals("Result", "identifier", t.getToken());
		assertEquals("Result", "a", t.getLexema());	
	}
	
	@Test(expected = UnclosedCommentException.class)
	public void testInvalidComment1() throws Exception {
		ALex tester = new ALex("test", "/*asd");
		Token t = tester.obtenerToken();	
		assertEquals("Result", null, t);
	}
	
	@Test(expected = OutOfAlphabetException.class)
	public void testInvalidInput() throws Exception {
		ALex tester = new ALex("test", "ñ");
		Token t = tester.obtenerToken();	
	}
	
		
}
