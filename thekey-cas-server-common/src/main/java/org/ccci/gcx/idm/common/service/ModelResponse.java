package org.ccci.gcx.idm.common.service;

import java.util.Collection;

import org.ccci.gcx.idm.common.model.ModelObject;


/**
 * <b>ModelResponse</b> is used to encapsulate a service response that
 * relates to the domain model.
 * 
 * @author Greg Crider  Oct 11, 2006  2:37:43 PM
 */
public class ModelResponse extends BusinessResponse 
{
    /** Response payload */
    private Object m_Result = null ;
    

    /**
     * Constructor for a single {@link ModelObject} payload.
     * 
     * @param a_Result {@link ModelObject} response.
     */
    public ModelResponse( ModelObject a_Result )
    {
        this.setResult( a_Result ) ;
    }
    
    
    /**
     * Constructor for a collection of {@link ModelObject}s in the payload.
     * 
     * @param a_Result {@link ModelObject} collection response.
     */
    public ModelResponse( Collection<ModelObject> a_Result )
    {
        this.setResult( a_Result ) ;
    }
    
    
    /**
     * Tests whether or not the model response is a collection.
     * 
     * @return <tt>True</tt> if the model response is a {@link Collection} as opposed to a
     *         single {@link ModelObject}.
     */
    public boolean isCollection()
    {
        boolean result = false ;
        
        if ( this.m_Result == null ) {
            throw new BusinessServiceException( "No model response has been set" ) ;
        }
        
        if ( this.m_Result instanceof Collection == true ) {
            result = true ;
        }
        
        return result ;
    }

    
    /**
     * Get the encapsulated payload as a single {@link ModelObject}.
     * 
     * @return {@link ModelObject} response.
     */
    public ModelObject getResultModel()
    {
        if ( ( this.m_Result != null ) && ( this.m_Result instanceof ModelObject == false ) ) {
            throw new BusinessServiceException( 
                    "ModelResponse is of type \"" + this.m_Result.getClass().getName() + "\" not of type \"ModelObject\"" ) ;
        }
        
        return (ModelObject)this.m_Result ;
    }
    /**
     * Set the {@link ModelObject} response.
     * 
     * @param a_Result {@link ModelObject} response.
     */
    public void setResult( ModelObject a_Result )
    {
        this.m_Result = a_Result ;
    }
    
    
    /**
     * Get the encapsulated payload as a {@link Collection} of {@link ModelObject}s.
     * 
     * @return {@link Collection} of {@link ModelObject}s.
     */
    @SuppressWarnings("unchecked")
    public Collection<ModelObject> getResultCollection()
    {
        if ( ( this.m_Result != null ) && ( this.m_Result instanceof Collection == false ) ) {
            throw new BusinessServiceException( 
                    "ModelResponse is of type \"" + this.m_Result.getClass().getName() + "\" not of type \"Collection\"" ) ;
        }
        
        return (Collection<ModelObject>)this.m_Result ;
    }
    /**
     * Set the {@link ModelObject} response {@link Collection}.
     * 
     * @param a_Result {@link ModelObject} response {@link Collection}.
     */
    public void setResult( Collection<ModelObject> a_Result )
    {
        this.m_Result = a_Result ;
    }
}
