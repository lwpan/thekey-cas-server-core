package org.ccci.gcx.idm.web;

import java.io.Serializable;

import org.jasig.cas.authentication.Authentication;

/**
 * Represents the user related fields on a form for
 * login and signup.
 * 
 * @author ken
 *
 */
public class SimpleLoginUser implements Serializable {
	private static final long serialVersionUID = 6389858010249925207L;

	private String username;
	private String password;
	private String retypePassword;
	private String firstName;
	private String lastName;
	private String securityQuestion;
	private String securityAnswer;
    private Authentication authentication;

	public String getLOGINSESSIONATTRIBUTE(){
		return Constants.SESSIONATTRIBUTE_LOGIN;
	}

    public Authentication getAuthentication() {
	return this.authentication;
    }

    public boolean isAuthenticated() {
	return this.authentication != null;
    }

    public void setAuthentication(final Authentication auth) {
	this.authentication = auth;
    }

	public String getRetypePassword() {
		return retypePassword;
	}

	public void setRetypePassword(String retypePassword) {
		this.retypePassword = retypePassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

	public void setUsername(String a_username)
	{
		username = a_username;
	}
	
	public void setPassword(String a_password)
	{
		password = a_password;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}

}
