package org.ccci.gcx.idm.common.spring2;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Abstract ApplicationContext superclass responsible for providing ordering context
 * resources prior to load.  This permits easy bean overriding from test context configurations.
 * Test context configurations are always loaded last.  Any file matching the resource
 * pattern "classpath*:contexts/*.xml" will be loaded.  Those which have file names beginning
 * with "TEST-" will be loaded last.
 *
 * <b>Note:</b> For context xml files that are not prefixed with TEST-, the order of their
 * being loaded is driven purely by {@link ResourcePatternResolver}. The resources are sorted
 * using the {@link ResourceOrderComparator}. This doesn't put things in any particular order, but
 * just makes sure that TEST- comes last. The bottom line is, if you define the same bean more
 * than once, you may get bean overrides, but in an order that wasn't expected.
 *
 * @author Greg Crider May 30, 2006 @ 12:24:34 PM
 */
public class ApplicationContext
{
    /**
     * Internal application context; must be compatible with the
     * {@link WebApplicationContext} so that it can be used with web application
     * based components.
     */
    private static XmlWebApplicationContext ctx = null ;


    /**
     * Make sure that only one instance of the internal application context is
     * ever created.
     */
    static {
        synchronized ( ApplicationContext.class ) {
            ApplicationContext.ctx = ApplicationContext.init() ;
        }
    }
    
    
    /**
     * Get the locations of all the context files that follow the rules for putting
     * TEST-*.xml at the end so that they can override the behavior of previously
     * defined beans.
     * 
     * @return Context file locations.
     */
    public static String[] getConfigLocations()
    {
        String[] result = null ;
        
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver() ;
            Resource[] resources = resolver.getResources( "classpath*:contexts/*.xml" ) ;
            result = orderResources( resources ) ;
        } catch ( IOException ioe ) {
            throw new SpringException( "Unable to acquire context file locations." ) ;
        }
        
        return result ;
    }


    /**
     * Initialize the delegate ApplicationContext instance.
     *
     * @return WebXmlApplicationContext
     */
    protected static XmlWebApplicationContext init()
    {
        XmlWebApplicationContext result = null ;

        result = new XmlWebApplicationContext() ;
        result.setConfigLocations( getConfigLocations() ) ;
        result.refresh() ;

        return result ;
    }


    /**
     * Order the matched resources according to the algorithm specified by
     * <code>ResourceOrderComparator</code>.
     *
     * @param resources
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected static String[] orderResources( Resource[] a_Resources ) throws IOException
    {
        Arrays.sort( a_Resources, new ResourceOrderComparator() ) ;
        String[] locations = new String[a_Resources.length] ;

        for( int i = 0; i < locations.length; i++ )
            locations[ i ] = a_Resources[ i ].getURL().toExternalForm() ;

        return locations ;
    }


    /**
     * Get the specified {@link Object}.
     *
     * @param key
     *            Bean lookup.
     * @return Corresponding {@link Object}.
     */
    public static Object getObject( String a_Key )
    {
        return ctx.getBean( a_Key ) ;
    }


    /**
     * Return all of the bean names in the current context.
     *
     * @return All bean names.
     */
    public static String[] getObjectNames()
    {
        return ctx.getBeanDefinitionNames() ;
    }


    /**
     * Return all of the bean names, for the specified class type,
     * in the current context.
     *
     * @param a_Class Class type of bean names to be returned.
     *
     * @return All matching bean names.
     */
    public static String[] getObjectNamesByType( Class<?> a_Class )
    {
        return ctx.getBeanNamesForType( a_Class ) ;
    }


    /**
     * Convenience class for ordering resources to allow for test contexts to be
     * loaded last.
     *
     * @author Jon Crater May 30, 2006 @ 12:29:12 PM
     */
    @SuppressWarnings("unchecked")
    private static class ResourceOrderComparator implements Comparator
    {
        public int compare( Object a_Arg0, Object a_Arg1 )
        {
            Resource first = (Resource)a_Arg0 ;
            return first.getFilename().startsWith( "TEST-" ) ? 1 : -1 ;
        }
    }


    /**
     * Return the underlying {@link XmlWebApplicationContext} instance.
     *
     * @return ConfigurableApplicationContext
     */
    public static XmlWebApplicationContext getWrappedContext()
    {
        return ApplicationContext.ctx ;
    }
}