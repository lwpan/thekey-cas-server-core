package org.ccci.gto.cas.relay;

import static me.thekey.cas.relay.Constants.CREDS_ATTR_CAS_ASSERTION;
import static org.ccci.gto.cas.Constants.VALIDGUIDREGEX;
import static org.ccci.gto.cas.relay.Constants.ATTR_FIRSTNAME;
import static org.ccci.gto.cas.relay.Constants.ATTR_GUID;
import static org.ccci.gto.cas.relay.Constants.ATTR_LASTNAME;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import me.thekey.cas.federation.LinkedIdentitySyncService;
import me.thekey.cas.federation.api.CommunicationException;
import me.thekey.cas.federation.api.IdentityLinkingServiceApi;
import me.thekey.cas.federation.model.Identity;
import me.thekey.cas.service.UserAlreadyExistsException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.federation.AbstractFederationProcessor;
import org.ccci.gto.cas.federation.FederationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class RelayFederationProcessor extends AbstractFederationProcessor<TheKeyCredentials> {
    private static final Logger LOG = LoggerFactory.getLogger(RelayFederationProcessor.class);

    @Inject
    @NotNull
    private IdentityLinkingServiceApi api;

    @Inject
    @NotNull
    private LinkedIdentitySyncService sync;

    public RelayFederationProcessor() {
        super(TheKeyCredentials.class);
    }

    @Override
    protected boolean supportsInternal(final TheKeyCredentials credentials) {
        return credentials.getAttribute(CREDS_ATTR_CAS_ASSERTION) != null;
    }

    @Override
    protected boolean linkIdentityInternal(final GcxUser user, final TheKeyCredentials credentials,
                                           final Number strength) throws FederationException {
        // prevent linking to an unverified account
        if (!user.isVerified()) {
            return false;
        }

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

        // link identities in identity linking service
        try {
            api.linkIdentities(Identity.thekey(user.getGUID()), Identity.relay(guid), strength.doubleValue(), null);
        } catch (final CommunicationException e) {
            LOG.error("error linking identities in Identity Linking Service", e);
        }

        // trigger a sync to update linked identities
        sync.syncIdentities(Identity.relay(guid), true);

        // return success
        return true;
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

        // create a new user object (attempt to keep the guids identical)
        final GcxUser user = new GcxUser();
        user.setGUID(guid);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordAllowChange(true);
        user.setLoginDisabled(false);
        user.setVerified(false);

        // use the users existing password if possible
        if (credentials instanceof UsernamePasswordCredentials) {
            user.setPassword(((UsernamePasswordCredentials) credentials).getPassword());
        }

        // create the new user
        try {
            this.getUserService().createUser(user);

            // link identities in identity linking service
            try {
                api.linkIdentities(Identity.thekey(user.getGUID()), Identity.relay(guid), strength.doubleValue(), null);
            } catch (final CommunicationException e) {
                LOG.error("error linking identities in Identity Linking Service", e);
            }

            // trigger a sync to update linked identities
            sync.syncIdentities(Identity.relay(guid), true);

            return true;
        } catch (final UserAlreadyExistsException e) {
            throw new RelayIdentityExistsFederationException(new Object[] { StringEscapeUtils.escapeHtml(email) });
        }
    }
}
