package org.ccci.gto.cas.admin.action.edir;

import org.ccci.gto.cas.admin.action.AbstractUserSearchAction;

/**
 * <b>EdirMergeSearchAction</b> is used to perform merge search lookups.
 *
 * @author Greg Crider  Dec 4, 2008  7:05:42 PM
 */
public class EdirMergeSearchAction extends AbstractUserSearchAction
{
    private static final long serialVersionUID = -2361647729743441489L ;

    /**
     * Label to be used for the update button.
     * 
     * @return Label for update button.
     */
    public String getUpdateButtonLabel()
    {
        return "View" ;
    }
}
