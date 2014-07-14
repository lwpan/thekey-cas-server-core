package org.ccci.gto.cas.facebook;

import com.restfb.types.User;
import me.thekey.cas.service.UserAlreadyExistsException;
import me.thekey.cas.service.UserManager;
import me.thekey.cas.service.UserNotFoundException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.federation.AbstractFederationProcessor;
import org.ccci.gto.cas.federation.FederationException;
import org.ccci.gto.cas.federation.IdentityExistsFederationException;

public class FacebookFederationProcessor extends AbstractFederationProcessor<FacebookCredentials> {
    public FacebookFederationProcessor() {
        super(FacebookCredentials.class);
    }

    private void unlinkExistingLinkedIdentities(final String facebookId) throws UserNotFoundException {
        final UserManager userService = this.getUserService();
        GcxUser user = userService.findUserByFacebookId(facebookId);
        while (user != null) {
            final GcxUser freshUser = userService.getFreshUser(user);
            freshUser.removeFacebookId(facebookId);
            userService.updateUser(freshUser);

            // check for any other user accounts linked to this Facebook ID
            user = userService.findUserByFacebookId(facebookId);
        }
    }

    @Override
    protected boolean linkIdentityInternal(final GcxUser user, final FacebookCredentials credentials,
                                           final Number strength) throws FederationException {
        // prevent linking to an unverified account
        if (!user.isVerified()) {
            return false;
        }

        try {
            final User fbUser = credentials.getFbUser();
            if (fbUser == null) {
                return false;
            }

            // get the facebook id from the fbUser object
            final String facebookId = fbUser.getId();
            if (StringUtils.isBlank(facebookId)) {
                // TODO: throw an exception?
                return false;
            }

            // unlink any accounts this facebook id is currently linked to
            unlinkExistingLinkedIdentities(facebookId);

            // update the user with the new facebook id
            final UserManager userService = this.getUserService();
            final GcxUser freshUser = userService.getFreshUser(user);
            freshUser.setFacebookId(facebookId, strength);
            userService.updateUser(freshUser);
            user.setFacebookId(facebookId, strength);

            return true;
        } catch (final UserNotFoundException e) {
            return false;
        }
    }

    @Override
    protected final boolean createIdentityInternal(final FacebookCredentials credentials,
                                                   final Number strength) throws FederationException {
        final User fbUser = credentials.getFbUser();
        if (fbUser == null) {
            return false;
        }

        // fetch all the attributes needed for creating an account
        final String facebookId = fbUser.getId();
        final String email = fbUser.getEmail();
        final String firstName = fbUser.getFirstName();
        final String lastName = fbUser.getLastName();
        // TODO: validate the email address??
        if (StringUtils.isBlank(facebookId) || StringUtils.isBlank(email)) {
            // TODO: throw an exception
            return false;
        }

        // unlink the facebookId from any previously linked identities
        try {
            unlinkExistingLinkedIdentities(facebookId);
        } catch (final UserNotFoundException e) {
            return false;
        }

        // create a new user
        final GcxUser user = new GcxUser();
        user.setEmail(email);
        user.setFacebookId(facebookId, strength);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordAllowChange(true);
        user.setForcePasswordChange(true);
        user.setLoginDisabled(false);
        user.setVerified(false);

        try {
            this.getUserService().createUser(user);
            return true;
        } catch (final UserAlreadyExistsException e) {
            throw new IdentityExistsFederationException(new Object[] { StringEscapeUtils.escapeHtml(email) });
        }
    }
}
