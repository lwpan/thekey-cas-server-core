package me.thekey.cas.ssl;

import org.apache.commons.ssl.HostnameVerifier;
import org.apache.commons.ssl.SSL;
import org.apache.commons.ssl.SSLClient;
import org.apache.commons.ssl.SSLSocketWrapper;
import org.apache.commons.ssl.TrustMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.GeneralSecurityException;
import java.util.Collection;

public class TheKeyLDAPSocket extends SSLClient {
    private static final Logger LOG = LoggerFactory.getLogger(TheKeyLDAPSocket.class);
    private static final TheKeyLDAPSocket INSTANCE;

    static {
        TheKeyLDAPSocket sf = null;
        try {
            sf = new TheKeyLDAPSocket();
        } catch (Exception e) {
            System.out.println("could not create TheKeyLDAPSocket: " + e);
            e.printStackTrace();
        } finally {
            INSTANCE = sf;
        }
    }

    private final boolean duplicateSslObj;
    private final SSL ssl;

    private TheKeyLDAPSocket() throws GeneralSecurityException, IOException {
        // try retrieving the SSL object from the parent object
        SSL ssl = null;
        try {
            final Field f = SSLClient.class.getDeclaredField("ssl");
            f.setAccessible(true);
            ssl = (SSL) f.get(this);
        } catch (final Exception ignored) {
        } finally {
            this.duplicateSslObj = ssl == null;
            this.ssl = ssl != null ? ssl : new SSL();
        }

        setCheckHostname(true);
        setTrustMaterial(TrustMaterial.DEFAULT);
    }

    public static SocketFactory getDefault() {
        return getInstance();
    }

    public static TheKeyLDAPSocket getInstance() {
        return INSTANCE;
    }

    @Override
    public void addAllowedName(final String s) {
        super.addAllowedName(s);
        if (duplicateSslObj) {
            ssl.addAllowedName(s);
        }
    }

    @Override
    public void addAllowedNames(final Collection c) {
        super.addAllowedNames(c);
        if (duplicateSslObj) {
            ssl.addAllowedNames(c);
        }
    }

    @Override
    public void clearAllowedNames() {
        super.clearAllowedNames();
        if (duplicateSslObj) {
            ssl.clearAllowedNames();
        }
    }

    @Override
    public void setCheckHostname(final boolean check) {
        super.setCheckHostname(check);
        if (duplicateSslObj) {
            ssl.setCheckHostname(check);
        }
    }

    @Override
    public void setHostnameVerifier(final HostnameVerifier verifier) {
        super.setHostnameVerifier(verifier);
        if (duplicateSslObj) {
            ssl.setHostnameVerifier(verifier);
        }
    }

    /**
     * We override this to ensure hostname validation is executed when this socket is connected
     *
     * @return
     * @throws IOException
     */
    @Override
    public Socket createSocket() throws IOException {
        final Socket socket = super.createSocket();
        if (socket instanceof SSLSocket) {
            return new PostConnectSslSocketWrapper((SSLSocket) socket, ssl);
        }
        return socket;
    }

    private static class PostConnectSslSocketWrapper extends SSLSocketWrapper {
        private final SSL ssl;

        public PostConnectSslSocketWrapper(final SSLSocket s, final SSL ssl) {
            super(s);
            this.ssl = ssl;
        }

        @Override
        public void connect(final SocketAddress endpoint) throws IOException {
            super.connect(endpoint);
            super.startHandshake();
            if (endpoint instanceof InetSocketAddress) {
                ssl.doPostConnectSocketStuff(this, ((InetSocketAddress) endpoint).getHostString());
            }
        }

        @Override
        public void connect(final SocketAddress endpoint, final int timeout) throws IOException {
            super.connect(endpoint, timeout);
            super.startHandshake();
            if (endpoint instanceof InetSocketAddress) {
                ssl.doPostConnectSocketStuff(this, ((InetSocketAddress) endpoint).getHostString());
            }
        }

        @Override
        public void startHandshake() throws IOException {
            // disable manual handshake, we already started the handshake on connect
            // XXX: not ideal, but it gives us hostname verification by sacrificing connection renegotiation
        }
    }
}
