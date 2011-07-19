package org.ccci.gto.cas.selfservice.validator.impl;

import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_LOWERREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MAXLENGTH;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MINLENGTH;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MISMATCHRETYPE;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_NUMBERREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_SYMBOLREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_UPPERREQUIRED;

import java.util.Collection;
import java.util.HashSet;

import javax.validation.constraints.NotNull;

import net.sf.json.JSONObject;

import org.ccci.gto.cas.selfservice.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

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
	    if (variety < variety) {
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
	// generate rules JSON objects
	final JSONObject passwordRules = new JSONObject();
	final JSONObject retypeRules = new JSONObject();

	// generate messages JSON objects
	final JSONObject passwordMsgs = new JSONObject();
	final JSONObject retypeMsgs = new JSONObject();

	// password is required
	passwordRules.element("required", true);

	// retype password matches
	{
	    retypeRules.accumulate("comparePW", "#password").accumulate(
		    "comparePW", "#retypePassword");
	    retypeMsgs.element("comparePW",
		    getMessage(ERROR_PASSWORD_MISMATCHRETYPE));
	}

	// minimum length
	{
	    passwordRules.element("minlength", this.minLength);
	    passwordMsgs.element("minlength",
		    getMessage(ERROR_PASSWORD_MINLENGTH));
	}

	// maximum length
	{
	    passwordRules.element("maxlength", this.maxLength);
	    passwordMsgs.element("maxlength",
		    getMessage(ERROR_PASSWORD_MAXLENGTH));
	}

	// generate JSON for optional validation rules
	if (this.requireNumber) {
	    passwordRules.element("haveNumber", true);
	    passwordMsgs.element("haveNumber",
		    getMessage(ERROR_PASSWORD_NUMBERREQUIRED));
	}
	if (this.requireSymbol) {
	    passwordRules.element("haveSymbol", true);
	    passwordMsgs.element("haveSymbol",
		    getMessage(ERROR_PASSWORD_SYMBOLREQUIRED));
	}
	if (this.requireUppercase) {
	    passwordRules.element("haveUppercase", true);
	    passwordMsgs.element("haveUppercase",
		    getMessage(ERROR_PASSWORD_UPPERREQUIRED));
	}
	if (this.requireLowercase) {
	    passwordRules.element("haveLowercase", true);
	    passwordMsgs.element("haveLowercase",
		    getMessage(ERROR_PASSWORD_LOWERREQUIRED));
	}

	// generate main JSON object
	final JSONObject rules = new JSONObject().element("password",
		passwordRules).element("retypePassword", retypeRules);
	final JSONObject msgs = new JSONObject().element("password",
		passwordMsgs).element("retypePassword", retypeMsgs);
	final JSONObject json = new JSONObject().element("rules", rules)
		.element("messages", msgs);

	return "$(\"#user\").validate(" + json.toString() + ");";
    }
}
