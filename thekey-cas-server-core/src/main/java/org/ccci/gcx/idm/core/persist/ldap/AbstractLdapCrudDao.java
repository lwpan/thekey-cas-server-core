package org.ccci.gcx.idm.core.persist.ldap;

import java.io.Serializable;

import javax.naming.directory.Attributes;

import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.util.LdapUtil;
import org.ccci.gto.cas.persist.ldap.bind.AttributeBind;
import org.ccci.gto.persist.AbstractCrudDao;
import org.ccci.gto.persist.CrudDao;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;

/**
 * <b>AbstractLdapCrudDao</b> contains common functionality used by all concrete
 * implementations of {@link CrudDao} for use with LDAP. This DAO is intended
 * for use with one distinct ModelObject implementation. The dependencies for
 * this class are explained as follows:
 * <p>
 * <ul>
 * <li> <tt>attributeBind</tt> - is the {@link AttributeBind} implementation used
 * to marshal a base ModelObject entity into it's respective LDAP
 * {@link Attributes} representation.
 * <li> <tt>ldapTemplate</tt> - is the Spring-LDAP {@link LdapTemplate} used to
 * access the LDAP server's context.
 * <li> <tt>modelDN</tt> - is the base DN used to bind on a single LDAP entry for
 * the base ModelObject. It can contain subsitution variables which will be
 * injected with the properties found in the ModelObject entity (see
 * <tt>modelDNSubstitutionProperties</tt> below. Substitution variables are
 * indexed from <tt>0</tt> (zero) to <tt>n</tt> and take the form of
 * <tt>{n}</tt>. For instance you may have a template such as:
 * 
 * <pre>
 *     cn={0},ou=sso,dc=mygcx,dc=org
 * </pre>
 * 
 * where <tt>{0}</tt> will be substituted with the first property listed in
 * <tt>modelDNSubstitutionProperties</tt>.
 * <li> <tt>modelDNSubstitutionProperties</tt> is an ordered list of the
 * substitution properties from the base ModelObject. Each property is used to
 * substitute the value found in the base ModelObject with the indexed variable
 * found in <tt>modelDN</tt>.
 * </ul>
 * 
 * @author Greg Crider Oct 29, 2008 4:42:21 PM
 */
public abstract class AbstractLdapCrudDao<T> extends AbstractCrudDao<T> {
    /** Attribute binder to bind object to LDAP attributes. */
    private AttributeBind<T> m_AttributeBind = null;
    /** LDAP template to access context. */
    private LdapTemplate m_LdapTemplate = null ;
    /** Based DN for DAO's domain model object with subsitution patterns. */
    private String m_ModelDN = null ;
    /** The maximum number of allowed matches */
    private int m_MaxSearchResults = Constants.SEARCH_NO_LIMIT ;

    
    /**
     * @return the attributeBind
     */
    public AttributeBind<T> getAttributeBind()
    {
        return this.m_AttributeBind ;
    }
    /**
     * @param a_attributeBind the attributeBind to set
     */
    public void setAttributeBind(AttributeBind<T> a_attributeBind)
    {
        this.m_AttributeBind = a_attributeBind ;
    }

    
    /**
     * @return the ldapTemplate
     */
    public LdapTemplate getLdapTemplate()
    {
        return this.m_LdapTemplate ;
    }
    /**
     * @param a_ldapTemplate the ldapTemplate to set
     */
    public void setLdapTemplate( LdapTemplate a_ldapTemplate )
    {
        this.m_LdapTemplate = a_ldapTemplate ;
    }

    
    /**
     * @return the modelDN
     */
    public String getModelDN()
    {
        return this.m_ModelDN ;
    }
    /**
     * @param a_modelDN the modelDN to set
     */
    public void setModelDN( String a_modelDN )
    {
        this.m_ModelDN = a_modelDN ;
    }

    /**
     * @return the maxSearchResults
     */
    public int getMaxSearchResults()
    {
        return this.m_MaxSearchResults ;
    }
    /**
     * @param a_maxSearchResults the maxSearchResults to set
     */
    public void setMaxSearchResults( int a_maxSearchResults )
    {
        this.m_MaxSearchResults = a_maxSearchResults ;
    }

    /**
     * Generate a DN for the specified object
     * 
     * @param object
     *            {@link Object} to be used in subsititution.
     * 
     * @return Fully qualified DN.
     */
    protected abstract DistinguishedName generateModelDN(final T object);

    /**
     * @param object
     * @see org.ccci.gcx.idm.common.persist.CrudDao#delete(java.lang.Object)
     */
    @Override
    public void delete(final T object) {
	final DistinguishedName generatedDN = this.generateModelDN(object);

	if (log.isDebugEnabled()) {
	    log.debug("***** Preparing to recursively delete entry:");
	    log.debug("***** \tDN: " + generatedDN);
	}

	this.getLdapTemplate().unbind(generatedDN, true);
    }

    /**
     * @param object
     * @see CrudDao#save(ModelObject)
     */
    @Override
    public void save(final T object) {
	this.assertValidObject(object);
	final DistinguishedName generatedDN = this.generateModelDN(object);
	Attributes attr = this.getAttributeBind().build(object);
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) {
            log.debug( "***** Preparing to bind new entry:" ) ;
            log.debug( "***** \tDN: " + generatedDN ) ;
            log.debug( "***** \tAttributes: " + LdapUtil.attributesToString( attr ) ) ;
        }
        
        this.getLdapTemplate().bind( generatedDN, null, attr ) ;
    }

    /**
     * @param object
     * @see org.ccci.gcx.idm.common.persist.CrudDao#update(java.lang.Object)
     */
    @Override
    public void update(final T object) {
	this.assertValidObject(object);
	this.updateInternal(this.generateModelDN(object), object);
    }

    protected void updateInternal(final DistinguishedName dn, final T object) {
	this.assertValidObject(object);
	if (log.isDebugEnabled()) {
	    log.debug("***** Preparing to udpate new entry:");
	    log.debug("***** \tDN: " + dn);
	    log.debug("***** \tAttributes: "
		    + LdapUtil.attributesToString(this.getAttributeBind()
			    .build(object)));
	}

	final LdapTemplate template = this.getLdapTemplate();
	final DirContextOperations ctx = template.lookupContext(dn);
	this.getAttributeBind().mapToContext(object, ctx);

	template.modifyAttributes(ctx);
    }
}
