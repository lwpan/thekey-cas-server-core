package org.ccci.gcx.idm.core.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>RandomPasswordGenerator</b> is used to generate random passwords intended for temporary
 * use. This implementation is rudimentary. You can specify character strings for a specific
 * language code. Only the values found in that string will be used to generate a password. When
 * generating the password for a specific language, if that language is not found, the default
 * is used. You can also specify what that default language and character string is; otherwise
 * they are set to <tt>DEFAULT_LANG</tt> and <tt>DEFAULT_CHARS</tt> respectively.
 * <p>
 * When specifying a character string for a given language, make sure you include all of the 
 * letters, digits and special characters required; in other words, make sure they match
 * the requirement rules.
 *
 * @author Greg Crider  Dec 1, 2008  12:58:00 PM
 */
public class RandomPasswordGenerator
{
    protected static final Log log = LogFactory.getLog( RandomPasswordGenerator.class ) ;
    
    public static final String DEFAULT_LANG = "EN" ;
    
    //original: public static final String DEFAULT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890~!@_$#^-" ;
    //modified as per IDM-16
    public static final String DEFAULT_CHARS = "abcdefghijklmnopqrstuvwxyz01234567890" ;
    
    private static SecureRandom RAND = null ;

    /** Default language, such as EN */
    private String m_DefaultLang = RandomPasswordGenerator.DEFAULT_LANG ;
    /** Default characters */
    private String m_DefaultCharacters = RandomPasswordGenerator.DEFAULT_CHARS ;
    /** Vaild character sets, by language, for generating passwords */
    private Map<String, String> m_ValidCharacters = null ;
    /** Valid characters sets, reformatted, for internal use */
    private Map<String, List<Character>> m_Characters = null ;
    /** Flag to indicate that passwords must start with a letter */
    private boolean m_StartWithLetter = true ;
    /** Maximum number of punctuation characters; -1 means no limit */
    private int m_MaxPunctuation = 0 ; //IDM-16: set from -1 to 0
    /** Maximum number of digits; -1 means no limit */
    private int m_MaxDigits = -1 ;
    
    
    static {
        synchronized( RandomPasswordGenerator.class ) {
            try {
                /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Creating random number generator" ) ;
                RandomPasswordGenerator.RAND = SecureRandom.getInstance( "SHA1PRNG" ) ;
                RandomPasswordGenerator.RAND.setSeed( System.currentTimeMillis() ) ;
            } catch ( NoSuchAlgorithmException nsae ) {
                String error = "Unable to create secure random number generator with SHA1PRNG algorithm." ;
                throw new IllegalArgumentException( error, nsae ) ;
            }
        }
    }
    
    
    /**
     * @return the defaultLang
     */
    public String getDefaultLang()
    {
        return this.m_DefaultLang ;
    }
    /**
     * @param a_defaultLang the defaultLang to set
     */
    public void setDefaultLang( String a_defaultLang )
    {
        this.m_DefaultLang = a_defaultLang ;
    }


