package me.thekey.cas.services.web;

import com.google.common.collect.ForwardingList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * provides the list of supported languages, loaded from a property file
 */
public class Languages extends ForwardingList<Entry<Locale, String>> {
    private static final Logger LOG = LoggerFactory.getLogger(Languages.class);

    private final ResourceLoader resourceLoader;
    private final PropertiesPersister propertiesPersister;

    private String location;
    private Properties properties = null;
    private Map<Locale, String> languages = Collections.emptyMap();
    private List<Entry<Locale, String>> sortedLanguages = Collections.emptyList();

    private final static Comparator<Entry<Locale, String>> COMPARATOR_LANGUAGES = new Comparator<Entry<Locale,
            String>>() {
        public int compare(final Entry<Locale, String> ent0, final Entry<Locale, String> ent1) {
            final String lang0 = ent0.getValue().toLowerCase(ent0.getKey());
            final String lang1 = ent1.getValue().toLowerCase(ent1.getKey());
            return lang0.compareTo(lang1);
        }
    };

    public Languages() {
        this(null);
    }

    Languages(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
        this.propertiesPersister = new DefaultPropertiesPersister();
    }

    @Override
    protected List<Entry<Locale, String>> delegate() {
        return this.sortedLanguages;
    }

    /**
     * load the sortedLanguages from the current location
     */
    private synchronized void loadLanguages() {
        LOG.debug("Loading the language list");
        final HashMap<Locale, String> raw = new HashMap<>();
        final Properties properties = new Properties();
        try {
            // load the language properties
            final Resource resource = this.resourceLoader.getResource(this.location);
            if (resource == null || !resource.exists()) {
                LOG.error("Error loading languages, {} is invalid or doesn't exist", this.location);
                return;
            }
            this.propertiesPersister.load(properties, resource.getInputStream());

            // extract all sortedLanguages from the loaded properties
            for (final String key : properties.stringPropertyNames()) {
                // skip any language properties
                if (key.contains(".")) {
                    continue;
                }

                // store the language in the list of sortedLanguages
                final String code = key.toLowerCase();
                final String language = properties.getProperty(key);
                LOG.debug("Adding language: {}: {}", code, language);
                raw.put(Locale.forLanguageTag(code), language);
            }
        } catch (final IOException e) {
            LOG.error("Error loading languages, using existing list.", e);
            return;
        }

        LOG.debug("generating the sorted list of languages");
        final Map<Locale, String> locked = Collections.unmodifiableMap(raw);
        final ArrayList<Entry<Locale, String>> sorted = new ArrayList<>(locked.entrySet());
        Collections.sort(sorted, COMPARATOR_LANGUAGES);

        LOG.debug("replacing languages list");
        this.languages = locked;
        this.sortedLanguages = Collections.unmodifiableList(sorted);
        this.properties = properties;
    }

    /**
     * return the locale we have a translation for that is the best match for the specified locale
     *
     * @param locale the locale we are looking for a best match against
     * @return the closest matching locale
     */
    public Locale getBestMatch(final Locale locale) {
        // check for an exact match first
        if (this.languages.containsKey(locale)) {
            return locale;
        }

        // use fuzzy matching logic
        // TODO: this isn't exactly correct, but works fine for the current list of supported languages
        final Locale.Builder builder = new Locale.Builder();
        builder.setLocale(locale).clearExtensions();
        for (int q = 3; q > 0; q--) {
            // strip parts of the Locale until it matches
            switch (q) {
                case 1:
                    builder.setRegion(null);
                case 2:
                    builder.setScript(null);
                case 3:
                    builder.setVariant(null);
            }

            // return the stripped locale if it's a supported language
            final Locale tmp = builder.build();
            if (this.languages.containsKey(tmp)) {
                return tmp;
            }
        }

        // default to the provided locale
        return locale;
    }

    /**
     * return the direction of the specified locale
     *
     * @param locale
     * @return
     */
    public String getDirection(final Locale locale) {
        return this.getDirection(this.getBestMatch(locale).toLanguageTag());
    }

    private String getDirection(final String code) {
        // check to see if the dir property is rtl
        if (this.properties != null && this.properties.getProperty(code + ".dir", "ltr").equalsIgnoreCase("rtl")) {
            return "rtl";
        }

        // default to ltr
        return "ltr";
    }

    public String getName(final Locale locale) {
        return this.languages.get(this.getBestMatch(locale));
    }

    public synchronized void setLocation(final String location) {
        this.location = location;
        this.loadLanguages();
    }
}
