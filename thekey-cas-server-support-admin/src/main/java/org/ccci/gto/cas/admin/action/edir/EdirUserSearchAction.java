package org.ccci.gto.cas.admin.action.edir;

import org.ccci.gto.cas.admin.action.AbstractUserSearchAction;

/**
 * <b>EdirUserSearchAction</b> is used to perform eDirectory user lookups.
 *
 * @author Greg Crider  Nov 13, 2008  3:37:18 PM
 */
public class EdirUserSearchAction extends AbstractUserSearchAction
{
    private static final long serialVersionUID = -8294803337069082414L ;

    /**
     * Label to be used for the update button.
     * 
     * @return Label for update button.
     */
    public String getUpdateButtonLabel()
    {
        return "Update" ;
    }
}
