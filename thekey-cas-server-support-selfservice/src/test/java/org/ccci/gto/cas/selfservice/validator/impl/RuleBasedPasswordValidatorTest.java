package org.ccci.gto.cas.selfservice.validator.impl;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleBasedPasswordValidatorTest extends TestCase
{
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private RuleBasedPasswordValidator getPasswordValidator() {
	// create a default RuleBasedPasswordValidator
	final RuleBasedPasswordValidator pwv = new RuleBasedPasswordValidator();
	pwv.setBlacklist(null);
	pwv.setMinLength(0);
	pwv.setMaxLength(32);
	pwv.setRequireLowercase(false);
	pwv.setRequireNumber(false);
	pwv.setRequireSymbol(false);
	pwv.setRequireUppercase(false);
	pwv.setCharVariety(0);

	return pwv;
    }

    public void testMinLength() throws Exception {
	final RuleBasedPasswordValidator pwv = this.getPasswordValidator();
	Assert.assertTrue(pwv.isAcceptablePassword(""));
	Assert.assertTrue(pwv.isAcceptablePassword("a"));
	Assert.assertTrue(pwv.isAcceptablePassword("aa"));
	pwv.setMinLength(1);
	Assert.assertFalse(pwv.isAcceptablePassword(""));
	Assert.assertTrue(pwv.isAcceptablePassword("a"));
	Assert.assertTrue(pwv.isAcceptablePassword("aa"));
	pwv.setMinLength(2);
	Assert.assertFalse(pwv.isAcceptablePassword(""));
	Assert.assertFalse(pwv.isAcceptablePassword("a"));
	Assert.assertTrue(pwv.isAcceptablePassword("aa"));
    }
}
