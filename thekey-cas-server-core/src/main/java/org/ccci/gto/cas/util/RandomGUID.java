package org.ccci.gto.cas.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * This class is used to generate random GUID's that can optionally be
 * cryptographically secure by using a SecureRandom RNG
 * 
 * @author Daniel Frett
 */
public final class RandomGUID {
    private final static SecureRandom secureRand = new SecureRandom();
    private final static Random rand = new Random(secureRand.nextLong());

    private final String guid;

    /*
     * Default constructor. With no specification of security option, this
     * constructor defaults to lower security, high performance.
     */
    public RandomGUID() {
	this(false);
    }

    /*
     * Constructor with security option. Setting secure true enables each random
     * number generated to be cryptographically strong. Secure false defaults to
     * the standard Random function seeded with a single cryptographically
     * strong random number.
     */
    public RandomGUID(boolean secure) {
	this.guid = generateGuid(secure);
    }

    // use the actual guid as the String representation of this object
    public String toString() {
	return this.guid;
    }

    /*
     * Method to generate a random GUID using a regular RNG
     */
    public static String generateGuid() {
	return generateGuid(false);
    }

    /*
     * Method to generate a random GUID that may optionally be generated with a
     * Secure RNG
     */
    public static String generateGuid(boolean secure) {
	// generate 16 bytes of randomness for this GUID
	final byte[] data = new byte[16];
	if (secure) {
	    secureRand.nextBytes(data);
	} else {
	    // synchronize access to the RNG
	    synchronized (rand) {
		rand.nextBytes(data);
	    }
	}

	// format the generated randomness into a GUID
	final String rawData = new BigInteger(1, data).toString(16);
	final String rawGuid = StringUtils.repeat("0", 32 - rawData.length())
		+ rawData.toUpperCase();
	final StringBuffer guid = new StringBuffer();
	guid.append(rawGuid.substring(0, 8));
	guid.append("-");
	guid.append(rawGuid.substring(8, 12));
	guid.append("-");
	guid.append(rawGuid.substring(12, 16));
	guid.append("-");
	guid.append(rawGuid.substring(16, 20));
	guid.append("-");
	guid.append(rawGuid.substring(20));

	// return the generated GUID
	return guid.toString();
    }
}
