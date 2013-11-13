package common;

public class CommonHelper {

	public static String Empty = "";
	
	public static boolean isNullOrEmpty(String s){
		return s == null || s.trim().length() == 0;
	}
	
	public static boolean isPredefinedClass(String name){
		return name.equals("Oject") || name.equals("System");	
	}
	
	public static boolean isPrimitiveType(String name){
		return name.equals("int") ||
			   name.equals("char") ||
			   name.equals("boolean") ||
			   name.equals("String");			   			
	}
}
