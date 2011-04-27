package org.ccci.gto.cas.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.config.XmlConfigurator;

/**
 * This is a generic server config list object that can be used to load a
 * whitelist, redlist, greenlist, etc used to dynamically enable/disable
 * features based on the service
 * 
 * @author Daniel Frett
 */
public class ServerConfigList {
    /** Logger that is available to subclasses */
    protected final Log log = LogFactory.getLog(getClass());

    private static final String DEFAULT_SERVERELEMENT = "server";
    private static final String DEFAULT_LIST = "classpath:config/whitelist.xml";

    /** list of servers in the specified config. */
    private final ArrayList<String> list = new ArrayList<String>();
    private String location;
    private String serverElement;

    public ServerConfigList() {
	this(DEFAULT_LIST, DEFAULT_SERVERELEMENT);
    }

    public ServerConfigList(String location) {
	this(location, DEFAULT_SERVERELEMENT);
    }

    public ServerConfigList(String location, String serverElement) {
	this.location = location;
	this.serverElement = serverElement;
	this.loadList();
    }

    /**
     * method that loads the specified list into memory
     */
    private synchronized void loadList() {
	if (log.isDebugEnabled()) {
	    log.debug("loading list from: " + this.location);
	}

	try {
	    // clear any existing parameters in the list
	    this.list.clear();

	    // load the config
	    XmlConfigurator config = new XmlConfigurator(this.location);
	    config.refresh();

	    // expand the list to include some possible variations.
	    // TODO: this code needs to become much more robust
	    for (String authority : config.getListAsString(this.serverElement)) {
		if (authority.startsWith("www.")) {
		    this.list.add(authority);
		    this.list.add(authority.substring(4));
		} else {
		    this.list.add(authority);
		    this.list.add("www." + authority);
		}
	    }
	} catch (Exception e) {
	    this.list.clear();
	    log.error("There was a problem loading the list.", e);
	}
    }

    /**
     * determines if a particular service is in this list
     * 
     * @param service
     * @return
     */
    public boolean inList(String service) {
	try {
	    // parse the service to extract the authority
	    final URL url = new URL(service);

	    // return true if the authority is in the whitelist
	    if (this.list.contains(url.getAuthority())) {
		return true;
	    }
	} catch (MalformedURLException e) {
	    if (log.isDebugEnabled())
		log.debug("Could not parse service to URL: " + service);
	}

	return false;
    }

    /**
     * @param location
     *            the location of the list being loaded
     */
    public void setLocation(final String location) {
	this.location = location;
	this.loadList();
    }

    /**
     * @param serverElement
     *            the serverElement of entries to load from the xml config file
     */
    public void setServerElement(final String serverElement) {
	this.serverElement = serverElement;
	this.loadList();
    }
}
