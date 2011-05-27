package org.ccci.gto.cas.persist.ldap;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.ccci.gcx.idm.core.util.GeneralizedTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;

/**
 * <b>AbstractAttributesMapper</b>
 * 
 * @author Daniel Frett
 */
public abstract class AbstractAttributesMapper implements AttributesMapper {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Get the specified attribute value safely, in case it isn't present, and
     * would otherwise throw a {@link NullPointerException}.
     * 
     * @param attrs
     *            {@link Attributes} returned from the LDAP server.
     * @param name
     *            Name of {@link Attribute} to retrieve.
     * 
     * @return Recovered {@link Attribute} or <tt>null</tt> if not present.
     * 
     * @throws NamingException
     *             if the specified attribute name is not found
     */
    protected Object getValue(final Attributes attrs, final String name)
	    throws NamingException {
	final Attribute attr = attrs.get(name);
	if (attr != null && attr.size() > 0) {
	    return attr.get();
	}

	return null;
    }

    protected boolean getBooleanValue(final Attributes attrs, final String name)
	    throws NamingException {
	return Boolean.parseBoolean(this.getStringValue(attrs, name));
    }

    protected String getStringValue(final Attributes attrs, final String name)
	    throws NamingException {
	final Object value = this.getValue(attrs, name);
	if (value instanceof String) {
	    return (String) value;
	}

	return null;
    }

    /**
     * Get the specified generalized time attribute safely, in case it isn't
     * present, and would otherwise throw a {@link NullPointerException}. If the
     * underlying timestamp found in the {@link Attributes} is invalid, this
     * method will simply return a <tt>null</tt>
     * 
     * @param attrs
     *            {@link Attributes} returned from the LDAP server.
     * @param name
     *            Name of {@link Attribute} to retrieve.
     * 
     * @return {@link Date} version of specified {@link Attribute}.
     * 
     * @throws NamingException
     *             if the specified attribute name is not found
     */
    protected Date getTimeValue(final Attributes attrs, final String name)
	    throws NamingException {
	try {
	    return new GeneralizedTime(this.getStringValue(attrs, name))
		    .getCalendar().getTime();
	} catch (ParseException pe) {
	}

	return null;
    }

    protected List<?> getValues(final Attributes attrs, final String name)
	    throws NamingException {
	final Attribute attr = attrs.get(name);
	if (attr != null) {
	    return Collections.list(attr.getAll());
	}

	return Collections.EMPTY_LIST;
    }

    protected List<? extends String> getStringValues(final Attributes attrs,
	    final String name) throws NamingException {
	final List<String> results = new ArrayList<String>();

	for (Object obj : this.getValues(attrs, name)) {
	    if (obj instanceof String) {
		results.add((String) obj);
	    }
	}

	return results;
    }
}
