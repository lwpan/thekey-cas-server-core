package org.ccci.gto.cas.oauth.web;

import static org.ccci.gto.cas.oauth.Constants.ERROR_SERVER_ERROR;
import static org.ccci.gto.cas.oauth.Constants.ERROR_UNSUPPORTED_RESPONSE_TYPE;
import static org.ccci.gto.cas.oauth.Constants.FLOW_ATTR_CLIENT;
import static org.ccci.gto.cas.oauth.Constants.FLOW_ATTR_PARAMS;
import static org.ccci.gto.cas.oauth.Constants.FLOW_ATTR_REDIRECT_URI;
import static org.ccci.gto.cas.oauth.Constants.PARAM_AUTHORIZATION_CODE;
import static org.ccci.gto.cas.oauth.Constants.PARAM_CLIENT_ID;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ERROR;
import static org.ccci.gto.cas.oauth.Constants.PARAM_REDIRECT_URI;
import static org.ccci.gto.cas.oauth.Constants.PARAM_RESPONSE_TYPE;
import static org.ccci.gto.cas.oauth.Constants.PARAM_STATE;
import static org.ccci.gto.cas.oauth.Constants.RESPONSE_TYPE_CODE;
import static org.ccci.gto.cas.oauth.Constants.RESPONSE_TYPE_TOKEN;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;

import org.ccci.gto.cas.oauth.OAuthManager;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.webflow.execution.RequestContext;

public final class OAuthAction {
    private final static Logger LOG = LoggerFactory.getLogger(OAuthAction.class);

    @NotNull
    private AuthenticationManager authenticationManager;

    @NotNull
    private OAuthManager oauthManager;

    /**
     * We manually manage transactions in this object to workaround a
     * short-coming in the usage of Spring AOP proxies inside Webflows
     */
    @Autowired
    private PlatformTransactionManager transactionManager = null;

    /**
     * @param authenticationManager
     *            the AuthenticationManager to use
     */
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setOAuthManager(final OAuthManager oauthManager) {
        this.oauthManager = oauthManager;
    }

    public void setTransactionManager(final PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * This method will parse an incoming OAuth authorization request and set
     * the required flow attributes
     * 
     * @param context
     *            The RequestContext for the active flow
     */
    public void parseRequest(final RequestContext context) {
        // extract all parameters we care about from the initial request
        final Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_CLIENT_ID, context.getRequestParameters().get(PARAM_CLIENT_ID));
        params.put(PARAM_REDIRECT_URI, context.getRequestParameters().get(PARAM_REDIRECT_URI));
        params.put(PARAM_RESPONSE_TYPE, context.getRequestParameters().get(PARAM_RESPONSE_TYPE, RESPONSE_TYPE_CODE));
        params.put(PARAM_STATE, context.getRequestParameters().get(PARAM_STATE));
        context.getFlowScope().put(FLOW_ATTR_PARAMS, Collections.unmodifiableMap(params));

        // lookup the specified client, this is wrapped in a transaction to
        // capture and retry any deadlocks
        // TODO: actually retry on deadlocks
        Client client = null;
        final TransactionStatus tx = transactionManager != null ? transactionManager.getTransaction(null) : null;
        try {
            client = this.oauthManager.getClient(Long.parseLong(params.get(PARAM_CLIENT_ID)));

            // commit the transaction if we have one
            if (tx != null) {
                transactionManager.commit(tx);
            }
        } catch (final NumberFormatException e) {
            client = null;
        } finally {
            // rollback the transaction if it is still open
            if (tx != null && !tx.isCompleted()) {
                transactionManager.rollback(tx);
            }
        }
        context.getFlowScope().put(FLOW_ATTR_CLIENT, client);

        // determine the redirect_uri based on the request and client specified
        if (client != null) {
            URI redirectUri = null;
            try {
                final String paramUri = params.get(PARAM_REDIRECT_URI);
                final String clientUri = client.getRedirectUri();
                if (paramUri != null) {
                    redirectUri = new URI(paramUri);
                } else if (clientUri != null) {
                    redirectUri = new URI(clientUri);
                }

                // ensure we have an absolute redirect_uri
                if (redirectUri != null && !redirectUri.isAbsolute()) {
                    redirectUri = null;
                }
            } catch (final URISyntaxException e) {
                redirectUri = null;
            }
            setRedirectUri(context, redirectUri);
        }
    }

