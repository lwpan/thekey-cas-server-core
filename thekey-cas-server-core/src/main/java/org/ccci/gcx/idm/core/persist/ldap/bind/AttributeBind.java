package org.ccci.gcx.idm.core.persist.ldap.bind;

import javax.naming.directory.Attributes;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.springframework.ldap.core.DirContextOperations;

/**
 * <b>AttributeBind</b> defines functionality used to bind a {@link ModelObject}
 * to a corresponding LDAP {@link Attributes} representation.
 *
 * @author Greg Crider  Oct 29, 2008  2:32:07 PM
 */
public interface AttributeBind
{

    /**
     * Construct the LDAP {@link Attributes} representation of the specified {@link ModelObject}. This
     * is intended for use with bind() operations.
     * 
     * @param a_ModelObject {@link ModelObject} to be converted to an {@link Attributes} representation.
     * 
     * @return {@link Attributes} representation of the specified {@link ModelObject}.
     */
    public Attributes build( ModelObject a_ModelObject ) ;
    
    
    /**
     * Map the specified objects values to the context. This method is intended for use with entry
     * modifications.
     * 
     * @param a_ModelObject {@link ModelObject} to be mapped.
     * @param a_DirContextOperations Context where {@link ModelObject} should be mapped.
     */
    public void mapToContext( ModelObject a_ModelObject, DirContextOperations a_DirContextOperations ) ;
    
}
