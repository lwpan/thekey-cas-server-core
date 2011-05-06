package org.ccci.gcx.idm.core.persist.ldap;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.common.persist.CrudDao;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;
import org.ccci.gcx.idm.core.persist.ldap.bind.AttributeBind;
import org.ccci.gcx.idm.core.util.LdapUtil;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.Filter;

/**
 * <b>AbstractLdapCrudDao</b> contains common functionality used by all concrete
 * implementations of {@link CrudDao} for use with LDAP. This DAO is intended for
 * use with one distinct {@link ModelObject} implementation. The dependencies for this
 * class are explained as follows:
 * <p>
 * <ul>
 * <li> <tt>attributeBind</tt> - is the {@link AttributeBind} implementation used to
 * marshal a base {@link ModelObject} entity into it's respective LDAP {@link Attributes}
 * representation.
 * <li> <tt>ldapTemplate</tt> - is the Spring-LDAP {@link LdapTemplate} used to access
 * the LDAP server's context.
 * <li> <tt>modelDN</tt> - is the base DN used to bind on a single LDAP entry for the
 * base {@link ModelObject}. It can contain subsitution variables which will be injected
 * with the properties found in the {@link ModelObject} entity (see <tt>modelDNSubstitutionProperties</tt>
 * below. Substitution variables are indexed from <tt>0</tt> (zero) to <tt>n</tt> and take
 * the form of <tt>{n}</tt>. For instance you may have a template such as:
 * <pre>
 *     cn={0},ou=sso,dc=mygcx,dc=org
 * </pre>
 * where <tt>{0}</tt> will be substituted with the first property listed in
 * <tt>modelDNSubstitutionProperties</tt>.
 * <li> <tt>modelDNSubstitutionProperties</tt> is an ordered list of the substitution
 * properties from the base {@link ModelObject}. Each property is used to substitute
 * the value found in the base {@link ModelObject} with the indexed variable found
 * in <tt>modelDN</tt>.
 * </ul>
 *
 * @author Greg Crider  Oct 29, 2008  4:42:21 PM
 */
public abstract class AbstractLdapCrudDao implements CrudDao
{
    protected static final Log log = LogFactory.getLog( AbstractLdapCrudDao.class ) ;

