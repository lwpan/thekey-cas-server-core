package org.ccci.gto.cas.service.css.impl;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternCssValidatorTest extends TestCase {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public void testIsValid() throws Exception {
	final PatternCssValidator validator = new PatternCssValidator();

	final ArrayList<String> patterns = new ArrayList<String>();
	patterns.add("@import");
	patterns.add("iframe");

	final ArrayList<String> good = new ArrayList<String>();
	good.add("body, p {margin:0; padding:0; border:0;}");
	good.add(".main {width:580px; margin:0 auto; font-size:13px; position:relative;}");
	good.add("!important");

	final ArrayList<String> bad = new ArrayList<String>();
	bad.add("@import");
	bad.add("@im\nport");
	bad.add("iframe");
	bad.add("something else iframe");
	bad.add("something else i    frame");
	bad.add("something else i \n\r\t frame");
	bad.add("ifra@importme");

	// test validator with no patterns set
	validator.setInvalidPatterns(null);
	for (final String css : good) {
	    assertTrue(validator.isValid(css));
	}
	for (final String css : bad) {
	    assertTrue(validator.isValid(css));
	}

	// test validator with patterns set
	validator.setInvalidPatterns(patterns);
	for (final String css : good) {
	    assertTrue(validator.isValid(css));
	}
	for (final String css : bad) {
	    assertFalse(validator.isValid(css));
	}
    }
}
