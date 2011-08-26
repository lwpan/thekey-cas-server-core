package org.ccci.gto.cas.util;

import java.util.HashSet;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomPasswordGeneratorTest extends TestCase {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    final static int RANDOMTESTSLIMIT = 1000;

    private RandomPasswordGenerator getPasswordValidator() {
	// create a default RuleBasedPasswordValidator
	final RandomPasswordGenerator rpg = new RandomPasswordGenerator();

	rpg.setMaxDigits(-1);
	rpg.setStartWithLetter(false);

	return rpg;
    }

    public void testGeneratePassword() {
	final RandomPasswordGenerator rpg = this.getPasswordValidator();

	// Test that the generated password is the correct length
	for (int i = 1; i < 15; i++) {
	    String password = rpg.generatePassword(i);
	    assertEquals(i, password.length());
	}

	// Test the max digits setting
	for (int i = 0; i < 5; i++) {
	    rpg.setMaxDigits(i);
	    for (int j = 0; j < RANDOMTESTSLIMIT; j++) {
		final String pw = rpg.generatePassword(20);
		int digits = 0;
		for (int k = 0; k < pw.length(); k++) {
		    if (Character.isDigit(pw.charAt(k))) {
			digits++;
		    }
		}
		assertTrue("Password has too many digits, pw: " + pw
			+ " maxDigits: " + i, digits <= i);
	    }
	}
	rpg.setMaxDigits(-1);

	// Test Start with letter setting
	rpg.setStartWithLetter(true);
	for (int j = 0; j < RANDOMTESTSLIMIT; j++) {
	    final String pw = rpg.generatePassword(2);
	    assertTrue("Password doesn't start with a letter. pw: " + pw,
		    Character.isLetter(pw.charAt(0)));
	}
	rpg.setStartWithLetter(false);

	// Test for different passwords being generated
	// This is a fuzzy test, it may fail due to the nature of random numbers
	final HashSet<String> pws = new HashSet<String>();
	for (int j = 0; j < RANDOMTESTSLIMIT; j++) {
	    pws.add(rpg.generatePassword(10));
	}
	assertEquals("Expecting " + RANDOMTESTSLIMIT
		+ " unique passwords but found " + pws.size(),
		RANDOMTESTSLIMIT, pws.size());
    }
}
