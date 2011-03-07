package org.ccci.idm.web.test;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gcx.idm.web.validation.impl.RuleBasedPasswordValidatorImpl;
import org.springframework.test.ConditionalTestCase;


public class PasswordValidatorTest extends ConditionalTestCase
{
    protected static final Log log = LogFactory.getLog( PasswordValidatorTest.class ) ;

    
    public void testPasswordValidator() throws Exception
    {
       //test brand extractor
    	
    	XmlConfigurator config = new XmlConfigurator("file:///Users/ken/Documents/workspace/ssoweb/passwords.xml");
    	
    	
    	RuleBasedPasswordValidatorImpl pwv = new RuleBasedPasswordValidatorImpl();
    	pwv.setConfigurator(config);
    	
    	pwv.setMinMix(2);
    	Assert.assertFalse(pwv.isAcceptablePassword("hi"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123@$."));
    	Assert.assertTrue(pwv.isAcceptablePassword("kendog123"));
    	pwv.setMinLength(16);
    	Assert.assertFalse(pwv.isAcceptablePassword("Kendog123!"));
    	pwv.setMinMix(0);
    	pwv.setMinLength(4);
    	pwv.setHaveSymbol(0);
    	pwv.setHaveNumber(0);
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123"));
    	pwv.setHaveNumber(1);
    	Assert.assertFalse(pwv.isAcceptablePassword("Kendog"));
    	pwv.setMinLength(8);
    	pwv.setMinMix(3);
    	Assert.assertFalse(pwv.isAcceptablePassword("Kendog3"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123"));
    	pwv.setHaveSymbol(1);
    	Assert.assertFalse(pwv.isAcceptablePassword("Kendog123"));
    	Assert.assertTrue(pwv.isAcceptablePassword("Kendog123!"));
    	
    	Assert.assertFalse(pwv.isAcceptablePassword("Password1"));
    	Assert.assertFalse(pwv.isAcceptablePassword("Vonette"));
    	
    	log.debug("ALL TESTS PASS");
    	
    	System.out.println(pwv.getClientJavascript());
    	
    }

}
