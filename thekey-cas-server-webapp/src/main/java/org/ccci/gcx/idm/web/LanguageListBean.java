package org.ccci.gcx.idm.web;

import static org.ccci.gcx.idm.web.Constants.DEFAULT_MESSAGES_LOCATION;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

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
    final private TreeMap<String, String> languages = new TreeMap<String, String>();

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
	} catch (Exception e) {
	    log.error("Exception trying to load languages.", e);
	}
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
	if (log.isDebugEnabled()) {
	    log.debug("Returning languages: " + this.languages.size());
	}

	return Collections.unmodifiableMap(this.languages);
    }

    public synchronized void setLocation(String location) {
	this.location = location;
	this.loadLanguages();
    }
}
