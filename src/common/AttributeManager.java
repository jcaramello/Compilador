package common;

/**
 * Attribute Manager - Maneja una instancia singleton de la implemtacion del manager(AttributeManagerImpl)
 * Dicha instancia singleton maneja la pila y donde se van almacenando los atributos sintetizados y/o heredados 
 *     
 * @author jcaramello, nechegoyen
 *
 */
public class AttributeManager {
	
	/**
	 * Singleton Instance.
	 */
	private static IAttributeManager Current;
	
	/**
	 * Private constructor
	 */
	private AttributeManager()
	{
		
	}
	
	/**
	 * Get the current singleton instance
	 * @return
	 */
	public static IAttributeManager getCurrent()
	{
		if(AttributeManager.Current == null)
			AttributeManager.Current = new AttributeManagerImpl();
		
		return AttributeManager.Current;
	}

}
