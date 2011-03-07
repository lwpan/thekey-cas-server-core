package org.ccci.gcx.idm.common.model.type;

import java.io.Serializable;


/**
 * <b>TypeCode</b> is used to encapsulate a code value, it's associated label
 * (for GUI display), and a detailed description.
 *
 * @author Greg Crider  Mar 28, 2007  1:12:37 PM
 */
public interface TypeCode extends Serializable, Comparable<Object>
{
    /**
     * Return a String representation of the code.
     * 
     * @return String representation.
     */
    public String codeToString() ;
 
    
    /**
     * Return the type of code.
     * 
     * @return Type value.
     */
    public String getType() ;
    
    /**
     * Get the underlying code value.
     * 
     * @return Code value.
     */
    public Object getCode() ;
    
    
    /**
     * Get the associated GUI label.
     * 
     * @return GUI label.
     */
    public String getLabel() ;
    
    
    /**
     * Get the associated description.
     * 
     * @return Description.
     */
    public String getDescription() ;

}
