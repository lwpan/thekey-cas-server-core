package org.ccci.gcx.idm.core.service.impl;

import static org.ccci.gcx.idm.core.Constants.INTERNAL_CREATEDBY;
import static org.ccci.gcx.idm.core.Constants.INTERNAL_SOURCE;

import me.thekey.cas.service.UserManager;
import me.thekey.cas.service.UserNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.persist.GcxUserDao;
import org.ccci.gto.cas.service.audit.AuditException;
import org.ccci.gto.cas.util.RandomPasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * <b>AbstractGcxUserService</b> contains common functionality for all concrete implementations of
 * {@link UserManager}.
 *
 * @author Greg Crider  Oct 21, 2008  1:14:00 PM
 */
public abstract class AbstractGcxUserService extends AbstractAuditableService implements UserManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGcxUserService.class);

    /** Random password generator */
    private RandomPasswordGenerator m_RandomPasswordGenerator = null ;
    /** Length of newly generated password */
    private int m_NewPasswordLength = 0 ;
    
    @NotNull
    private GcxUserDao userDao;
    
    /**
     * Return the maximum number of allowed results.
     * 
     * @return Number of maximum allowed search results or 0 (<tt>SEARCH_NO_LIMIT</tt>) if
     *         there is no imposed limit.
     */
    public int getMaxSearchResults()
    {
	return this.getUserDao().getMaxSearchResults();
    }

    /**
     * @return the randomPasswordGenerator
     */
    public RandomPasswordGenerator getRandomPasswordGenerator()
    {
        return this.m_RandomPasswordGenerator ;
    }
    /**
     * @param a_randomPasswordGenerator the randomPasswordGenerator to set
     */
    public void setRandomPasswordGenerator( RandomPasswordGenerator a_randomPasswordGenerator )
    {
        this.m_RandomPasswordGenerator = a_randomPasswordGenerator ;
    }


    /**
     * @return the newPasswordLength
     */
    public int getNewPasswordLength()
    {
        return this.m_NewPasswordLength ;
    }
    /**
     * @param a_newPasswordLength the newPasswordLength to set
     */
    public void setNewPasswordLength( int a_newPasswordLength )
    {
        this.m_NewPasswordLength = a_newPasswordLength ;
    }

    /**
     * @param userDao
     *            the userDao to use
     */
    public void setUserDao(final GcxUserDao userDao) {
	this.userDao = userDao;
    }

    /**
     * @return the userDao to use
     */
    protected GcxUserDao getUserDao() {
	return this.userDao;
    }

    /**
     * Validate the integrity of the specified {@link GcxUser} object. If there are
     * problems, such as those created when a user is manually updated outside of the
     * applications, we attempt to repair them here, and then save the new version.
     *
     * @param user {@link GcxUser} to be validated and repaired.
     */
    protected void validateUserIntegrity(final GcxUser user) {
        if (user != null) {
            LOG.trace("***** Validating user: {}", user);

            /*
             * If the user is not deactivated, but the e-mail and userid aren't equal, reset the userid
             * so that it matches the e-mail.
             */
            if ((!user.isDeactivated()) && (StringUtils.isNotBlank(user.getEmail())) && (!user.getEmail().equals(user
                    .getUserid()))) {
                LOG.trace("***** Repairing user: {}", user);
                final GcxUser original = user.clone();
                user.setUserid(user.getEmail());
                this.getUserDao().update(user);

                // Audit the change
                LOG.warn("The user failed the integrity test and was modified\n\t:user: {}", user);
                try {
                    this.getAuditService().update(INTERNAL_SOURCE, INTERNAL_CREATEDBY, user.getEmail(),
                            "Repairing GCX User integrity", original, user);
                } catch (final AuditException e) {
                    // log any audit errors, but suppress them because audits are not critical functionality
                    LOG.error("error auditing update", e);
                }
            }
        }
    }
    
    
    /**
     * Validate the {@link List} of {@link GcxUser} objects. If an object has
     * a problem, such as those created when a user is manually updated outside of the
     * applications, we attempt to repair them here, and then save the new version.
     *
     * @param users {@link List} of {@link GcxUser} objects to be validated and repaired.
     */
    protected void validateUserIntegrity(final Collection<GcxUser> users) {
        if (users != null) {
            for (final GcxUser user : users) {
                this.validateUserIntegrity(user);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GcxUser getFreshUser(final GcxUser original)
	    throws UserNotFoundException {
	Assert.notNull(original);
	// attempt retrieving the fresh object using the original object's guid
	final GcxUser fresh = this.findUserByGuid(original.getGUID());

	// throw an error if the guid wasn't found
	if (fresh == null) {
	    throw new UserNotFoundException(
		    "Cannot find a fresh instance of the specified user");
	}

	// return the fresh user object
	return fresh;
    }
}
