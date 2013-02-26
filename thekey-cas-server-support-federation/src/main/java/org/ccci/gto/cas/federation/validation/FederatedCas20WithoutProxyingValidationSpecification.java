package org.ccci.gto.cas.federation.validation;

import org.ccci.gto.cas.federation.util.FederationUtil;
import org.jasig.cas.validation.Assertion;
import org.jasig.cas.validation.Cas20WithoutProxyingValidationSpecification;

public class FederatedCas20WithoutProxyingValidationSpecification extends Cas20WithoutProxyingValidationSpecification {
    public FederatedCas20WithoutProxyingValidationSpecification() {
        super();
    }

    public FederatedCas20WithoutProxyingValidationSpecification(final boolean renew) {
        super(renew);
    }

    @Override
    protected boolean isSatisfiedByInternal(final Assertion assertion) {
        return super.isSatisfiedByInternal(assertion) && !FederationUtil.requireProxyValidation(assertion);
    }
}
