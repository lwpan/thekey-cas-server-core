package org.ccci.gcx.idm.web.test;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.AuthenticationException;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationRequest;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationResponse;
import org.ccci.gcx.idm.core.service.AuthenticationService;

public class HttpClientTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( HttpClientTest.class ) ;

    private CasAuthenticationResponse logmein() throws AuthenticationException
    {
        AuthenticationService auth = (AuthenticationService)this.getApplicationContext().getBean( Constants.BEAN_AUTHENTICATION_SERVICE ) ;

    	CasAuthenticationRequest req = new CasAuthenticationRequest();
        
        req.setPrincipal("kenburcham@gmail.com");
        req.setCredential("Kendog@123");
        req.setService("http://www.kenburcham.com");
        
		CasAuthenticationResponse res = (CasAuthenticationResponse) auth.handleLoginRequest(req);
		
		return res;
    	
    }
    
    public void testProxy() throws AuthenticationException 
    {
    	   //test proxy requests...
	final AuthenticationService auth = (AuthenticationService) this
		.getApplicationContext().getBean(
			Constants.BEAN_AUTHENTICATION_SERVICE);
        
	final CasAuthenticationResponse res = this.logmein();

        Assert.assertNotNull(res);
	final String service = "https://www.mygcx.org/Public/screen/home?";
	final CasAuthenticationRequest va2req = new CasAuthenticationRequest();
        va2req.setService(service);
	va2req.setCookies(res.getCookies());
		
	final CasAuthenticationResponse res2 = (CasAuthenticationResponse) auth
		.handleSSORequest(va2req);
	log.debug("And the location we got back was::: " + res2.getLocation());
	Assert.assertNotNull("response from validate is null", res2);
	Assert.assertFalse("validate returned an error", res2.isError());
        
        log.debug("DECODED SERVICE == "+va2req.getService());
	log.debug("LOCATION == " + res2.getLocation());
    }
}

