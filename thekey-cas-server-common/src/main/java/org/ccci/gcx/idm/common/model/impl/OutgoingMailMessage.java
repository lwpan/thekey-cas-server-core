package org.ccci.gcx.idm.common.model.impl;

import java.io.Serializable;
import java.util.Map;

/**
 * <b>OutgoingMailMessage</b> represents a single, outgoing e-mail message transmission
 * record.
 *
 * @author Greg Crider  December 1, 2008  11:06:28 AM
 */
public class OutgoingMailMessage implements Serializable {
    private static final long serialVersionUID = -2881295410275256320L ;

    /** Recipient of message */
    private String m_To = null ;
    
    // NOT PERSISTED
    
    /** Reply-to address */
    private String m_ReplyTo = null ;
    /** Blind carbon copy recipient */
    private String m_Bcc = null ;
    /** Carbon copy recipient */
    private String m_Cc = null ;
    /** Message content model; used to fill e-mail template. */
    private Map<String,Object> m_MessageContentModel = null ;
    /** 
     * Attached resource content model; used to create attachments from resources found
     * along the classpath.
     */
    private Map<String,String> m_ResourceContentModel = null ;
    
    
    /**
     * @return the bcc
     */
    public String getBcc()
    {
        return this.m_Bcc ;
    }
    /**
     * @param a_bcc the bcc to set
     */
    public void setBcc( String a_bcc )
    {
        this.m_Bcc = a_bcc ;
    }
    
    
    /**
     * @return the cc
     */
    public String getCc()
    {
        return this.m_Cc ;
    }
    /**
     * @param a_cc the cc to set
     */
    public void setCc( String a_cc )
    {
        this.m_Cc = a_cc ;
    }
    
    
    /**
     * @return the messageContentModel
     */
    public Map<String,Object> getMessageContentModel()
    {
        return this.m_MessageContentModel ;
    }
    /**
     * @param a_messageContentModel the messageContentModel to set
     */
    public void setMessageContentModel( Map<String,Object> a_messageContentModel )
    {
        this.m_MessageContentModel = a_messageContentModel ;
    }
    
    
    /**
     * @return the replyTo
     */
    public String getReplyTo()
    {
        return this.m_ReplyTo ;
    }
    /**
     * @param a_replyTo the replyTo to set
     */
    public void setReplyTo( String a_replyTo )
    {
        this.m_ReplyTo = a_replyTo ;
    }
    
    
    /**
     * @return the resourceContentModel
     */
    public Map<String,String> getResourceContentModel()
    {
        return this.m_ResourceContentModel ;
    }
    /**
     * @param a_resourceContentModel the resourceContentModel to set
     */
    public void setResourceContentModel( Map<String,String> a_resourceContentModel )
    {
        this.m_ResourceContentModel = a_resourceContentModel ;
    }
    
    
    /**
     * @return the to
     */
    public String getTo()
    {
        return this.m_To ;
    }
    /**
     * @param a_to the to to set
     */
    public void setTo( String a_to )
    {
        this.m_To = a_to ;
    }
}
