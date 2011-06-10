package org.ccci.gcx.idm.web.admin;

/**
 * <b>Constants</b> used throughout the web portion of the application.
 *
 * @author Greg Crider  Nov 6, 2008  12:34:27 PM
 */
public interface Constants
{

    // SESSION ATTRIBUTES
    
    public static final String SESSION_AUTHENTICATED_USER = "authenticateduser" ;

    public static final String SESSION_USER_SEARCH_RESPONSE = "usersearchresponse" ;
    
    public static final String SESSION_USER_SEARCH_CURRENTPAGE = "usersearchcurrentpage" ;
    
    public static final String SESSION_SELECTED_USER = "selecteduser" ;
    
    public static final String SESSION_WORKFLOW_FLAG = "workflowflag" ;
    
    public static final String SESSION_LAST_KNOWN_PAGE = "lastknownpage" ;
    
    public static final String SESSION_LAST_KNOWN_FIRSTNAME = "lastknownfirstname" ;
    
    public static final String SESSION_LAST_KNOWN_LASTNAME = "lastknownlastname" ;
    
    public static final String SESSION_LAST_KNOWN_EMAIL = "lastknownemail" ;
    
    public static final String SESSION_USER_BEING_UPDATED = "usertoudpate" ;
    
    public static final String SESSION_STATUS_MESSAGE = "statusmessage" ;
    
    public static final String SESSION_SEARCH_ACTION_NAME = "searchactionname" ;
    
    // ACTION KEY WORDS
    
    public static final String ACTION_SEARCH = "search" ;
    
    public static final String ACTION_PAGINATE = "paginate" ;
    
    public static final String ACTION_UPDATE = "update" ;
    
    public static final String ACTION_CANCEL = "cancel" ;
    
    public static final String ACTION_APPLY = "apply" ;
    
    public static final String ACTION_DEACTIVATE = "deactivate" ;
    
    public static final String ACTION_MERGE = "merge" ;
    
    public static final String ACTION_MERGE_SEARCH = "mergesearch" ;
    
    public static final String ACTION_ACTIVATE = "activate" ;
    
    public static final String ACTION_SAVE = "save" ;
    
    public static final String ACTION_RESET_PASSWORD = "resetpassword" ;
    
    // WORKFLOW FLAGS
    
    public static final String WORKFLOW_FLAG_RETURN_TO_PREVIOUS = "returntoprevious" ;
}
