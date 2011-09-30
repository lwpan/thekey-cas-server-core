package org.ccci.gcx.idm.common.persist.hibernate ;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.ccci.gto.persist.QueryDao;
import org.hibernate.Query;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.util.Assert;


/**
 * <b>AbstractQueryDao</b> is the concrete, base implentor of {@link QueryDao}, and
 * provides query functionality based on Hibernate.
 *
 * @author Greg Crider  Oct 12, 2006  2:36:36 PM
 */
public abstract class AbstractQueryDao<T> extends AbstractDao<T> implements
	QueryDao<T> {
    /**
     * Left join fetch queries that have multiple child rows will return
     * single instances of parent.
     */
    private boolean m_IsDistinctResult = true ;


    /**
     * Is thre result distinct?
     *
     * @return <tt>True</tt> if distinct.
     */
    public boolean isDistinctResult()
    {
        return this.m_IsDistinctResult ;
    }
    /**
     * Set distinct result flag.
     *
     * @param a_IsDistinctResult Flag for is distinct.
     */
    public void setDistinctResult( boolean a_IsDistinctResult )
    {
        this.m_IsDistinctResult = a_IsDistinctResult ;
    }

    /**
     * Get a persistent object based on the domain model class and the specified
     * key. Get object matching the given key and return it.
     * 
     * @param key
     *            Unique lookup key for model class.
     */
    public T get(final Serializable key) {
	final Class<? extends T> clazz = this.getModelClass();
	return clazz.cast(this.getSession().get(clazz, key));
    }

    /**
     * @param key
     * @return
     * @see org.ccci.gcx.idm.common.persist.QueryDao#load(Serializable)
     */
    public T load(final Serializable key) {
	return this.get(key);
    }

    /**
     * Create a simple query based on the query string passed in.
     *
     * @param a_QueryStr The query string for the query to create
     *
     * @return Corresponding query.
     */
    protected Query createQuery( String a_QueryStr )
    {
        return this.getSession().createQuery( a_QueryStr ) ;
    }


    /**
     * Create a query based on the query string and a single parameter.
     *
     * @param a_QueryStr The query string for the query to create.
     * @param a_Param The first parameter to set.
     *
     * @return Corresponding query with set parameter.
     */
    protected Query createQuery( String a_QueryStr, String a_Param )
    {
        Query result = this.getSession().createQuery( a_QueryStr ) ;
        result.setParameter( 0, a_Param ) ;

        return result ;
    }

    /**
     * Create a query based on a given query string and a map of parameters.
     * Iterate over the map adding them into the query.
     *
     * @param a_QueryStr The query string for the query to create.
     * @param a_Params The parameter Map.
     *
     * @return Corresponding query with the parameters set.
     */
	protected Query createQuery( String a_QueryStr, Map<String, ?> a_Params )
	{
		Query result = this.getSession().createQuery( a_QueryStr ) ;

		if ( null != a_Params ) {
			for(Map.Entry<String, ?> entry: a_Params.entrySet()) {
				result.setParameter( entry.getKey(), entry.getValue() ) ;
			}
		}

		return result ;
	}

    /**
     * Finds the first lazy property of the object.
     *
     * @param a_Object Object to be inspected.
     *
     * @return First lazy propery, or <tt>null</tt> if none found.
     */
    protected String findLazyProperty( Object a_Object )
    {
        String result = null ;
        
        ClassMetadata metaData = getSessionFactory().getClassMetadata( a_Object.getClass() ) ;
        if ( metaData != null ) {
            boolean[] laziness = metaData.getPropertyLaziness() ;
            for( int i = 0; i < laziness.length; i++ ) {
                if ( laziness[ i ] == true ) {
                    result = metaData.getPropertyNames()[ i ] ;
                    break ;
                }
            }
        }

        return result ;
    }


    /**
     * Returns names of all declared lazy properties.
     *
     * @param a_Object Object to be inspected.
     *
     * @return List, if any, of the lazy properties.
     */
    protected List<String> getLazyPropertyNames( Object a_Object )
    {
        List<String> result = new ArrayList<String>() ;
        
        ClassMetadata metaData = getSessionFactory().getClassMetadata( a_Object.getClass() ) ;

        boolean[] laziness = metaData.getPropertyLaziness() ;
        for( int i = 0; i < laziness.length; i++ ) {
            if ( laziness[ i ] == true ) {
                result.add( metaData.getPropertyNames()[ i ] ) ;
            }
        }

        return result ;
    }


    /**
     * Perform a find operation based on the named query.
     *
     * @param a_QueryName Name of query.
     *
     * @return List, if any, of the matching, persisted objects.
     */
    protected List<?> findByNamedQuery( String a_QueryName )
    {
        List<?> result = this.getHibernateTemplate().findByNamedQuery( a_QueryName ) ;

        return this.postProcessResult( result ) ;
    }


    /**
     * Perform a find for the named query and the parameter name/value pair.
     *
     * @param a_QueryName Name of query.
     * @param a_ParamName Name of parameter.
     * @param a_Value Value of parameter.
     *
     * @return List, if any, of the matching, persisted objects.
     */
    protected List<?> findByNamedQueryAndNamedParam( String a_QueryName, String a_ParamName, Object a_Value )
    {
        List<?> result = this.getHibernateTemplate().findByNamedQueryAndNamedParam( a_QueryName, a_ParamName, a_Value ) ;

        return this.postProcessResult( result ) ;
    }


    /**
     * Perform a find for the named query and the parameter name/value pairs.
     *
     * @param a_QueryName Name of query.
     * @param a_Params Parameters for query.
     *
     * @return List, if any, of the matching, persisted objects.
     */
	protected List<?> findByNamedQueryAndParamMap( final String a_QueryName, final Map<String, ?> a_Params )
	{
		Assert.notNull( a_Params, "Null parameter map." ) ;
		Assert.isTrue( !a_Params.isEmpty(), "Empty parameter map." ) ;

		Query queryObject = getSession().getNamedQuery( a_QueryName ) ;
		for( Map.Entry<String, ?> entry: a_Params.entrySet()) {
			queryObject.setParameter( entry.getKey(), entry.getValue() ) ;
		}

		return this.executeQuery( queryObject ) ;
	}


    /**
     * Execute the specified and fully qualified (meaning, the parameters are set as required) query.
     *
     * @param query Query to execute.
     *
     * @return List, if any, of the matching, persisted objects.
     */
    protected List<?> executeQuery( Query query )
    {
        List<?> result = query.list() ;

        return this.postProcessResult( result ) ;
    }

    
    /**
     * Called after the rows have been retrieved by Hibernate, before they have
     * been returned by the table loader
     *
     * @param a_Rows Retrieved rows from a query.

     * @return Rows to be returned.
     */
	protected List<?> postProcessResult( List<?> a_Rows )
	{
		if ( this.isDistinctResult() ) {
			// For join queries the result set can have multiple instances of
			// the same record
			// Placing them in a HashSet removes duplicates
			HashSet<Object> objects = new HashSet<Object>() ;
			for( int i = 0; i < a_Rows.size(); ) {
				Object row = a_Rows.get( i ) ;
				if ( objects.contains( row ) ) {
					a_Rows.remove( row ) ;
				} else {
					objects.add( row ) ;
					i++ ;
				}
			}
		}

		return a_Rows ;
	}
}
