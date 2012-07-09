package org.ccci.gto.cas.util;

import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomKeyGeneratorTest extends TestCase {
    private final static Logger LOG = LoggerFactory.getLogger(RandomKeyGeneratorTest.class);

    public void testEntropy() {
        for (int x = 1; x < 100; x++) {
            final String key = RandomKeyGenerator.generateKey(x);
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
            final String key = RandomKeyGenerator.generateUriSafeKey(length);
            assertEquals("key is not the correct length", length, key.length());
            assertTrue(key + " is not uri safe", uriSafe.matcher(key).matches());
        }
    }
}
