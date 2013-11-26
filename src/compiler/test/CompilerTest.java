package compiler.test;

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

import asema.TS;
import asema.exceptions.SemanticErrorException;
import asint.ASint;
import asint.UnexpectedTokenException;

import common.Application;
import common.CodeGenerator;
import common.Logger;

import enums.LogType;
import enums.TokenType;


@RunWith(Parameterized.class)
public class CompilerTest {

	private String testFile;
	private String expected; // Mensaje de excepción esperado, o null si se espera que tenga éxito
	
	public CompilerTest(String testFile, String exp) {
		Application.Name = testFile;
		this.testFile =  testFile;
		this.expected = exp;
	}
	
	@Parameterized.Parameters(name= "{0}")
	public static Collection testCases() {
	   return Arrays.asList(new String[][] {
			   //{"ClaseVaciaTest.correcto", null},
			   //{"ClaseHelloWordTest.correcto", null},
			   //{"PrintB.correcto", null},
			   //{"PrintC.correcto", null},
			   //{"PrintI.correcto", null},
			   //{"PrintS.correcto", null},
			   //{"PrintSln.correcto", null},
			   //{"Read.correcto", null}			  
			   //{"IfThen.correcto", null},
			   //{"IfThenElse.correcto", null},
			   //{"For.correcto", null},
			   //{"While.correcto", null},
			   //{"Asignacion.correcto", null},
			   //{"BloqueAnidado.correcto", null},
			   //{"ClaseCtorDefault.correcto", null},
			   //{"ClaseCtorCustom.correcto", null},
			   //{"ClaseCtorCustomYParams.correcto", null},
			   //{"ClaseConAtributo.correcto", null},
			   //{"DosClases.correcto", null},
			   //{"ClaseConHerencia.correcto", null},
			   //{"ClaseConMetodoRedefinido.correcto", null},
			   //{"This.correcto", null},
			   //{"ClaseConMultiplesAtributos.correcto", null},
			   //{"AsignacionTipoClase.correcto", null}
			   //{"ExpresionSR.correcto", null},
			   //{"ExpresionMulDivMod.correcto", null},
			   //{"ExpresionComparaciones.correcto", null},
			   //{"ExpresionIgualdadDesigualdadPrimitivos.correcto", null},
			   //{"ExpresionIgualdadDesigualdadClase.correcto", null}
			   //{"LlamadaConRetorno.correcto", null},
			   //{"LlamadaConNew.correcto", null},
			   {"LlamadaEncadenada.correcto", null}
			   //{"SentenciaVacia.correcto", null},
			   //{"ExpresionOr.correcto", null},
			   //{"ExpresionAnd.correcto", null},
			   //{"ExpresionBoolean.correcto", null}
			   //{"ExpresionUnario.correcto", null}
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
	public void testGenerico() throws UnexpectedTokenException, SemanticErrorException {
		
			URL url = this.getClass().getResource("");
									
			try {													
				TS.initialize();
				new ASint(url.getPath() + "testcases/" + testFile);	
				TS.execute();																							
				// El siguiente assert lo agrege para el caso en cuando es el test es correcto.
				// ya que en ese caso, no se va a disparar ninguna excepcion y deberiamos chequear que 
				// eso era realmente lo que se esperaba, y eso lo hago justamente mirando el nombre del file.
				// Si el nombre del file contiene en algun lugar el sub string "incorrecto", entonces asumo 
				// que se esperaba que se disparase una excepcion, lo cual no sucedio y por lo que debo hacer fallar el test.
				assertEquals(testFile.toLowerCase().contains("incorrecto".toLowerCase()), false);
			} catch (SemanticErrorException e) {
				Logger.log(e.getMessage());
				assertEquals(e.getMessage(), expected);
			}finally{
				Logger.close();
				CodeGenerator.close();
			}
			
					
	}
	
}
