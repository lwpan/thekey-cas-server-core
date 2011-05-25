package org.ccci.gcx.idm.common.model.type;


/**
 * <b>AbstractAlphaNumericTypeCode</b> represents a {@link TypeCode} whose
 * constituent codes are alpha/numeric, and can consequenlty be bound to
 * a string.
 *
 * @author Greg Crider  Mar 28, 2007  1:12:37 PM
 */
public abstract class AbstractAlphaNumericTypeCode extends AbstractTypeCode
{
    private static final long serialVersionUID = -8573100644950237329L ;


    /**
     * Constructor when code and label are known.
     * 
     * @param a_Type Type value.
     * @param a_Code Code value.
     * @param a_Label Label value.
     */
    protected AbstractAlphaNumericTypeCode( String a_Type, String a_Code, String a_Label )
    {
        super( a_Type, a_Code, a_Label ) ;
    }

    
    /**
     * Constructor when all elements are known.
     * 
     * @param a_Type Type value.
     * @param a_Code Code value.
     * @param a_Label Label value.
     * @param a_Description Description value.
     */
    protected AbstractAlphaNumericTypeCode( String a_Type, String a_Code, String a_Label, String a_Description )
    {
        super( a_Type, a_Code, a_Label, a_Description ) ;
    }


    /**
     * Constructor when code and label are known.
     * 
     * @param a_TypeClass Type class value.
     * @param a_Code Code value.
     * @param a_Label Label value.
     */
    protected AbstractAlphaNumericTypeCode( Class<?> a_TypeClass, String a_Code, String a_Label )
    {
        super( a_TypeClass, a_Code, a_Label ) ;
    }

    
    /**
     * Constructor when all elements are known.
     * 
     * @param a_TypeClass Type class value.
     * @param a_Code Code value.
     * @param a_Label Label value.
     * @param a_Description Description value.
     */
    protected AbstractAlphaNumericTypeCode( Class<?> a_TypeClass, String a_Code, String a_Label, String a_Description )
    {
        super( a_TypeClass, a_Code, a_Label, a_Description ) ;
    }

    
    /**
     * Return code as a string.
     * 
     * @return Code.
     */
    public String codeToString()
    {
        return (String)this.getCode() ;
    }

}
