package org.ccci.gcx.idm.common.persist.hibernate ;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * <b>AbstractDao</b> contains common functionality required by all {@link Dao}
 * concrete implementations.
 *
 * @author Greg Crider  Oct 12, 2006  2:36:36 PM
 */
public class AbstractDao extends HibernateDaoSupport
{
    /** The model class must be declared for each dao */
    protected Class<?> m_ModelClass = null ;

    
    /**
     * Default, empty constructor.
     */
    public AbstractDao()
    {
    }


    /**
     * Full constructor with the known domain model class.
     *
     * @param a_ModelClass Domain model class.
     */
    public AbstractDao( Class<?> a_ModelClass )
    {
        this.m_ModelClass = a_ModelClass ;
    }


    /**
     * Get the domain model class.
     *
     * @return Domain model class.
     */
    public Class<?> getModelClass()
    {
        if ( this.m_ModelClass == null ) {
            throw new IllegalStateException( "Model class must be set prior to using the accessor" ) ;
        }

        return this.m_ModelClass ;
    }
    
    
    /**
     * Set the domain model class.
     *
     * @param a_ModelClass Domain model class.
     */
    public void setModelClass( Class<?> a_ModelClass )
    {
        if ( this.m_ModelClass != null ) {
            throw new IllegalStateException( "Model class can not be changed once set" ) ;
        }

        this.m_ModelClass = a_ModelClass ;
    }

}
