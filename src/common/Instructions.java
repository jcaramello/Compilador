package common;

public class Instructions {
	
	/**
	 * Aritmethics Instructions
	 */
	public static final String ADD = "ADD";
	public static final String SUB = "SUB";
	public static final String MUL = "MUL";
	public static final String DIV = "DIV";
	
	/**
	 * Logical Instructions
	 */
	public static final String DW = "DW";
	public static final String NOP = "NOP";
	public static final String NOT = "NOT";
	public static final String NEG = "NEG";	
	public static final String OR = "OR";
	public static final String AND = "AND";
	public static final String GT = "GT";
	public static final String GE = "GE";
	public static final String LT = "LT";
	public static final String LE = "LE";
	public static final String NE = "NE";
	public static final String EQ = "EQ";
	
	/**
	 * Memory instructions
	 */
	public static final String PUSH = "PUSH";
	public static final String POP = "POP";
	public static final String LOAD = "LOAD";
	public static final String STORE = "STORE";
	public static final String STOREREF = "STOREREF";
	public static final String LOADREF = "LOADREF";
	public static final String LOADFP = "LOADFP";
	public static final String LOADSP = "LOADSP";
	public static final String STOREFP = "STOREFP";
	public static final String STORESP = "STORESP";
	public static final String FMEM = "FMEM";
	public static final String RMEM = "RMEM";
	public static final String DUP = "DUP";
	public static final String SWAP = "SWAP";
	
	/**
	 * Sections
	 */
	public static final String CODE_SECTION = ".CODE";
	public static final String DATA_SECTION = ".DATA";
	public static final String VTLabel = "VT_%s:"; // VT_[clase]	
	public static final String LABEL = "%s_%s:";	//[clase]_[metodo] :
	
	/**
	 * Other
	 */	
	public static final String CALL = "CALL";
	public static final String JUMP = "JUMP";
	public static final String BF = "BF";
	public static final String HALT = "HALT";	
	public static final String RET = "RET";
	public static final String READ = "READ";
	public static final String PRNLN = "PRNLN";
	public static final String BPRINT = "BPRINT";
	public static final String CPRINT = "CPRINT";
	public static final String IPRINT = "IPRINT";
	public static final String SPRINT = "SPRINT";
	
}
