package common;

import enums.AttributeType;

/**
 * Attribute Class
 * @author jcaramello, nechegoyen
 *
 * @param <T>
 */
public class Attribute<T>{
	
	/**
	 * Type
	 */
	public AttributeType Type;
	
	/**
	 * Value
	 */
	public T Value;
	
	/**
	 * Default Constructor
	 * @param t Type
	 * @param v Value
	 */
	public Attribute(AttributeType t, T v)
	{
		this.Type = t;
		this.Value = v;	
	}
}
