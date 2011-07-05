package org.ccci.gto.cas.admin.response.impl;

import org.ccci.gcx.idm.core.model.impl.GcxUser;

import com.opensymphony.xwork2.ActionContext;

/**
 * <b>FilteredUserSearchResponse</b> is an extension of {@link UserSearchResponse} but
 * will filter out the {@link GcxUser} held in the session from the list of results.
 *
 * @author Greg Crider  Dec 5, 2008  4:24:57 PM
 */
public class FilteredUserSearchResponse extends UserSearchResponse
{
    private static final long serialVersionUID = 3949127373277829115L ;

    /** Name of session variable that holds the user to be filtered out of the result list */
    public static final String SESSION_FILTERED_USER_OBJECT = "sessionfiltereduserobject" ;
    
    
    /**
     * Calculate the pagination details based on available information. If the filtered {@link GcxUser} object
     * is present in the <tt>entries</tt>, we will remove it here.
     */
    public void calculate()
    {
        // Recover the user to be filtered from the session
        GcxUser filteredUser = (GcxUser)ActionContext.getContext().getSession().get( FilteredUserSearchResponse.SESSION_FILTERED_USER_OBJECT ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Attempting to filter off user: " + filteredUser ) ;
        
        // Is the filtered user in the result set?
        if ( this.getEntries().contains( filteredUser ) ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** The filtered user \"" + filteredUser.getEmail() + "\" is in the results." ) ;
            this.getEntries().remove( filteredUser ) ;
        }
        
        // Okay, now we can perform the usual calculation stuff
        super.calculate() ;
    }
}
