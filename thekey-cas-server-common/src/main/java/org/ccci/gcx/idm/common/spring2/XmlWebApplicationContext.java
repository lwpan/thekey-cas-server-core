package org.ccci.gcx.idm.common.spring2;

import org.springframework.core.io.ClassPathResource ;
import org.springframework.core.io.Resource ;
import org.springframework.web.struts.ContextLoaderPlugIn;


/**
 * <b>XmlWebApplicationContext</b> extends the Spring
 * {@link XmlWebApplicationContext}, but overrides resource acquistion. In a
 * servlet construct, resources are acquired through the servlet context. In
 * this case, we refer back to the classpath. This was done to support
 * {@link org.ccci.gcx.idm.common.ApplicationContext} being used with the
 * {@link ContextLoaderPlugIn}. The {@link ContextLoaderPlugin} expects a web aware
 * context which is implemented by {@link XmlWebApplicationContext}.
 * <p>
 * For Struts2 applications, this context should be registered in the <tt>web.xml</tt>
 * file as follows:
 * <p>
 * <pre>
 *     &lt;context-param&gt;
 *         &lt;param-name&gt;contextClass&lt;/param-name&gt;
 *         &lt;param-value&gt;org.ccci.gcx.idm.common.spring2.XmlWebApplicationContext&lt;/param-value&gt;
 *     &lt;/context-param&gt;
 *     &lt;context-param&gt;
 *         &lt;param-name&gt;contextConfigLocation&lt;/param-name&gt;
 *         &lt;param-value&gt;classpath*:contexts/*.xml&lt;/param-value&gt;
 *     &lt;/context-param%gt;
 * </pre>
 * <p>
 * <b>Note:</b> when registering this context in the <tt>web.xml</tt> file, be sure not
 * to deploy any of the <tt>TEST-*.xml</tt> context files; they will override the
 * production level bean definitions if they exist.
 *
 * @author Greg Crider Oct 12, 2006 2:36:36 PM
 */
public class XmlWebApplicationContext extends org.springframework.web.context.support.XmlWebApplicationContext
{

    /**
     * Default constructor.
     */
    public XmlWebApplicationContext()
    {
    }

    /**
     * Get resources from the classpath, and not the servlet context. This is
     * necessary for use with
     * {@link org.ccci.gcx.idm.common.spring2.ApplicationContext} which must read other
     * files (such as hibernate.cfg.xml}. These files do not reside within the
     * servlet context message resource.
     */
    protected Resource getResourceByPath( String a_Path )
    {
        return new ClassPathResource( a_Path, this.getClassLoader() ) ;
    }

}
