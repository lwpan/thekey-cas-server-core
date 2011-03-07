package org.ccci.gcx.idm.common.spring2.ldap.ssl;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
/**
 * <b>DummySSLSocketFactory</b> is an SSLSocketFactory that accepts every certificate without validation.
 * <p>
 * This code was adapted from the original source found in the Apache Directory Project. It has been enhanced
 * slightly to ensure that it is a true singleton, among other things.
 * <p>
 * <b>NOTE:</b> this should not be used for production.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DummySSLSocketFactory extends SSLSocketFactory
{
    /** The default instance */
    private static SocketFactory Instance ;

    /** The delegate */
    private SSLSocketFactory m_Delegate = null;

    
    /**
     * Static initializer to properly create singleton instance.
     */
    static {
        synchronized( DummySSLSocketFactory.class ) {
            Instance = new DummySSLSocketFactory() ;
        }
    }
    

    /**
     * Gets the default Instance.
     * 
     * Note: This method is invoked from the JNDI framework when creating a
     * ldaps:// connection.
     * 
     * @return the default Instance
     */
    public static SocketFactory getDefault()
    {
        return Instance ;
    }

    
    /**
     * Creates a new Instance of DummySSLSocketFactory.
     */
    public DummySSLSocketFactory()
    {
        try {
            TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers()
                {
                    return new X509Certificate[0] ;
                }

                public void checkClientTrusted( X509Certificate[] arg0, String arg1 ) throws CertificateException
                {
                }

                public void checkServerTrusted( X509Certificate[] arg0, String arg1 ) throws CertificateException
                {
                }
            } ;
            TrustManager[] tma = { tm } ;
            SSLContext sc = SSLContext.getInstance( "TLS" ) ; //$NON-NLS-1$
            sc.init( null, tma, new SecureRandom() ) ;
            this.m_Delegate = sc.getSocketFactory() ;
        } catch ( Exception e ) {
            e.printStackTrace() ;
        }
    }

    
    /**
     * @see javax.net.ssl.SSLSocketFactory#getDefaultCipherSuites()
     */
    public String[] getDefaultCipherSuites()
    {
        return this.m_Delegate.getDefaultCipherSuites() ;
    }

    
    /**
     * @see javax.net.ssl.SSLSocketFactory#getSupportedCipherSuites()
     */
    public String[] getSupportedCipherSuites()
    {
        return this.m_Delegate.getSupportedCipherSuites() ;
    }

    
    /**
     * @see javax.net.ssl.SSLSocketFactory#createSocket(java.net.Socket,
     *      java.lang.String, int, boolean)
     */
    public Socket createSocket( Socket arg0, String arg1, int arg2, boolean arg3 ) throws IOException
    {
        try {
            return this.m_Delegate.createSocket( arg0, arg1, arg2, arg3 ) ;
        } catch ( IOException e ) {
            e.printStackTrace() ;
            throw e ;
        }
    }

    
    /**
     * @see javax.net.SocketFactory#createSocket(java.lang.String, int)
     */
    public Socket createSocket( String arg0, int arg1 ) throws IOException, UnknownHostException
    {
        try {
            return this.m_Delegate.createSocket( arg0, arg1 ) ;
        } catch ( IOException e ) {
            e.printStackTrace() ;
            throw e ;
        }
    }

    
    /**
     * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int)
     */
    public Socket createSocket( InetAddress arg0, int arg1 ) throws IOException
    {
        try {
            return this.m_Delegate.createSocket( arg0, arg1 ) ;
        } catch ( IOException e ) {
            e.printStackTrace() ;
            throw e ;
        }
    }

    
    /**
     * @see javax.net.SocketFactory#createSocket(java.lang.String, int,
     *      java.net.InetAddress, int)
     */
    public Socket createSocket( String arg0, int arg1, InetAddress arg2, int arg3 ) throws IOException,
            UnknownHostException
    {
        try {
            return this.m_Delegate.createSocket( arg0, arg1, arg2, arg3 ) ;
        } catch ( IOException e ) {
            e.printStackTrace() ;
            throw e ;
        }
    }

    
    /**
     * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int,
     *      java.net.InetAddress, int)
     */
    public Socket createSocket( InetAddress arg0, int arg1, InetAddress arg2, int arg3 ) throws IOException
    {
        try {
            return this.m_Delegate.createSocket( arg0, arg1, arg2, arg3 ) ;
        } catch ( IOException e ) {
            e.printStackTrace() ;
            throw e ;
        }
    }

}
