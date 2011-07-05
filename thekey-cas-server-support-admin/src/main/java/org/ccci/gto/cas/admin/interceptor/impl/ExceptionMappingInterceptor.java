package org.ccci.gto.cas.admin.interceptor.impl;

import org.aopalliance.intercept.Invocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ExceptionHolder;


/**
 * <b>ExceptionMappingInterceptor</b> overrides some of the built in
 * functionality that comes stock with the XWork2 implmentation.
 * 
 * @author Greg Crider Nov 6, 2008 4:05:24 PM
 */
public class ExceptionMappingInterceptor extends com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor
{
    private static final long  serialVersionUID = 181680234663766776L ;

    protected static final Log log = LogFactory.getLog( ExceptionMappingInterceptor.class ) ;

    
    /**
     * Extends publishing to perform additional logging.
     * 
     * @param a_Invocation Original, intercepted {@link Invocation}.
     * @param a_ExceptionHolder {@link ExceptionHolder} that contains the offending {@link Exception}.
     * 
     * @see com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor#publishException(com.opensymphony.xwork2.ActionInvocation, com.opensymphony.xwork2.interceptor.ExceptionHolder)
     */
    protected void publishException( ActionInvocation a_Invocation, ExceptionHolder a_ExceptionHolder )
    {
        if ( a_ExceptionHolder != null ) {
            /*= ERROR =*/ log.error( 
                    "Exception was detected:\n--------------------------------------------------", 
                    a_ExceptionHolder.getException() ) ;
        }

        // Proceed with the normal processing to publish
        super.publishException( a_Invocation, a_ExceptionHolder ) ;
    }

}
