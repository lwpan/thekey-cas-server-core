package org.ccci.gto.cas.inspektr.common.spi.support;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;

import com.github.inspektr.common.spi.PrincipalResolver;

public final class PrincipalResolverChain implements PrincipalResolver {
    private final List<PrincipalResolver> resolvers = new ArrayList<PrincipalResolver>();

    /**
     * @param resolvers
     *            the resolvers to use when attempting to resolve the principal
     */
    public void setResolvers(final List<PrincipalResolver> resolvers) {
        this.resolvers.clear();
        this.resolvers.addAll(resolvers);
    }

    @Override
    public String resolveFrom(final JoinPoint auditTarget, final Object returnValue) {
        for (final PrincipalResolver resolver : resolvers) {
            final String response = resolver.resolveFrom(auditTarget, returnValue);
            if (response != null && !response.equals(UNKNOWN_USER)) {
                return response;
            }
        }

        return UNKNOWN_USER;
    }

    @Override
    public String resolveFrom(final JoinPoint auditTarget, final Exception exception) {
        for (final PrincipalResolver resolver : resolvers) {
            final String response = resolver.resolveFrom(auditTarget, exception);
            if (response != null && !response.equals(UNKNOWN_USER)) {
                return response;
            }
        }

        return UNKNOWN_USER;
    }

    @Override
    public String resolve() {
        for (final PrincipalResolver resolver : resolvers) {
            final String response = resolver.resolve();
            if (response != null && !response.equals(UNKNOWN_USER)) {
                return response;
            }
        }

        return UNKNOWN_USER;
    }
}
