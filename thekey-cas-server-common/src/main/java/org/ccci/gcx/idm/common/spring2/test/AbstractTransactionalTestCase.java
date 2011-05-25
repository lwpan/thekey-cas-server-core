package org.ccci.gcx.idm.common.spring2.test;

import org.ccci.gcx.idm.common.spring2.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.AbstractTransactionalSpringContextTests;

/**
 * <b>AbstractTransactionalTestCase</b> extends {@link AbstractTransactionalSpringContextTests}
 * to create an {@link ApplicationContext} that conforms to the rules surrounding context files
 * that are prefixed with TEST-*.xml.
 *
 * @author Greg Crider  Oct 19, 2008  7:19:42 PM
 */
public abstract class AbstractTransactionalTestCase extends
	AbstractTransactionalSpringContextTests {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Override the default behavior to return the same context files as {@link ApplicationContext}.
     * 
     * @return Context file location.
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    protected String[] getConfigLocations() 
    {
        return ApplicationContext.getConfigLocations() ;
    }
    
}
