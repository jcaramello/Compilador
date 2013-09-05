package asint.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
 
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import asint.ASint;
import asint.UnexpectedTokenException;

import common.Application;
import common.Logger;

import enums.LogType;
import enums.TokenType;


@RunWith(Parameterized.class)
public class ASintTest {

	private String testFile;
	private String expected; // Mensaje de excepci�n esperado, o null si se espera que tenga �xito
	
	public ASintTest(String testFile, String exp) {
		this.testFile = testFile;
		this.expected = exp;
	}
	
	@Parameterized.Parameters(name= "{0}")
	public static Collection testCases() {
	   return Arrays.asList(new String[][] {
			   { "correcto_full.test", null },
			   { "correcto1.test", null },
			   { "correcto2.test", null },
			   { "incorrecto_classSinNombre.test", "(!) Error, se esperaba identificador en l�nea 3" }
	   });
	}
	
    @Before
    public void setUp() {
        Application.isTesting = false; // i.e. no estamos testeando el ALex
        Application.logType = LogType.Console;
    }

    @After
    public void tearDown() {
        
    }
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void testGenerico() throws UnexpectedTokenException {

		if(expected == null) { // Se espera �xito
			URL url = this.getClass().getResource(testFile);
			ASint tester = new ASint(url.getPath());
		}
		else {
			try {					
				URL url = this.getClass().getResource(testFile);
				ASint tester = new ASint(url.getPath());
			} catch (UnexpectedTokenException e) {
				Logger.log(e.getMessage());
				assertEquals(e.getMessage(), expected);
			}
			
		}		
	}
	
}
