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
			   { "incorrecto_atributoSinVar.test", "(!) Error, definicion del miembro de clase no valido. se esperaba: var, identificador o modificador de método (static o dynamic) y no int, en línea 4" },
			   { "incorrecto_atributoNombreProhibido.test", "(!) Error, se esperaba identificador (nombre de variable), en línea 4" },
			   { "incorrecto_atributoSinPuntoYComa.test", "(!) Error, se esperaba , o ; en línea 5" },
			   { "incorrecto_listaAtributosMalFormada.test", "(!) Error, se esperaba identificador (nombre de variable) en línea 4" },
			   { "incorrecto_listaAtributosMalFormada2.test", "(!) Error, se esperaba identificador (nombre de variable) en línea 4" }, 
			   { "incorrecto_ctorArgumentosSinParentesis.test", "(!) Error, se esperaba ( abriendo lista de argumentos formales en línea 4" },
			   { "incorrecto_ctorArgumentosSinTipo.test", "(!) Error, se esperaba identificador (nombre de argumento formal), en línea 4" }, // Obviamente interpreta el supuesto nombre de la variable como tipo no primitivo
			   { "incorrecto_variableLocalSinVar.test", "(!) Error, se esperaba var (para variables locales) o { para apertura de bloque en línea 5" }, 
			   { "incorrecto_ctorSinBloque.test", "(!) Error, se esperaba var en línea 8" }, 
			   { "incorrecto_metodoSinBloque.test", "(!) Error, se esperaba var en línea 8" }, 
			   { "incorrecto_metodoSinMod.test", "(!) Error, definicion del miembro de clase no valido. se esperaba: var, identificador o modificador de método (static o dynamic) y no char, en línea 5" }, 
			   { "incorrecto_segundaClassInvalida.test", "(!) Error, se esperaba class en línea 8" }, 
			   { "incorrecto_tokenInvalidoEnClass.test", "(!) Error, definicion del miembro de clase no valido. se esperaba: var, identificador o modificador de método (static o dynamic) y no ;, en línea 5" },
			   { "incorrecto_variableVoid.test", "(!) Error, se esperaba boolean, int, char, String o identificador de tipo después de var, en línea 4" },
			   { "incorrecto_metodoSinNada.test", "(!) Error, se esperaba var (para variables locales) o { para apertura de bloque en línea 7" },
			   { "correcto_expression.test", null },
			   { "incorrecto_expressionMalTerminada.test", "(!) Error, Expresion mal formada, el token } no es valido, en línea 11" },
			   { "incorrecto_expressionMalParentizada.test", "(!) Error, se esperaba ; después de asignación en línea 10" },
			   { "incorrecto_expressionSinPuntoYComaAlFinal.test", "(!) Error, Expresion mal formada, el token } no es valido, en línea 11" },
			   { "correcto_sentenciaReturnVacio.test", null },
			   { "correcto_ExpresionLlamada.test", null },
			   { "incorrecto_MetodoConCuerpoInvalido.test", "(!) Error, el token = no se corresponde con el comienzo de una sentencia valida, en línea 3" },			   			  
			   { "incorrecto_Asignacion_error6bis.test", "(!) Error, el token 4 no se corresponde con el comienzo de una sentencia valida, en línea 3"},
			   { "incorrecto_IfThenElseMalFormado.test", "(!) Error, el token else no se corresponde con el comienzo de una sentencia valida, en línea 3"},	
			   { "incorrecto_IfThenMalFormado.test", "(!) Error, el token } no se corresponde con el comienzo de una sentencia valida, en línea 4"},			   
			   { "incorrecto_ExpressionLogicaTriple.test", "(!) Error, Expression mal formada, el token < no es valido, en línea 3"}
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
				// El siguiente assert lo agrege para el caso en cuando es el test es correcto.
				// ya que en ese caso, no se va a disparar ninguna excepcion y deberiamos chequear que 
				// eso era realmente lo que se esperaba, y eso lo hago justamente mirando el nombre del file.
				// Si el nombre del file contiene en algun lugar el sub string "incorrecto", entonces asumo 
				// que se esperaba que se disparase una excepcion, lo cual no sucedio y por lo que debo hacer fallar el test.
				assertEquals(testFile.toLowerCase().contains("incorrecto".toLowerCase()), false);
			} catch (UnexpectedTokenException e) {
				Logger.log(e.getMessage());
				assertEquals(e.getMessage(), expected);
			}
			
		}		
	}
	
}
