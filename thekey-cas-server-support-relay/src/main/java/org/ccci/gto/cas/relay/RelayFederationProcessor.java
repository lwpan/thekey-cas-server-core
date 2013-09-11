package org.ccci.gto.cas.relay;

import static org.ccci.gto.cas.Constants.VALIDGUIDREGEX;
import static org.ccci.gto.cas.relay.Constants.ATTR_FIRSTNAME;
import static org.ccci.gto.cas.relay.Constants.ATTR_GUID;
import static org.ccci.gto.cas.relay.Constants.ATTR_LASTNAME;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.GcxUserAlreadyExistsException;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.federation.AbstractFederationProcessor;
import org.ccci.gto.cas.federation.FederationException;
import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
import org.ccci.gto.cas.util.RandomGUID;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelayFederationProcessor extends AbstractFederationProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(RelayFederationProcessor.class);

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof CasCredentials;
    }

    private void unlinkExistingLinkedIdentities(final String guid) throws GcxUserNotFoundException {
        final GcxUserService userService = this.getUserService();
        GcxUser user = userService.findUserByRelayGuid(guid);
        while (user != null) {
            final GcxUser freshUser = userService.getFreshUser(user);
            freshUser.removeRelayGuid(guid);
            userService.updateUser(freshUser, false, "RelayFederationProcessor", user.getEmail());

            // check for any other user accounts linked to this Relay GUID
            user = userService.findUserByRelayGuid(guid);
        }
    }

    @Override
    public boolean linkIdentity(final GcxUser user, final Credentials rawCredentials, final Number strength)
            throws FederationException {
        final CasCredentials credentials = (CasCredentials) rawCredentials;
        final GcxUserService userService = this.getUserService();
        try {
            final Assertion assertion = credentials.getAssertion();
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
            userService.updateUser(freshUser, false, "RelayFederationProcessor", freshUser.getEmail());
            user.setRelayGuid(guid, strength);

            // return success
            return true;
        } catch (final GcxUserNotFoundException e) {
            // this error should never occur, because we are processing a user
            // account that was just loaded, but log it just in case
            LOG.error("Unexpected error while linking identities", e);
            return false;
        }
    }

    @Override
    public boolean createIdentity(final Credentials rawCredentials, final Number strength) throws FederationException {
        final CasCredentials credentials = (CasCredentials) rawCredentials;

        // get the Relay CAS Assertion out of the CasCredentials
        final Assertion assertion = credentials.getAssertion();
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
        } catch (final GcxUserNotFoundException e) {
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

        // try a different guid if a user already exists
        final GcxUserService userService = this.getUserService();
        if (userService.doesUserExist(user)) {
            user.setGUID(RandomGUID.generateGuid(true));
        }

        // create the new user
        try {
            userService.createUser(user, "RelayFederationProcessor", false, null);
            return true;
        } catch (final GcxUserAlreadyExistsException e) {
            throw new RelayIdentityExistsFederationException(new Object[] { StringEscapeUtils.escapeHtml(email) });
        }
    }
}
