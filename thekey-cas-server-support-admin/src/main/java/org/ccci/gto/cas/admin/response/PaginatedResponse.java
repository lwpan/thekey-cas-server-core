package org.ccci.gto.cas.admin.response;

import java.io.Serializable;
import java.util.List;

/**
 * <b>PaginatedResponse</b> defines a paginated response used to walk through a large dataset.
 *
 * @author Greg Crider  Nov 17, 2008  2:32:14 PM
 */
public interface PaginatedResponse<T> extends Serializable {
    /**
     * @return the entries
     */
    public List<T> getEntries();

    /**
     * @param entries
     *            the entries to set
     */
    public void setEntries(final List<T> entries);

    /**
     * @return the total
     */
    public int getTotal() ;
    
    
    /**
     * @return the pageNumber
     */
    public int getPageNumber() ;
    /**
     * @param a_pageNumber the pageNumber to set
     */
    public void setPageNumber( int a_pageNumber ) ;
    
    
    /**
     * @return the pagecount
     */
    public int getPageCount() ;
    
    
    /**
     * @return the entriesPerPage
     */
    public int getEntriesPerPage() ;
    /**
     * @param a_entriesPerPage the entriesPerPage to set
     */
    public void setEntriesPerPage( int a_entriesPerPage ) ;
    
    
    /**
     * Calculate the pagination details based on available information.
     */
    public void calculate() ;
    
    
    /**
     * Create a current page of entries.
     * 
     * @param a_PageNumber Desired page number.
     */
    public void createPage( int a_PageNumber ) ;
    
    
    /**
     * Return the current page as specified internally by the page number.
     * 
     * @return Jost those entries that reside within the specified page.
     */
    public List<?> currentPage() ;

}
