package org.ccci.gto.cas.services.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;

/**
 * provides the list of supported languages, loaded from a property file
 */
public class Languages implements List<Entry<Locale, String>> {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
    private final DefaultPropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private String location;
    private Properties properties = null;
    private List<Entry<Locale, String>> languages = Collections.emptyList();

    private final static Comparator<Entry<Locale, String>> languageComparator = new Comparator<Entry<Locale, String>>() {
        public int compare(final Entry<Locale, String> ent0, final Entry<Locale, String> ent1) {
            final String lang0 = ent0.getValue().toLowerCase(ent0.getKey());
            final String lang1 = ent1.getValue().toLowerCase(ent1.getKey());
	    return lang0.compareTo(lang1);
	}
    };

    /**
     * load the languages from the current location
     */
    protected synchronized void loadLanguages() {
	log.debug("Loading the raw language list");
        final HashMap<Locale, String> raw = new HashMap<Locale, String>();
	final Properties properties = new Properties();
	try {
	    // load the language properties
	    final Resource resource = this.resourceLoader
		    .getResource(this.location);
	    if (resource == null || !resource.exists()) {
		log.error(
			"Error loading languages, {} is invalid or doesn't exist",
			this.location);
		return;
	    }
	    this.propertiesPersister
		    .load(properties, resource.getInputStream());

	    // extract all languages from the loaded properties
	    for (final String key : properties.stringPropertyNames()) {
		// skip any language properties
		if (key.contains(".")) {
		    continue;
		}

		// store the language in the list of languages
		final String code = key.toLowerCase();
		final String language = properties.getProperty(key);
		if (log.isDebugEnabled()) {
		    log.debug("Adding language: " + code + ": " + language);
		}
                raw.put(Locale.forLanguageTag(code), language);
	    }
	} catch (final IOException e) {
	    log.error("Error loading languages, using existing list.", e);
	    return;
	}

	log.debug("generating the sorted list of languages");
        final Map<Locale, String> locked = Collections.unmodifiableMap(raw);
        final ArrayList<Entry<Locale, String>> sorted = new ArrayList<Entry<Locale, String>>(locked.entrySet());
	Collections.sort(sorted, languageComparator);

	log.debug("replacing languages list");
	this.languages = Collections.unmodifiableList(sorted);
	this.properties = properties;
    }

    /**
     * return the direction of the specified locale
     * 
     * @param locale
     * @return
     */
    public String getDirection(final Locale locale) {
        return this.getDirection(locale.toLanguageTag());
    }

    private String getDirection(final String code) {
	// check to see if the dir property is rtl
	if (properties != null
		&& properties.getProperty(code + ".dir", "ltr")
			.equalsIgnoreCase("rtl")) {
	    return "rtl";
	}

	// default to ltr
	return "ltr";
    }

    public synchronized void setLocation(final String location) {
	this.location = location;
	this.loadLanguages();
    }

    /** wrapped List interface methods */
    @Override
    public boolean add(final Entry<Locale, String> e) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int index, final Entry<Locale, String> element) {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends Entry<Locale, String>> c) {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Entry<Locale, String>> c) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final Object o) {
	return languages.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
	return languages.containsAll(c);
    }

    @Override
    public Entry<Locale, String> get(final int index) {
	return languages.get(index);
    }

    @Override
    public int indexOf(final Object o) {
	return languages.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
	return languages.isEmpty();
    }

    @Override
    public Iterator<Entry<Locale, String>> iterator() {
        return this.languages.iterator();
    }

    @Override
    public int lastIndexOf(final Object o) {
	return languages.lastIndexOf(o);
    }

    @Override
    public ListIterator<Entry<Locale, String>> listIterator() {
	return languages.listIterator();
    }

    @Override
    public ListIterator<Entry<Locale, String>> listIterator(final int index) {
	return languages.listIterator(index);
    }

    @Override
    public boolean remove(final Object o) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Locale, String> remove(final int index) {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Locale, String> set(final int index, final Entry<Locale, String> element) {
	throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
	return languages.size();
    }

    @Override
    public List<Entry<Locale, String>> subList(final int fromIndex,
	    final int toIndex) {
	return languages.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
	return languages.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
	return languages.toArray(a);
    }
}
