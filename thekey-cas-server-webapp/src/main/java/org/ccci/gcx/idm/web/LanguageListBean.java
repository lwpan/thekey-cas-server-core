package org.ccci.gcx.idm.web;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * provides the list of languages active in our system. It retrieves this from a
 * properties file.
 * 
 * @author Ken Burcham, Daniel Frett
 */
public class LanguageListBean {
    protected static final Log log = LogFactory.getLog(LanguageListBean.class);

    private String location;
    final private TreeMap<String, String> languages = new TreeMap<String, String>();

    public LanguageListBean() {
	this.setLocation(Constants.DEFAULT_MESSAGES_LOCATION);
    }

    /**
     * load the languages from the current location
     */
    protected synchronized void loadLanguages() {
	log.debug("Loading language list");
	this.languages.clear();
	try {
	    Properties p = PropertiesLoaderUtils
		    .loadAllProperties(this.location);

	    // store all properties in a TreeMap
	    for (Object key : p.keySet()) {
		if (log.isDebugEnabled())
		    log.debug("Adding language: "
			    + p.getProperty(key.toString()));
		languages.put(key.toString().trim().toLowerCase(),
			p.getProperty(key.toString().trim()));
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

    @Deprecated
    public Map<String, String> getLanguageList() {
	return this.getLanguages();
    }

    /**
     * @return the languages
     */
    public Map<String, String> getLanguages() {
	if (log.isDebugEnabled()) {
	    log.debug("Returning languages: " + this.languages.size());
	}

	return this.languages;
    }

    public synchronized void setLocation(String location) {
	this.location = location;
	this.loadLanguages();
    }
}
