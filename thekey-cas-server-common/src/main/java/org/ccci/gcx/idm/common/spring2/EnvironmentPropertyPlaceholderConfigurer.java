package org.ccci.gcx.idm.common.spring2 ;


import java.io.IOException ;
import java.util.HashSet ;
import java.util.Properties ;
import java.util.Set ;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.IdmException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer ;
import org.springframework.core.io.Resource ;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver ;
import org.springframework.core.io.support.ResourcePatternResolver ;
import org.springframework.util.StringUtils ;


/**
 * Subclass of Spring's <code>PropertyPlaceholderConfigurer</code> to support
 * environment-specific property replacement. To use this, create a context
 * file, such as <tt>config.xml</tt>, and add:
 * <pre>
 * 
 *   <bean class="org.ccci.gcx.idm.common.spring2.EnvironmentPropertyPlaceholderConfigurer">
 *       <property name="location" value="config/env/global.properties"/>
 *   </bean>
 *   
 * </pre>
 * <p>
 * This file looks for a system property under the key "target.env" and expects
 * one of four values to be configured:
 * <ul>
 * <li>local - Local PC as runtime environment</li>
 * <li>dev - Development environment</li>
 * <li>test - Test environment</li>
 * <li>prod - Production environment</li>
 * </ul>
 * If no system property is found under this key, "dev" is assumed. Using this
 * value, all files matching the pattern "classpath*:config/env/<target.env>/*" will be loaded
 * and used to replace placeholders in the spring context files used for
 * the applicable container. If the system property for the
 * target envronment is <tt>@default@</tt>, then none of the environment overrides 
 * then none of the environment specific properties are used for overrides; in other
 * words, only the injected location is used as defined in the application context.
 * 
 * @author Greg Crider Dec 10, 2007 2:48:59 PM
 */
public class EnvironmentPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer
{
    private static Log log = LogFactory.getLog( EnvironmentPropertyPlaceholderConfigurer.class ) ;

    public static final String TARGET_ENV_KEY = "target.env" ;

    private static final String DEFAULT_ENV = "local" ;

    public static final Set<String> VALID_ENVIRONMENTS = new HashSet<String>( 4 ) ;
    
    public static final String RELOADAS_DEFAULT = "@default@" ;

    
    static {
        synchronized ( EnvironmentPropertyPlaceholderConfigurer.class ) {
            VALID_ENVIRONMENTS.add( "local" ) ;
            VALID_ENVIRONMENTS.add( "dev" ) ;
            VALID_ENVIRONMENTS.add( "test" ) ;
            VALID_ENVIRONMENTS.add( "prod" ) ;
        }
    }

    
    /**
     * Extend superclass implementation to be aware of a system property
     * pointing to an environment directory from which all files will be loaded
     * in order to replace configured placeholders in the ApplicationContext.
     */
    protected void loadProperties( Properties props ) throws IOException
    {
        // load pre-configured locations first to allow environment-specific
        // values to override
        super.loadProperties( props ) ;

        // initialize to the default environment
        String targetEnv = DEFAULT_ENV ;

        // make sure a System property was configured. if not, use the default.
        String candidateEnv = System.getProperty( TARGET_ENV_KEY ) ;

        // Make sure a valid target environment is specified. If the value is @default@, then we are only going to
        // use the property file injected onto the bean
        if ( StringUtils.hasText( candidateEnv ) ) {
            if ( 
                    ( !EnvironmentPropertyPlaceholderConfigurer.VALID_ENVIRONMENTS.contains( candidateEnv ) ) &&
                    ( !candidateEnv.equals( EnvironmentPropertyPlaceholderConfigurer.RELOADAS_DEFAULT ) )
                ) {
                throw new IdmException( "Unrecognized target environment found under system property '" + TARGET_ENV_KEY + "'" ) ;
            }
            targetEnv = candidateEnv ;
        }

        // If the target environment is not the reloadas default, then load in the additional property file
        if ( !targetEnv.equals(  EnvironmentPropertyPlaceholderConfigurer.RELOADAS_DEFAULT ) ) {
            /*= INFO =*/ log.info( "Using target environment found under system property '" + TARGET_ENV_KEY + "'" ) ;
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver() ;
            Resource[] locations = resolver.getResources( "classpath:config/env/" + targetEnv + "/*.properties" ) ;

            // if any environment-specific values are configured, load them
            if ( locations != null && locations.length > 0 ) {
                this.setLocations( locations ) ;
                super.loadProperties( props ) ;
            }
        } else if ( log.isInfoEnabled() ) {
            /*= INFO =*/ log.info( "No target environment specified; only the original injected properties will be used" ) ;
        }
    }

}
