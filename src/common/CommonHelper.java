package common;

public class CommonHelper {

	public static String Empty = "";
	
	public static boolean isNullOrEmpty(String s){
		return s == null || s.trim().length() == 0;
	}
}
