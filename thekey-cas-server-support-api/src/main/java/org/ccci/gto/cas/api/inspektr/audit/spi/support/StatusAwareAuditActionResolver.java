package org.ccci.gto.cas.api.inspektr.audit.spi.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.aspectj.lang.JoinPoint;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.github.inspektr.audit.annotation.Audit;
import com.github.inspektr.audit.spi.support.AbstractSuffixAwareAuditActionResolver;

public class StatusAwareAuditActionResolver extends AbstractSuffixAwareAuditActionResolver {
    final HashMap<Integer, String> codes = new HashMap<Integer, String>();

    protected StatusAwareAuditActionResolver(final String successSuffix, final String failureSuffix,
            final Map<String, String> codes) {
        super(successSuffix, failureSuffix);
        if (codes != null) {
            for (final Entry<String, String> code : codes.entrySet()) {
                this.codes.put(Integer.parseInt(code.getKey()), code.getValue());
            }
        }
    }

    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Object retval, final Audit audit) {
        return audit.action() + getSuccessSuffix();
    }

    @Override
    public String resolveFrom(final JoinPoint auditableTarget, final Exception exception, final Audit audit) {
        // handle ResourceExceptions
        if (exception instanceof ResourceException) {
            final Status status = ((ResourceException) exception).getStatus();
            if (status != null && this.codes.containsKey(status.getCode())) {
                return audit.action() + this.codes.get(status.getCode());
            }
        }

        return audit.action() + getFailureSuffix();
    }
}
