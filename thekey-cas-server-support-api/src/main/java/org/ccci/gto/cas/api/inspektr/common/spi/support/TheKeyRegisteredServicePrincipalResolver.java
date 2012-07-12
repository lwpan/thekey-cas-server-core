package org.ccci.gto.cas.api.inspektr.common.spi.support;

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
            return args[0].toString();
        }

        return UNKNOWN_USER;
    }
}
