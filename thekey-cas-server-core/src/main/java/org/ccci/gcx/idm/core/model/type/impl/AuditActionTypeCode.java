package org.ccci.gcx.idm.core.model.type.impl;

import org.ccci.gcx.idm.common.model.type.AbstractAlphaNumericTypeCode;

/**
 * <b>AuditActionTypeCode</b> represent the possible actions taken that triggered an audit.
 *
 * @author Greg Crider  Apr 1, 2008  4:59:06 PM
 */
public class AuditActionTypeCode extends AbstractAlphaNumericTypeCode
{
    private static final long serialVersionUID = -6421779766097561359L ;

    public static final AuditActionTypeCode NONE = new AuditActionTypeCode( "", "None" ) ;
    public static final AuditActionTypeCode UPDATE_FIELD = new AuditActionTypeCode( "U", "Update Field" ) ;
    public static final AuditActionTypeCode CREATE = new AuditActionTypeCode( "C", "Create" ) ;
    public static final AuditActionTypeCode DELETE = new AuditActionTypeCode( "DE", "Delete" ) ;
    public static final AuditActionTypeCode DISABLE = new AuditActionTypeCode( "DI", "Disable" ) ;
    public static final AuditActionTypeCode ENABLE = new AuditActionTypeCode( "E", "Enable" ) ;
    public static final AuditActionTypeCode ACTIVATE = new AuditActionTypeCode( "A", "Activate" ) ;
    public static final AuditActionTypeCode MERGE = new AuditActionTypeCode( "M", "Merge" ) ;
    
    
    /**
     * Constructor for code and label.
     * 
     * @param a_Code Code value.
     * @param a_Label Label value.
     */
    private AuditActionTypeCode( String a_Code, String a_Label )
    {
        super( AuditActionTypeCode.class, a_Code, a_Label ) ;
    }

}
