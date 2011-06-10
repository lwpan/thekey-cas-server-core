package org.ccci.gcx.idm.web.admin.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>AbstractUserUpdateAction</b> contains common functionality of all {@link Action} implementations
 * that are going to update or view a {@link GcxUser} in detail.
 *
 * @author Greg Crider  Dec 6, 2008  6:15:04 PM
 */
public class AbstractUserUpdateAction extends AbstractUserAction
{
    private static final long serialVersionUID = -477927593250881495L ;

    protected static final Log log = LogFactory.getLog( AbstractUserUpdateAction.class ) ;

    
    public List<String> getDomainsVisitedFormatted()
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** DomainsVisited: " + this.getGcxUser().getDomainsVisitedString() ) ;
        
        return this.getGcxUser().getDomainsVisited() ;
    }
    public void setDomainsVisitedFormatted( List<String> a_DomainsVisited )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** DomainsVisited: size(" + (( a_DomainsVisited != null ) ? a_DomainsVisited.size() : "?" ) + ") values(" + a_DomainsVisited + ")" ) ;

        this.getGcxUser().setDomainsVisited( a_DomainsVisited ) ;
    }

    
    public List<String> getDomainsVisitedAdditionalFormatted()
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** DomainsVisitedAdditional: " + this.getGcxUser().getDomainsVisitedAdditionalString() ) ;
        
        return this.getGcxUser().getDomainsVisitedAdditional() ;
    }
    public void setDomainsVisitedAdditionalFormatted( List<String> a_DomainsVisitedAdditional )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** DomainsVisitedAdditional: size(" + (( a_DomainsVisitedAdditional != null ) ? a_DomainsVisitedAdditional.size() : "?" ) + ") values(" + a_DomainsVisitedAdditional + ")" ) ;

        this.getGcxUser().setDomainsVisitedAdditional( a_DomainsVisitedAdditional ) ;
    }

    
    public List<String> getGUIDAdditionalFormatted()
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** GUIDAdditional: " + this.getGcxUser().getGUIDAdditionalString() ) ;
        
        return this.getGcxUser().getGUIDAdditional() ;
    }
    public void setGUIDAdditionalFormatted( List<String> a_GUIDAdditional )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** GUIDAdditional: size(" + (( a_GUIDAdditional != null ) ? a_GUIDAdditional.size() : "?" ) + ") values(" + a_GUIDAdditional + ")" ) ;

        this.getGcxUser().setGUIDAdditional( a_GUIDAdditional ) ;
    }

}
