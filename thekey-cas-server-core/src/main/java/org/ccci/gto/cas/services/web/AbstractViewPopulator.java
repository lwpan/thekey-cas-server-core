package org.ccci.gto.cas.services.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractViewPopulator implements ViewPopulator {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public final void populate(final ViewContext context) {
	this.populateInternal(context);
    }

    protected abstract void populateInternal(final ViewContext context);
}
