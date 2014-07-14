package org.ccci.gto.cas.relay;

import static me.thekey.cas.relay.Constants.CREDS_ATTR_CAS_ASSERTION;
import static org.ccci.gto.cas.Constants.VALIDGUIDREGEX;
import static org.ccci.gto.cas.relay.Constants.ATTR_FIRSTNAME;
import static org.ccci.gto.cas.relay.Constants.ATTR_GUID;
import static org.ccci.gto.cas.relay.Constants.ATTR_LASTNAME;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import me.thekey.cas.service.UserAlreadyExistsException;
import me.thekey.cas.service.UserManager;
import me.thekey.cas.service.UserNotFoundException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.federation.AbstractFederationProcessor;
import org.ccci.gto.cas.federation.FederationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RelayFederationProcessor extends AbstractFederationProcessor<TheKeyCredentials> {
    private static final Logger LOG = LoggerFactory.getLogger(RelayFederationProcessor.class);

    public RelayFederationProcessor() {
        super(TheKeyCredentials.class);
    }

    @Override
    protected boolean supportsInternal(final TheKeyCredentials credentials) {
        return credentials.getAttribute(CREDS_ATTR_CAS_ASSERTION) != null;
    }

    private void unlinkExistingLinkedIdentities(final String guid) throws UserNotFoundException {
        final UserManager userService = this.getUserService();
        GcxUser user = userService.findUserByRelayGuid(guid);
        while (user != null) {
            final GcxUser freshUser = userService.getFreshUser(user);
            freshUser.removeRelayGuid(guid);
            userService.updateUser(freshUser);

            // check for any other user accounts linked to this Relay GUID
            user = userService.findUserByRelayGuid(guid);
        }
    }

    @Override
    protected boolean linkIdentityInternal(final GcxUser user, final TheKeyCredentials credentials,
                                           final Number strength) throws FederationException {
        // prevent linking to an unverified account
        if (!user.isVerified()) {
            return false;
        }

        final UserManager userService = this.getUserService();
        try {
            final Assertion assertion = credentials.getAttribute(CREDS_ATTR_CAS_ASSERTION, Assertion.class);
            if (assertion == null) {
                return false;
            }

            // get the guid from the Relay CAS Assertion
            final String guid = (String) assertion.getPrincipal().getAttributes().get(ATTR_GUID);
            if (StringUtils.isBlank(guid)) {
                // TODO: throw an exception
                return false;
            }

            // unlink the relayGuid from any previously linked identities
            unlinkExistingLinkedIdentities(guid);

            // update the user with the new relayGuid
            final GcxUser freshUser = userService.getFreshUser(user);
            freshUser.setRelayGuid(guid, strength);
            userService.updateUser(freshUser);
            user.setRelayGuid(guid, strength);

            // return success
            return true;
        } catch (final UserNotFoundException e) {
            // this error should never occur, because we are processing a user
            // account that was just loaded, but log it just in case
            LOG.error("Unexpected error while linking identities", e);
            return false;
        }
    }

    @Override
    protected boolean createIdentityInternal(final TheKeyCredentials credentials,
                                             final Number strength) throws FederationException {
        // get the Relay CAS Assertion out of the CasCredentials
        final Assertion assertion = credentials.getAttribute(CREDS_ATTR_CAS_ASSERTION, Assertion.class);
        if (assertion == null) {
            return false;
        }

        // fetch all the attributes needed for creating an account out of the
        // Assertion
        final AttributePrincipal principal = assertion.getPrincipal();
        final Map<String, Object> principalAttributes = principal.getAttributes();
        final String email = principal.getName();
        final String guid = (String) principalAttributes.get(ATTR_GUID);
        final String firstName = (String) principalAttributes.get(ATTR_FIRSTNAME);
        final String lastName = (String) principalAttributes.get(ATTR_LASTNAME);
        // TODO: validate the email address??
        if (guid == null || !VALIDGUIDREGEX.matcher(guid.toUpperCase()).matches() || StringUtils.isBlank(email)) {
            // TODO: throw an exception
            return false;
        }

        // unlink the relayGuid from any previously linked identities
        try {
            unlinkExistingLinkedIdentities(guid);
        } catch (final UserNotFoundException e) {
            return false;
        }

        // create a new user object (attempt to keep the guids identical)
        final GcxUser user = new GcxUser();
        user.setGUID(guid);
        user.setEmail(email);
        user.setRelayGuid(guid, strength);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordAllowChange(true);
        user.setForcePasswordChange(true);
        user.setLoginDisabled(false);
        user.setVerified(false);

        // create the new user
        try {
            this.getUserService().createUser(user);
            return true;
        } catch (final UserAlreadyExistsException e) {
            throw new RelayIdentityExistsFederationException(new Object[] { StringEscapeUtils.escapeHtml(email) });
        }
    }
}
