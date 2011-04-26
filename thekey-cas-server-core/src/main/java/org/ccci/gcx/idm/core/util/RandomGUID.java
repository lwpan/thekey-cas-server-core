package org.ccci.gcx.idm.core.util ;

import java.net.InetAddress ;
import java.net.UnknownHostException ;
import java.security.MessageDigest ;
import java.security.NoSuchAlgorithmException ;
import java.security.SecureRandom ;
import java.util.Random ;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.IdmException;

/*
 * In the multitude of java GUID generators, I found none that
 * guaranteed randomness. GUIDs are guaranteed to be globally unique
 * by using ethernet MACs, IP addresses, time elements, and sequential
 * numbers. GUIDs are not expected to be random and most often are
 * easy/possible to guess given a sample from a given generator.
 * SQL Server, for example generates GUID that are unique but
 * sequencial within a given instance.
 * 
 * GUIDs can be used as security devices to hide things such as
 * files within a filesystem where listings are unavailable (e.g. files
 * that are served up from a Web server with indexing turned off).
 * This may be desireable in cases where standard authentication is not
 * appropriate. In this scenario, the RandomGUIDs are used as directories.
 * Another example is the use of GUIDs for primary keys in a database
 * where you want to ensure that the keys are secret. Random GUIDs can
 * then be used in a URL to prevent hackers (or users) from accessing
 * records by guessing or simply by incrementing sequential numbers.
 * 
 * There are many other possiblities of using GUIDs in the realm of
 * security and encryption where the element of randomness is important.
 * This class was written for these purposes but can also be used as a
 * general purpose GUID generator as well.
 * 
 * RandomGUID generates truly random GUIDs by using the system's
 * IP address (name/IP), system time in milliseconds (as an integer),
 * and a very large random number joined together in a single String
 * that is passed through an MD5 hash. The IP address and system time
 * make the MD5 seed globally unique and the random number guarantees
 * that the generated GUIDs will have no discernable pattern and
 * cannot be guessed given any number of previously generated GUIDs.
 * It is generally not possible to access the seed information (IP, time,
 * random number) from the resulting GUIDs as the MD5 hash algorithm
 * provides one way encryption.
 * 
 * ----> Security of RandomGUID: <-----
 * 
 * RandomGUID can be called one of two ways -- with the basic java Random
 * number generator or a cryptographically strong random generator
 * (SecureRandom). The choice is offered because the secure random
 * generator takes about 3.5 times longer to generate its random numbers
 * and this performance hit may not be worth the added security
 * especially considering the basic generator is seeded with a
 * cryptographically strong random seed.
 * 
 * Seeding the basic generator in this way effectively decouples
 * the random numbers from the time component making it virtually impossible
 * to predict the random number component even if one had absolute knowledge
 * of the System time. Thanks to Ashutosh Narhari for the suggestion
 * of using the static method to prime the basic random generator.
 * 
 * Using the secure random option, this class complies with the statistical
 * random number generator tests specified in FIPS 140-2, Security
 * Requirements for Cryptographic Modules, secition 4.9.1.
 * 
 * I converted all the pieces of the seed to a String before handing
 * it over to the MD5 hash so that you could print it out to make
 * sure it contains the data you expect to see and to give a nice
 * warm fuzzy. If you need better performance, you may want to stick
 * to byte[] arrays.
 * 
 * I believe that it is important that the algorithm for
 * 
 * generating random GUIDs be open for inspection and modification.
 * This class is free for all uses.
 * 
 * - Marc
 */
/**
 * <b>RandomGUID</b> is a cleaned up version of the orignal code. The following
 * things were improved or fixed:
 * 
 * <ul>
 * <li> Static block was not synchronized, which may have cause the class variables to
 * be initialized more than once depending on certain race conditions.
 * <li> Some exceptions were eaten, and should have resulted in a hard error leading to
 * a full stop of the application.
 * <li> Errors logged into the log file.
 * <li> It is unclear from the source code, documentation and online searches as to
 * whether or not SecureRandom is thread-safe. It appears that java.util.Random.next()
 * is by checking to see if seed values are reused. To hedge against this race condition,
 * I added the current thread name to the MD5 hash to distinquish it from another running
 * in the same container. This problem is only occurring because of the decision to move
 * the Random/SecureRandom initialization into a static initializer, those turning this
 * class into a Singleton.
 * </ul>
 * 
 * @author Greg Crider Oct 21, 2008 5:42:39 PM
 */
public class RandomGUID
{
    protected static final Log log = LogFactory.getLog( RandomGUID.class ) ;
    
    private static Random       MyRand = null ;
    private static SecureRandom MySecureRand = null ;
    private static String       S_Id = null ;

