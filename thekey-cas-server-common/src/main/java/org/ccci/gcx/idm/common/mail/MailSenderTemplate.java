package org.ccci.gcx.idm.common.mail;

import java.util.HashMap;
import java.util.Map;

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
    private String subject = null;
    private String defaultSubject = null;
    
    private final Map<String, String> resources = new HashMap<String, String>();
    
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

    public final String getSubject() {
        return this.subject;
    }

    public final void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * @return the default subject
     */
    public String getDefaultSubject() {
        return this.defaultSubject;
    }

    /**
     * @param subject
     *            the default subject to use when there isn't a localized
     *            subject
     */
    public void setDefaultSubject(final String subject) {
        this.defaultSubject = subject;
    }

    public Map<String, String> getResources() {
        return this.resources;
    }

    public void setResources(final Map<String, String> resources) {
        this.resources.clear();
        if (resources != null) {
            this.resources.putAll(resources);
        }
    }
}
