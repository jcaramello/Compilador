package asema.exceptions;

public class SemanticErrorException extends Exception{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public SemanticErrorException(String msg)
	{
		super(msg);
	}

}
