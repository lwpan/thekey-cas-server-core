package org.ccci.gto.cas.authentication.principal;

import com.google.common.collect.ListMultimap;
import me.thekey.cas.authentication.principal.TheKeyCredentials;
import me.thekey.cas.authentication.principal.TheKeyCredentials.Lock;
import me.thekey.cas.service.UserManager;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheKeyCredentialsToPrincipalResolver implements CredentialsToPrincipalResolver {
    @Autowired
    @NotNull
    private UserManager userManager;

    public void setUserManager(final UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Principal resolvePrincipal(final Credentials rawCredentials) {
        final TheKeyCredentials credentials = (TheKeyCredentials) rawCredentials;
        final GcxUser user = credentials.getUser();

        if (user != null) {
            // retrieve the attributes for the user authenticating
            final ListMultimap<String, String> attrs = this.userManager.getUserAttributes(user);

            // munge attributes into required format
            final Map<String, Object> attrs2 = new HashMap<>();
            for (final String key : attrs.keySet()) {
                final List<String> vals = attrs.get(key);
                switch (vals.size()) {
                    case 0:
                        break;
                    case 1:
                        attrs2.put(key, vals.get(0));
                        break;
                    default:
                        attrs2.put(key, new ArrayList<>(vals));
                        break;
                }
            }

            return new SimplePrincipal(user.getEmail(), attrs2);
        }
        // no user object and the NULLUSER lock is not enabled, return a GUEST
        // placeholder principal object
        else if (!credentials.observeLock(Lock.NULLUSER)) {
            return new SimplePrincipal("GUEST");
        }
        // no user and the NULLUSER lock is enabled, don't resolve the principal
        // here
        else {
            return null;
        }
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof TheKeyCredentials;
    }
}
