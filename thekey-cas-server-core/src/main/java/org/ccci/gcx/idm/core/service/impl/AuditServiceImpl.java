package org.ccci.gcx.idm.core.service.impl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.common.IdmException;
import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.core.model.impl.Audit;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.model.type.impl.AuditActionTypeCode;
import org.ccci.gcx.idm.core.service.AuditService;
import org.ccci.gto.audit.model.Auditable;

/**
 * <b>AuditServiceImpl</b> is the concrete implementation of {@link AuditService}.
 *
 * @author Greg Crider  Oct 19, 2008  9:17:45 PM
 */
public class AuditServiceImpl extends AbstractAuditService implements AuditService
{
    /**
     * Create a new audit based on the specified, generic information.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Entity New entity that is being audited.
     * 
     * @see org.ccci.gcx.idm.core.service.AuditService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
     */
    public void create( String a_Source, String a_ChangedBy, String a_Userid, String a_Description,
            Object a_Entity )
    {
        Audit audit = new Audit() ;
        
        Date currentDate = new Date() ;
        
        String objectType = a_Entity.getClass().getName().substring( a_Entity.getClass().getName().lastIndexOf( "." ) + 1 ) ;
        
        audit.setCreateDate( currentDate ) ;
        audit.setAuditActionTypeCode( AuditActionTypeCode.CREATE ) ;
        audit.setSource( a_Source ) ;
        audit.setChangeDate( currentDate ) ;
        audit.setChangedBy( a_ChangedBy ) ;
        audit.setUserid( a_Userid ) ;
        audit.setDescription( a_Description ) ;
        audit.setObjectType( objectType ) ;
        
        if ( log.isDebugEnabled() ) log.debug( "Creating new Audit: " + audit ) ;
        
        this.getAuditDao().save( audit ) ;
        
        if ( log.isDebugEnabled() ) log.debug( "Audit saved" ) ;
    }
    
    
    /**
     * Audit an object that is being deleted.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Entity New entity that is being audited.
     */
    public void delete( String a_Source, String a_ChangedBy, String a_Userid, String a_Description, Object a_Entity )
    {
        Audit audit = new Audit() ;
        
        Date currentDate = new Date() ;
        
        String objectType = a_Entity.getClass().getName().substring( a_Entity.getClass().getName().lastIndexOf( "." ) + 1 ) ;
        
        audit.setCreateDate( currentDate ) ;
        audit.setAuditActionTypeCode( AuditActionTypeCode.DELETE ) ;
        audit.setSource( a_Source ) ;
        audit.setChangeDate( currentDate ) ;
        audit.setChangedBy( a_ChangedBy ) ;
        audit.setUserid( a_Userid ) ;
        audit.setDescription( a_Description ) ;
        audit.setObjectType( objectType ) ;
        
        if ( log.isDebugEnabled() ) log.debug( "Creating new Audit: " + audit ) ;
        
        this.getAuditDao().save( audit ) ;
        
        if ( log.isDebugEnabled() ) log.debug( "Audit saved" ) ;
    }
    
    
    /**
     * Audit an object that was updated.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Original Original version of object before change was made.
     * @param a_Current Current version of object with new changes to it.
     */
    public void update(String a_Source, String a_ChangedBy, String a_Userid,
	    String a_Description, Auditable a_Original, Auditable a_Current) {
        List<ModelObject> audits = new ArrayList<ModelObject>() ;
        
        Date currentDate = new Date() ;
        
        String objectType = a_Current.getClass().getName().substring( a_Current.getClass().getName().lastIndexOf( "." ) + 1 ) ;
        
        // Make sure the two model objects are compatible
        if ( !a_Original.getClass().isAssignableFrom( a_Current.getClass() ) ) {
            String error = "Current model object is not equal to or a subclass of \"" + a_Original.getClass() + "\"." ;
            /*= ERROR =*/ log.error( error ) ;
            throw new IdmException( error ) ;
        }
        
        // Check each auditable property for a change
        for( int i=0; ( a_Original.getAuditProperties() != null ) && ( i<a_Original.getAuditProperties().length); i++ ) {
            String name = a_Original.getAuditProperties()[i] ;
            Object originalValue = null ;
            Object currentValue = null ;
            boolean wasChanged = false ;
            try {
                originalValue = BeanUtils.getProperty( a_Original, name ) ;
                currentValue = BeanUtils.getProperty( a_Current, name ) ;
                PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor( a_Current, name ) ;
                // For String objects, make sure the change is necessary and not based on padding
                // Do String based comparison to pick up on null/whitespace type distinctions
                if ( String.class.isAssignableFrom( descriptor.getPropertyType() ) ) {
                    originalValue = StringUtils.trim( (String)originalValue ) ;
                    currentValue = StringUtils.trim( (String)currentValue ) ;
                    wasChanged = ( ( ( !StringUtils.isBlank( (String)originalValue ) ) && ( !originalValue.equals( currentValue ) ) ) ||
                                   ( (  StringUtils.isBlank( (String)originalValue ) ) && ( !StringUtils.isBlank( (String)currentValue ) ) ) ) ;
                    // For all other object types, use basic comparators
                } else {
                    wasChanged = ( ( ( originalValue != null ) && ( !originalValue.equals( currentValue ) ) ) ||
                                   ( ( originalValue == null ) && ( currentValue != null ) ) ) ;
                }
            } catch ( Exception e ) {
                String error = "Unable to locate property \"" + name + "\" from ModelObject: " + a_Current ;
                /*= ERROR =*/ log.error( error, e ) ;
                throw new IdmException( error, e ) ;
            }
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Compare: Name(" + name +") New(" + currentValue + ") -> Original(" + originalValue + ") = Changed(" + wasChanged + ")" ) ;
            if ( wasChanged ) {
                /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** UPDATED: ModelObject property \"" + name + "\" is being updated" ) ;
                String originalValueStr = ( originalValue == null ) ? "" : originalValue.toString() ;
                String currentValueStr = ( currentValue == null ) ? "" : currentValue.toString() ;
                Audit audit = new Audit() ;
                audit.setCreateDate( currentDate ) ;
                audit.setAuditActionTypeCode( AuditActionTypeCode.UPDATE_FIELD ) ;
                audit.setSource( a_Source ) ;
                audit.setChangeDate( currentDate ) ;
                audit.setChangedBy( a_ChangedBy ) ;
                audit.setUserid( a_Userid ) ;
                audit.setDescription( a_Description ) ;
                audit.setObjectType( objectType ) ;
                audit.setName( name ) ;
                audit.setOldValue( originalValueStr ) ;
                audit.setCurrentValue( currentValueStr ) ;
                audits.add( audit ) ;
            }
        }
        
