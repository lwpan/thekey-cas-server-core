package org.ccci.gcx.idm.web.cache;

 import java.util.HashMap;  
 import java.util.Map;  
   
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
   
     private Map<String,JCS> caches = new HashMap<String,JCS>();  
   
     private String configLocation = null;  
     private String region = null;  
   
     public void setConfigLocation(String configLocation) {  
         this.configLocation = configLocation;  
     }  
   
     public void setRegion(String region) {  
         this.region = region;  
     }      
   
     public Object getObject() throws Exception {  
         try{  
             String cacheRegionKey =  
                 new StringBuffer( configLocation )  
                     .append( "." ).append( "region" ).toString();  
   
             JCS cache = null;  
   
             if( caches.containsKey( cacheRegionKey ) ){  
                 cache = caches.get( cacheRegionKey );  
             }  
             else{  
                 //JCS.setConfigFilename( configLocation );  
                 cache = JCS.getInstance( region );  
                 caches.put( cacheRegionKey, cache );  
            }  
   
             return cache;  
         }  
         catch ( CacheException e ){  
             throw new RuntimeException( "exception while initializing cache (JCS)", e );  
         }  
     }  
   
     public Class getObjectType() {  
         return JCS.class;  
     }  
   
     public boolean isSingleton() {  
         return true;  
     }  
 }  
