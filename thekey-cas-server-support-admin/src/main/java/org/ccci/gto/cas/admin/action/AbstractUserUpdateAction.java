package org.ccci.gto.cas.admin.action;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>AbstractUserUpdateAction</b> contains common functionality of all {@link Action} implementations
 * that are going to update or view a {@link GcxUser} in detail.
 *
 * @author Greg Crider  Dec 6, 2008  6:15:04 PM
 */
public class AbstractUserUpdateAction extends AbstractUserAction
{
    private static final long serialVersionUID = -477927593250881495L ;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractUserUpdateAction.class);

    public List<String> getDomainsVisitedFormatted()
    {
        final List<String> domains = new ArrayList<>(this.getModel().getDomainsVisited());

        if (LOG.isTraceEnabled()) {
            LOG.trace("***** DomainsVisited: {}", StringUtils.join(domains.toArray(), ", "));
        }

        return domains;
    }

    public void setDomainsVisitedFormatted( List<String> a_DomainsVisited )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** DomainsVisited: size(" + (( a_DomainsVisited != null ) ? a_DomainsVisited.size() : "?" ) + ") values(" + a_DomainsVisited + ")" ) ;

	this.getModel().setDomainsVisited(a_DomainsVisited);
    }

    
    public List<String> getDomainsVisitedAdditionalFormatted()
    {
        final List<String> domains = new ArrayList<>(this.getModel().getDomainsVisitedAdditional());

        if (LOG.isTraceEnabled()) {
            LOG.trace("***** DomainsVisitedAdditional: {}", StringUtils.join(domains.toArray(), ", "));
        }

        return domains;
    }

    public void setDomainsVisitedAdditionalFormatted( List<String> a_DomainsVisitedAdditional )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** DomainsVisitedAdditional: size(" + (( a_DomainsVisitedAdditional != null ) ? a_DomainsVisitedAdditional.size() : "?" ) + ") values(" + a_DomainsVisitedAdditional + ")" ) ;

	this.getModel().setDomainsVisitedAdditional(a_DomainsVisitedAdditional);
    }

    
    public List<String> getGUIDAdditionalFormatted()
    {
        final List<String> guids = new ArrayList<>(this.getModel().getGUIDAdditional());

        if (LOG.isTraceEnabled()) {
            LOG.trace("***** GUIDAdditional: {}", StringUtils.join(guids.toArray(), ", "));
	}
        
        return guids;
    }

    public void setGUIDAdditionalFormatted( List<String> a_GUIDAdditional )
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** GUIDAdditional: size(" + (( a_GUIDAdditional != null ) ? a_GUIDAdditional.size() : "?" ) + ") values(" + a_GUIDAdditional + ")" ) ;

	this.getModel().setGUIDAdditional(a_GUIDAdditional);
    }
}
