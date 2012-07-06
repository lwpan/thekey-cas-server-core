package org.ccci.gto.cas.util;

import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

public final class RandomKeyGenerator {
    private final static SecureRandom RAND = new SecureRandom();

    public static String generateKey(final int length) {
        String key = "";
        while (key.length() < length) {
            // generate enough entropy to fill the missing characters in the
            // output string
            // ((characters + 3) / 4) * 3
            // we align the needed entropy at 3 byte intervals to ensure full
            // entropy for every generated character
            final byte[] data = new byte[((length + 3 - key.length()) / 4) * 3];
            RAND.nextBytes(data);

            // base64 encode the entropy to produce a random character sequence
            final String raw = DatatypeConverter.printBase64Binary(data);
            key = key + raw;
        }

        // only return the requested number of characters
        return key.substring(0, length);
    }

    // generate a key using only uri safe characters
    public static String generateUriSafeKey(final int length) {
        return generateKey(length).replace("/", "_").replace("+", "-");
    }
}
