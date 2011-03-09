package org.ccci.gcx.idm.web.test;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;

import org.ccci.gcx.idm.web.css.impl.SimpleCssScrubberImpl;

import java.util.*;

import junit.framework.Assert;


public class CssScrubberTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( CssScrubberTest.class ) ;

    
    public void testSimpleCssScrubber()
    {
    	SimpleCssScrubberImpl scrub = new SimpleCssScrubberImpl();
    	
    	ArrayList<String> rules = new ArrayList<String>();
    	
    	rules.add("import");
    	rules.add("@");
    	rules.add("%40");
    	rules.add("behavior");
    	rules.add("script");
    	rules.add("iframe");
    	
    	scrub.setRules(rules);
    	
    	StringBuffer basecss = new StringBuffer("body, p, table, ul, ol, form {margin:0; padding:0; border:0;}");
basecss.append("body {color:#000; font-family:Verdana, sans-serif; background-color:#333; font-size:15px; line-height:1.333em; margin-bottom:35px;}")
.append(".main {width:580px; margin:0 auto; font-size:13px; position:relative;}")
.append(".top {padding:18px 20px; background-color:#fff; height:54px; clear:both;}")
		.append(".top .logo {float:right; clear:none;}")
				.append(".top .tagline {float:left; clear:none; height:54px; width:364px; background:transparent url(images/tagline.gif) no-repeat left top;}");

String bad1 = "@import something bad."; 
String bad2 = "imp\n"; 
String bad2b = "or t\n";
String bad2c = "(something)\n";
String bad3 = "behavior";
String bad4 = "%40impo rt(something)";
String good1 = ".main {width:580px; margin:0 auto; font-size:13px; position:relative;}";
    	
		Assert.assertFalse(scrub.isProboten(basecss.toString(),rules));
		Assert.assertTrue(scrub.isProboten(basecss.toString()+bad1,rules));
		Assert.assertTrue(scrub.isProboten(basecss.toString()+bad2+bad2b+bad2c,rules));
		Assert.assertTrue(scrub.isProboten(basecss.toString()+bad3,rules));
		Assert.assertTrue(scrub.isProboten(basecss.toString()+bad4,rules));
		Assert.assertFalse(scrub.isProboten(basecss.toString()+good1,rules));
    	
    	

    }
    
    
}
