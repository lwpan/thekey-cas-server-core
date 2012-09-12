package org.ccci.gto.cas.federation.web.flow;

import static org.ccci.gto.cas.Constants.STRENGTH_FULL;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.federation.FederationException;
import org.ccci.gto.cas.federation.FederationProcessor;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.UnknownUsernameAuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

public final class FederatedAction {
    private final static Logger LOG = LoggerFactory.getLogger(FederatedAction.class);

    @NotNull
    private AuthenticationManager authenticationManager;

    private final ArrayList<FederationProcessor> federatedProcessors = new ArrayList<FederationProcessor>();

    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setFederatedProcessors(final Collection<FederationProcessor> federatedProcessors) {
        this.federatedProcessors.clear();
        if (federatedProcessors != null) {
            this.federatedProcessors.addAll(federatedProcessors);
        }
    }

    public String createNewIdentity(final RequestContext context, final Credentials federatedCredentials,
            final MessageContext messageContext) {
        // validate the login ticket
        if (!validateLoginTicket(context, messageContext)) {
            return "error";
        }

        // create a new identity from the federatedCredentials
        for (final FederationProcessor processor : this.federatedProcessors) {
            if (processor.supports(federatedCredentials)) {
                try {
                    if (processor.createIdentity(federatedCredentials, STRENGTH_FULL)) {
                        return "success";
                    }
                } catch (final FederationException e) {
                    // destroy the LT before continuing (use side-effect of
                    // retrieving the LT)
                    WebUtils.getLoginTicketFromFlowScope(context);

                    // set the return error message
                    try {
                        messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).args(e.getArgs())
                                .defaultText(e.getCode()).build());
                    } catch (final Exception me) {
                        LOG.error(me.getMessage(), me);
                    }
                    return "error";
                }
            }
        }

        return "success";
    }

    public String linkIdentities(final RequestContext context, final Credentials credentials,
            final Credentials federatedCredentials, final MessageContext messageContext) {
        // validate the login ticket
        if (!validateLoginTicket(context, messageContext)) {
            return "error";
        }

        // authenticate the provided credentials
        final GcxUser user;
        try {
            final Authentication authentication = this.authenticationManager.authenticate(credentials);
            user = AuthenticationUtil.getUser(authentication);

            // this should be handled within authentication handlers, but make
            // sure before we continue processing
            if (user == null) {
                throw UnknownUsernameAuthenticationException.ERROR;
            }
        } catch (final AuthenticationException e) {
            // destroy the LT before continuing (use side-effect of retrieving
            // the LT)
            WebUtils.getLoginTicketFromFlowScope(context);

            // set the return error message
            try {
                messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).defaultText(e.getCode())
                        .build());
            } catch (final Exception me) {
                LOG.error(me.getMessage(), me);
            }
            return "error";
        }

        // link the federatedCredentials to the authenticated identity
        for (final FederationProcessor processor : this.federatedProcessors) {
            if (processor.supports(federatedCredentials)) {
                try {
                    if (processor.linkIdentity(user, federatedCredentials, STRENGTH_FULL)) {
                        return "success";
                    }
                } catch (final FederationException e) {
                    // destroy the LT before continuing (use side-effect of
                    // retrieving the LT)
                    WebUtils.getLoginTicketFromFlowScope(context);

                    // set the return error message
                    try {
                        messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).args(e.getArgs())
                                .defaultText(e.getCode()).build());
                    } catch (final Exception me) {
                        LOG.error(me.getMessage(), me);
                    }
                    return "error";
                }
            }
        }

        // no linking was successful, return an error, destroy the LT before
        // continuing (use side-effect of retrieving the LT)
        WebUtils.getLoginTicketFromFlowScope(context);
        return "error";
    }

    // method that will validate a login ticket
    private boolean validateLoginTicket(final RequestContext context, final MessageContext messageContext) {
        // Validate login ticket
        final String authoritativeLoginTicket = WebUtils.getLoginTicketFromFlowScope(context);
        final String providedLoginTicket = WebUtils.getLoginTicketFromRequest(context);
        if (authoritativeLoginTicket.equals(providedLoginTicket)) {
            // put the login ticket back in the flow scope for subsequent
            // actions
            WebUtils.putLoginTicket(context, authoritativeLoginTicket);
            return true;
        } else {
            LOG.warn("Invalid login ticket {}", providedLoginTicket);
            final String code = "INVALID_TICKET";
            messageContext.addMessage(new MessageBuilder().error().code(code).arg(providedLoginTicket)
                    .defaultText(code).build());
            return false;
        }
    }
}
