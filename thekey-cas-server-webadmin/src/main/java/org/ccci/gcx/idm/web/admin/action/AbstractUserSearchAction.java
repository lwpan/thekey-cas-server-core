package org.ccci.gcx.idm.web.admin.action;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.IdmException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;
import org.ccci.gcx.idm.web.admin.Constants;
import org.ccci.gto.cas.admin.response.impl.UserSearchResponse;

/**
 * <b>AbstractUserSearchAction</b> contains the common functionality required by all action
 * classes that need to perform user searches, and select a specific user for further work.
 *
 * @author Greg Crider  Dec 4, 2008  12:19:55 PM
 */
public abstract class AbstractUserSearchAction extends AbstractUserAction
{
    private static final long serialVersionUID = 2717839402355343192L ;

    protected static final Log log = LogFactory.getLog( AbstractUserSearchAction.class ) ;
    
    /** Control parameters for the current search */
    private SearchControlParameters m_SearchControlParameters = null ;
    /** Search control parameter object name in Session */
    private String m_SearchControlParametersName = null ;
    /** How many entries per page in search results listing */
    private int m_EntriesPerPage = 10 ;
    /** Name of the action to be called from within the form */
    private String m_SearchActionName = null ;
    /** Name of class for UserSearchResponse */
    private Class<UserSearchResponse> m_UserSearchResponseClass = null ;
    
    
    /**
     * This callback method is executed when the user selects a user from the
     * search results, and wants to operate on it.
     */
    protected abstract void updateCallback() ;
    
    
    /**
     * Label to be used for the update button.
     * 
     * @return Label for update button.
     */
    public abstract String getUpdateButtonLabel() ;
    
    
    /**
     * @return the searchControlParameters
     */
    protected SearchControlParameters getSearchControlParameters()
    {
        return this.m_SearchControlParameters ;
    }


    /**
     * @return the searchControlParametersName
     */
    public String getSearchControlParametersName()
    {
        return this.m_SearchControlParametersName ;
    }
    /**
     * @param a_searchControlParametersName the searchControlParametersName to set
     */
    public void setSearchControlParametersName( String a_searchControlParametersName )
    {
        this.m_SearchControlParametersName = a_searchControlParametersName ;
    }
    
    
    /**
     * @return the entriesPerPage
     */
    public int getEntriesPerPage()
    {
        return this.m_EntriesPerPage ;
    }
    /**
     * @param a_entriesPerPage the entriesPerPage to set
     */
    public void setEntriesPerPage( int a_entriesPerPage )
    {
        this.m_EntriesPerPage = a_entriesPerPage ;
    }
    
    
    /**
     * @return the searchActionName
     */
    public String getSearchActionName()
    {
        return this.m_SearchActionName ;
    }
    /**
     * @param a_searchActionName the searchActionName to set
     */
    public void setSearchActionName( String a_searchActionName )
    {
        this.m_SearchActionName = a_searchActionName ;
    }


