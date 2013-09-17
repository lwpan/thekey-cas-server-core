package org.ccci.gto.cas.facebook;

import me.thekey.cas.service.UserManager;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.GcxUserAlreadyExistsException;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.federation.AbstractFederationProcessor;
import org.ccci.gto.cas.federation.FederationException;
import org.jasig.cas.authentication.principal.Credentials;

import com.restfb.types.User;

public class FacebookFederationProcessor extends AbstractFederationProcessor {
    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof FacebookCredentials;
    }

    private void unlinkExistingLinkedIdentities(final String facebookId) throws GcxUserNotFoundException {
        final UserManager userService = this.getUserService();
        GcxUser user = userService.findUserByFacebookId(facebookId);
        while (user != null) {
            final GcxUser freshUser = userService.getFreshUser(user);
            freshUser.removeFacebookId(facebookId);
            userService.updateUser(freshUser, false, "FacebookFederationProcessor", user.getEmail());

            // check for any other user accounts linked to this Facebook ID
            user = userService.findUserByFacebookId(facebookId);
        }
    }

    @Override
    public boolean linkIdentity(final GcxUser user, final Credentials rawCredentials, final Number strength)
            throws FederationException {
        final FacebookCredentials credentials = (FacebookCredentials) rawCredentials;

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
            userService.updateUser(freshUser, false, "FacebookFederationProcessor", freshUser.getEmail());
            user.setFacebookId(facebookId, strength);

            return true;
        } catch (final GcxUserNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean createIdentity(final Credentials rawCredentials, final Number strength) throws FederationException {
        final FacebookCredentials credentials = (FacebookCredentials) rawCredentials;

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
        } catch (final GcxUserNotFoundException e) {
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
            final UserManager userService = this.getUserService();
            userService.createUser(user);
        } catch (final GcxUserAlreadyExistsException e) {
            return false;
        }

        return true;
    }
}