    /**
     * @return the defaultCharacters
     */
    public String getDefaultCharacters()
    {
        return this.m_DefaultCharacters ;
    }
    /**
     * @param a_defaultCharacters the defaultCharacters to set
     */
    public void setDefaultCharacters( String a_defaultCharacters )
    {
        this.m_DefaultCharacters = a_defaultCharacters ;
    }
    
    
    /**
     * @return the validCharacters
     */
    public Map<String, String> getValidCharacters()
    {
        return this.m_ValidCharacters ;
    }
    /**
     * @param a_validCharacters the validCharacters to set
     */
    public void setValidCharacters( Map<String, String> a_validCharacters )
    {
        this.m_ValidCharacters = a_validCharacters ;
    }
    
    
    /**
     * @return the startWithLetter
     */
    public boolean isStartWithLetter()
    {
        return this.m_StartWithLetter ;
    }
    /**
     * @param a_startWithLetter the startWithLetter to set
     */
    public void setStartWithLetter( boolean a_startWithLetter )
    {
        this.m_StartWithLetter = a_startWithLetter ;
    }
    
    
    /**
     * @return the maxPuncuation
     */
    public int getMaxPunctuation()
    {
        return this.m_MaxPunctuation ;
    }
    /**
     * @param a_maxPuncuation the maxPuncuation to set
     */
    public void setMaxPunctuation( int a_maxPunctuation )
    {
        this.m_MaxPunctuation = a_maxPunctuation ;
    }
    
    
    /**
     * @return the maxDigits
     */
    public int getMaxDigits()
    {
        return this.m_MaxDigits ;
    }
    /**
     * @param a_maxDigits the maxDigits to set
     */
    public void setMaxDigits( int a_maxDigits )
    {
        this.m_MaxDigits = a_maxDigits ;
    }
    
    
    /**
     * Convert the specified string into list of characters.
     * 
     * @param a_Characters String of characters to be converted.
     * 
     * @return {@link List} of characters.
     */
    private List<Character> generateCharacterList( String a_Characters ) 
    {
        List<Character> results = new LinkedList<Character>() ;
        
        for( int i=0; i<a_Characters.length(); i++ ) {
            results.add( new Character( a_Characters.charAt( i ) ) ) ;
        }
        
        return results ;
    }
    
    
    /**
     * Evaluates current setup to determine if any flags indicat that a first character requriement
     * exists.
     * 
     * @return <tt>True</tt> if there is a requirement for the first character in the password.
     */
    private boolean hasFirstCharacterRequirement()
    {
        return ( this.m_StartWithLetter ) ;
    }

    
    /**
     * Initialize object based on its current settings.
     */
    public void initialize()
    {
        // Create internal map of characters
        this.m_Characters = new HashMap<String, List<Character>>() ;
        
        // Convert characters into their respective lists
        if ( ( this.m_ValidCharacters != null ) && ( this.m_ValidCharacters.size() > 0 ) ) {
            Iterator<String> it = this.m_ValidCharacters.keySet().iterator() ;
            while( it.hasNext() ) {
                String lang = it.next() ;
                /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Adding lang: (" + lang + ") chars(" + this.m_ValidCharacters.get( lang ) + ")" ) ;
                this.m_Characters.put( lang, this.generateCharacterList( this.m_ValidCharacters.get( lang ) ) ) ;
            }
        }
        
        // If there isn't a default character set, then add it
        if ( !this.m_Characters.containsKey( this.getDefaultLang() ) ) {
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Adding default language since it wasn't specified" ) ;
            this.m_Characters.put( this.getDefaultLang(), this.generateCharacterList( this.getDefaultCharacters() ) ) ;
        }
    }
    
    
    /**
     * Generate a password with the specified language and length.
     * <p>
     * <b>Note:</b> This is synchronized becuase I'm not convinced that {@link SecureRandom}
     * is thread-safe. If it turns out to be, then this doesn't have to remain synchronized.
     * </p>
     * 
     * @param a_Lang Language to use. If not found, the default is used instead.
     * @param a_Length Password length.
     * 
     * @return Generated password.
     */
    public synchronized String generatePassword( String a_Lang, int a_Length )
    {
        StringBuffer result = new StringBuffer() ;
        List<Character> chars = this.m_Characters.get( this.getDefaultLang() ) ;
        
        if ( ( StringUtils.isNotBlank( a_Lang ) ) && ( this.m_Characters.containsKey( a_Lang ) ) ) {
            chars = this.m_Characters.get( a_Lang ) ;
        } else {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** The specified language \"" + a_Lang + "\" does not exist; using default." ) ;
        }
        
        // Randomize the characters
        Collections.shuffle( chars ) ;
        
        int range = chars.size() ;
        int punctuations = 0 ;
        int digits = 0 ;
        
        for( int i=0; i<a_Length; i++ ) {
            boolean found = false ;
            Character c = null ;
            while( !found ) {
                c = chars.get( RandomPasswordGenerator.RAND.nextInt( range ) ) ;
                // If this is the first character are we required to set it in a special way?
                if ( ( i == 0 ) && ( this.hasFirstCharacterRequirement() ) ) { 
                    if ( ( this.m_StartWithLetter ) && ( Character.isLetter( c.charValue() ) ) ) {
                        found = true ;
                    }
                // Test for digit limitation
                } else if ( Character.isDigit( c.charValue() ) ) {
                    if ( this.m_MaxDigits < 0 ) {
                        found = true ;
                    } else if ( digits < this.m_MaxDigits ) {
                        found = true ;
                    }
                    digits += ( found ) ? 1 : 0 ;
                // Test for puncuation/special characters (not letter or digit)
                } else if ( ( !Character.isDigit( c.charValue() )) && ( !Character.isLetter( c.charValue() ) ) ) {
                    if ( this.m_MaxPunctuation < 0 ) {
                        found = true ;
                    } else if ( punctuations < this.m_MaxPunctuation ) {
                        found = true ;
                    }
                    punctuations += ( found ) ? 1 : 0 ;
                // Otherwise, add it
                } else {
                    found = true ;
                }
            }
            result.append( c ) ;
        }
        
        return result.toString() ;
    }
    
    
    /**
     * Generate a password using the default language.
     * 
     * @param a_Length Password length.
     * 
     * @return Generated password.
     */
    public String generatePassword( int a_Length )
    {
        return this.generatePassword( null, a_Length ) ;
    }
    
}
