package org.ccci.gto.cas.admin.response.impl;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;

/**
 * <b>UserSearchResponse</b> is used to hold the results of an eDirectory based
 * search for {@link GcxUser} entities.
 *
 * @author Greg Crider  Nov 14, 2008  7:02:12 PM
 */
public class UserSearchResponse extends AbstractPaginatedResponse<GcxUser> {
    private static final long serialVersionUID = 5198620415498714638L ;

    /**
     * Update the list of entries with the specified {@link GcxUser} object. Presumably,
     * the specified user already exists in the entry list, but has been changed. To
     * prevent having to repeat the task that created the initial list, this method
     * will locate the orignal entry and replace it with the one specified.
     * 
     * @param updated New version of {@link GcxUser} to replace the one currently
     *        in the entries.
     */
    public void updateUserInEntries(final GcxUser updated) {
	// Make sure the updated user is valid and this response has entries
	if (updated != null && this.getEntries() != null) {
	    // Iterate over the list until you find the original entry based on
	    // GUID, the replace it with the updated version
	    for (int i = 0; i < this.getEntries().size(); i++) {
		final GcxUser user = this.getEntries().get(i);
		if (StringUtils.isNotBlank(user.getGUID())
			&& user.getGUID().equals(updated.getGUID())) {
		    log.debug("***** Located user in entries and updated it");
		    this.getEntries().set(i, updated);
		    return;
		}
	    }
	}
    }
}
