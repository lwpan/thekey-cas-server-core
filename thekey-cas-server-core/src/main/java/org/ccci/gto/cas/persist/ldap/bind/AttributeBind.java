package org.ccci.gto.cas.persist.ldap.bind;

import javax.naming.directory.Attributes;

import org.springframework.ldap.core.DirContextOperations;

/**
 * <b>AttributeBind</b> defines functionality used to bind an object to a
 * corresponding LDAP {@link Attributes} representation.
 * 
 * @author Daniel Frett
 */
public interface AttributeBind<T> {
    /**
     * Construct the LDAP {@link Attributes} representation of the specified
     * object. This is intended for use with bind() operations.
     * 
     * @param object
     *            object to be converted to an {@link Attributes}
     *            representation.
     * 
     * @return {@link Attributes} representation of the specified object.
     */
    public Attributes build(final T object);

    /**
     * Map the specified objects values to the context. This method is intended
     * for use with entry modifications.
     * 
     * @param object
     *            object to be mapped.
     * @param ctx
     *            Context where object should be mapped.
     */
    public void mapToContext(final T object, final DirContextOperations ctx);
}
