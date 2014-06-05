package me.thekey.cas.util;

import org.jasig.cas.util.RandomStringGenerator;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;

public final class Base64RandomStringGenerator implements RandomStringGenerator {
    private static final SecureRandom RAND = new SecureRandom();

    /** The default maximum length. */
    private static final int DEFAULT_LENGTH = 36;

    /** The maximum length the random string can be. */
    private final int length;

    private static final boolean DEFAULT_URI_SAFE = true;
    private boolean uriSafe = true;

    public Base64RandomStringGenerator() {
        this(DEFAULT_LENGTH);
    }

    public Base64RandomStringGenerator(final int length) {
        this(length, DEFAULT_URI_SAFE);
    }

    public Base64RandomStringGenerator(final int length, final boolean uriSafe) {
        this.length = length;
        this.uriSafe = uriSafe;
    }

    @Override
    public int getMinLength() {
        return this.length;
    }

    @Override
    public int getMaxLength() {
        return this.length;
    }

    @Override
    public String getNewString() {
        String key = "";
        while (key.length() < this.length) {
            // generate enough entropy to fill the missing characters in the
            // output string ((characters + 3) / 4) * 3, we align the needed
            // entropy at 3 byte intervals to ensure full entropy for every
            // generated character
            final byte[] data = new byte[((this.length + 3 - key.length()) / 4) * 3];
            RAND.nextBytes(data);

            // base64 encode the entropy to produce a random character sequence
            final String raw = DatatypeConverter.printBase64Binary(data);
            key = key + raw;
        }
        key = key.substring(0, this.length);

        // was a uriSafe string requested
        if (this.uriSafe) {
            key = key.replace("/", "_").replace("+", "-");
        }

        // return the generated key
        return key;
    }

    @Override
    public byte[] getNewStringAsBytes() {
        throw new RuntimeException("getNewStringAsBytes() is not implemented because it is an implementation specific" +
                " function used by DefaultRandomStringGenerator");
    }
}
