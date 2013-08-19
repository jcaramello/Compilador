package alex;

import java.io.BufferedWriter;
import java.io.FileWriter;


public class Main {
	
	public static void main(String args[])
	{
		try {
			
			ALex analizador = null;
			if (args.length == 0) {
				System.err.println("Debe ingresarse al menos un parámetro. Modo de uso: ALex <Archivo_fuente> [<Archivo_destino>]");
				return;
			}
			if (args.length == 1) {
				analizador = new ALex(args[0], false);
				
				System.out.format("%-16s%-32s%-8s", "TOKEN", "LEXEMA", "LINEA");
				System.out.println();
				
				Token t = null;
				do {		
					t = analizador.obtenerToken();
					if(t!=null)
						System.out.format("%-16s%-32s%-8s", t.getToken(), t.getLexema(), t.getLinea());
						System.out.println();
				} while(t!=null); // EOF
			}
			else {
				analizador = new ALex(args[0], true);
				
				FileWriter fstream = new FileWriter(args[1]);
				BufferedWriter out = new BufferedWriter(fstream);
			
				out.write(String.format("%-16s", "TOKEN")+String.format("%-32s", "LEXEMA")+String.format("%-8s", "LINEA"));
				out.write("\r\n");
				
				Token t = null;
				do {		
					t = analizador.obtenerToken();		
					if(t!=null) {
						System.out.println("*** Encontrado token "+ t.getToken() + ": " + t.getLexema() +" en línea "+ t.getLinea()+ " ***");
						out.write(String.format("%-16s", t.getToken())+String.format("%-32s",t.getLexema())+String.format("%-8s", t.getLinea()));
						out.write("\r\n");
					}
				} while(t!=null); // EOF
				out.close();	
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
}
