package org.ccci.gto.cas.persist.ldap;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.util.GeneralizedTime;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>AbstractAttributesMapper</b>
 * 
 * @author Daniel Frett
 */
public abstract class AbstractAttributesMapper implements AttributesMapper {
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
	return this.getBooleanValue(attrs, name, false);
    }

    protected boolean getBooleanValue(final Attributes attrs,
	    final String name, final Boolean defaultValue)
	    throws NamingException {
	final String value = this.getStringValue(attrs, name);
	if (value != null) {
	    return Boolean.parseBoolean(value);
	}
	return defaultValue;
    }

    protected Map<String, Double> getStrengthValues(final Attributes attrs, final String name) throws NamingException {
        return this.getStrengthValues(attrs, name, "$");
    }

    protected Map<String, Double> getStrengthValues(final Attributes attrs, final String name, final String separator)
            throws NamingException {
        final Map<String, Double> strengths = new HashMap<String, Double>();
        for (final String value : this.getStringValues(attrs, name)) {
            final String[] values = StringUtils.split(value, separator);

            // only add valid values
            if (values.length != 2) {
                continue;
            }

            // set the strength value, catching any parsing errors
            try {
                strengths.put(values[0], Double.parseDouble(values[1]));
            } catch (final Exception e) {
            }
        }

        return strengths;
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

    protected List<String> getStringValues(final Attributes attrs, final String name) throws NamingException {
        final List<String> results = new ArrayList<>();

        for (Object obj : this.getValues(attrs, name)) {
            if (obj instanceof String) {
                results.add((String) obj);
            }
        }

        return results;
    }
}
