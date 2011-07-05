package org.ccci.gto.cas.admin.action;

import java.util.List;

import org.ccci.gcx.idm.core.model.impl.GcxUser;

/**
 * <b>AbstractUserUpdateAction</b> contains common functionality of all {@link Action} implementations
 * that are going to update or view a {@link GcxUser} in detail.
 *
 * @author Greg Crider  Dec 6, 2008  6:15:04 PM
 */
public class AbstractUserUpdateAction extends AbstractUserAction
{
    private static final long serialVersionUID = -477927593250881495L ;

    public List<String> getDomainsVisitedFormatted()
    {
	final GcxUser user = this.getModel();
	if (log.isTraceEnabled()) {
	    log.trace("***** DomainsVisited: " + user.getDomainsVisitedString());
	}
        
	return user.getDomainsVisited();
    }
    public void setDomainsVisitedFormatted( List<String> a_DomainsVisited )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** DomainsVisited: size(" + (( a_DomainsVisited != null ) ? a_DomainsVisited.size() : "?" ) + ") values(" + a_DomainsVisited + ")" ) ;

	this.getModel().setDomainsVisited(a_DomainsVisited);
    }

    
    public List<String> getDomainsVisitedAdditionalFormatted()
    {
	final GcxUser user = this.getModel();
	if (log.isTraceEnabled()) {
	    log.trace("***** DomainsVisitedAdditional: "
		    + user.getDomainsVisitedAdditionalString());
	}
        
	return user.getDomainsVisitedAdditional();
    }
    public void setDomainsVisitedAdditionalFormatted( List<String> a_DomainsVisitedAdditional )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** DomainsVisitedAdditional: size(" + (( a_DomainsVisitedAdditional != null ) ? a_DomainsVisitedAdditional.size() : "?" ) + ") values(" + a_DomainsVisitedAdditional + ")" ) ;

	this.getModel().setDomainsVisitedAdditional(a_DomainsVisitedAdditional);
    }

    
    public List<String> getGUIDAdditionalFormatted()
    {
	final GcxUser user = this.getModel();
	if (log.isTraceEnabled()) {
	    log.trace("***** GUIDAdditional: " + user.getGUIDAdditionalString());
	}
        
	return user.getGUIDAdditional();
    }
    public void setGUIDAdditionalFormatted( List<String> a_GUIDAdditional )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** GUIDAdditional: size(" + (( a_GUIDAdditional != null ) ? a_GUIDAdditional.size() : "?" ) + ") values(" + a_GUIDAdditional + ")" ) ;

	this.getModel().setGUIDAdditional(a_GUIDAdditional);
    }

}
