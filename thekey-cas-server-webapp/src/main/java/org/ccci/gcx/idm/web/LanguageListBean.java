package org.ccci.gcx.idm.web;

import static org.ccci.gcx.idm.web.Constants.DEFAULT_MESSAGES_LOCATION;

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
 * provides the list of languages active in our system. It retrieves this from a
 * properties file.
 * 
 * @author Ken Burcham, Daniel Frett
 */
public class LanguageListBean {
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

    public LanguageListBean() {
	this.setLocation(DEFAULT_MESSAGES_LOCATION);
    }

    /**
     * load the languages from the current location
     */
    protected synchronized void loadLanguages() {
	log.debug("Loading language list");
	this.languages.clear();
	try {
	    final Properties p = PropertiesLoaderUtils
		    .loadAllProperties(this.location);

	    // store all properties in a TreeMap
	    for (final String key : p.stringPropertyNames()) {
		final String code = key.toLowerCase();
		final String language = p.getProperty(key);
		if (log.isDebugEnabled()) {
		    log.debug("Adding language: " + code + ": " + language);
		}
		languages.put(code, language);
	    }
	} catch (final IOException e) {
	    log.error("Exception trying to load languages.", e);
	}

	log.debug("generating sorted languages list");
	this.sortedLanguages.clear();
	this.sortedLanguages.addAll(this.getLanguages().entrySet());
	Collections.sort(this.sortedLanguages, languageComparator);
    }

    /**
     * force a reload of the languages
     */
    public void reloadConfiguration() {
	this.loadLanguages();
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

    public synchronized void setLocation(String location) {
	this.location = location;
	this.loadLanguages();
    }
}
