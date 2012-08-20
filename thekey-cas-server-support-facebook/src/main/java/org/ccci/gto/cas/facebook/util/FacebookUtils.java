package org.ccci.gto.cas.facebook.util;

import java.util.Arrays;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restfb.json.JsonObject;

public final class FacebookUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FacebookUtils.class);
    private static final Pattern SIGNEDREQUESTPARSER = Pattern.compile("\\.");

    /**
     * @return the data
     */
    public static final JsonObject getSignedData(final String signedRequest) {
	final String[] parts = SIGNEDREQUESTPARSER.split(signedRequest, 2);
	final String data = new String(Base64.decodeBase64(parts[1]));
        LOG.debug(data);
	return new JsonObject(data);
    }

    /**
     * @param signedRequest
     * @param secret
     * @param algorithm
     * @return
     */
    public static final boolean validateSignedRequest(
	    final String signedRequest, final String secret,
	    final String algorithm) {
	// extract the specified signature from the request
	final String[] parts = SIGNEDREQUESTPARSER.split(signedRequest, 2);
	final byte[] sig = Base64.decodeBase64(parts[0]);

	// determine which HMAC object to use
	final Mac mac;
	if (algorithm.equals("HMAC-SHA256")) {
	    try {
		mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
	    } catch (final Exception e) {
                LOG.error("error initializing HmacSHA256", e);
		return false;
	    }
	} else {
	    // algorithm is unrecognized, so return false
            LOG.error("Unrecognized HMAC algorithm: {}", algorithm);
	    return false;
	}

	// calculate the signature for the payload
	final byte[] calculatedSig = mac.doFinal(parts[1].getBytes());

	// throw an error if the signature is invalid
	if (!Arrays.equals(sig, calculatedSig)) {
            LOG.error("Bad signature for signed request");
	    return false;
	}

	// signatures match, return true
	return true;
    }
}
