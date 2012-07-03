package org.ccci.gto.cas.services;

import java.util.Arrays;

import junit.framework.TestCase;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl;

public final class TheKeyRegisteredServiceImplTest extends TestCase {
    public void testCloneEqualsAndHashCode() throws Exception {
        final TheKeyRegisteredServiceImpl source = new TheKeyRegisteredServiceImpl();

        // test simple object
        {
            // clone the source registered service
            final TheKeyRegisteredServiceImpl target = (TheKeyRegisteredServiceImpl) source.clone();

            assertTrue(source.equals(target));
            assertEquals(source.hashCode(), target.hashCode());
        }

        // test inherited object properties
        {
            source.setEnabled(false);
            source.setAnonymousAccess(false);
            source.setIgnoreAttributes(true);
            source.setAllowedAttributes(Arrays.asList("attr1", "attr2"));
            source.setAllowedToProxy(false);
            source.setServiceId("serviceId");
            source.setId(98765);
            source.setName("name");
            source.setTheme("theme");
            source.setSsoEnabled(false);
            source.setDescription("description");
            source.setEvaluationOrder(19);

            // clone the source registered service
            final TheKeyRegisteredServiceImpl target = (TheKeyRegisteredServiceImpl) source.clone();

            assertEquals(false, target.isEnabled());
            assertEquals(false, target.isAnonymousAccess());
            assertEquals(true, target.isIgnoreAttributes());
            assertEquals(2, target.getAllowedAttributes().size());
            assertTrue(target.getAllowedAttributes().contains("attr1"));
            assertTrue(target.getAllowedAttributes().contains("attr2"));
            assertFalse(target.getAllowedAttributes().contains("attr3"));
            assertEquals(false, target.isAllowedToProxy());
            assertEquals("serviceId", target.getServiceId());
            assertEquals(98765, target.getId());
            assertEquals("name", target.getName());
            assertEquals("theme", target.getTheme());
            assertEquals(false, target.isSsoEnabled());
            assertEquals("description", target.getDescription());
            assertEquals(19, target.getEvaluationOrder());

            assertTrue(source.equals(target));
            assertEquals(source.hashCode(), target.hashCode());
        }

        // test legacy flags
        {
            final boolean legacyHeaders = !source.isLegacyHeaders();
            source.setLegacyHeaders(legacyHeaders);
            final boolean legacyLogin = !source.isLegacyLogin();
            source.setLegacyLogin(legacyLogin);

            // clone the source registered service
            final TheKeyRegisteredServiceImpl target = (TheKeyRegisteredServiceImpl) source.clone();

            assertEquals(legacyHeaders, target.isLegacyHeaders());
            assertEquals(legacyLogin, target.isLegacyLogin());

            assertTrue(source.equals(target));
            assertEquals(source.hashCode(), target.hashCode());
        }

        // test regex flag
        {
            final boolean regex = !source.isRegex();
            source.setRegex(regex);

            // clone the source registered service
            final TheKeyRegisteredServiceImpl target = (TheKeyRegisteredServiceImpl) source.clone();

            assertEquals(regex, target.isRegex());

            assertTrue(source.equals(target));
            assertEquals(source.hashCode(), target.hashCode());
        }

        // test contact and template attributes
        {
            source.setContactEmail("emailAddr");
            source.setTemplateCssUrl("cssUri");

            // clone the source registered service
            final TheKeyRegisteredServiceImpl target = (TheKeyRegisteredServiceImpl) source.clone();

            assertEquals("emailAddr", target.getContactEmail());
            assertEquals("cssUri", target.getTemplateCssUrl());

            assertTrue(source.equals(target));
            assertEquals(source.hashCode(), target.hashCode());
        }
    }

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
