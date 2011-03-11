package org.ccci.gcx.idm.web.cache;

import java.util.HashMap;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.springframework.beans.factory.FactoryBean;
 
 /**
  * cache factor
  * @author ken
  * copied from: 
  * http://gleichmann.wordpress.com/2008/04/29/pragmatic-caching-a-simple-cache-configuration-model-for-spring/#
  *
  */
 public class JCSCacheFactoryBean implements FactoryBean {  
   
    private HashMap<String, JCS> caches = new HashMap<String, JCS>();
   
     private String configLocation = null;  
     private String region = null;  
   
     public void setConfigLocation(String configLocation) {  
         this.configLocation = configLocation;  
     }  
   
     public void setRegion(String region) {  
         this.region = region;  
     }      

    public Object getObject() throws Exception {
	// Attempt fetching an existing JCS object
	String cacheRegionKey = new StringBuffer(this.configLocation)
		.append(".").append(this.region).toString();
	JCS cache = this.caches.get(cacheRegionKey);

	// Initialize this JCS object if one wasn't found
	if (cache != null) {
	    try {
		cache = JCS.getInstance(this.region);
	    } catch (CacheException e) {
		throw new RuntimeException(
			"exception while creating cache (JCS)", e);
	    }

	    // Store created JCS cache
	    caches.put(cacheRegionKey, cache);
	}
	return cache;
    }

    public Class<JCS> getObjectType() {
	return JCS.class;
    }

     public boolean isSingleton() {  
         return true;  
     }  
 }  
