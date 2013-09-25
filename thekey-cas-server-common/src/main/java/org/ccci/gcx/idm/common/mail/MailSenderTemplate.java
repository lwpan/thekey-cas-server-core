package org.ccci.gcx.idm.common.mail;

/**
 * <b>MailSenderTemplate</b> is a simple data value object used to encapsulate
 * template based information used to create an e-mail message.
 *
 * @author Greg Crider  December 1, 2008 5:21:26 PM
 */
public class MailSenderTemplate
{
    /** Template for plain text */
    private String m_PlainTextTemplate = null;
    /** Template for HTML */
    private String m_HtmlTemplate = null;
    /** Subject line */
    private String m_Subject = null;
    
    
    /**
     * @return the htmlTemplate
     */
    public String getHtmlTemplate()
    {
        return this.m_HtmlTemplate ;
    }
    /**
     * @param a_htmlTemplate the htmlTemplate to set
     */
    public void setHtmlTemplate( String a_htmlTemplate )
    {
        this.m_HtmlTemplate = a_htmlTemplate ;
    }
    
    
    /**
     * @return the plainTextTemplate
     */
    public String getPlainTextTemplate()
    {
        return this.m_PlainTextTemplate ;
    }
    /**
     * @param a_plainTextTemplate the plainTextTemplate to set
     */
    public void setPlainTextTemplate( String a_plainTextTemplate )
    {
        this.m_PlainTextTemplate = a_plainTextTemplate ;
    }
    
    
    /**
     * @return the subject
     */
    public String getSubject()
    {
        return this.m_Subject ;
    }
    /**
     * @param a_subject the subject to set
     */
    public void setSubject( String a_subject )
    {
        this.m_Subject = a_subject ;
    }
    
}
