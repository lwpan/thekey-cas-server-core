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
public class Languages implements List<Entry<String, String>> {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
    private final DefaultPropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private String location;
    private Properties properties = null;
    private List<Entry<String, String>> languages = Collections.emptyList();

    private final static Comparator<Entry<String, String>> languageComparator = new Comparator<Entry<String, String>>() {
	public int compare(final Entry<String, String> arg0,
		final Entry<String, String> arg1) {
	    return arg0.getValue().compareTo(arg1.getValue());
	}
    };

    /**
     * load the languages from the current location
     */
    protected synchronized void loadLanguages() {
	log.debug("Loading the raw language list");
	final HashMap<String, String> raw = new HashMap<String, String>();
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

		// store the language in the list of langauges
		final String code = key.toLowerCase();
		final String language = properties.getProperty(key);
		if (log.isDebugEnabled()) {
		    log.debug("Adding language: " + code + ": " + language);
		}
		raw.put(code, language);
	    }
	} catch (final IOException e) {
	    log.error("Error loading languages, using existing list.", e);
	    return;
	}

	log.debug("generating the sorted list of languages");
	final Map<String, String> locked = Collections.unmodifiableMap(raw);
	final ArrayList<Entry<String, String>> sorted = new ArrayList<Entry<String, String>>(
		locked.entrySet());
	Collections.sort(sorted, languageComparator);

	log.debug("replacing languages list");
	this.languages = Collections.unmodifiableList(sorted);
	this.properties = properties;
    }

    /**
     * return the direction of the specified language code
     * 
     * @param code
     * @return
     */
    public String getDirection(final String code) {
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
    public boolean add(final Entry<String, String> e) {
	throw new UnsupportedOperationException();
    }

    public void add(final int index, final Entry<String, String> element) {
	throw new UnsupportedOperationException();
    }

    public boolean addAll(final Collection<? extends Entry<String, String>> c) {
	throw new UnsupportedOperationException();
    }

    public boolean addAll(final int index,
	    final Collection<? extends Entry<String, String>> c) {
	throw new UnsupportedOperationException();
    }

    public void clear() {
	throw new UnsupportedOperationException();
    }

    public boolean contains(final Object o) {
	return languages.contains(o);
    }

    public boolean containsAll(final Collection<?> c) {
	return languages.containsAll(c);
    }

    public Entry<String, String> get(final int index) {
	return languages.get(index);
    }

    public int indexOf(final Object o) {
	return languages.indexOf(o);
    }

    public boolean isEmpty() {
	return languages.isEmpty();
    }

    public Iterator<Entry<String, String>> iterator() {
	return languages.iterator();
    }

    public int lastIndexOf(final Object o) {
	return languages.lastIndexOf(o);
    }

    public ListIterator<Entry<String, String>> listIterator() {
	return languages.listIterator();
    }

    public ListIterator<Entry<String, String>> listIterator(final int index) {
	return languages.listIterator(index);
    }

    public boolean remove(final Object o) {
	throw new UnsupportedOperationException();
    }

    public Entry<String, String> remove(final int index) {
	throw new UnsupportedOperationException();
    }

    public boolean removeAll(final Collection<?> c) {
	throw new UnsupportedOperationException();
    }

    public boolean retainAll(final Collection<?> c) {
	throw new UnsupportedOperationException();
    }

    public Entry<String, String> set(final int index,
	    final Entry<String, String> element) {
	throw new UnsupportedOperationException();
    }

    public int size() {
	return languages.size();
    }

    public List<Entry<String, String>> subList(final int fromIndex,
	    final int toIndex) {
	return languages.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
	return languages.toArray();
    }

    public <T> T[] toArray(final T[] a) {
	return languages.toArray(a);
    }
}