    /**
     * @return the email
     */
    public String getEmail()
    {
        return this.m_SearchControlParameters.getEmail() ;
    }
    /**
     * @param a_email the email to set
     */
    public void setEmail( String a_email )
    {
        this.m_SearchControlParameters.setEmail( a_email ) ;
    }
    
    
    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return this.m_SearchControlParameters.getFirstName() ;
    }
    /**
     * @param a_firstName the firstName to set
     */
    public void setFirstName( String a_firstName )
    {
        this.m_SearchControlParameters.setFirstName( a_firstName ) ;
    }
    
    
    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return this.m_SearchControlParameters.getLastName() ;
    }
    /**
     * @param a_lastName the lastName to set
     */
    public void setLastName( String a_lastName )
    {
        this.m_SearchControlParameters.setLastName( a_lastName ) ;
    }
    
    
    /**
     * @return the requestedPageNumber
     */
    public Integer getRequestedPageNumber()
    {
        return this.m_SearchControlParameters.getRequestedPageNumber() ;
    }
    /**
     * @param a_requestedPageNumber the requestedPageNumber to set
     */
    public void setRequestedPageNumber( Integer a_requestedPageNumber )
    {
        this.m_SearchControlParameters.setRequestedPageNumber( a_requestedPageNumber ) ;
    }
    
    
    /**
     * @return the action
     */
    public String getSearchAction()
    {
        return this.m_SearchControlParameters.getSearchAction() ;
    }
    /**
     * @param a_action the action to set
     */
    public void setSearchAction( String a_action )
    {
        this.m_SearchControlParameters.setSearchAction( a_action ) ;
    }
    
    
    /**
     * @return the selectedUser
     */
    public String getSelectedUserEmail()
    {
        return this.m_SearchControlParameters.getSelectedUserEmail() ;
    }
    /**
     * @param a_selectedUser the selectedUser to set
     */
    public void setSelectedUserEmail( String a_selectedUser )
    {
        this.m_SearchControlParameters.setSelectedUserEmail( a_selectedUser ) ;
    }
    
    
    /**
     * @return the userSearchResponseClass
     */
    public Class<UserSearchResponse> getUserSearchResponseClass()
    {
        return this.m_UserSearchResponseClass ;
    }
    /**
     * @param a_userSearchResponseClass the userSearchResponseClass to set
     */
    public void setUserSearchResponseClass( Class<UserSearchResponse> a_userSearchResponseClass )
    {
        this.m_UserSearchResponseClass = a_userSearchResponseClass ;
    }

    
    /**
     * Factory method to create an instance of the {@link UserSearchResponse} implementation
     * used for pagination within this action and the view.
     * 
     * @return New instance of the class set in <tt>userSearchResponseClass</tt>.
     */
    private UserSearchResponse userSearchResponseFactory()
    {
        UserSearchResponse result = null ;
        
        try {
            Constructor<UserSearchResponse> con = this.m_UserSearchResponseClass.getConstructor( new Class[]{} ) ;
            result = con.newInstance( new Object[]{} ) ;
        } catch ( Exception e ) {
            String error = "Unable to create an instance of \"" + this.m_UserSearchResponseClass.getName() + "\"." ;
            /*= ERROR =*/ log.error( error, e ) ;
            throw new IdmException( error, e ) ;
        }
        
        return result ;
    }
    

    /**
     * Validate the request parametes for a user search.
     * 
     * @return <tt>True</tt> if the search is valid.
     */
    private boolean isValidSearchRequest()
    {
        boolean result = true ;
        
        int count = 0 ;
        count += ( StringUtils.isNotBlank( this.getFirstName() ) ) ? 1 : 0 ;
        count += ( StringUtils.isNotBlank( this.getLastName() ) ) ? 1 : 0 ;
        count += ( StringUtils.isNotBlank( this.getEmail() ) ) ? 1 : 0 ;
        
        if ( count == 1 ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** We can do the search" ) ;
            result = true ;
        } else if ( count == 0 ) {
            this.addActionError( this.getText( "edir.error.missing.searchparameters" ) ) ;
            this.addFieldError( "firstName", this.getText( "edir.error.specify.firstname" ) ) ;
            this.addFieldError( "lastName", this.getText( "edir.error.specify.lastname" ) ) ;
            this.addFieldError( "email", this.getText( "edir.error.specify.email" ) ) ;
            result = false ;
        } else {
            this.addActionError( this.getText( "edir.error.toomany.searchparameters" ) ) ;
            this.addFieldError( "firstName", this.getText( "edir.error.specify.firstname" ) ) ;
            this.addFieldError( "lastName", this.getText( "edir.error.specify.lastname" ) ) ;
            this.addFieldError( "email", this.getText( "edir.error.specify.email" ) ) ;
            result = false ;
        }
        
        if ( !result ) {
            this.getSession().remove( Constants.SESSION_USER_SEARCH_RESPONSE ) ;
            this.getSession().remove( Constants.SESSION_USER_SEARCH_CURRENTPAGE ) ;
        }
       
        return result ;
    }
    
    
    /**
     * Analyze the workflow if present, and adjust the current action accordingly.
     */
    private void analyzeWorkflow()
    {
        /*
         * Test to see if we are returning from another page back to the previous search.
         * Remove the workflow flag so that the search validation is not bypassed on a
         * subsequent search.
         */
        String workFlowFlag = (String)this.getSession().get( Constants.SESSION_WORKFLOW_FLAG ) ;
        boolean returnFromPrevious = ( StringUtils.isNotBlank( workFlowFlag ) && workFlowFlag.equals( Constants.WORKFLOW_FLAG_RETURN_TO_PREVIOUS ) ) ;
        this.getSession().remove( Constants.SESSION_WORKFLOW_FLAG ) ;
        
        // If we are returning to a previous search, we can simply reissue the last pagination request, after
        // we update the user in the list
        if ( returnFromPrevious ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Updating user in list" ) ;
            // Recover the updated version of the selected user
            this.m_SearchControlParameters.setSelectedUser( (GcxUser )this.getSession().get( Constants.SESSION_SELECTED_USER ) ) ;
            GcxUser updatedUser = this.m_SearchControlParameters.getSelectedUser() ;
            // Recover the response with the original search list
            UserSearchResponse response = this.m_SearchControlParameters.getUserSearchResponse() ;
            // Update the user in the reponse
            response.udpateUserInEntries( updatedUser ) ;
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Returning to previous page" ) ;
            this.setSearchAction( Constants.ACTION_PAGINATE ) ;
        }
    }
    

    /**
     * Initialize the user search by cleaning up previous search resources.
     * 
     * @return Result name.
     */
    public String userSearchInitialize()
    {
        String result = AbstractUserSearchAction.SUCCESS ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Initializing a new search" ) ;
        
        // Create new search control and save in session
        this.m_SearchControlParameters = new SearchControlParameters() ;
        this.getSession().put( this.m_SearchControlParametersName, this.m_SearchControlParameters ) ;

        // Remove any workflow flags
        this.getSession().remove( Constants.SESSION_WORKFLOW_FLAG ) ;
        
        // Remove previous search results, in case there were any
        this.getSession().remove( Constants.SESSION_USER_SEARCH_RESPONSE ) ;
        this.getSession().remove( Constants.SESSION_USER_SEARCH_CURRENTPAGE ) ;
        
        return result ;
    }
    
    
    /**
     * Perform a lookup based on the specified search parameters.
     * 
     * @return Result name.
     */
    public String userSearch()
    {
        String result = AbstractUserSearchAction.SUCCESS ;
        List<GcxUser> lookup = null ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Action: " + this.getSearchAction() ) ;
        
        // Check workflow
        this.analyzeWorkflow() ;
        
        // ACTION: Search
        if ( this.getSearchAction().equals( Constants.ACTION_SEARCH ) )  {
            if ( log.isDebugEnabled() ) log.debug( "***** Search: FirstName(" + this.getFirstName() + ") LastName(" + this.getLastName() + ") Email(" + this.getEmail() + ")" ) ;
            // Validate the search
            if ( this.isValidSearchRequest() ) {
                boolean exceedMax = false ;
                try {
                    if ( StringUtils.isNotBlank( this.getFirstName() ) ) {
                        lookup = this.getGcxUserService().findAllByFirstName( this.getFirstName() ) ;
                    } else if ( StringUtils.isNotBlank( this.getLastName() ) ) {
                        lookup = this.getGcxUserService().findAllByLastName( this.getLastName() ) ;
                    } else {
                        lookup = this.getGcxUserService().findAllByUserid( this.getEmail(), true ) ;
                    }
                } catch ( ExceededMaximumAllowedResults emar ) {
                    exceedMax = true ;
                }
                // Did we exceed the max allowed search results 
                if ( exceedMax ) {
                    this.addActionError( 
                            this.getText( "edir.error.toomany.results", 
                                          new String[] { Integer.toString( this.getGcxUserService().getMaxSearchResults() ) } ) ) ;
                    result = AbstractUserSearchAction.ERROR ;
                // Are there any search results?
                } else if ( ( lookup != null ) && ( lookup.size() > 0 ) ) {
                    UserSearchResponse response = this.userSearchResponseFactory() ;
                    response.setEntries( lookup ) ;
                    response.setEntriesPerPage( this.getEntriesPerPage() ) ;
                    response.calculate() ;
                    response.createPage( 1 ) ;
                    List<GcxUser> currentPage = response.currentUsersPage() ;
                    this.m_SearchControlParameters.setUserSearchResponse( response ) ;
                    this.getSession().put( Constants.SESSION_USER_SEARCH_RESPONSE, response ) ;
                    /*
                     * For some reason, the Struts tag is unable to get the current
                     * page directly from the response object once it is put into
                     * the session. I don't know why, but the only current work
                     * around is to put the current page list directly into a
                     * separate session object.
                     */
                    this.getSession().put( Constants.SESSION_USER_SEARCH_CURRENTPAGE, currentPage ) ;
                // If there are no search results, remove response from view
                } else {
                    this.getSession().remove( Constants.SESSION_USER_SEARCH_RESPONSE ) ;
                    this.getSession().remove( Constants.SESSION_USER_SEARCH_CURRENTPAGE ) ;
                }
            } else {
                result = AbstractUserSearchAction.ERROR ;
            }
        // ACTION: Update
        } else if ( this.getSearchAction().equals( Constants.ACTION_UPDATE ) ) {
            // Execute the concrete implementations callback method for update
            this.updateCallback() ;
            result = Constants.ACTION_UPDATE ;
        // ACTION: Paginate
        } else {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Going to page \"" + this.getRequestedPageNumber() + "\"" ) ;
            UserSearchResponse response = this.m_SearchControlParameters.getUserSearchResponse() ;
            response.createPage( this.getRequestedPageNumber() ) ;
            List<GcxUser> currentPage = response.currentUsersPage() ;
            this.getSession().put( Constants.SESSION_USER_SEARCH_RESPONSE, response ) ;
            /*
             * For some reason, the Struts tag is unable to get the current
             * page directly from the response object once it is put into
             * the session. I don't know why, but the only current work
             * around is to put the current page list directly into a
             * separate session object.
             */
            this.getSession().put( Constants.SESSION_USER_SEARCH_CURRENTPAGE, currentPage ) ;
        }

        // Enforce the current search controls in the session
        this.getSession().put( this.m_SearchControlParametersName, this.m_SearchControlParameters ) ;

        return result ;
    }
    
    
    /**
     * We can override the prepare step if there are some resources that need to be
     * setup prior to the action running.
     * 
     * @throws Exception If an error occurs.
     */
    public void prepare() throws Exception
    {
        super.prepare() ;
        
        // Recover the search control
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Recovering search control parameters: " + this.m_SearchControlParametersName ) ;
        this.m_SearchControlParameters = (SearchControlParameters)this.getSession().get( this.m_SearchControlParametersName ) ;
        
        // Set the search action name
        this.getSession().put( Constants.SESSION_SEARCH_ACTION_NAME, this.getSearchActionName() ) ;
    }
    
    
    /**
     * <b>SearchControlParameters</b> is a simple data object used to hold the current values and
     * settings of the search.
     *
     * @author Greg Crider  Dec 4, 2008  1:18:00 PM
     */
    public static class SearchControlParameters implements Serializable
    {
        private static final long serialVersionUID = -2125011566366433010L ;
        
        /** E-mail address pattern specified in the search */
        private String m_Email = null ;
        /** First name pattern specified in the search */
        private String m_FirstName = null ;
        /** Last name pattern specified in the search */
        private String m_LastName = null ;
        /** Requested search action; driven by which button is selected */
        private String m_SearchAction = Constants.ACTION_SEARCH ;
        /** Requested page number if navigating through returned results */
        private Integer m_RequestedPageNumber = new Integer( 1 ) ;
        /** User selected for update */
        private String m_SelectedUserEmail = null ;
        /** Search response holding the pagination data */
        private UserSearchResponse m_UserSearchResponse = null ;
        /** Currently selected user object */
        private GcxUser m_SelectedUser = null ;
        
        
        /**
         * @return the email
         */
        public String getEmail()
        {
            return this.m_Email ;
        }
        /**
         * @param a_email the email to set
         */
        public void setEmail( String a_email )
        {
            this.m_Email = a_email ;
        }
        
        
        /**
         * @return the firstName
         */
        public String getFirstName()
        {
            return this.m_FirstName ;
        }
        /**
         * @param a_firstName the firstName to set
         */
        public void setFirstName( String a_firstName )
        {
            this.m_FirstName = a_firstName ;
        }
        
        
        /**
         * @return the lastName
         */
        public String getLastName()
        {
            return this.m_LastName ;
        }
        /**
         * @param a_lastName the lastName to set
         */
        public void setLastName( String a_lastName )
        {
            this.m_LastName = a_lastName ;
        }
        
        
        /**
         * @return the searchAction
         */
        public String getSearchAction()
        {
            return this.m_SearchAction ;
        }
        /**
         * @param a_searchAction the searchAction to set
         */
        public void setSearchAction( String a_searchAction )
        {
            this.m_SearchAction = a_searchAction ;
        }
        
        
        /**
         * @return the requestedPageNumber
         */
        public Integer getRequestedPageNumber()
        {
            return this.m_RequestedPageNumber ;
        }
        /**
         * @param a_requestedPageNumber the requestedPageNumber to set
         */
        public void setRequestedPageNumber( Integer a_requestedPageNumber )
        {
            this.m_RequestedPageNumber = a_requestedPageNumber ;
        }
        
        
        /**
         * @return the selectedUser
         */
        public String getSelectedUserEmail()
        {
            return this.m_SelectedUserEmail ;
        }
        /**
         * @param a_selectedUser the selectedUser to set
         */
        public void setSelectedUserEmail( String a_selectedUserEmail )
        {
            this.m_SelectedUserEmail = a_selectedUserEmail ;
        }
        
        
        /**
         * @return the userSearchResponse
         */
        public UserSearchResponse getUserSearchResponse()
        {
            return this.m_UserSearchResponse ;
        }
        /**
         * @param a_userSearchResponse the userSearchResponse to set
         */
        public void setUserSearchResponse( UserSearchResponse a_userSearchResponse )
        {
            this.m_UserSearchResponse = a_userSearchResponse ;
        }
        /**
         * @return the selectedUser
         */
        public GcxUser getSelectedUser()
        {
            return this.m_SelectedUser ;
        }
        
        
        /**
         * @param a_selectedUser the selectedUser to set
         */
        public void setSelectedUser( GcxUser a_selectedUser )
        {
            this.m_SelectedUser = a_selectedUser ;
        }

    }
}
