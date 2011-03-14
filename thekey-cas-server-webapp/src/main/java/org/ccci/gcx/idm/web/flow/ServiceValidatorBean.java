package org.ccci.gcx.idm.web.flow;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationRequest;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationResponse;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.AuthenticationService;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.AttributeXMLComposer;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.IdmUtil;
import org.springframework.webflow.context.ExternalContext;


/**
 * Provides service validation for SSO.
 * @author ken
 *
 */
public class ServiceValidatorBean {

	protected static final Log log = LogFactory.getLog(ServiceValidatorBean.class);

	//DI
	private AuthenticationService authservice;
	public void setAuthenticationService (AuthenticationService a_svc)
	{
		authservice = a_svc;
	}
	private AttributeXMLComposer ac;
	public void setAttributeXMLComposer(AttributeXMLComposer a_ac)
	{
		ac = a_ac;
	}
	private GcxUserService gcxuserservice;
	public void setGcxUserService(GcxUserService a_svc)
	{
		gcxuserservice = a_svc;
	}

	/**
	 * Validates the service parameter given (REQUIRED) against CAS
	 * Then we look up the user and add our custom attributes to the results and 
	 * store them in the "message" session variable. 
	 * 
	 * @param a_externalContext
	 * @return
	 * @throws Exception
	 */
	public boolean processServiceValidation(ExternalContext a_externalContext) throws Exception
	{
		HttpServletRequest request = (HttpServletRequest) a_externalContext.getNativeRequest();
        
		CasAuthenticationRequest casrequest = IdmUtil.buildCasAuthenticationRequest(request);
        
        CasAuthenticationResponse casresponse = (CasAuthenticationResponse) authservice.handleServiceValidateRequest((casrequest));

        if(casresponse.isError())
        {
            request.getSession().setAttribute(Constants.GLOBALMESSAGEPARAMETER, casresponse.getContent());
            return false;
        }
        
        GcxUser gcxuser;
        
        try{
        	gcxuser = gcxuserservice.findUserByEmail(IdmUtil.extractEmail(casresponse.getContent()));
        }catch(Exception e)
        {
        	log.warn("gcxuserservice generated an exception. means we didn't get a user.");            
            request.getSession().setAttribute(Constants.GLOBALMESSAGEPARAMETER, casresponse.getContent());
            return false;
        }

        if(log.isDebugEnabled()) log.debug("Found a user from the validation message: "+gcxuser.getEmail());

	// add domain visited if it isn't there already.
	IdmUtil.addDomainVisited(gcxuser, casrequest.getService(),
		gcxuserservice, Constants.SOURCEIDENTIFIER_SERVICEVALIDATOR);

	StringBuffer message = new StringBuffer(casresponse.getContent());
        message.insert(message.lastIndexOf(Constants.CAS_ATTRIBUTE_USER_CLOSE)
        		+ Constants.CAS_ATTRIBUTE_USER_CLOSE.length(), 
        	ac.composeUserAttributes(gcxuser, casresponse.getService()));

        if(log.isDebugEnabled()) log.debug("READY to return attributes: \n "+message.toString());
        
        request.getSession().setAttribute(Constants.GLOBALMESSAGEPARAMETER, message.toString());
        
        return true;
        
	}
}