    public String               m_ValueBeforeMD5 = "" ;
    public String               m_ValueAfterMD5  = "" ;

    
    /*
     * Static block to take care of one time secureRandom seed.
     * It takes a few seconds to initialize SecureRandom. You might
     * want to consider removing this static block or replacing
     * it with a "time since first loaded" seed to reduce this time.
     * This block will run only once per JVM instance.
     */

    static {
        synchronized( RandomGUID.class ) {
            MySecureRand = new SecureRandom() ;
            long secureInitializer = MySecureRand.nextLong() ;
            MyRand = new Random( secureInitializer ) ;

            try {   
                S_Id = InetAddress.getLocalHost().toString() ;
            } catch ( UnknownHostException e ) {
                String error = "Unable to acquire localhost." ;
                /*= FATAL =*/ log.fatal( error, e ) ;
                throw new IdmException( error, e ) ;
            }
        }
    }

    
    /*
     * Default constructor. With no specification of security option,
     * this constructor defaults to lower security, high performance.
     */
    public RandomGUID()
    {
        this.getRandomGUID( false ) ;
    }

    
    /*
     * Constructor with security option. Setting secure true
     * enables each random number generated to be cryptographically
     * strong. Secure false defaults to the standard Random function seeded
     * with a single cryptographically strong random number.
     */
    public RandomGUID( boolean a_Secure )
    {
        this.getRandomGUID( a_Secure ) ;
    }

    
    /*
     * Method to generate the random GUID
     */
    private void getRandomGUID( boolean a_Secure )
    {

        MessageDigest md5 = null ;
        StringBuffer sbValueBeforeMD5 = new StringBuffer() ;

        try {
            md5 = MessageDigest.getInstance( "MD5" ) ;
        } catch ( NoSuchAlgorithmException e ) {
            String error = "Unable to get the MD5 message digest" ;
            /*= FATAL =*/ log.fatal( error, e ) ;
            throw new IdmException( error, e ) ;
        }

        try {
            long time = System.currentTimeMillis() ;
            long rand = 0 ;

            if ( a_Secure ) {
                // TODO: Verify that this is in fact thread-safe; the java.util.Random.next() method
                //       does appear to check if a seed has been used; however, it is not apparent
                //       from the source of SecureRandom. This may lead to GUID collision in an
                //       app server container.
                rand = MySecureRand.nextLong() ;
            } else {
                rand = MyRand.nextLong() ;
            }

            // This StringBuffer can be a long as you need; the MD5
            // hash will always return 128 bits. You can change
            // the seed to include anything you want here.
            // You could even stream a file through the MD5 making
            // the odds of guessing it at least as great as that
            // of guessing the contents of the file!

            sbValueBeforeMD5.append( S_Id ) ;
            sbValueBeforeMD5.append( ":" ) ;
            sbValueBeforeMD5.append( Thread.currentThread().getName() ) ;
            sbValueBeforeMD5.append( ":" ) ;
            sbValueBeforeMD5.append( Long.toString( time ) ) ;
            sbValueBeforeMD5.append( ":" ) ;
            sbValueBeforeMD5.append( Long.toString( rand ) ) ;

            m_ValueBeforeMD5 = sbValueBeforeMD5.toString() ;

            md5.update( m_ValueBeforeMD5.getBytes() ) ;
            byte[] array = md5.digest() ;
            StringBuffer sb = new StringBuffer() ;
            for( int j = 0; j < array.length; ++j ) {
                int b = array[ j ] & 0xFF ;
                if ( b < 0x10 )
                    sb.append( '0' ) ;
                sb.append( Integer.toHexString( b ) ) ;
            }

            m_ValueAfterMD5 = sb.toString() ;
        } catch ( Exception e ) {
            String error = "Unable to generate the next GUID value or create MD5 hash." ;
            /*= FATAL =*/ log.fatal( error, e ) ;
            throw new IdmException( error, e ) ;
        }

    }

    
    /*
     * Convert to the standard format for GUID
     * (Useful for SQL Server UniqueIdentifiers, etc.)
     * Example: C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
     */

    public String toString()
    {
        String raw = m_ValueAfterMD5.toUpperCase() ;
        StringBuffer sb = new StringBuffer() ;
        sb.append( raw.substring( 0, 8 ) ) ;
        sb.append( "-" ) ;
        sb.append( raw.substring( 8, 12 ) ) ;
        sb.append( "-" ) ;
        sb.append( raw.substring( 12, 16 ) ) ;
        sb.append( "-" ) ;
        sb.append( raw.substring( 16, 20 ) ) ;
        sb.append( "-" ) ;
        sb.append( raw.substring( 20 ) ) ;

        return sb.toString() ;
    }

}
