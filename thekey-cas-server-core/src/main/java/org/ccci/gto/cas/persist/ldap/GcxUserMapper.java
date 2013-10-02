package org.ccci.gto.cas.persist.ldap;

import static org.ccci.gto.cas.Constants.LDAP_ATTR_ADDITIONALDOMAINSVISITED;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_CHANGEEMAILKEY;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_DOMAINSVISITED;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_FACEBOOKIDSTRENGTH;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_GROUPS;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_GUID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_LASTNAME;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_LOGINTIME;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_PROPOSEDEMAIL;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_RELAYGUID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_RELAYGUIDSTRENGTH;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_RESETPASSWORDKEY;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_SIGNUPKEY;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_USERID;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_ALLOWPASSWORDCHANGE;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_LOCKED;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_LOGINDISABLED;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_STALEPASSWORD;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_VERIFIED;
import static org.ccci.gto.cas.Constants.STRENGTH_LEGACYFACEBOOK;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>GcxUserMapper</b> is used to map {@link GcxUser} from the attributes
 * returned by certain LDAP lookups.
 * 
 * @author Daniel Frett
 */
public class GcxUserMapper extends AbstractAttributesMapper {
    private static final Logger LOG = LoggerFactory.getLogger(GcxUserMapper.class);

    /**
     * @param attrs
     * @return a GcxUser object
     * @throws NamingException
     * @see org.springframework.ldap.core.AttributesMapper#mapFromAttributes(javax.naming.directory.Attributes)
     */
    public Object mapFromAttributes(final Attributes attrs)
	    throws NamingException {
	final GcxUser user = new GcxUser();

	// Base attributes
	user.setEmail(this.getStringValue(attrs, LDAP_ATTR_EMAIL));
        user.setGUID(this.getStringValue(attrs, LDAP_ATTR_GUID));
        user.setFirstName(this.getStringValue(attrs, LDAP_ATTR_FIRSTNAME));
        user.setLastName(this.getStringValue(attrs, LDAP_ATTR_LASTNAME));

	// Meta-data
        user.setLoginTime(this.getTimeValue(attrs, LDAP_ATTR_LOGINTIME));
        user.setUserid(this.getStringValue(attrs, LDAP_ATTR_USERID));

        // federated identities
        final Map<String, Double> facebookIdStrengths = this.getStrengthValues(attrs, LDAP_ATTR_FACEBOOKIDSTRENGTH);
        for (final String facebookId : this.getStringValues(attrs, LDAP_ATTR_FACEBOOKID)) {
            user.setFacebookId(facebookId, facebookIdStrengths.get(facebookId));

            // handle legacy facebook id's that don't currently have strengths
            if (!facebookIdStrengths.containsKey(facebookId)) {
                user.setFacebookId(facebookId, STRENGTH_LEGACYFACEBOOK);
            }
        }
        final Map<String, Double> relayGuidStrengths = this.getStrengthValues(attrs, LDAP_ATTR_RELAYGUIDSTRENGTH);
        for (final String relayGuid : this.getStringValues(attrs, LDAP_ATTR_RELAYGUID)) {
            user.setRelayGuid(relayGuid, relayGuidStrengths.get(relayGuid));
        }

	// Multi-value attributes
        user.setGroupMembership(this.getStringValues(attrs, LDAP_ATTR_GROUPS));
        user.setDomainsVisited(this.getStringValues(attrs, LDAP_ATTR_DOMAINSVISITED));
        user.setGUIDAdditional(this.getStringValues(attrs, LDAP_ATTR_ADDITIONALGUIDS));
        user.setDomainsVisitedAdditional(this.getStringValues(attrs, LDAP_ATTR_ADDITIONALDOMAINSVISITED));

	// Flags
        user.setPasswordAllowChange(this.getBooleanValue(attrs, LDAP_FLAG_ALLOWPASSWORDCHANGE));
        user.setLoginDisabled(this.getBooleanValue(attrs, LDAP_FLAG_LOGINDISABLED));
        user.setLocked(this.getBooleanValue(attrs, LDAP_FLAG_LOCKED));
        user.setForcePasswordChange(this.getBooleanValue(attrs, LDAP_FLAG_STALEPASSWORD));
        user.setVerified(this.getBooleanValue(attrs, LDAP_FLAG_VERIFIED, true));

        // various self-service keys
        user.setSignupKey(this.getStringValue(attrs, LDAP_ATTR_SIGNUPKEY));
        user.setChangeEmailKey(this.getStringValue(attrs, LDAP_ATTR_CHANGEEMAILKEY));
        user.setProposedEmail(this.getStringValue(attrs, LDAP_ATTR_PROPOSEDEMAIL));
        user.setResetPasswordKey(this.getStringValue(attrs, LDAP_ATTR_RESETPASSWORDKEY));

        // return the loaded User object
        LOG.debug("User loaded from LDAP: {}", user.getGUID());
	return user;
    }
}
