package org.ccci.gcx.idm.core.model.impl;

import java.util.Date;

import org.ccci.gcx.idm.common.model.impl.AbstractModelObject;
import org.ccci.gcx.idm.core.model.type.impl.AuditActionTypeCode;

/**
 * <b>Audit</b> holds the contents of a single audit record.
 *
 * @author Greg Crider  Oct 16, 2008  7:26:27 PM
 */
public class Audit extends AbstractModelObject
{
    private static final long serialVersionUID = -7229160490997576500L ;

    /** Source responsible for generating the audit; this could be an application or web service name */
    private String m_Source = null ;
    /** Userid of user responsible for altering the audited object */
    private String m_ChangedBy = null ;
    /** Date the change was made */
    private Date m_ChangeDate = null ;
    
    /** Userid associated with changed object */
    private String m_Userid = null ;
  
    /** Action taken */
    private String m_Action = null ;
    /** Description of action taken */
    private String m_Description = null ;

    // OPTIONAL: used if an individual property (field) was changed
  
    /** Object or class type that is being audited */
    private String m_ObjectType = null ;
    /** Property name that was altered */
    private String m_Name = null ;
    /** Previous, or old value before alteration */
    private String m_OldValue = null ;
    /** Current value */
    private String m_CurrentValue = null ;
    
    
    /**
     * @return the source
     */
    public String getSource()
    {
        return this.m_Source ;
    }
    /**
     * @param a_source the source to set
     */
    public void setSource( String a_source )
    {
        this.m_Source = a_source ;
    }
    
    
    /**
     * @return the changedBy
     */
    public String getChangedBy()
    {
        return this.m_ChangedBy ;
    }
    /**
     * @param a_changedBy the changedBy to set
     */
    public void setChangedBy( String a_changedBy )
    {
        this.m_ChangedBy = a_changedBy ;
    }
    
    
    /**
     * @return the changeDate
     */
    public Date getChangeDate()
    {
        return this.m_ChangeDate ;
    }
    /**
     * @param a_changeDate the changeDate to set
     */
    public void setChangeDate( Date a_changeDate )
    {
        this.m_ChangeDate = a_changeDate ;
    }
    
    
    /**
     * @return the userid
     */
    public String getUserid()
    {
        return this.m_Userid ;
    }
    /**
     * @param a_userid the userid to set
     */
    public void setUserid( String a_userid )
    {
        this.m_Userid = a_userid ;
    }
    
    
    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return this.m_ObjectType ;
    }
    /**
     * @param a_objectType the objectType to set
     */
    public void setObjectType( String a_objectType )
    {
        this.m_ObjectType = a_objectType ;
    }
    
    
    /**
     * @return the action
     */
    public String getAction()
    {
        return this.m_Action ;
    }
    /**
     * @param a_action the action to set
     */
    public void setAction( String a_action )
    {
        this.m_Action = a_action ;
    }
    public AuditActionTypeCode getAuditActionTypeCode()
    {
        return (AuditActionTypeCode)this.getAbstractTypeCode( this.m_Action, AuditActionTypeCode.NONE, AuditActionTypeCode.class ) ;
    }
    public void setAuditActionTypeCode( AuditActionTypeCode a_AuditActionTypeCode )
    {
        this.m_Action = a_AuditActionTypeCode.codeToString() ;
    }

    
    /**
     * @return the description
     */
    public String getDescription()
    {
        return this.m_Description ;
    }
    /**
     * @param a_description the description to set
     */
    public void setDescription( String a_description )
    {
        this.m_Description = a_description ;
    }
    
    
    /**
     * @return the name
     */
    public String getName()
    {
        return this.m_Name ;
    }
    /**
     * @param a_name the name to set
     */
    public void setName( String a_name )
    {
        this.m_Name = a_name ;
    }
    
    
    /**
     * @return the oldValue
     */
    public String getOldValue()
    {
        return this.m_OldValue ;
    }
    /**
     * @param a_oldValue the oldValue to set
     */
    public void setOldValue( String a_oldValue )
    {
        this.m_OldValue = a_oldValue ;
    }
    
    
    /**
     * @return the currentValue
     */
    public String getCurrentValue()
    {
        return this.m_CurrentValue ;
    }
    /**
     * @param a_currentValue the currentValue to set
     */
    public void setCurrentValue( String a_currentValue )
    {
        this.m_CurrentValue = a_currentValue ;
    }
    
    
}
