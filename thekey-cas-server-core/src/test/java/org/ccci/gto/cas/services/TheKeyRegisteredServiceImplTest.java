package org.ccci.gto.cas.services;

import junit.framework.TestCase;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl;

public final class TheKeyRegisteredServiceImplTest extends TestCase {
    public void testRegex() {
	final Service service1 = new SimpleWebApplicationServiceImpl(
		"https://localhost/cas/services");
	final Service service2 = null;
	final TheKeyRegisteredServiceImpl rService = new TheKeyRegisteredServiceImpl();

	// ant-style valid patterns
	{
	    rService.setRegex(false);
	    final String[] patterns = new String[] { "https://localhost/**",
		    "http*://localhost/**" };
	    for (final String pattern : patterns) {
		rService.setServiceId(pattern);
		assertTrue("valid pattern", rService.matches(service1));
		assertFalse("valid pattern against null service",
			rService.matches(service2));
	    }
	}

	// ant-style invalid patterns
	{
	    rService.setRegex(false);
	    final String[] patterns = new String[] { "http://localhost/**",
		    "localhost" };
	    for (final String pattern : patterns) {
		rService.setServiceId(pattern);
		assertFalse("invalid pattern", rService.matches(service1));
		assertFalse("invalid pattern against null service",
			rService.matches(service2));
	    }
	}

	// regex valid patterns
	{
	    rService.setRegex(true);
	    final String[] patterns = new String[] { "\\Ahttps...localhost",
		    "https?\\:\\/\\/localhost\\/", "localhost" };
	    for (final String pattern : patterns) {
		rService.setServiceId(pattern);
		assertTrue("valid regex", rService.matches(service1));
		assertFalse("valid regex against null service",
			rService.matches(service2));
	    }
	}

	// regex invalid patterns
	{
	    rService.setRegex(true);
	    final String[] patterns = new String[] { "http://localhost/**",
		    "^localhost", "\\Alocalhost" };
	    for (final String pattern : patterns) {
		rService.setServiceId(pattern);
		assertFalse("invalid regex", rService.matches(service1));
		assertFalse("invalid regex against null service",
			rService.matches(service2));
	    }
	}
    }
}
