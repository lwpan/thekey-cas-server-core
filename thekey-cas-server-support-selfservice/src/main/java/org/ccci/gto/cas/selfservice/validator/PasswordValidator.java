package org.ccci.gto.cas.selfservice.validator;

/**
 * A PasswordValidator ensures that a string meets the acceptable password
 * criteria as defined by the implementing validator.
 */
public interface PasswordValidator {
    /**
     * Determines if the password string supplied is acceptable according to the
     * implemented criteria.
     * 
     * @param password
     * @return
     */
    public boolean isAcceptablePassword(final String password);

    /**
     * returns the javascript fragment that can be used on the client-side to
     * verify password validity
     * 
     * @return
     */
    public String getValidationJavascript();
}
