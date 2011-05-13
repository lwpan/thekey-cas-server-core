package org.ccci.gto.cas.web.flow;

import org.ccci.gto.cas.Constants;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.web.bind.CredentialsBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.DefaultMessageResolver;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.message.MessageResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.RequestContext;

@SuppressWarnings("deprecation")
public class AuthenticationViaFormAction {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    final org.jasig.cas.web.flow.AuthenticationViaFormAction delegate = new org.jasig.cas.web.flow.AuthenticationViaFormAction();

    public String submit(final RequestContext context,
	    final Credentials credentials, final MessageContext messageContext)
	    throws Exception {
	// issue underlying request, intercepting any MessageResolvers looking
	// for the stale password error code
	final IntMessageContext intMsgContext = new IntMessageContext(
		messageContext);
	final String response = delegate.submit(context, credentials,
		intMsgContext);

	// return the stalePassword state if a stalePassword error was thrown
	if (response.equals("error") && intMsgContext.stalePassword) {
	    return "stalePassword";
	}

	return response;
    }

    public void doBind(final RequestContext context,
	    final Credentials credentials) throws Exception {
	delegate.doBind(context, credentials);
    }

    public void setCentralAuthenticationService(
	    final CentralAuthenticationService centralAuthenticationService) {
	delegate.setCentralAuthenticationService(centralAuthenticationService);
    }

    public void setCredentialsBinder(final CredentialsBinder credentialsBinder) {
	delegate.setCredentialsBinder(credentialsBinder);
    }

    public void setWarnCookieGenerator(final CookieGenerator warnCookieGenerator) {
	delegate.setWarnCookieGenerator(warnCookieGenerator);
    }

    private class IntMessageContext implements MessageContext {
	private final MessageContext delegate;
	private boolean stalePassword = false;

	public IntMessageContext(final MessageContext delegate) {
	    this.delegate = delegate;
	}

	public Message[] getAllMessages() {
	    return delegate.getAllMessages();
	}

	public Message[] getMessagesBySource(final Object source) {
	    return delegate.getMessagesBySource(source);
	}

	public Message[] getMessagesByCriteria(final MessageCriteria criteria) {
	    return delegate.getMessagesByCriteria(criteria);
	}

	public boolean hasErrorMessages() {
	    return delegate.hasErrorMessages();
	}

	public void addMessage(final MessageResolver messageResolver) {
	    delegate.addMessage(messageResolver);

	    // check to see if the MessageResolver is for the stale password
	    // error
	    if (messageResolver instanceof DefaultMessageResolver) {
		for (String code : ((DefaultMessageResolver) messageResolver)
			.getCodes()) {
		    if (code.equals(Constants.ERROR_STALEPASSWORD)) {
			stalePassword = true;
		    }
		}
	    }
	}

	public void clearMessages() {
	    delegate.clearMessages();
	}
    }
}
