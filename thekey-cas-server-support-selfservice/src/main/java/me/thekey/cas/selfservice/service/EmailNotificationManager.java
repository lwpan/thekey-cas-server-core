package me.thekey.cas.selfservice.service;

import static me.thekey.cas.selfservice.Constants.PARAMETER_EMAIL;
import static me.thekey.cas.selfservice.Constants.PARAMETER_KEY;
import static me.thekey.cas.selfservice.Constants.PARAMETER_PROPOSED_EMAIL;

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

    @NotNull
    private UriBuilder resetPasswordUri;

    @NotNull
    private UriBuilder changeEmailUri;

    private String replyTo;

    // mail templates
    private MailSenderTemplate emailVerificationTemplate;
    private MailSenderTemplate resetPasswordTemplate;
    private MailSenderTemplate changeEmailTemplate;

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

    public final void setResetPasswordTemplate(final MailSenderTemplate template) {
        this.resetPasswordTemplate = template;
    }

    public final void setChangeEmailTemplate(final MailSenderTemplate template) {
        this.changeEmailTemplate = template;
    }

    public final void setVerificationUri(final String uri) {
        this.verificationUri = UriBuilder.fromUri(uri);
        this.verificationUri.replaceQueryParam(PARAMETER_EMAIL, "{" + PARAMETER_EMAIL + "}");
        this.verificationUri.replaceQueryParam(PARAMETER_KEY, "{" + PARAMETER_KEY + "}");
    }

    public final void setResetPasswordUri(final String uri) {
        this.resetPasswordUri = UriBuilder.fromUri(uri);
        this.resetPasswordUri.replaceQueryParam(PARAMETER_EMAIL, "{" + PARAMETER_EMAIL + "}");
        this.resetPasswordUri.replaceQueryParam(PARAMETER_KEY, "{" + PARAMETER_KEY + "}");
    }

    public final void setChangeEmailUri(final String uri) {
        this.changeEmailUri = UriBuilder.fromUri(uri);
        this.changeEmailUri.replaceQueryParam(PARAMETER_EMAIL, "{" + PARAMETER_EMAIL + "}");
        this.changeEmailUri.replaceQueryParam(PARAMETER_KEY, "{" + PARAMETER_KEY + "}");
        this.changeEmailUri.replaceQueryParam(PARAMETER_PROPOSED_EMAIL, "{" + PARAMETER_PROPOSED_EMAIL + "}");
    }

    @Override
    public void sendEmailVerificationMessage(final GcxUser user, final Locale locale, final String uriParams) {
        final OutgoingMailMessage message = this.buildBaseMessage(user, locale);

        // set the verification uri
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(PARAMETER_EMAIL, user.getEmail());
        values.put(PARAMETER_KEY, user.getSignupKey());
        final URI uri = this.verificationUri.buildFromMap(values);
        message.addToModel("verificationUri", uri.toString()
                + (StringUtils.isNotBlank(uriParams) ? "&" + uriParams : ""));

        // send the message
        this.mailSender.send(this.emailVerificationTemplate, message);
    }

    @Override
    public void sendResetPasswordMessage(final GcxUser user, final Locale locale, final String uriParams) {
        final OutgoingMailMessage message = this.buildBaseMessage(user, locale);

        // set the reset password uri
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(PARAMETER_EMAIL, user.getEmail());
        values.put(PARAMETER_KEY, user.getResetPasswordKey());
        final URI uri = this.resetPasswordUri.buildFromMap(values);
        message.addToModel("resetPasswordUri", uri.toString()
                + (StringUtils.isNotBlank(uriParams) ? "&" + uriParams : ""));

        // send the message
        this.mailSender.send(this.resetPasswordTemplate, message);
    }

    @Override
    public void sendChangeEmailMessage(final GcxUser user, final Locale locale, final String uriParams) {
        final OutgoingMailMessage message = this.buildBaseMessage(user, locale);
        message.setTo(user.getProposedEmail());

        // set the verification uri
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(PARAMETER_EMAIL, user.getEmail());
        values.put(PARAMETER_KEY, user.getChangeEmailKey());
        values.put(PARAMETER_PROPOSED_EMAIL, user.getProposedEmail());
        final URI uri = this.changeEmailUri.buildFromMap(values);
        message.addToModel("changeEmailUri", uri.toString()
                + (StringUtils.isNotBlank(uriParams) ? "&" + uriParams : ""));

        // send the message
        this.mailSender.send(this.changeEmailTemplate, message);
    }

    private OutgoingMailMessage buildBaseMessage(final GcxUser user, final Locale locale) {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);

        final OutgoingMailMessage message = new OutgoingMailMessage();
        message.setFrom(this.replyTo);
        message.setTo(user.getEmail());
        message.setMessageSource(this.messageSource);
        message.setLocale(locale != null ? locale : Locale.US);
        message.setModel(model);

        return message;
    }
}
