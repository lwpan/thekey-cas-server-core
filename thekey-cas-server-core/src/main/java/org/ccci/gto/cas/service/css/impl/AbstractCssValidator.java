package org.ccci.gto.cas.service.css.impl;

import org.ccci.gto.cas.service.css.CssValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCssValidator implements CssValidator {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public abstract boolean isValid(final String css);
}
