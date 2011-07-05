package org.ccci.gto.cas.admin.response.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;

/**
 * <b>UserSearchResponse</b> is used to hold the results of an eDirectory based
 * search for {@link GcxUser} entities.
 *
 * @author Greg Crider  Nov 14, 2008  7:02:12 PM
 */
public class UserSearchResponse extends AbstractPaginatedResponse
{
    private static final long serialVersionUID = 5198620415498714638L ;

    /**
     * Return the current page as specified internally by the page number.
     * 
     * @return Jost those entries that reside within the specified page.
     */
    @SuppressWarnings("unchecked")
    public List<GcxUser> currentUsersPage()
    {
        return (List<GcxUser>)super.currentPage() ;
    }
    
    
    /**
     * Update the list of entries with the specified {@link GcxUser} object. Presumably,
     * the specified user already exists in the entry list, but has been changed. To
     * prevent having to repeat the task that created the initial list, this method
     * will locate the orignal entry and replace it with the one specified.
     * 
     * @param a_UpdatedUser New version of {@link GcxUser} to replace the one currently
     *        in the entries.
     */
    @SuppressWarnings("unchecked")
    public void udpateUserInEntries( GcxUser a_UpdatedUser )
    {
        // Make sure the update user is valid and this response has entries
        if ( ( a_UpdatedUser != null ) && ( this.getEntries() != null ) ) {
            boolean done = false ;
            // Iterate over the list until you find the original entry based on GUID, the replace it with the updated version
            for( int i=0; (!done) && (i<this.getEntries().size()); i++ ) {
                GcxUser user = (GcxUser)this.getEntries().get( i ) ;
                if ( ( StringUtils.isNotBlank( user.getGUID() ) ) && ( user.getGUID().equals( a_UpdatedUser.getGUID() ) ) ) {
                    ((List<GcxUser>)this.getEntries()).set( i, a_UpdatedUser ) ;
                    done = true ;
                    /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Located user in entries and update it" ) ;
                }
            }
        }

    }
}
