package me.thekey.cas.selfservice.service;

import static me.thekey.cas.selfservice.Constants.PARAMETER_VERIFICATION_KEY;
import static me.thekey.cas.selfservice.Constants.PARAMETER_VERIFICATION_USERNAME;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.common.mail.MailSender;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.springframework.context.MessageSource;

public class EmailNotificationManager implements NotificationManager {
    @NotNull
    private MailSender mailSender;

    @NotNull
    private MessageSource messageSource;

    @NotNull
    private UriBuilder verificationUri;

    private String replyTo;

    // mail templates
    private MailSenderTemplate emailVerificationTemplate;

    public final void setMailSender(final MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public final void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public final void setReplyTo(final String replyTo) {
        this.replyTo = replyTo;
    }

    public final void setEmailVerificationTemplate(final MailSenderTemplate template) {
        this.emailVerificationTemplate = template;
    }

    public final void setVerificationUri(final String uri) {
        this.verificationUri = UriBuilder.fromUri(uri);
        this.verificationUri.replaceQueryParam(PARAMETER_VERIFICATION_USERNAME, "{arg1}");
        this.verificationUri.replaceQueryParam(PARAMETER_VERIFICATION_KEY, "{arg2}");
    }

    @Override
    public void sendEmailVerificationMessage(final GcxUser user, final Locale locale, final String uriParams) {
        final OutgoingMailMessage message = this.buildBaseMessage(user, locale);

        // set the verification uri
        final URI uri = this.verificationUri.build(user.getEmail(), user.getSignupKey());
        message.addToModel("verificationUri", uri.toString()
                + (StringUtils.isNotBlank(uriParams) ? "&" + uriParams : ""));

        // send the message
        this.mailSender.send(this.emailVerificationTemplate, message);
    }

    private OutgoingMailMessage buildBaseMessage(final GcxUser user, Locale locale) {
        if (locale == null) {
            locale = Locale.US;
        }

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("locale", locale);
        model.put("messageSource", this.messageSource);

        final OutgoingMailMessage message = new OutgoingMailMessage();
        message.setTo(user.getEmail());
        message.setFrom(this.replyTo);
        message.setModel(model);

        return message;
    }
}