    public boolean isValidRequest(final RequestContext context) {
        final Client client = getClient(context);
        final Map<String, String> params = getParams(context);
        final URI redirectUri = getRedirectUri(context);

        // validate the redirect_uri
        if (redirectUri == null || !client.isValidRedirectUri(redirectUri)) {
            setRedirectUri(context, null);
            return false;
        }

        // validate the response_type
        switch (params.get(PARAM_RESPONSE_TYPE)) {
        case RESPONSE_TYPE_CODE:
            return true;
        case RESPONSE_TYPE_TOKEN:
        default:
            setRedirectUriParam(context, PARAM_ERROR, ERROR_UNSUPPORTED_RESPONSE_TYPE);
            return false;
        }
    }

    public final void setError(final RequestContext context, final String errorCode) {
        try {
            setRedirectUriParam(context, PARAM_ERROR, errorCode);
        } catch (final IllegalArgumentException e) {
            // suppress IllegalArgumentExceptions when we are already trying
            // to set an error code
        }
    }

    public final boolean validLt(final RequestContext context, final MessageContext messageContext) {
        // validate login ticket
        final String authoritativeLoginTicket = WebUtils.getLoginTicketFromFlowScope(context);
        final String providedLoginTicket = WebUtils.getLoginTicketFromRequest(context);
        if (!authoritativeLoginTicket.equals(providedLoginTicket)) {
            LOG.warn("Invalid login ticket " + providedLoginTicket);
            final String code = "INVALID_TICKET";
            messageContext.addMessage(new MessageBuilder().error().code(code).arg(providedLoginTicket)
                    .defaultText(code).build());
            return false;
        }

        return true;
    }

    public Authentication authenticate(final RequestContext context, final Credentials credentials,
            final MessageContext messageContext) {
        // validate login ticket, we can reuse the validLT
        if (!this.validLt(context, messageContext)) {
            return null;
        }

        // attempt to authenticate
        try {
            return this.authenticationManager.authenticate(credentials);
        } catch (final AuthenticationException e) {
            populateErrorsInstance(e, messageContext);
        }

        return null;
    }

    public final void generateAuthorizationCode(final RequestContext context, final Authentication authentication,
            final MessageContext messageContext) {
        final Client client = getClient(context);
        final String guid = AuthenticationUtil.getUser(authentication).getGUID();

        // start a transaction if we have a transaction manager
        Code code = null;
        final TransactionStatus tx = transactionManager != null ? transactionManager.getTransaction(null) : null;
        try {
            // create a new authorization code
            code = this.oauthManager.createAuthorizationCode(client, guid, null);

            // commit the transaction if we have one
            if (tx != null) {
                transactionManager.commit(tx);
            }

        } finally {
            // rollback the transaction if it is still open
            if (tx != null && !tx.isCompleted()) {
                transactionManager.rollback(tx);
            }
        }

        // set the code in the redirect_uri
        if (code != null) {
            setRedirectUriParam(context, PARAM_AUTHORIZATION_CODE, code.getCode());
        } else {
            setRedirectUriParam(context, PARAM_ERROR, ERROR_SERVER_ERROR);
        }
    }

    public final void returnState(final RequestContext context) {
        final Map<String, String> params = getParams(context);
        final String state = params.get(PARAM_STATE);

        if (state != null) {
            setRedirectUriParam(context, PARAM_STATE, state);
        }
    }

    private static Client getClient(final RequestContext context) {
        return (Client) context.getFlowScope().get(FLOW_ATTR_CLIENT);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getParams(final RequestContext context) {
        return (Map<String, String>) context.getFlowScope().get(FLOW_ATTR_PARAMS);
    }

    private static URI getRedirectUri(final RequestContext context) {
        return (URI) context.getFlowScope().get(FLOW_ATTR_REDIRECT_URI);
    }

    private static void setRedirectUri(final RequestContext context, final URI uri) {
        context.getFlowScope().put(FLOW_ATTR_REDIRECT_URI, uri);
    }

    private static void setRedirectUriParam(final RequestContext context, final String name, final String... values) {
        final UriBuilder uri = UriBuilder.fromUri(getRedirectUri(context));
        uri.replaceQueryParam(name, (Object[]) values);
        setRedirectUri(context, uri.build());
    }

    private void populateErrorsInstance(final AuthenticationException e, final MessageContext messageContext) {
        try {
            messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).defaultText(e.getCode()).build());
        } catch (final Exception fe) {
            LOG.error(fe.getMessage(), fe);
        }
    }
}