        // If there was any audit information found, persist it.
        if ( audits.size() > 0 ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** SAVING: Saving audit information." ) ;
            this.getAuditDao().saveAll( audits ) ;
        }
    }
    
    
    /**
     * Audit an individual property, by name, that was changed.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Entity Updated entity that is being audited.
     * @param a_PropertyName Name of property on entity that was updated.
     */
    public void updateProperty( String a_Source, String a_ChangedBy, String a_Userid, String a_Description, Object a_Entity, String a_PropertyName ) 
    {
        Audit audit = new Audit() ;
        
        Date currentDate = new Date() ;
        
        String objectType = a_Entity.getClass().getName().substring( a_Entity.getClass().getName().lastIndexOf( "." ) + 1 ) ;
        
        audit.setCreateDate( currentDate ) ;
        audit.setAuditActionTypeCode( AuditActionTypeCode.UPDATE_FIELD ) ;
        audit.setSource( a_Source ) ;
        audit.setChangeDate( currentDate ) ;
        audit.setChangedBy( a_ChangedBy ) ;
        audit.setUserid( a_Userid ) ;
        audit.setDescription( a_Description ) ;
        audit.setObjectType( objectType ) ;
        audit.setName( a_PropertyName ) ;
        
        if ( log.isDebugEnabled() ) log.debug( "Creating new Audit: " + audit ) ;
        
        this.getAuditDao().save( audit ) ;
        
        if ( log.isDebugEnabled() ) log.debug( "Audit saved" ) ;
    }
    
    
    /**
     * Audit a user being merged into another user.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_PrimaryUser Primary {@link GcxUser}.
     * @param a_UserBeingMerged {@link GcxUser} being merged into primary.
     * @param a_Description Description of change.
     */
    public void merge( String a_Source, String a_ChangedBy, GcxUser a_PrimaryUser, GcxUser a_UserBeingMerged, String a_Description )
    {
        Audit audit = new Audit() ;
        
        Date currentDate = new Date() ;
        
        String objectType = a_PrimaryUser.getClass().getName().substring( a_PrimaryUser.getClass().getName().lastIndexOf( "." ) + 1 ) ;
        
        audit.setCreateDate( currentDate ) ;
        audit.setAuditActionTypeCode( AuditActionTypeCode.MERGE ) ;
        audit.setSource( a_Source ) ;
        audit.setChangeDate( currentDate ) ;
        audit.setChangedBy( a_ChangedBy ) ;
        audit.setUserid( a_PrimaryUser.getEmail() ) ;
        audit.setDescription( a_Description ) ;
        audit.setObjectType( objectType ) ;
        audit.setName( GcxUser.FIELD_GUID ) ;
        audit.setCurrentValue( a_PrimaryUser.getGUID() ) ;
        audit.setOldValue( a_UserBeingMerged.getGUID() ) ;
        
        if ( log.isDebugEnabled() ) log.debug( "Creating new Audit: " + audit ) ;
        
        this.getAuditDao().save( audit ) ;
        
        if ( log.isDebugEnabled() ) log.debug( "Audit saved" ) ;
    }

}
