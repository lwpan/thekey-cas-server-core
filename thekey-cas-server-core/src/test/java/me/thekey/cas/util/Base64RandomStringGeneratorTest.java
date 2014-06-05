package me.thekey.cas.util;

import java.util.regex.Pattern;

import junit.framework.TestCase;
import me.thekey.cas.util.Base64RandomStringGenerator;

public class Base64RandomStringGeneratorTest extends TestCase {
    public void testEntropy() {
        for (int x = 1; x < 100; x++) {
            final String key = new Base64RandomStringGenerator(x).getNewString();
            assertEquals(x, key.length());

            // no padding characters (=) should be returned, it lessens the
            // entropy of the generated key
            assertFalse("key shouldn't contain base64 padding characters", key.contains("="));
        }
    }

    public void testUriSafeKey() {
        final Pattern uriSafe = Pattern.compile("^[a-zA-Z0-9\\-_\\.~]*$");
        final int length = 1000;

        for (int x = 1; x < 1000; x++) {
            final String key = new Base64RandomStringGenerator(length, true).getNewString();
            assertEquals("key is not the correct length", length, key.length());
            assertTrue(key + " is not uri safe", uriSafe.matcher(key).matches());
        }
    }
}
