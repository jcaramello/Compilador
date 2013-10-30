package common;

import java.util.Map;

import enums.AttributeType;

/**
 * Attribute Manager Interface
 * @author jcaramello
 * 
 *  Notar que en cada metodo de la edt, siempre trabajamos con un conjunto de atributos sintetizados y otro de atributos heredados.
 * cada vez que se procesa la gramatica, los atributos sintetizados pasan a ser hereados
 * 
 * Forma de uso:
 * 
 *  1) Para leer y/o setear un atributo sintetizado
 *  
 * 		AttributeManager.getCurrent().getSynthesizedAttribute(AttributeType.Type)
 * 		AttributeManager.getCurrent().setSynthesizedAttribute(new Attribute<Type>(AttributeType.Type, new Type()))
 * 
 *  2) Para obtener todos los atributos sintetizados y/o heredados actuales
 *  
 *  	AttributeManager.getCurrent().getCurrentSynthesizedAttributes();
 * 		AttributeManager.getCurrent().getCurrentInheritedAttributes();
 * 
 *  3) Antes de salir del metodo actual es necesario actualizar contexto al manager,
 *  es decir(los sintetizados pasan a ser heredados, y los sintetizados se resetean)
 *  
 *     AttributeManager.getCurrent().loadContext();
 *    
 *  4) cuando se termina la ejecucion de algun metodo de la edt hay que restaurar el contexto actual   
 *     
 *     AttributeManager.getCurrent().unloadContext();
 */
public interface IAttributeManager {

	public Map<AttributeType, Attribute<?>> getCurrentSynthesizedAttributes();
	public Map<AttributeType, Attribute<?>> getCurrentInheritedAttributes();
	public void loadContext();
	public void unloadContext();
	public void setSynthesizedAttribute(Attribute<?> attribute);
	public Attribute<?> getSynthesizedAttribute(AttributeType type);
	public void setInheritedAttribute(Attribute<?> attribute);
	public Attribute<?> getInheritedAttribute(AttributeType type);
}
