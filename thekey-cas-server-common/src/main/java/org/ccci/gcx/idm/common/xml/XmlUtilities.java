package org.ccci.gcx.idm.common.xml;


import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.IdmException;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * <b>XmlUtilities</b> is a collection of utility methods for XML.
 *
 * @author Greg Crider  Mar 13, 2007  11:58:00 AM
 */
public class XmlUtilities
{
    /** Logger */
    private static Log log = LogFactory.getLog( XmlUtilities.class ) ;


    /**
     * Convert the specified XML string into a pretty-print format.
     *
     * @param a_Xml String to pretty-print.
     *
     * @return Pretty-print version.
     */
    public static String prettyPrint( String a_Xml )
    {
        StringWriter result = new StringWriter() ;

        try {
            SAXBuilder builder = new SAXBuilder( "org.apache.xerces.parsers.SAXParser", false ) ;
            builder.setValidation( false ) ;
            builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false ) ;
            Document doc = builder.build( new StringReader( a_Xml ) ) ;
            XMLOutputter outputter = new XMLOutputter( Format.getPrettyFormat() ) ;
            outputter.output( doc, result ) ;
        } catch ( Exception e ) {
            /*= ERROR =*/ log.error( "Unable to pretty-print XML", e ) ;
            throw new IdmException( "Unable to pretty-print XML", e ) ;
        }

        return result.toString() ;
    }
}
