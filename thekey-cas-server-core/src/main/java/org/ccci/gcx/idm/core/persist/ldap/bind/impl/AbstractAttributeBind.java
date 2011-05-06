package org.ccci.gcx.idm.core.persist.ldap.bind.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.core.persist.ldap.bind.AttributeBind;
import org.ccci.gcx.idm.core.util.GeneralizedTime;

/**
 * <b>AbstractAttributeBind</b> contains the common functionality used by all concrete
 * implementations of {@link AttributeBind}.
 *
 * @author Greg Crider  Oct 29, 2008  2:35:40 PM
 */
public abstract class AbstractAttributeBind implements AttributeBind
{

    /**
     * Test the specified {@link ModelObject} to see if it is of the right class.
     * 
     * @param a_ModelObject {@link ModelObject} to be tested.
     * @param a_ExpectedClass Expected {@link Class}.
     * 
     * @exception IllegalArgumentException if the specified {@link ModelObject} is not the right type.
     */
    protected void assertModelObject( ModelObject a_ModelObject, Class<?> a_ExpectedClass )
    {
        if ( !a_ModelObject.getClass().isAssignableFrom( a_ExpectedClass ) ) {
            throw new IllegalArgumentException( "Expecting model class of \"" + a_ExpectedClass.getName() + "\" not \"" + a_ModelObject.getClass().getName() + "\"" ) ;
        }
    }
    
    
    /**
     * Add the list of attribute values for the specified DN to the {@link Attributes}. List
     * values are added in order.
     * 
     * @param a_Attributes {@link Attributes} to which the list should be added.
     * @param a_DN Distinquished name of list.
     * @param a_Values Values of list.
     * @param a_IncludeIfEmpty If <tt>true</tt> and the list of values is <tt>null</tt> or empty, the
     *        list attribute is still added.
     */
    protected void addAttributeList( Attributes a_Attributes, String a_DN, List<String> a_Values, boolean a_IncludeIfEmpty )
    {
        if ( ( a_Values == null ) || ( a_Values.size() == 0 ) ) {
            if ( a_IncludeIfEmpty ) {
                a_Attributes.put( a_DN, "" ) ;
            }
        } else {
            Attribute attr = new BasicAttribute( a_DN, true ) ;
            int count = 0 ;
            Iterator<String> it = a_Values.iterator() ;
            while( it.hasNext() ) {
                attr.add( count++, it.next() ) ;
            }
            a_Attributes.put( attr ) ;
        }
    }
    
    
    /**
     * Convert the specified date/time into the generalized time format of YYYYMMDDHHmmssZ.
     * 
     * @param a_Date {@link Date} to be converted into generalized GMT time.
     * 
     * @return Generalized GMT time format.
     */
    protected String convertToGeneralizedTime( Date a_Date )
    {
        String result = null ;

        if ( a_Date != null ) {
            Calendar loginCal = Calendar.getInstance() ;
            loginCal.setTime( a_Date ) ;
            loginCal.setTimeZone( TimeZone.getTimeZone( "GMT" ) ) ;
            GeneralizedTime gt = new GeneralizedTime( loginCal ) ;
            result = gt.toGeneralizedTime( 
                        GeneralizedTime.Format.YEAR_MONTH_DAY_HOUR_MIN_SEC,
                        GeneralizedTime.FractionDelimiter.DOT,
                        3,
                        GeneralizedTime.TimeZoneFormat.Z
                        ) ;
        }
        
        return result ;
    }
    
}