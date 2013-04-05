package org.ccci.gto.cas.oauth.model;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

public class ClientTest {
    private static String[] validMobileUris = { "thekey:/oauth/mobile/android", "thekey:/oauth/mobile/ios",
            "thekey:///oauth/mobile/android" };
    private static String[] invalidMobileUris = { "thekey:android", "https://www.example.com", "http://www.example.com" };

    @Test
    public void redirectUriTest() {
        // create a mobile client
        final Client client = new Client();
        client.setMobile(true);

        for (final String uri : validMobileUris) {
            Assert.assertTrue("Valid mobile uri: " + uri, client.isValidRedirectUri(URI.create(uri)));
        }

        for (final String uri : invalidMobileUris) {
            Assert.assertFalse("Invalid mobile uri: " + uri, client.isValidRedirectUri(URI.create(uri)));
        }
    }
}
