package org.ccci.gcx.idm.web.validation.impl;

import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_LOWERREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MAXLENGTH;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MINLENGTH;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_MISMATCHRETYPE;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_NUMBERREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_SYMBOLREQUIRED;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_PASSWORD_UPPERREQUIRED;

import java.util.ArrayList;
import java.util.List;

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
public class RuleBasedPasswordValidatorImpl implements PasswordValidator {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private int minLength = 8;
    private int maxLength = 32;
    private boolean requireUppercase = false;
    private boolean requireLowercase = false;
    private boolean requireSymbol = false;
    private boolean requireNumber = false;
    private int variety = 2;
    private final ArrayList<String> blacklist = new ArrayList<String>();

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

    public void setBlacklist(final List<String> blacklist) {
	this.blacklist.clear();
	if (blacklist != null) {
	    this.blacklist.addAll(blacklist);
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
	for (final String blacklisted : this.blacklist) {
	    if (blacklisted.equalsIgnoreCase(pw)) {
		return true;
	    }
	}

	return false;
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

	private String getMessage(String code)
	{
	return messageSource.getMessage(
				code,
				null,
				LocaleContextHolder.getLocale()
			);
	}

	/**
	 * uses a parameter/rules based criteria to determine if the password is acceptable. 
	 * parameters can be set via spring DI.
	 */
	public boolean isAcceptablePassword(String a_str)
	{

		
		int mixcnt = 0;
		
		if(a_str == null) 
		{
			if(log.isDebugEnabled())log.debug("not acceptable: password is null");
			return false;
		}
		
		if(a_str.length()<minLength || a_str.length()>maxLength)
		{
			if(log.isDebugEnabled())log.debug("not acceptable: password fails length requirements");
			return false;
		}
		
		//check for upper case
		if(!a_str.equals(a_str.toLowerCase()))
		{	
			mixcnt++;
		}
		else
		{
			if(log.isDebugEnabled())log.debug("No upper case."+a_str);
	    if (requireUppercase) {
				if(log.isDebugEnabled())log.debug("not acceptable: password fails uppercase requirement");
				return false;
			}
		}
		
		if(!a_str.equals(a_str.toUpperCase()))
		{
			mixcnt++;
		}
		else
		{
	    if (requireLowercase) {
				if(log.isDebugEnabled())log.debug("not acceptable: password fails lowercase requirement");
				return false;
			}
		}
		
		if(a_str.matches(".*[0-9].*"))
		{
			mixcnt++;
		}
		else
		{
	    if (requireNumber) {
				if(log.isDebugEnabled())log.debug("not acceptable: password fails number requirement");
				return false;
			}
		}
		
		if(!a_str.matches("^[a-zA-Z0-9]*$"))
		{
			mixcnt++;
		}
		else
		{
	    if (requireSymbol) {
				if(log.isDebugEnabled())log.debug("not acceptable: password fails symbol requirement");
				return false;
			}
		}
		
	if (mixcnt < variety) {
			if(log.isDebugEnabled())log.debug("not acceptable: password fails mix requirement");
			return false;
		}
		
		if(isBlacklisted(a_str))
		{
			if(log.isDebugEnabled())log.debug("not acceptable: password on blacklist");
			return false;
		}
		
		return true;
		
	}
}
