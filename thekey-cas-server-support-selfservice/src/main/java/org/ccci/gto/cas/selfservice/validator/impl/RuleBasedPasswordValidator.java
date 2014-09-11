package org.ccci.gto.cas.selfservice.validator.impl;

import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_LOWERREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MAXLENGTH;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MINLENGTH;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MISMATCHRETYPE;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_NUMBERREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_SYMBOLREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_UPPERREQUIRED;

import org.ccci.gto.cas.selfservice.validator.PasswordValidator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;

/**
 * Provides a rule-based password validator.
 */
public class RuleBasedPasswordValidator implements PasswordValidator {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private int minLength = 8;
    private int maxLength = 32;
    private boolean requireUppercase = false;
    private boolean requireLowercase = false;
    private boolean requireSymbol = false;
    private boolean requireNumber = false;
    private int variety = 2;
    private final HashSet<String> blacklist = new HashSet<String>();

    @NotNull
    private MessageSource messageSource;

    public void setMinLength(int length) {
	this.minLength = length;
    }

    public void setMaxLength(int length) {
	this.maxLength = length;
    }

    public void setRequireUppercase(boolean flag) {
	this.requireUppercase = flag;
    }

    public void setRequireLowercase(boolean flag) {
	this.requireLowercase = flag;
    }

    public void setRequireSymbol(boolean flag) {
	this.requireSymbol = flag;
    }

    public void setRequireNumber(boolean flag) {
	this.requireNumber = flag;
    }

    public void setCharVariety(int variety) {
	this.variety = variety;
    }

    public void setBlacklist(final Collection<String> blacklist) {
	this.blacklist.clear();
	if (blacklist != null) {
	    for (final String pw : blacklist) {
		this.blacklist.add(pw.toLowerCase());
	    }
	}
    }

    public void setMessageSource(final MessageSource source) {
	messageSource = source;
    }

    /**
     * Is this string on the blacklist?
     * 
     * @param pw
     * @return true if blacklisted, false if not.
     */
    private boolean isBlacklisted(final String pw) {
	return this.blacklist.contains(pw.toLowerCase());
    }

    private String getMessage(final String code) {
	return messageSource.getMessage(code, null,
		LocaleContextHolder.getLocale());
    }

    /**
     * uses a parameter/rules based criteria to determine if the password is
     * acceptable. parameters can be set via spring DI.
     */
    public boolean isAcceptablePassword(final String password) {
	log.debug("checking password to determine if it is acceptable");

	if (password == null) {
	    log.debug("not acceptable: password is null");
	    return false;
	}

	// test the password to determine what it contains
	final boolean validLength = password.length() >= minLength
		&& password.length() <= maxLength;
	final boolean hasUppercase = !password.equals(password.toLowerCase());
	final boolean hasLowercase = !password.equals(password.toUpperCase());
	final boolean hasNumber = password.matches(".*[0-9].*");
	final boolean hasSymbol = !password.matches("^[a-zA-Z0-9]*$");
	final int variety = (hasUppercase ? 1 : 0) + (hasLowercase ? 1 : 0)
		+ (hasNumber ? 1 : 0) + (hasSymbol ? 1 : 0);
	final boolean blacklisted = this.isBlacklisted(password);

	// Output debugging information only if the debug log is enabled
	if (log.isDebugEnabled()) {
	    if (!validLength) {
		log.debug("not acceptable: password fails length requirements");
	    }
	    if (requireUppercase && !hasUppercase) {
		log.debug("not acceptable: password fails uppercase requirement");
	    }
	    if (requireLowercase && !hasLowercase) {
		log.debug("not acceptable: password fails lowercase requirement");
	    }
	    if (requireNumber && !hasNumber) {
		log.debug("not acceptable: password fails number requirement");
	    }
	    if (requireSymbol && !hasSymbol) {
		log.debug("not acceptable: password fails symbol requirement");
	    }
	    if (variety < this.variety) {
		log.debug("not acceptable: password fails mix requirement");
	    }
	    if (blacklisted) {
		log.debug("not acceptable: password on blacklist");
	    }
	}

	// some password requirement failed, return false
	if ((requireUppercase && !hasUppercase)
		|| (requireLowercase && !hasLowercase)
		|| (requireNumber && !hasNumber)
		|| (requireSymbol && !hasSymbol) || !validLength
		|| variety < this.variety || blacklisted) {
	    return false;
	}

	return true;
    }

    /**
     * provides client javascript for password validation.
     */
    public String getValidationJavascript() {
        // generate rules and msgs JSON objects
        final JSONObject rules = new JSONObject();
        final JSONObject msgs = new JSONObject();

        // retype password matches
        msgs.put("equalTo", getMessage(ERROR_PASSWORD_MISMATCHRETYPE));

        // minimum length
        rules.put("minlength", this.minLength);
        msgs.put("minlength", getMessage(ERROR_PASSWORD_MINLENGTH));

        // maximum length
        rules.put("maxlength", this.maxLength);
        msgs.put("maxlength", getMessage(ERROR_PASSWORD_MAXLENGTH));

        // generate JSON for optional validation rules
        if (this.requireNumber) {
            rules.put("haveNumber", true);
            msgs.put("haveNumber", getMessage(ERROR_PASSWORD_NUMBERREQUIRED));
        }
        if (this.requireSymbol) {
            rules.put("haveSymbol", true);
            msgs.put("haveSymbol", getMessage(ERROR_PASSWORD_SYMBOLREQUIRED));
        }
        if (this.requireUppercase) {
            rules.put("haveUppercase", true);
            msgs.put("haveUppercase", getMessage(ERROR_PASSWORD_UPPERREQUIRED));
        }
        if (this.requireLowercase) {
            rules.put("haveLowercase", true);
            msgs.put("haveLowercase", getMessage(ERROR_PASSWORD_LOWERREQUIRED));
        }

        final JSONObject json = new JSONObject().put("rules", rules).put("messages", msgs);

        return json.toString();
    }
}
