package common;

import java.util.*;
import enums.AttributeType;

/**
 * Esta es un clase interna que implementa el manager de atributos
 * @author jcaramello
 *
 */
class AttributeManagerImpl implements IAttributeManager{

	/**
	 * Default Constructor
	 */
	public AttributeManagerImpl()
	{
		this.CurrentSynthesizedAttributes = new HashMap<AttributeType, Attribute<?>>();
		this.Stack = new Stack<Map<AttributeType, Attribute<?>>>();
	}
	
	/**
	 * Stack donde se mantienen los atributos
	 */
	private Stack<Map<AttributeType, Attribute<?>>> Stack;
	
	/**
	 * Current synthesized attributes
	 */
	private Map<AttributeType, Attribute<?>> CurrentSynthesizedAttributes;
	
	/**
	 * Get the current Synthesized Attributes
	 * @return  A map of attibutes
	 */
	public Map<AttributeType, Attribute<?>> getCurrentSynthesizedAttributes()
	{
		return this.CurrentSynthesizedAttributes;
	}
	
	/**
	 * Get the current inherited Attributes
	 * @return A map of attibutes
	 */
	public Map<AttributeType, Attribute<?>> getCurrentInheritedAttributes()
	{
		return this.Stack.peek();
	}
	
	/**
	 * Load next context
	 */
	public void loadContext()
	{
		this.Stack.push(this.CurrentSynthesizedAttributes);
		this.CurrentSynthesizedAttributes = new HashMap<AttributeType, Attribute<?>>();
	}
	
	/**
	 * Unload current context
	 */
	public void unloadContext()
	{		
		this.CurrentSynthesizedAttributes = this.Stack.pop();
	}
	
	/**
	 * Set a Synthesized Attribute 
	 */
	public void setSynthesizedAttribute(Attribute<?> attribute)
	{
		if(this.CurrentSynthesizedAttributes.containsKey(attribute.Type))
			this.CurrentSynthesizedAttributes.remove(attribute.Type);
		
		this.CurrentSynthesizedAttributes.put(attribute.Type, attribute);
	}
	
	/**
	 * Get a Synthesized Attribute of Attribute Type type
	 * @param type
	 * @return Attribute
	 */
	public Attribute<?> getSynthesizedAttribute(AttributeType type)
	{
		return this.CurrentSynthesizedAttributes.get(type);
	}
	
	/**
	 * Set a Inherited Attribute 
	 */
	public void setInheritedAttribute(Attribute<?> attribute)
	{
		
		if(this.getCurrentInheritedAttributes().containsKey(attribute.Type))
			this.getCurrentInheritedAttributes().remove(attribute.Type);
		
		this.getCurrentInheritedAttributes().put(attribute.Type, attribute);
	}
	
	/**
	 * Get a Inherited Attribute of Attribute Type type
	 * @param type
	 * @return Attribute
	 */
	public Attribute<?> getInheritedAttribute(AttributeType type)
	{
		return this.getCurrentInheritedAttributes().get(type);
	}
}
