package org.ccci.gto.cas.util;

import java.security.SecureRandom;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>RandomPasswordGenerator</b> is used to generate random passwords intended
 * for temporary use. This implementation is rudimentary. You can specify
 * character strings for a specific language code. Only the values found in that
 * string will be used to generate a password. When generating the password for
 * a specific language, if that language is not found, the default is used. You
 * can also specify what that default language and character string is;
 * otherwise they are set to <tt>DEFAULT_LANG</tt> and <tt>DEFAULT_CHARS</tt>
 * respectively.
 * <p>
 * When specifying a character string for a given language, make sure you
 * include all of the letters, digits and special characters required; in other
 * words, make sure they match the requirement rules.
 * 
 * @author Daniel Frett
 */
public final class RandomPasswordGenerator {
    private static final String DEFAULT_LANG = "en";
    private static final String DEFAULT_CHARS = "abcdefghjkmnpqrstuvwxyz23456789";
    private final static SecureRandom RAND = new SecureRandom();

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final HashMap<String, String> characters = new HashMap<String, String>();

    /** Default language, such as EN */
    private String defaultLang;
    /** Flag to indicate that passwords must start with a letter */
    private boolean startWithLetter = true;
    /** Maximum number of digits; -1 means no limit */
    private int maxDigits = -1;

    public RandomPasswordGenerator() {
	this.setValidCharacters(DEFAULT_LANG, DEFAULT_CHARS);
	this.setDefaultLanguage(DEFAULT_LANG);
    }

    /**
     * @param language
     *            the default language to set
     */
    public void setDefaultLanguage(final String language) {
	this.defaultLang = language.toLowerCase();
    }

    /**
     * @param maxDigits
     *            the maxDigits to set
     */
    public void setMaxDigits(int maxDigits) {
	this.maxDigits = maxDigits;
    }

    /**
     * @param startWithLetter
     *            the startWithLetter to set
     */
    public void setStartWithLetter(boolean startWithLetter) {
	this.startWithLetter = startWithLetter;
    }

    public void setValidCharacters(final String language,
	    final String characters) {
	if (StringUtils.isNotBlank(characters)) {
	    this.characters.put(language.toLowerCase(), characters);
	} else {
	    this.characters.remove(language.toLowerCase());
	}
    }

    /**
     * Evaluates current setup to determine if any flags indicate that a first
     * character requirement exists.
     * 
     * @return <tt>True</tt> if there is a requirement for the first character
     *         in the password.
     */
    private boolean hasFirstCharacterRequirement() {
	return this.startWithLetter;
    }

    private String getValidCharacters(final String language) {
	if (characters.containsKey(language.toLowerCase())) {
	    return characters.get(language.toLowerCase());
	} else if (characters.containsKey(this.defaultLang)) {
	    return characters.get(this.defaultLang);
	} else if (characters.containsKey(DEFAULT_LANG)) {
	    return characters.get(DEFAULT_LANG);
	} else {
	    return DEFAULT_CHARS;
	}
    }

    /**
     * Generate a password using the default language.
     * 
     * @param length
     *            Password length.
     * 
     * @return Generated password.
     */
    public String generatePassword(int length) {
	return this.generatePassword(length, this.defaultLang);
    }

    /**
     * Generate a password with the specified language and length.
     * 
     * @param language
     *            Language to use. If not found, the default is used instead.
     * @param length
     *            Password length.
     * 
     * @return Generated password.
     */
    public String generatePassword(final int length, final String language) {
	final StringBuffer result = new StringBuffer();
	final String chars = this.getValidCharacters(language);

	int range = chars.length();
	int digits = 0;

	for (int i = 0; i < length; i++) {
	    char c = 0;
	    while (c == 0) {
		synchronized (RAND) {
		    c = chars.charAt(RAND.nextInt(range));
		}

		// Handle any special rules for the first character
		if ((i == 0) && (this.hasFirstCharacterRequirement())) {
		    if (this.startWithLetter && !Character.isLetter(c)) {
			c = 0;
			continue;
		    }
		}

		// perform some additional tests on supported character types
		switch (Character.getType(c)) {
		// Letter characters
		case Character.UPPERCASE_LETTER:
		case Character.LOWERCASE_LETTER:
		case Character.TITLECASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		    break;
		// Digit characters
		case Character.DECIMAL_DIGIT_NUMBER:
		    if (this.maxDigits >= 0 && digits >= this.maxDigits) {
			c = 0;
		    }
		    break;
		default:
		    c = 0;
		    break;
		}
	    }

	    // count the types of characters
	    switch (Character.getType(c)) {
	    // Digit characters
	    case Character.DECIMAL_DIGIT_NUMBER:
		digits++;
		break;
	    }

	    // append the character
	    result.append(c);
	}

	return result.toString();
    }
}
