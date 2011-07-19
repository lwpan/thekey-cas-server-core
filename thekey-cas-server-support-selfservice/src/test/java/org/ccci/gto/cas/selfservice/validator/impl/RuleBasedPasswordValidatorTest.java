package org.ccci.gto.cas.selfservice.validator.impl;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleBasedPasswordValidatorTest extends TestCase {
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
	pwv.setMinLength(0);
	assertTrue(pwv.isAcceptablePassword(""));
	assertTrue(pwv.isAcceptablePassword("a"));
	assertTrue(pwv.isAcceptablePassword("aa"));
	pwv.setMinLength(1);
	assertFalse(pwv.isAcceptablePassword(""));
	assertTrue(pwv.isAcceptablePassword("a"));
	assertTrue(pwv.isAcceptablePassword("aa"));
	pwv.setMinLength(2);
	assertFalse(pwv.isAcceptablePassword(""));
	assertFalse(pwv.isAcceptablePassword("a"));
	assertTrue(pwv.isAcceptablePassword("aa"));
    }

    public void testMaxLength() throws Exception {
	final RuleBasedPasswordValidator pwv = this.getPasswordValidator();
	pwv.setMaxLength(5);
	assertTrue(pwv.isAcceptablePassword("aaa"));
	assertTrue(pwv.isAcceptablePassword("aaaa"));
	assertTrue(pwv.isAcceptablePassword("aaaaa"));
	pwv.setMaxLength(4);
	assertTrue(pwv.isAcceptablePassword("aaa"));
	assertTrue(pwv.isAcceptablePassword("aaaa"));
	assertFalse(pwv.isAcceptablePassword("aaaaa"));
	pwv.setMaxLength(3);
	assertTrue(pwv.isAcceptablePassword("aaa"));
	assertFalse(pwv.isAcceptablePassword("aaaa"));
	assertFalse(pwv.isAcceptablePassword("aaaaa"));
    }

    public void testRequireLowercase() throws Exception {
	final RuleBasedPasswordValidator pwv = this.getPasswordValidator();
	pwv.setRequireLowercase(false);
	assertTrue(pwv.isAcceptablePassword("aa"));
	assertTrue(pwv.isAcceptablePassword("aA"));
	assertTrue(pwv.isAcceptablePassword("AA"));
	pwv.setRequireLowercase(true);
	assertTrue(pwv.isAcceptablePassword("aa"));
	assertTrue(pwv.isAcceptablePassword("aA"));
	assertFalse(pwv.isAcceptablePassword("AA"));
    }

    public void testRequireNumber() throws Exception {
	final RuleBasedPasswordValidator pwv = this.getPasswordValidator();
	pwv.setRequireNumber(false);
	assertTrue(pwv.isAcceptablePassword("aa"));
	assertTrue(pwv.isAcceptablePassword("a1"));
	assertTrue(pwv.isAcceptablePassword("11"));
	pwv.setRequireNumber(true);
	assertFalse(pwv.isAcceptablePassword("aa"));
	assertTrue(pwv.isAcceptablePassword("a1"));
	assertTrue(pwv.isAcceptablePassword("11"));
    }

    public void testRequireSymbol() throws Exception {
	final RuleBasedPasswordValidator pwv = this.getPasswordValidator();
	pwv.setRequireSymbol(false);
	assertTrue(pwv.isAcceptablePassword("aa"));
	assertTrue(pwv.isAcceptablePassword("a!"));
	assertTrue(pwv.isAcceptablePassword("@#"));
	pwv.setRequireSymbol(true);
	assertFalse(pwv.isAcceptablePassword("aa"));
	assertTrue(pwv.isAcceptablePassword("a!"));
	assertTrue(pwv.isAcceptablePassword("@#"));
    }

    public void testRequireUppercase() throws Exception {
	final RuleBasedPasswordValidator pwv = this.getPasswordValidator();
	pwv.setRequireUppercase(false);
	assertTrue(pwv.isAcceptablePassword("aa"));
	assertTrue(pwv.isAcceptablePassword("aA"));
	assertTrue(pwv.isAcceptablePassword("AA"));
	pwv.setRequireUppercase(true);
	assertFalse(pwv.isAcceptablePassword("aa"));
	assertTrue(pwv.isAcceptablePassword("aA"));
	assertTrue(pwv.isAcceptablePassword("AA"));
    }

    public void testVariety() throws Exception {
	final RuleBasedPasswordValidator pwv = this.getPasswordValidator();
	final String[] chars = { "a", "A", "1", "+" };
	final ArrayList<String> passwords = new ArrayList<String>();

	// generate all the passwords being tested
	passwords.add("");
	for (final String char1 : chars) {
	    for (final String char2 : chars) {
		if (char2.equals(char1)) {
		    continue;
		}
		for (final String char3 : chars) {
		    if (char3.equals(char1) || char3.equals(char2)) {
			continue;
		    }
		    for (final String char4 : chars) {
			if (char4.equals(char1) || char4.equals(char2)
				|| char4.equals(char3)) {
			    continue;
			}
			passwords.add(char1 + char2 + char3 + char4);
		    }
		    passwords.add(char1 + char2 + char3);
		}
		passwords.add(char1 + char2);
	    }
	    passwords.add(char1);
	}

	// test generated passwords
	for (int i = 0; i < 5; i++) {
	    pwv.setCharVariety(i);
	    for (final String password : passwords) {
		assertEquals("variety: " + i + " password: " + password,
			i <= password.length(),
			pwv.isAcceptablePassword(password));
	    }
	}
    }

    public void testBlacklist() throws Exception {
	final RuleBasedPasswordValidator pwv = this.getPasswordValidator();
	final ArrayList<String> blacklist = new ArrayList<String>();
	final ArrayList<String> passwords = new ArrayList<String>();

	// add several random passwords to use for blacklist testing
	passwords.add("Aandsf");
	passwords.add("Aawer");
	passwords.add("vwhA");
	passwords.add("aonweroA");
	passwords.add("Avnasdf");
	passwords.add("Aawher");
	passwords.add("Avntor");

	for (int i = 0; i < 1L << passwords.size(); i++) {
	    int i2 = i;
	    // generate current blacklist
	    blacklist.clear();
	    for (int j = 0; j < passwords.size(); j++) {
		if (i2 % 2 == 1) {
		    blacklist.add(passwords.get(j));
		    i2--;
		}
		i2 /= 2;
	    }

	    // test all the passwords against the blacklist
	    pwv.setBlacklist(blacklist);
	    for (final String password : passwords) {
		final boolean inBlacklist = blacklist.contains(password);
		assertEquals(!inBlacklist, pwv.isAcceptablePassword(password));
		assertEquals(!inBlacklist,
			pwv.isAcceptablePassword(password.toLowerCase()));
		assertEquals(!inBlacklist,
			pwv.isAcceptablePassword(password.toUpperCase()));
	    }
	}
    }
}
