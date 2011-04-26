package org.ccci.gcx.idm.web.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.Constants;
import org.springframework.ldap.filter.EqualsFilter;

public class PasswordEqualsTest extends TestCase {
    protected static final Log log = LogFactory.getLog( PasswordEqualsTest.class ) ;

    public void testEquals() {
	log.info("***** BEGIN: testEquals");

	// generate a list of passwords to test
	Map<String, String> passwords = new HashMap<String, String>();
	passwords.put("Kendog", "Kendog");
	passwords.put("Kendog!", "Kendog!");
	passwords.put("Kendog)", "Kendog\\29");

	// test all defined passwords
	for (Map.Entry<String, String> password : passwords.entrySet()) {
	    EqualsFilter filter = new EqualsFilter(Constants.LDAP_KEY_PASSWORD,
		    password.getKey());
	    System.out.println("Encoded '" + password.getKey() + "' as "
		    + filter.encode());
	    Assert.assertEquals("(" + Constants.LDAP_KEY_PASSWORD + "="
		    + password.getValue() + ")", filter.encode());
	}

	log.info("***** END: testEquals");
    }
}
