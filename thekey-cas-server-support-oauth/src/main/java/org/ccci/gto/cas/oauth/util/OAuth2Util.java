package org.ccci.gto.cas.oauth.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

public final class OAuth2Util {
    public static Set<String> parseScope(final String scope) {
        final Set<String> resp = new HashSet<String>();
        resp.addAll(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        return resp;
    }
}
