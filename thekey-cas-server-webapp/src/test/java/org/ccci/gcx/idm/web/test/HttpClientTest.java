package org.ccci.gcx.idm.web.test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.AuthenticationException;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationRequest;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationResponse;
import org.ccci.gcx.idm.core.service.AuthenticationService;
import org.ccci.gcx.idm.core.util.URLUtil;

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
    
    
	public void testLogin() throws AuthenticationException, URISyntaxException, UnsupportedEncodingException, MalformedURLException
	{
	final AuthenticationService auth = (AuthenticationService) this
		.getApplicationContext().getBean(
			Constants.BEAN_AUTHENTICATION_SERVICE);
        
	final HashMap<String, String> cookies = new HashMap<String, String>();

	{
	    final CasAuthenticationResponse res = this.logmein();
        Assert.assertNotNull(res);

	    cookies.putAll(res.getCookies());

	if (log.isDebugEnabled()) {
	    for (Map.Entry<String, String> cookie : cookies.entrySet()) {
		log.debug("Cookie: " + cookie.getKey() + ", Value: "
			+ cookie.getValue());
	    }
	}

			Assert.assertNotNull(res.getCASTGCValue());
	}
		
			//ok now try an sso request
	{
	    final CasAuthenticationRequest ssoreq = new CasAuthenticationRequest();
	        ssoreq.setService("http://www.mygcx.org");
	        ssoreq.setCookies(cookies);
	        
	    final CasAuthenticationResponse res = (CasAuthenticationResponse) auth
		    .handleSSORequest(ssoreq);
			
	        Assert.assertNotNull(res);

	        log.debug("Location === "+res.getLocation());
	        
	        Assert.assertNotNull(res.getLocation()); // location will have a redirect with a ticket if we passed muster.
	}
	        
	        //now test to see if we can validate with url-encoded characters.
	{
	    final CasAuthenticationRequest va2req = new CasAuthenticationRequest();
	    final String service = "https%3A%2F%2Fdataserver.tntkdware.com%2Fdataserver%2Ftoontown%2Fstaffportal%2Flogin.aspx%3FReturnUrl%3D%252fdataserver%252ftoontown%252fstaffportal%252fdefault.aspx";

	    va2req.setService(service);
	    va2req.setCookies(cookies);

	    final CasAuthenticationResponse res = (CasAuthenticationResponse) auth
		    .handleSSORequest(va2req);
	    Assert.assertNotNull("response from validate 2 is null", res);

	    final String ticket = URLUtil.extractParm(res.getLocation(),
		    "ticket");
	    log.debug("--> Ticket: " + ticket);

	    Assert.assertNotNull(ticket);
	}
	        
	        
	        
			//ok now lets try daniel's two issues:
	{
	        //sso request and the redirect should not lose our parms
	    final CasAuthenticationRequest va2req = new CasAuthenticationRequest();
	    final String service = "http%3A%2F%2Fsearch.christian-music.org%2Fsearch%3Fsite%3Ddefault_collection%26client%3Dglobal%26output%3Dxml_no_dtd%26proxystylesheet%3Dglobal";
	        va2req.setService(service);
			va2req.setCookies(cookies);
	    final CasAuthenticationResponse res = (CasAuthenticationResponse) auth
		    .handleSSORequest(va2req);
	        log.debug("And the location we got back was::: "+res.getLocation());
	        Assert.assertNotNull("response from validate is null",res);
	        Assert.assertFalse("validate returned an error",res.isError());
	        Assert.assertNotNull("did NOT get a ticket parm back",URLUtil.extractParm(res.getLocation(), Constants.CAS_TICKET));

	}

	{
	    // http://jira.mygcx.org/browse/GCX-631
	    final String service = "http%3A%2F%2Fsearch.mygcx.org%2Fsearch%3Fas_oq%3Dsite%253Awww.mygcx.org%252FGTO%26output%3Dxml_no_dtd%26client%3Dgcx_frontend%26oe%3DUTF-8%26site%3Ddefault_collection%26q%3Dmessage%2520karin";
	    final CasAuthenticationRequest va2req = new CasAuthenticationRequest();
	        va2req.setService(service);
			va2req.setCookies(cookies);
	    final CasAuthenticationResponse res = (CasAuthenticationResponse) auth
		    .handleSSORequest(va2req);
	        log.debug("And the location we got back was::: "+res.getLocation());
	        Assert.assertNotNull("response from validate is null",res);
	        Assert.assertFalse("validate returned an error",res.isError());
	        Assert.assertNotNull("did NOT get a ticket parm back",URLUtil.extractParm(res.getLocation(), Constants.CAS_TICKET));
	}
	        
	        
	        //ok now lets try the google search issue.
	        //http://search.mygcx.org/search?q=search+%26+test&btnG=Search&client=global&output=xml_no_dtd&proxystylesheet=global&oe=UTF-8&ie=UTF-8&sort=date:D:L:d1&entqr=0&ud=1&site=default_collection
	CasAuthenticationRequest va2req = new CasAuthenticationRequest();
	String service = "http://search.mygcx.org/search?q=search+%26+test";
	        va2req.setService(service);
	        
			va2req.setCookies(cookies);
	CasAuthenticationResponse res = (CasAuthenticationResponse) auth
		.handleSSORequest(va2req);
	        log.debug("And the location we got back was::: "+res.getLocation());
	        Assert.assertNotNull("response from validate is null",res);
	        Assert.assertFalse("validate returned an error",res.isError());
	        
	        log.debug("DECODED SERVICE == "+va2req.getService());
	        log.debug("LOCATION == "+res.getLocation());
	        //log.debug("EXTRACTION RESULT == "+URLUtil.extractParm(res.getLocation(), "q"));
	        
		    try{
		    	URI u = new URI(res.getLocation());
		    	Assert.assertNotNull(u);
		    }catch(Exception e){
		    	Assert.fail("Could not convert encoded url to URI: "+res.getLocation());
		    }
	        
		    
	        
		    
		    //ok now lets try the mantis parm  issue.
	        va2req = new CasAuthenticationRequest();
	        service="https%3A%2F%2Fwww.globalopsccci.org%2Fmantis%2Flogin.php%3Freturn%3D%252Fmantis%252Fview.php%253Fid%253D255";
	        
	        va2req.setService(service);
	        
			va2req.setCookies(cookies);
	        res = (CasAuthenticationResponse) auth.handleSSORequest(va2req);
	        log.debug("And the location we got back was::: "+res.getLocation());
	        Assert.assertNotNull("response from validate is null",res);
	        Assert.assertFalse("validate returned an error",res.isError());
	        
	        log.debug("DECODED SERVICE == "+va2req.getService());
	        log.debug("LOCATION == "+res.getLocation());
	        log.debug("EXTRACTION RESULT == "+URLUtil.extractParm(res.getLocation(), "i"));
	        
		    try{
		    	URI u = new URI(res.getLocation());
		    	Assert.assertNotNull(u);
		    }catch(Exception e){
		    	Assert.fail("Could not convert encoded url to URI: "+res.getLocation());
		    }

		    
		 
	        
	        //ok now try an logout request
	        CasAuthenticationRequest loreq = new CasAuthenticationRequest();
	        loreq.setCookies(cookies);
	        
	        try
	        {
	        	auth.handleLogoutRequest(loreq);
	        }catch(Exception e)
	        {
	        	Assert.assertTrue("Should never be here or else logout threw exception",false);
	        }
		}
}

