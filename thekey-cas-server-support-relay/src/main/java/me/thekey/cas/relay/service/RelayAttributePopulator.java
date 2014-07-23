package me.thekey.cas.relay.service;

import static me.thekey.cas.relay.Constants.PRINCIPAL_ATTR_RELAYGUID;

import com.google.common.collect.ListMultimap;
import me.thekey.cas.service.UserManagerImpl;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class RelayAttributePopulator implements UserManagerImpl.AttributePopulator {
    @Autowired
    private ApplicationContext applicationContext;

    // XXX: we can't configure this dependency through Spring because of a dependency cycle involving proxies
    private RelayUserManager relayUserManager;

    public void setRelayUserManager(final RelayUserManager relayUserManager) {
        this.relayUserManager = relayUserManager;
    }

    @Override
    public void lookupAttributes(final GcxUser user, final ListMultimap<String, String> attrs) {
        // XXX: ugly hack to work around a dependency cycle using proxies
        if (relayUserManager == null) {
            relayUserManager = applicationContext.getBean(RelayUserManager.class);
        }

        attrs.put(PRINCIPAL_ATTR_RELAYGUID, relayUserManager.getRelayGuid(user));
    }
}
