package org.ccci.gto.cas.facebook.util;

import junit.framework.TestCase;

import org.ccci.gto.cas.facebook.util.FacebookUtils;

public class FacebookUtilsTest extends TestCase {
    private static final String GOODEXAMPLE = "vlXgu64BQGFSQrY0ZcJBZASMvYvTHu9GQ0YM9rjPSso.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsIjAiOiJwYXlsb2FkIn0";
    private static final String BADEXAMPLE = "vlXgu64BQGFSQrY0ZcJBZASMvYvTHu9GQ0YM9rjPSso.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsIjAiOiJwYXlsc2FkIn0";
    private static final String GOODSECRET = "secret";
    private static final String BADSECRET = "terces";

    public void testValidateSignedRequest() throws Exception {
	assertTrue(FacebookUtils.validateSignedRequest(GOODEXAMPLE, GOODSECRET,
		"HMAC-SHA256"));
	assertFalse(FacebookUtils.validateSignedRequest(BADEXAMPLE, GOODSECRET,
		"HMAC-SHA256"));
	assertFalse(FacebookUtils.validateSignedRequest(GOODEXAMPLE,
		GOODSECRET, "HMAC-MD5"));
	assertFalse(FacebookUtils.validateSignedRequest(BADEXAMPLE, GOODSECRET,
		"HMAC-MD5"));

	assertFalse(FacebookUtils.validateSignedRequest(GOODEXAMPLE, BADSECRET,
		"HMAC-SHA256"));
	assertFalse(FacebookUtils.validateSignedRequest(BADEXAMPLE, BADSECRET,
		"HMAC-SHA256"));
	assertFalse(FacebookUtils.validateSignedRequest(GOODEXAMPLE, BADSECRET,
		"HMAC-MD5"));
	assertFalse(FacebookUtils.validateSignedRequest(BADEXAMPLE, BADSECRET,
		"HMAC-MD5"));
    }
}
