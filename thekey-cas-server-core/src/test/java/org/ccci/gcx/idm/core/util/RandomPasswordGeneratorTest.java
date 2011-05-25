package org.ccci.gcx.idm.core.util;

import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;

/**
 * <b>RandomPasswordGeneratorTest</b> is used to unit test {@link RandomPasswordGenerator}.
 *
 * @author Greg Crider  Dec 1, 2008  2:07:13 PM
 */
public class RandomPasswordGeneratorTest extends AbstractTransactionalTestCase
{
    public void testBasic()
    {
	logger.info("***** BEGIN: testBasic");
        
        RandomPasswordGenerator rpg = (RandomPasswordGenerator)this.getApplicationContext().getBean( Constants.BEAN_RANDOM_PASSWORD ) ;
        
        for( int i=0; i<10; i++ ) {
            String password = rpg.generatePassword( 10 ) ;
	    logger.info("***** Password: " + password);
        }
        
	logger.info("***** END: testBasic");
    }
}
