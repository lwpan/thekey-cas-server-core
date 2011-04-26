package org.ccci.gcx.idm.web.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gcx.idm.web.validation.PasswordValidator;
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
	public String getClientJavascript()
	{
		StringBuffer validate = new StringBuffer();
		
		validate
		.append("$(\"#user\").validate({\n")
		.append("rules:{\n") 
		.append("		retypePassword:{\n")
		//.append("			required:true,\n")
		//.append("			equalTo:\"#password\"\n")
		.append("           comparePW: [\"#password\",\"#retypePassword\"]\n")
		.append("		},\n")
		.append("		password:{\n")
		.append("			required:true,\n")
		.append("			minlength:" + this.getMinLength() +",\n")
		.append("			maxlength:" + this.getMaxLength());
		
		String separator = ",\n";
		boolean needseparator = true;
		
		StringBuffer msgs = new StringBuffer(); //lets do both at the same time.
			msgs.append("messages:{ \n")
			.append("	retypePassword:{\n")
			//.append("	required:\""+getMessage(Constants.ERROR_RETYPEREQUIRED)+"\",\n")
			.append("	comparePW:\""+getMessage(Constants.ERROR_MISMATCHRETYPE)+"\"\n")
			.append("	},  \n");
			
			 msgs.append("password:{ \n")
			//.append("	required:\""+getMessage(Constants.ERROR_PASSWORDREQUIRED)+"\",\n")
			.append("	minlength:\""+getMessage(Constants.ERROR_PASSWORD_MINLENGTH)+"\",\n")
			.append("	maxlength:\""+getMessage(Constants.ERROR_PASSWORD_MAXLENGTH)+"\",\n");
		
		if(this.haveNumber>0)
		{
			appendField(needseparator, separator,"	haveNumber:true",validate);
			msgs.append("	haveNumber:\""+getMessage(Constants.ERROR_NUMBERREQUIRED)+"\"");
		}
		
		if(this.haveSymbol>0)
		{
			appendField(needseparator, separator,"	haveSymbol:true",validate);
			msgs.append("	haveSymbol:\""+getMessage(Constants.ERROR_SYMBOLREQUIRED)+"\",\n");
		}
		
		if(this.haveUppercase>0)
		{
			appendField(needseparator, separator,"	haveUppercase:true",validate);
			msgs.append("	haveUpper:\""+getMessage(Constants.ERROR_UPPERREQUIRED)+"\",\n");
		}
		
		if(this.haveLowercase>0)
		{
			appendField(needseparator, separator,"	haveLowercase:true",validate);
			msgs.append("	haveLower:\""+getMessage(Constants.ERROR_LOWERREQUIRED)+"\",\n");
		}
		
		validate.append("}\n  \n },\n");
		
		validate.append(msgs);
		
		validate.append("\n	}\n");
		
		validate.append("}  \n");
		
		validate.append("});\n  ");
		
					
		return validate.toString();
	}

	private String getMessage(String code)
	{
		return messagesource.getMessage(
				code,
				null,
				LocaleContextHolder.getLocale()
			);
	}
	
	private void appendField(boolean needseparator, String separator, String field, StringBuffer validate)
	{
		if(needseparator)
		{
			validate.append(separator);
		}

		validate.append(field);
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
