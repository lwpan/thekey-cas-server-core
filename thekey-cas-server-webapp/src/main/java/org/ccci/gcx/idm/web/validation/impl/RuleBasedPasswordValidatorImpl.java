package org.ccci.gcx.idm.web.validation.impl;

import static org.ccci.gcx.idm.web.Constants.ERROR_LOWERREQUIRED;
import static org.ccci.gcx.idm.web.Constants.ERROR_MISMATCHRETYPE;
import static org.ccci.gcx.idm.web.Constants.ERROR_NUMBERREQUIRED;
import static org.ccci.gcx.idm.web.Constants.ERROR_PASSWORD_MAXLENGTH;
import static org.ccci.gcx.idm.web.Constants.ERROR_PASSWORD_MINLENGTH;
import static org.ccci.gcx.idm.web.Constants.ERROR_SYMBOLREQUIRED;
import static org.ccci.gcx.idm.web.Constants.ERROR_UPPERREQUIRED;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gto.cas.validator.PasswordValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Provides a rule-based password validator. Parameters 
 * can be used to describe an acceptable password:
 *  
 * 	minLength = Minimum number of characters, default = 8
	haveUppercase = Uppercase letter ("A") required? 1=Yes, 0=No, default=1
	haveLowercase = Lowercase letter ("a") required? 1=Yes, 0=No, default=1
	haveSymbol =    Symbol character ("%") required? 1=Yes, 0=No, default=1
	haveNumber =    Number character ("7") required? 1=Yes, 0=No, default=1
	maxLength  =    Maximum number of characters, default=25
	minMix     =    Minimum number of items above required. For example,
					if minMix=3 then the password must have any three of
					Uppercase, Lowercase, Symbol or Number to be acceptable.
					The higher the minMix the more secure the password. Default=3
 *  
 *  
 * @author ken
 *
 */
public class RuleBasedPasswordValidatorImpl implements PasswordValidator {
	
	protected static final Log log = LogFactory.getLog(RuleBasedPasswordValidatorImpl.class);

	private XmlConfigurator config;
	private MessageSource messagesource;
	
	public void setMessageSource(MessageSource a_ms)
	{
		messagesource = a_ms;
	}

	/**
	 * expects an XmlConfigurator file using a passwords file format to configure the password options.
	 * @param a_config
	 * @throws Exception
	 */
	public void setConfigurator(XmlConfigurator a_config) throws Exception 
	{
		config = a_config;
		loadConfiguration();
	}
	
	/**
	 * reload the configuration.
	 * @param a_config
	 * @throws Exception
	 */
	public void reloadConfiguration() throws Exception
	{
		config.refresh();
		loadConfiguration();
	}
	
	private void loadConfiguration() throws Exception
	{
		blacklist = new ArrayList<String>();
		try
		{
			setMinLength(Integer.parseInt(config.getElementValue(Constants.CONFIGPASSWORD_MINLENGTH)));
			setMaxLength(Integer.parseInt(config.getElementValue(Constants.CONFIGPASSWORD_MAXLENGTH)));
			setHaveLowercase(Integer.parseInt(config.getElementValue(Constants.CONFIGPASSWORD_HAVELOWERCASE)));
			setHaveUppercase(Integer.parseInt(config.getElementValue(Constants.CONFIGPASSWORD_HAVEUPPERCASE)));
			setHaveSymbol(Integer.parseInt(config.getElementValue(Constants.CONFIGPASSWORD_HAVESYMBOL)));
			setHaveNumber(Integer.parseInt(config.getElementValue(Constants.CONFIGPASSWORD_HAVENUMBER)));
			setMinMix(Integer.parseInt(config.getElementValue(Constants.CONFIGPASSWORD_HAVEMINMIX)));
			
			setBlacklist(config.getListAsString(Constants.CONFIGPASSWORD_BLACKLIST));
			if(log.isDebugEnabled()) log.debug("Blacklisted password count = "+blacklist.size());

		}catch(Exception e)
		{
			log.error("configuration error: Cannot configure password validator.  Please check your configuration. Going ahead with the defaults which is probably too strict...",e);
			throw e;
		}

	}

	
	
	private void setBlacklist(List<String> a_blacklist) {
		if(a_blacklist != null)
			blacklist = a_blacklist;
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
	    retypeMsgs.element("comparePW", getMessage(ERROR_MISMATCHRETYPE));
	}

	// minimum length
	{
	    passwordRules.element("minlength", this.getMinLength());
	    passwordMsgs.element("minlength",
		    getMessage(ERROR_PASSWORD_MINLENGTH));
	}

	// maximum length
	{
	    passwordRules.element("maxlength", this.getMaxLength());
	    passwordMsgs.element("maxlength",
		    getMessage(ERROR_PASSWORD_MAXLENGTH));
	}

	// generate JSON for optional validation rules
	if (this.haveNumber > 0) {
	    passwordRules.element("haveNumber", true);
	    passwordMsgs
		    .element("haveNumber", getMessage(ERROR_NUMBERREQUIRED));
	}
	if (this.haveSymbol > 0) {
	    passwordRules.element("haveSymbol", true);
	    passwordMsgs
		    .element("haveSymbol", getMessage(ERROR_SYMBOLREQUIRED));
	}
	if (this.haveUppercase > 0) {
	    passwordRules.element("haveUppercase", true);
	    passwordMsgs.element("haveUppercase",
		    getMessage(ERROR_UPPERREQUIRED));
	}
	if (this.haveLowercase > 0) {
	    passwordRules.element("haveLowercase", true);
	    passwordMsgs.element("haveLowercase",
		    getMessage(ERROR_LOWERREQUIRED));
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
		return messagesource.getMessage(
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
			if(haveUppercase>0)
			{
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
			if(haveLowercase>0)
			{
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
			if(haveNumber>0)
			{
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
			if(haveSymbol>0)
			{
				if(log.isDebugEnabled())log.debug("not acceptable: password fails symbol requirement");
				return false;
			}
		}
		
		if(mixcnt<minMix)
		{
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
	
	/**
	 * Is this string on the blacklist?
	 * @param a_str
	 * @return true if blacklisted, false if not.
	 */
	public boolean isBlacklisted(String a_str)
	{
		if(log.isDebugEnabled()) log.debug("blacklisted? "+a_str);
		for(String blacklisted : blacklist)
		{
			if(blacklisted.equals(a_str))
			return true;
		}
		
		return false;
		
	}
	
	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getHaveUppercase() {
		return haveUppercase;
	}

	public void setHaveUppercase(int haveUppercase) {
		this.haveUppercase = haveUppercase;
	}

	public int getHaveLowercase() {
		return haveLowercase;
	}

	public void setHaveLowercase(int haveLowercase) {
		this.haveLowercase = haveLowercase;
	}

	public int getHaveSymbol() {
		return haveSymbol;
	}

	public void setHaveSymbol(int haveSymbol) {
		this.haveSymbol = haveSymbol;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMinMix() {
		return minMix;
	}

	public int getHaveNumber() {
		return haveNumber;
	}


	public void setHaveNumber(int haveNumber) {
		this.haveNumber = haveNumber;
	}


	public void setMinMix(int minMix) {
		this.minMix = minMix;
	}

	private int minLength=8;
	private int haveUppercase=1;
	private int haveLowercase=1;
	private int haveSymbol=1;
	private int haveNumber=1;
	private int maxLength=25;
	private int minMix=3;
	private List<String> blacklist;
}
