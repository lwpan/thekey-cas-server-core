package org.ccci.gto.cas.util;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomKeyGeneratorTest extends TestCase {
    protected final static Logger LOG = LoggerFactory.getLogger(RandomKeyGeneratorTest.class);

    public void testEntropy() {
        for (int x = 1; x < 100; x++) {
            final String key = RandomKeyGenerator.generateKey(x);
            assertEquals(x, key.length());

            // no padding characters (=) should be returned, it lessens the
            // entropy of the generated key
            assertFalse("key shouldn't contain base64 padding characters", key.contains("="));
        }
    }
}
