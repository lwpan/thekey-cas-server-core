package org.ccci.gcx.idm.web.test;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.validation.impl.RuleBasedPasswordValidatorImpl;
import org.springframework.test.ConditionalTestCase;


public class PasswordValidatorTest extends ConditionalTestCase
{
    protected static final Log log = LogFactory.getLog( PasswordValidatorTest.class ) ;

    
    public void testPasswordValidator() throws Exception
    {
    	RuleBasedPasswordValidatorImpl pwv = new RuleBasedPasswordValidatorImpl();
    	
	pwv.setCharVariety(2);
    	Assert.assertFalse(pwv.isAcceptablePassword("hi"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123@$."));
    	Assert.assertTrue(pwv.isAcceptablePassword("kendog123"));
    	pwv.setMinLength(16);
    	Assert.assertFalse(pwv.isAcceptablePassword("Kendog123!"));
	pwv.setCharVariety(0);
    	pwv.setMinLength(4);
	pwv.setRequireSymbol(false);
	pwv.setRequireNumber(false);
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123"));
	pwv.setRequireNumber(true);
    	Assert.assertFalse(pwv.isAcceptablePassword("Kendog"));
    	pwv.setMinLength(8);
	pwv.setCharVariety(3);
    	Assert.assertFalse(pwv.isAcceptablePassword("Kendog3"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123"));
	pwv.setRequireSymbol(true);
    	Assert.assertFalse(pwv.isAcceptablePassword("Kendog123"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123!"));
    	
    	Assert.assertFalse(pwv.isAcceptablePassword("Password1"));
    	Assert.assertFalse(pwv.isAcceptablePassword("Vonette"));
    	
    	log.debug("ALL TESTS PASS");
    }

}
