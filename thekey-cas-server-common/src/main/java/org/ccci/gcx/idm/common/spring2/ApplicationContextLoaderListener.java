package org.ccci.gcx.idm.common.spring2;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * <b>ApplicationContextLoaderListener</b> is used to bootstrap the {@link ApplicationContext}
 * as a servlet based application starts up. This listener should be registered in the
 * <tt>web.xml</tt> file as follows:
 * <p>
 * <pre>
 *     &lt;listener&gt;
 *       &lt;listener-class&gt;
 *           org.ccci.gcx.idm.common.spring2.ApplicationContextLoaderListener
 *       &lt;/listener-class&gt;
 *   &lt;/listener&gt;
 * </pre>
 * <p>
 * For Struts2, see {@link XmlWebApplicationContext} for proper configuration.
 *
 * @author Greg Crider  Jan 4, 2008  11:48:22 AM
 */
public class ApplicationContextLoaderListener implements ServletContextListener
{
    protected static final Log log = LogFactory.getLog( ApplicationContextLoaderListener.class ) ;

    /**
     * Notification that the servlet context is about to be shut down. All servlets have been destroy()ed before 
     * any ServletContextListeners are notified of context destruction.
     * <p>
     * This is where the {@link ApplicationContext} can be torn down.
     * 
     * @param a_Event Notification event.
     * 
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed( ServletContextEvent a_Event )
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "Tearing down ApplicationContext" ) ;

        // Close the ApplicationContext
        ApplicationContext.getWrappedContext().close() ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "ApplicationContext has been torn down" ) ;
    }

    /**
     * Notification that the web application initialization process is starting. All ServletContextListeners are 
     * notified of context initialisation before any filter or servlet in the web application is initialized.
     * <p>
     * This is where the {@link ApplicationContext} is initialized. A copy is also saved as a {@link ServletContext}
     * attribute to mimic what is done in the {@link ContextLoader} class.
     * 
     * @param a_Event Notification event.
     * 
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized( ServletContextEvent a_Event )
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "Initializing ApplicationContext" ) ;
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "Servlet Event: " + a_Event ) ;
        
        // Stir the context in order to bootstrap it
        ApplicationContext.getWrappedContext().refresh() ;
        
        // Store context in local instance variable, to guarantee that
        // it is available on ServletContext shutdown.
        a_Event.getServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, 
                ApplicationContext.getWrappedContext() ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) 
            log.debug( "Published root ApplicationContext as ServletContext attribute with name \"" + 
                       WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "\"" ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "ApplicationContext has been loaded" ) ;
    }

}
