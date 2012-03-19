package org.ccci.gto.cas.web.tag;

import java.util.ArrayList;
import java.util.List;

public final class TheKeyTags {
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

    public static List<?> push(List<? super Object> list, final Object item) {
	if (list == null) {
	    list = new ArrayList<Object>();
	}

	list.add(item);

	return list;
    }
}
