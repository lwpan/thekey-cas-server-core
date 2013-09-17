package me.thekey.cas.inspektr.audit.spi.support;

import org.ccci.gcx.idm.core.model.impl.GcxUser;

import com.github.inspektr.audit.spi.support.AbstractAuditResourceResolver;

public class UserAuditResourceResolver extends AbstractAuditResourceResolver {
    @Override
    protected String[] createResource(final Object[] args) {
        if (args.length >= 1 && args[0] instanceof GcxUser) {
            final GcxUser user = (GcxUser) args[0];
            return new String[] { "{guid=" + user.getGUID() + ",email=" + user.getEmail() + "}" };
        }
        return null;
    }
}
