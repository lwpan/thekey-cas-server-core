package org.ccci.gto.cas.web.tag;

public class TheKeyTags {
    public static boolean instanceOf(final Object obj, final String clazz) {
	if (obj == null || clazz == null) {
	    return false;
	}

	try {
	    return Class.forName(clazz, false,
		    Thread.currentThread().getContextClassLoader()).isInstance(
		    obj);
	} catch (final ClassNotFoundException e) {
	    return false;
	}
    }
}
