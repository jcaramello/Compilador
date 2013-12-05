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
	private String expected; // Mensaje de excepción esperado, o null si se espera que tenga éxito
	
	public CompilerTest(String testFile, String exp) {
		Application.OutputFile = testFile;
		this.testFile =  testFile;
		this.expected = exp;
	}
	
	@Parameterized.Parameters(name= "{0}")
	public static Collection testCases() {
	   return Arrays.asList(new String[][] {
			   {"ClaseVaciaTest.correcto", null},
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
			   {"ExpresionIgualdadDesigInt.correcto", null},
			   {"ExpresionIgualdadDesigChar.correcto", null},		  
			   {"ExpresionIgualdadDesigString.correcto", null},				  
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
			   {"LlamadaConParamClase.correcto", null},
			   {"VisibilidadVariables.correcto", null},
			   {"LlamadaMultiple.correcto", null},
			   {"ClaseConMetodoPolimorfico.correcto", null},
			   {"LlamadaConHerencia.correcto", null},
			   {"SentenciaSimplePop.correcto", null},
			   {"SentenciaSimpleNoPopOnVoid.correcto", null},
			   {"Fib.correcto", null},
			   {"HerenciaMetodosEstaticosYDinamicos.correcto", null},
			   {"LlamadaConParamClaseQueConforma.correcto", null},
			   {"LlamadaEncadenadaCompleja.correcto", null},
			   {"AsignacionLiteralNull.correcto", null},
			   {"AsignacionLiteralNull2.correcto", null},
			   {"AsignacionTipoClaseConThis.correcto", null},
			   {"AsignacionTipoClaseConforma.correcto", null},
			   {"MetodoConTipoDeRetornoVoidYSentReturnVacia.correcto", null},
			   {"ComparacionLiteralesNull.correcto", null},
			   {"AsignacionConParentesis.correcto", null},
			   {"AsignacionAParam.correcto", null},
			   {"BloqueOrdenSentencias.correcto", null},
			   {"LlamadaLocal.correcto", null},
			   {"VTables.correcto", null},
			   {"VisibilidadVariablesConHerencia.correcto", null},
			   {"ExpresionIgualdadDesigualdadPrimitivos.correcto", null},
			   {"Llamada.correcto", null},
			   {"MetodoConReturnNull.correcto", null},
			   
			   // Test Incorrectos (Se espera Error)
			   
			   {"ClaseConSobreCargaDeMetodos.incorrecto", "Error(!) - La clase ClaseConSobreCargaDeMetodos ya que contiene un metodo met1. Linea 7"},
			   {"ClaseRepetida.incorrecto", "Error(!) - La clase ClaseRepetida ya existe en la TS. Linea 9"},
			   {"MetodoDinamicoConParamRepetido.incorrecto", "Error(!) - El nombre del argumento esta repetido. Linea 3"},			   
			   {"ClaseConAtributoRepetido.incorrecto", "Error(!) - La clase ClaseConAtributoRepetido ya contiene un atributo a1. Linea 4"},
			   {"MetodoDinamicoConVarLocalRepetida.incorrecto", "Error(!) - La variable local v1 se encuentra repetida dentro de la lista de variables locales. Linea 5"},
			   {"FileSinMetodoMain.incorrecto", "Error(!). Alguna clase debe contener al menos un metodo main sin parametros"},
			   {"FileConMetodoMainConParam.incorrecto", "Error(!) - El metodo Main no puede contener parametros formales."},
			   {"ClaseConMasDeUnCtor.incorrecto", "Error(!) - La clase ClaseConMasDeUnCtor ya posee un constructor declarado. Linea 6"},
			   {"MetodoConNbreDeVarLocalIgualAParam.incorrecto", "Error(!) - El parametro formal p1 es ambiguo. intente renombralo. Linea 4"},
			   {"MetodoConParamDeTipoInvalido.incorrecto", "Error(!). Tipo indefinido: tipoInvalido. Linea 3"}, 
			   {"ClaseConSuperClaseInvalida.incorrecto", "Error(!). La clase: Clase1, no existe. Linea 1"}, 
			   {"LlamadaConHerencia.incorrecto", "Error(!). El método test no existe en la clase M1."},
			   {"AsignacionTipoClaseNoConforma.incorrecto", "Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea 8."},
			   {"AsignacionTipoClaseNoConforma2.incorrecto", "Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea 7."},
			   {"AsignacionTipoPrimitivoNoConforma.incorrecto", "Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea 4."},
			   {"ParametroTipoPrimitivoNoConforma.incorrecto", "Error(!). Los tipos de los parámetros actuales deben conformar a los tipos de los parámetros formales en parámetro 0, en línea 9."},
			   {"ParametroTipoClaseNoConforma.incorrecto", "Error(!). Los tipos de los parámetros actuales deben conformar a los tipos de los parámetros formales en parámetro 1, en línea 10."},
			   {"MetodoTipoRetornoInexistente.incorrecto", "Error(!). Tipo indefinido: X. Linea 3"},
			   {"AsignacionLiteralNullNoConforma.incorrecto", "Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea 7."},
			   {"LlamadaConDistintaCantParams.incorrecto", "Error(!). La cantidad de parámetros actuales debe coincidir con la cantidad de parámetros formales en línea 11."},
			   {"ClasesConHerenciaCircular.incorrecto", "Existe herencia circular en Clase2"},
			   {"ClaseConAtributoNombreClase.incorrecto", "C: ninguna clase puede definir variables de instancia con el mismo nombre que ella o que alguno de sus metodos, en línea 3"},
			   {"ClaseConAtributoNombreMetodo.incorrecto", "asd: ninguna clase puede definir variables de instancia con el mismo nombre que ella o que alguno de sus metodos, en línea 10"},
			   {"ClaseConMetodoNombreAtributo.incorrecto", "asd: ninguna clase puede definir métodos con el mismo nombre que ella o que alguna de sus variables de instancia, en línea 10"},
			   {"ClaseConMetodoNombreClase.incorrecto", "Error(!) - El método se llama igual a la clase a la que pertenece: C, en línea 9"},
			   {"ClaseConMetodoMalRedefinidoPorCantParams.incorrecto", "Error(!). El metodo Clase2.setA1 debe tener el mismo número y tipo de parametros que en la superclase, así como también el mismo modificador y tipo de retorno."},
			   {"ClaseConMetodoMalRedefinidoPorTipoParam.incorrecto", "Error(!). El metodo Clase2.setA1 debe tener el mismo número y tipo de parametros que en la superclase, así como también el mismo modificador y tipo de retorno."},
			   {"ClaseConMetodoMalRedefinidoPorTipoRetorno.incorrecto", "Error(!). El metodo Clase2.setA1 debe tener el mismo número y tipo de parametros que en la superclase, así como también el mismo modificador y tipo de retorno."},
			   {"ClaseConMetodoMalRedefinidoPorModificador.incorrecto", "Error(!). El metodo Clase2.setA1 debe tener el mismo número y tipo de parametros que en la superclase, así como también el mismo modificador y tipo de retorno."},
			   {"ThisEnEstatico.incorrecto", "No se puede acceder a this desde un método estático."},
			   {"MetodoConTipoDeRetornoVoidYSentReturnNoVacia.incorrecto", "Error(!) - Sentencia return invalida. El tipo de retorno del metodo es void. Linea 11"},
			   {"FileConMasDeUnMetodoMain.incorrecto", "Error(!). No puede haber mas de un metodo main."},
			   {"ExpresionIgualdadCharInt.incorrecto", "El tipo de los operandos == o != debe conformar, en línea 10"},
			   {"ExpresionIgualdadCharString.incorrecto", "El tipo de los operandos == o != debe conformar, en línea 10"},
			   {"ExpresionDesigualdadIntString.incorrecto", "El tipo de los operandos == o != debe conformar, en línea 10"},
			   {"ExpresionIgualdadClaseNoConforma.incorrecto", "El tipo de los operandos == o != debe conformar, en línea 14"},
			   {"ExpresionUnarioChar.incorrecto", "El tipo del operando de ! debe ser un boolean, en línea 9"},
			   {"ExpresionUnarioString.incorrecto", "El tipo del operando de + o - debe ser un entero, en línea 9"},
			   {"ExpresionOrMalFormada.incorrecto", "El tipo de los operandos && o || debe ser boolean, en línea 9"},
			   {"ExpresionComparacionesMalFormada.incorrecto", "El tipo de los operandos >, >=, <, <= debe ser entero, en línea 9"},
			   {"ExpresionSRMalFormada.incorrecto", "El tipo de los operandos +, -, *, / o % debe ser entero, en línea 8"},
			   {"IdentificadorNoEncontrado.incorrecto", "Error(!). a no es un identificador valido, en línea 7"},
			   {"IfThenCondicionMal.incorrecto", "El tipo de la expresión condicional del if debe ser boolean."},
			   {"LlamadaACtor.incorrecto", "Error(!). El método M no existe en la clase M."},
			   {"WhileCondicionMal.incorrecto", "El tipo de la expresión condicional del while debe ser boolean."},
			   {"ForCondicionMal.incorrecto", "El tipo de la expresión condicional del for debe ser boolean."},
			   {"ForIncrementoNoConforma.incorrecto", "El tipo de la expresión de incremento en el for debe conformar con el de la variable de iteración."},
			   {"AsignacionClaseATipoPrimitivo.incorrecto", "Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea 8."},
			   {"AsignacionPrimitivoATipoClase.incorrecto", "Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea 7."},
			   {"LlamadaAMetodoNoVoidConRetornoVacio.incorrecto", "El tipo de la expresión de retorno de un método no-void debe existir, en línea 16"},
			   {"LlamadaEstaticoConDistintaCantParams.incorrecto", "Error(!). La cantidad de parámetros actuales debe coincidir con la cantidad de parámetros formales en línea 8."},
			   {"LlamadaAMetodoNoVoidConRetornoVacio.incorrecto", "El tipo de la expresión de retorno de un método no-void debe existir, en línea 16"},
			   {"ClasesConHerenciaCircularDirecta.incorrecto", "Existe herencia circular en Clase1"},
			   {"LlamadaAMetodoNoVoidConRetornoVacio.incorrecto", "El tipo de la expresión de retorno de un método no-void debe existir, en línea 16"},
			   {"AsignacionDeClase.incorrecto", "Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea 6."},
			   {"LlamadaEncadenadaMetodoNoExistente.incorrecto", "Error(!). El método test2 no existe en la clase X."},
			   {"VisibilidadVariableOcultaConHerencia.incorrecto", "Error(!). a1 no es un identificador valido, en línea 39"},
			   {"LlamadaEstaticoDesdeVariable.incorrecto", "Error(!). No se puede llamar a un método estático desde una variable de tipo clase, en línea 9."},
			   {"LlamadaDinamicoDesdeClase.incorrecto", "Error(!). No se puede llamar a un método dinámico desde una clase sin instanciar, en línea 7."},
			   {"AsignacionClaseATipoPrimitivo.incorrecto", "Error(!). El tipo del lado derecho de una asignación debe conformar al tipo del lado izquierdo, en línea 8."},
			   
	   });
	}
	
    @Before
    public void setUp() {
        Application.isTesting = false; // i.e. no estamos testeando el ALex
        Application.logType = LogType.Console;  
        CodeGenerator.setPrefix("Tests");
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

