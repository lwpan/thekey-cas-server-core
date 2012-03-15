package org.ccci.gto.cas.util;

import static org.ccci.gto.cas.Constants.VALIDGUIDREGEX;

import java.util.HashSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>RandomGUIDTest</b> is used to unit test and prove out the
 * {@link RandomGUID} utility.
 * 
 * @author Greg Crider Oct 21, 2008 6:38:40 PM
 * @author Daniel Frett
 */
public class RandomGUIDTest extends TestCase {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** the number of guid's to generate */
    private final static int COUNT = 1000;

    public void testFormat() {
	for (int i = 0; i < COUNT; i++) {
	    final String guid = RandomGUID.generateGuid(true);
	    assertTrue("invalid guid generated", VALIDGUIDREGEX.matcher(guid)
		    .matches());
	}
    }

    public void testObject() {
	logger.info("***** BEGIN: testObject");
	final HashSet<String> guids = new HashSet<String>();

	logger.info("***** Testing non-Secure");
	for (int i = 0; i < COUNT; i++) {
	    final RandomGUID guid = new RandomGUID();
	    guids.add(guid.toString());
	    logger.info("***** non-Secure GUID: " + guid);
	}
	Assert.assertEquals("Expecting " + COUNT + " unique GUID's but found "
		+ guids.size(), COUNT, guids.size());
	guids.clear();

	logger.info("***** Testing Secure");
	for (int i = 0; i < COUNT; i++) {
	    final RandomGUID guid = new RandomGUID(true);
	    guids.add(guid.toString());
	    logger.info("***** Secure GUID: " + guid);
	}
	Assert.assertEquals("Expecting " + COUNT + " unique GUID's but found "
		+ guids.size(), COUNT, guids.size());

	logger.info("***** END: testObject");
    }

    public void testStatic() {
	logger.info("***** BEGIN: testStatic");
	final HashSet<String> guids = new HashSet<String>();

	logger.info("***** Testing non-Secure");
	for (int i = 0; i < COUNT; i++) {
	    final String guid = RandomGUID.generateGuid();
	    guids.add(guid);
	    logger.info("***** non-Secure GUID: " + guid);
	}
	Assert.assertEquals("Expecting " + COUNT + " unique GUID's but found "
		+ guids.size(), COUNT, guids.size());
	guids.clear();

	logger.info("***** Testing Secure");
	for (int i = 0; i < COUNT; i++) {
	    final String guid = RandomGUID.generateGuid(true);
	    guids.add(guid);
	    logger.info("***** Secure GUID: " + guid);
	}
	Assert.assertEquals("Expecting " + COUNT + " unique GUID's but found "
		+ guids.size(), COUNT, guids.size());

	logger.info("***** END: testObject");
    }
}
