package me.thekey.cas.selfservice.service;

import java.util.Locale;

import org.ccci.gcx.idm.core.model.impl.GcxUser;

public interface NotificationManager {
    void sendEmailVerificationMessage(GcxUser user, Locale locale, String uriParams);

    void sendResetPasswordMessage(GcxUser user, Locale locale, String uriParams);

    void sendChangeEmailMessage(GcxUser user, Locale locale, String uriParams);
}
