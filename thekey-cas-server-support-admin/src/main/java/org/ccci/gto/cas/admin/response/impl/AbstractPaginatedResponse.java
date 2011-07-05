package org.ccci.gto.cas.admin.response.impl;

import java.util.List;

import org.ccci.gcx.idm.common.model.impl.AbstractModelObject;
import org.ccci.gto.cas.admin.response.PaginatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * <b>AbstractPaginatedResponse</b> contains the common functionality for a paginated result
 * returned by a request.
 *
 * @author Greg Crider  Nov 14, 2008  6:52:43 PM
 */
public abstract class AbstractPaginatedResponse extends AbstractModelObject implements PaginatedResponse
{
    private static final long serialVersionUID = -7348441022186973290L ;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** Total number of pages */
    private int m_Total = 0 ;
    /** Current page number */
    private int m_PageNumber = 0 ;
    /** Total number of pages */
    private int m_PageCount = 0 ;
    /** How many entries per page */
    private int m_EntriesPerPage = 0 ;
    /** Entries to be paginated. */
    private List<?> m_Entries = null ;
    /** Holds just the entries from the current, calculated page. */
    private List<?> m_CurrentPageEntries = null ;
    /** Flag to test that page calculations were done. */
    private boolean m_Calcluated = false ;
    
    
    /**
     * @return the entries
     */
    public List<?> getEntries()
    {
        return this.m_Entries ;
    }
    /**
     * @param a_entries the entries to set
     */
    public void setEntries( List<?> a_entries )
    {
        this.m_Entries = a_entries ;
    }
    
    
    /**
     * @return the total
     */
    public int getTotal()
    {
        return this.m_Total ;
    }
    /**
     * @param a_total the total to set
     */
    protected void setTotal( int a_total )
    {
        this.m_Total = a_total ;
    }
    
    
    /**
     * @return the pageNumber
     */
    public int getPageNumber()
    {
        return this.m_PageNumber ;
    }
    /**
     * @param a_pageNumber the pageNumber to set
     */
    public void setPageNumber( int a_pageNumber )
    {
        this.m_PageNumber = a_pageNumber ;
    }
    
    
    /**
     * @return the pagecount
     */
    public int getPageCount()
    {
        return this.m_PageCount ;
    }
    /**
     * @param a_pagecount the pagecount to set
     */
    protected void setPageCount( int a_pagecount )
    {
        this.m_PageCount = a_pagecount ;
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
     * Calculate the pagination details based on available information.
     */
    public void calculate()
    {
        if ( ( this.m_Entries != null ) && ( this.m_Entries.size() > 0 ) ) {
            this.m_Total = this.m_Entries.size() ;
            this.m_PageCount = ( this.m_Entries.size() / this.m_EntriesPerPage ) +
                               ( ( this.m_Entries.size() % this.m_EntriesPerPage > 0 ) ? 1 : 0 ) ;
        } else {
            this.m_Total = 0 ;
            this.m_PageCount = 0 ;
            this.m_PageNumber = 0 ;
        }
        
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Page Information:\n\tEntries: total(" + this.m_Total + ") per page(" + this.m_EntriesPerPage + ")" +
                                                                                    "\n\tPages: number(" + this.m_PageNumber + ") count(" + this.m_PageCount + ")" ) ;
        
        this.m_Calcluated = true ;
    }
    
    
    /**
     * Create a current page of entries.
     * 
     * @param a_PageNumber Desired page number.
     */
    public void createPage( int a_PageNumber )
    {
        Assert.isTrue( this.m_Calcluated, "Must set details and perform calculate first" ) ;
        
        if ( ( this.m_Entries != null ) && ( this.m_Entries.size() > 0 ) ) {
            if ( a_PageNumber > this.m_PageCount ) {
                this.m_PageNumber = a_PageNumber ;
            } else if ( a_PageNumber <= 0 ) {
                this.m_PageNumber = 1 ;
            } else {
                this.m_PageNumber = a_PageNumber ;
            }
        
            int low = ( this.m_PageNumber - 1 ) * this.m_EntriesPerPage ;
            int high = low + this.m_EntriesPerPage ;
            if ( high > this.m_Entries.size() ) {
                high = this.m_Entries.size() ;
            }
        
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Page: number(" + this.m_PageNumber + ") low(" + low + ") high(" + high + ")" ) ;
        
            this.m_CurrentPageEntries = this.m_Entries.subList( low, high ) ;
        } else {
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** There are no entries" ) ;
            
            this.m_CurrentPageEntries = null ;
        }
    }
    
    
    /**
     * Return the current page as specified internally by the page number. You must
     * call the <tt>getPage</tt> method before invoking this method.
     * 
     * @return Just those entries that reside within the specified page.
     */
    public List<?> currentPage()
    {
        return this.m_CurrentPageEntries ;
    }
     
}
