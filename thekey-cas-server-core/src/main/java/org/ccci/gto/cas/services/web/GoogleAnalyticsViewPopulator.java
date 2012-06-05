package org.ccci.gto.cas.services.web;

import javax.validation.constraints.NotNull;

public final class GoogleAnalyticsViewPopulator extends AbstractViewPopulator {
    @NotNull
    private String account;

    @Override
    protected void populateInternal(final ViewContext context) {
        context.setAttribute("googleAnalyticsAccount", account);
    }

    /**
     * @param account
     *            the account to set
     */
    public void setAccount(final String account) {
        this.account = account;
    }
}
