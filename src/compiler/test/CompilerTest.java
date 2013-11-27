package compiler.test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
 
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;
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


@RunWith(Parameterized.class)
public class CompilerTest {

	private String testFile;
	private String expected; // Mensaje de excepci�n esperado, o null si se espera que tenga �xito
	
	public CompilerTest(String testFile, String exp) {
		Application.Name = testFile;
		this.testFile =  testFile;
		this.expected = exp;
	}
	
	@Parameterized.Parameters(name= "{0}")
	public static Collection testCases() {
	   return Arrays.asList(new String[][] {
			  /* {"ClaseVaciaTest.correcto", null},
			   {"ClaseHelloWordTest.correcto", null},
			   {"PrintB.correcto", null},
			   {"PrintC.correcto", null},
			   {"PrintI.correcto", null},
			   {"PrintS.correcto", null},
			   {"PrintSln.correcto", null},
			   {"Read.correcto", null},	  
			   {"IfThen.correcto", null},
			   {"IfThenElse.correcto", null},
			   {"For.correcto", null},
			   {"While.correcto", null},
			   {"Asignacion.correcto", null},
			   {"BloqueAnidado.correcto", null},
			   {"ClaseCtorDefault.correcto", null},
			   {"ClaseCtorCustom.correcto", null},
			   {"ClaseCtorCustomYParams.correcto", null},
			   {"ClaseConAtributo.correcto", null},
			   {"ClaseConAtributo2.correcto", null},
			   {"DosClases.correcto", null},
			   {"ClaseConHerencia.correcto", null},
			   {"ClaseConMetodoRedefinido.correcto", null},
			   {"ClaseConMetodoEstatico.correcto", null},
			   {"ClaseConMetodoEstaticoYParams.correcto", null},
			   {"ClaseConMetodoDinamico.correcto", null},
			   {"ClaseConMetodoDinamicoYParams.correcto", null},
			   {"This.correcto", null},
			   {"ClaseConMultiplesAtributos.correcto", null},
			   {"AsignacionTipoClase.correcto", null},
			   {"ExpresionSR.correcto", null},
			   {"ExpresionMulDivMod.correcto", null},
			   {"ExpresionComparaciones.correcto", null},
			   {"ExpresionIgualdadDesigualdadPrimitivos.correcto", null},
			   {"ExpresionIgualdadDesigualdadClase.correcto", null},
			   {"LlamadaConRetorno.correcto", null},
			   {"LlamadaConNew.correcto", null},
			   {"LlamadaEncadenada.correcto", null},
			   {"SentenciaVacia.correcto", null},
			   {"ExpresionOr.correcto", null},
			   {"ExpresionAnd.correcto", null},
			   {"ExpresionBoolean.correcto", null},
			   {"ExpresionUnario.correcto", null},
			   {"LlamadaConParams.correcto", null},
			   {"VisibilidadVariables.correcto", null},
			   {"LlamadaMultiple.correcto", null},
			   {"ClaseConMetodoPolimorfico.correcto", null}
			   
			   // Test Incorrectos (Se espera Error)
			   
			   {"ClaseConSobreCargaDeMetodos.incorrecto", "Error(!) - La clase ClaseConSobreCargaDeMetodos ya que contiene un metodo met1. Linea 7"},
			   {"ClaseRepetida.incorrecto", "Error! - La clase ClaseRepetida ya existe en la TS. Linea 9"},
			   {"MetodoDinamicoConParamRepetido.incorrecto", "Error(!) - El nombre del argumento esta repetido. Linea 3"},			   
			   {"ClaseConAtributoRepetido.incorrecto", "Error(!) - La clase ClaseConAtributoRepetido ya contiene un atributo a1. Linea 4"},
			   {"MetodoDinamicoConVarLocalRepetida.incorrecto", "Error(!) - La variable local v1 se encuentra repetida dentro de la lista de variables locales. Linea 5"},
			   {"FileSinMetodoMain.incorrecto", "Error(!). Alguna clase debe contener al menos un metodo main sin parametros"},
			   {"FileConMetodoMainConParam.incorrecto", "Error(!) - El metodo Main no puede contener parametros formales."},
			   {"ClaseConMasDeUnCtor.incorrecto", "Error(!) - La clase ClaseConMasDeUnCtor ya posee un constructor declarado. Linea 6"},
			   {"MetodoConNbreDeVarLocalIgualAParam.incorrecto", "Error(!) - El parametro formal p1 es ambiguo. intente renombralo. Linea 4"},*/
			   {"MetodoConParamDeTipoInvalido.incorrecto", "Error(!). Tipo indefinido: tipoInvalido. Linea 3"}
			   
	   });
	}
	
    @Before
    public void setUp() {
        Application.isTesting = false; // i.e. no estamos testeando el ALex
        Application.logType = LogType.Console;  
        CodeGenerator.refresh();
    }

    @After
    public void tearDown() {
    	Logger.close();
    	CodeGenerator.close();    		
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
			}						
	}
	
}

