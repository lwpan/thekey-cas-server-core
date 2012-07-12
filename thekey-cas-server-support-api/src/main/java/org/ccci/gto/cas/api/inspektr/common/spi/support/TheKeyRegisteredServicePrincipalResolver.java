package org.ccci.gto.cas.api.inspektr.common.spi.support;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.aspectj.lang.JoinPoint;
import org.ccci.gto.cas.services.TheKeyRegisteredService;

import com.github.inspektr.common.spi.PrincipalResolver;

public final class TheKeyRegisteredServicePrincipalResolver implements PrincipalResolver {
    @Override
    public String resolveFrom(final JoinPoint auditTarget, final Object returnValue) {
        return resolveFromInternal(auditTarget);
    }

    @Override
    public String resolveFrom(final JoinPoint auditTarget, final Exception exception) {
        return resolveFromInternal(auditTarget);
    }

    @Override
    public String resolve() {
        return UNKNOWN_USER;
    }

    protected String resolveFromInternal(final JoinPoint joinPoint) {
        final Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof TheKeyRegisteredService) {
            final TheKeyRegisteredService service = (TheKeyRegisteredService) args[0];

            final ToStringBuilder toStringBuilder = new ToStringBuilder(null, ToStringStyle.SHORT_PREFIX_STYLE);
            toStringBuilder.append("id", service.getId());
            toStringBuilder.append("name", service.getName());
            toStringBuilder.append("apiKey", service.getApiKey());
            toStringBuilder.append("attributes", service.getAllowedAttributes().toArray());

            return toStringBuilder.toString();
        }

        return UNKNOWN_USER;
    }
}