    /** Attribute binder to bind object to LDAP attributes. */
    private AttributeBind m_AttributeBind = null ;
    /** LDAP template to access context. */
    private LdapTemplate m_LdapTemplate = null ;
    /** Based DN for DAO's domain model object with subsitution patterns. */
    private String m_ModelDN = null ;
    /** Ordered list of property names from domain object used as subsitution variables in ModelDN. */
    private List<String> m_ModelDNSubstitutionProperties = null ;
    /** The maximum number of allowed matches */
    private int m_MaxSearchResults = Constants.SEARCH_NO_LIMIT ;

    
    /**
     * @return the attributeBind
     */
    public AttributeBind getAttributeBind()
    {
        return this.m_AttributeBind ;
    }
    /**
     * @param a_attributeBind the attributeBind to set
     */
    public void setAttributeBind( AttributeBind a_attributeBind )
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
     * @return the modelDNSubstitutionProperties
     */
    public List<String> getModelDNSubstitutionProperties()
    {
        return this.m_ModelDNSubstitutionProperties ;
    }
    /**
     * @param a_modelDNSubstitutionProperties the modelDNSubstitutionProperties to set
     */
    public void setModelDNSubstitutionProperties( List<String> a_modelDNSubstitutionProperties )
    {
        this.m_ModelDNSubstitutionProperties = a_modelDNSubstitutionProperties ;
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
     * Generate a DN based on the substitution pattern found in <tt>ModelDN</tt> with
     * the property values specified in <tt>ModelDNSubstitutionProperties</tt> using
     * the specified {@link ModelObject}.
     * 
     * @param a_Object {@link Object} to be used in subsititution.
     * 
     * @return Fully qualified DN.
     */
    protected String generateModelDN( Object a_Object )
    {
        return LdapUtil.generateModelDNFromPattern( a_Object, this.getModelDN(), this.getModelDNSubstitutionProperties() ) ;
    }
   
    
    /**
     * @param a_Object
     * @see org.ccci.gcx.idm.common.persist.CrudDao#delete(java.lang.Object)
     */
    public void delete( Object a_Object )
    {
        String generatedDN = this.generateModelDN( a_Object ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) {
            log.debug( "***** Preparing to recursively delete entry:" ) ;
            log.debug( "***** \tDN: " + generatedDN ) ;
        }
        
        this.getLdapTemplate().unbind( generatedDN, true ) ;
    }

    
    /**
     * @param a_Object
     * @return
     * @see org.ccci.gcx.idm.common.persist.CrudDao#save(java.lang.Object)
     */
    public Serializable save( Object a_Object )
    {
        String generatedDN = this.generateModelDN( a_Object ) ;
        Attributes attr = this.getAttributeBind().build( (ModelObject)a_Object ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) {
            log.debug( "***** Preparing to bind new entry:" ) ;
            log.debug( "***** \tDN: " + generatedDN ) ;
            log.debug( "***** \tAttributes: " + LdapUtil.attributesToString( attr ) ) ;
        }
        
        this.getLdapTemplate().bind( generatedDN, null, attr ) ;
        
        // To comply with the interface, we are just wrapping back the original object
        return (Serializable)a_Object ;
    }

    
    /**
     * @param a_ModelObjects
     * @see org.ccci.gcx.idm.common.persist.CrudDao#saveAll(java.util.Collection)
     */
    public void saveAll( Collection<ModelObject> a_ModelObjects )
    {
        // TODO Auto-generated method stub

    }

    
    /**
     * @param a_Object
     * @see org.ccci.gcx.idm.common.persist.CrudDao#saveOrUpdate(java.lang.Object)
     */
    public void saveOrUpdate( Object a_Object )
    {
        throw new UnsupportedOperationException( "This method is not currently implemented" ) ;
    }

    
    /**
     * @param a_Object
     * @see org.ccci.gcx.idm.common.persist.CrudDao#update(java.lang.Object)
     */
    public void update( Object a_Object )
    {
        String generatedDN = this.generateModelDN( a_Object ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) {
            log.debug( "***** Preparing to udpate new entry:" ) ;
            log.debug( "***** \tDN: " + generatedDN ) ;
	    log.debug("***** \tAttributes: "
		    + LdapUtil.attributesToString(this.getAttributeBind()
			    .build((ModelObject) a_Object)));
        }
        
        DirContextOperations ctx = this.getLdapTemplate().lookupContext( generatedDN ) ;
        this.getAttributeBind().mapToContext( (ModelObject)a_Object, ctx ) ;
        
        this.getLdapTemplate().modifyAttributes( ctx ) ;
    }

    
    /**
     * @param a_Object
     * @return
     * @see org.ccci.gcx.idm.common.persist.QueryDao#initialize(java.lang.Object)
     */
    public Object initialize( Object a_Object )
    {
        throw new UnsupportedOperationException( "This method is not currently implemented" ) ;
    }

    
    /**
     * @param a_Object
     * @return
     * @see org.ccci.gcx.idm.common.persist.QueryDao#isInitialized(java.lang.Object)
     */
    public boolean isInitialized( Object a_Object )
    {
        throw new UnsupportedOperationException( "This method is not currently implemented" ) ;
    }

    
    /**
     * @param a_Key
     * @return
     * @see org.ccci.gcx.idm.common.persist.QueryDao#load(java.io.Serializable)
     */
    public Object load( Serializable a_Key )
    {
        return this.get( a_Key ) ;
    }
    
    
    /**
     * Perform the specified search and assert that the maximum allowed results is not exceeded.
     * 
     * @param a_BaseDN Base DN for lookup
     * @param a_Filter Filter for matching criteria
     * @param a_Mapper Mapper for resulting matches 
     * 
     * @exception ExceededMaximumAllowedResults if the search would result in too many matches.
     */
    protected void assertResultSize( String a_BaseDN, Filter a_Filter, AttributesMapper a_Mapper )
    {
        if ( this.m_MaxSearchResults != Constants.SEARCH_NO_LIMIT ) {
        // Define search controls
            SearchControls searchControls = new SearchControls() ;
            searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE ) ;

            // Do a search with a limit of 1 entry
	    PagedResultsDirContextProcessor pager = new PagedResultsDirContextProcessor(
		    1);

            // Execute the search; we don't care about the results
	    this.getLdapTemplate().search(a_BaseDN, a_Filter.encode(),
		    searchControls, a_Mapper, pager);

            // Now we can look at the response control to determine how many
            // entries it thinks there are
            if ( pager.getResultSize() > this.m_MaxSearchResults ) {
                String error = "Search exceeds max allowed results of " + this.m_MaxSearchResults + ":\n\tBaseDN(" + a_BaseDN + ") Filter(" + a_Filter.encode() + ")\n\tResult Size(" + pager.getResultSize() + ")" ;
                /*= ERROR =*/ log.error( error ) ;
                throw new ExceededMaximumAllowedResults( error ) ;
            } else {
                /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Result Size: " + pager.getResultSize() ) ;
            }
        }
    }

}