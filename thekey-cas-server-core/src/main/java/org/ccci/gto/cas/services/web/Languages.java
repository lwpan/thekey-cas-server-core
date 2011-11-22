package org.ccci.gto.cas.services.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * provides the list of supported languages, loaded from a property file
 */
public class Languages {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String location;
    private final HashMap<String, String> languages = new HashMap<String, String>();
    private final ArrayList<Entry<String, String>> sortedLanguages = new ArrayList<Entry<String, String>>();

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
	log.debug("Loading language list");
	final HashMap<String, String> languages = new HashMap<String, String>();
	try {
	    final Properties p = PropertiesLoaderUtils
		    .loadAllProperties(this.location);

	    // store all properties in a Map
	    for (final String key : p.stringPropertyNames()) {
		final String code = key.toLowerCase();
		final String language = p.getProperty(key);
		if (log.isDebugEnabled()) {
		    log.debug("Adding language: " + code + ": " + language);
		}
		languages.put(code, language);
	    }
	} catch (final IOException e) {
	    log.error("Error loading languages, using existing list.", e);
	    return;
	}

	log.debug("replacing languages list");
	this.languages.clear();
	this.languages.putAll(languages);

	log.debug("generating sorted languages list");
	this.sortedLanguages.clear();
	this.sortedLanguages.addAll(this.getLanguages().entrySet());
	Collections.sort(this.sortedLanguages, languageComparator);
    }

    /**
     * @return the languages
     */
    public Map<String, String> getLanguages() {
	return Collections.unmodifiableMap(this.languages);
    }

    public List<Entry<String, String>> getSortedLanguages() {
	return Collections.unmodifiableList(this.sortedLanguages);
    }

    public synchronized void setLocation(final String location) {
	this.location = location;
	this.loadLanguages();
    }
}
