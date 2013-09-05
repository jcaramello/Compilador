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
	private String expected; // Mensaje de excepción esperado, o null si se espera que tenga éxito
	
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
			   { "incorrecto_classSinNombre.test", "(!) Error, se esperaba identificador en línea 3" },
			   { "incorrecto_empiezaSinClass.test", "(!) Error, se esperaba class en línea 3" },
			   { "incorrecto_extendsSinNombre.test", "(!) Error, se esperaba identificador en línea 3" },
			   { "incorrecto_classSinLlaveApertura.test", "(!) Error, se esperaba extends o { en línea 4" },
			   { "incorrecto_classConExtendsSinLlaveApertura.test", "(!) Error, se esperaba { en línea 4" },
			   { "incorrecto_atributoSinVar.test", "(!) Error, se esperaba atributo, constructor, método o } en línea 4" },
			   { "incorrecto_atributoNombreProhibido.test", "(!) Error, se esperaba identificador (nombre de variable), en línea 4" },
			   { "incorrecto_atributoSinPuntoYComa.test", "(!) Error, se esperaba , o ; en línea 5" },
			   { "incorrecto_listaAtributosMalFormada.test", "(!) Error, se esperaba identificador (nombre de variable) en línea 4" },
			   { "incorrecto_ctorArgumentosSinParentesis.test", "(!) Error, se esperaba ( abriendo lista de argumentos formales en línea 4" },
			   { "incorrecto_ctorArgumentosSinTipo.test", "(!) Error, se esperaba identificador (nombre de argumento formal), en línea 4" }, // Obviamente interpreta el supuesto nombre de la variable como tipo no primitivo
			   { "incorrecto_variableLocalSinVar.test", "(!) Error, se esperaba var (para variables locales) o { para apertura de bloque en línea 5" }, 
			   { "incorrecto_ctorSinBloque.test", "(!) Error, se esperaba var en línea 8" }, 
			   { "incorrecto_metodoSinBloque.test", "(!) Error, se esperaba var en línea 8" }, 
			   { "incorrecto_metodoSinMod.test", "(!) Error, se esperaba atributo, constructor, método o } en línea 5" }, 
			   { "incorrecto_segundaClassInvalida.test", "(!) Error, se esperaba class en línea 8" }, 
			   
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

		if(expected == null) { // Se espera éxito
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
