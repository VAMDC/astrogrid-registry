package org.astrogrid.registry.client.query;

import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class QueryFallBackDelegate {
	
	   /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory
            .getLog(QueryFallBackDelegate.class);
    
    
    /**
     * Method: createQueryv1_0
     * Description: Creates a 1.0 RegistryService interface object for querying 1.0
     * Resources.  Allowing the ability to query based on some new interface methods plus the
     * default RegistryService methods as well. Normally astrogrid endpoints end in RegistryQueryv1_0
     * @param endPoint url end point to a 1.0 web service.
     * @return RegistryService interface object connected to query the registry.
     */
    public static synchronized org.astrogrid.registry.client.query.v1_0.RegistryService createFallBackQueryv1_0(URL endPoint) {
      logger.info("createQuery(URL) - the ENDPOINT AT DELEGATE = "
                + "'" + endPoint + "'");
      
      if(endPoint.toString().endsWith("RegistryQuery")) {
		  try {
			  endPoint =  new URL(endPoint.toString() + "v1_0");
		  }catch(java.net.MalformedURLException me) {
			  logger.error(me);
			  throw new RuntimeException("Error could not construct url " + me.getMessage());
		  }
	  }
      return new org.astrogrid.registry.client.query.v1_0.QueryRegistry(endPoint);
        //return new org.astrogrid.registry.client.query.v0_1.QueryRegistry(contractEndpoint);
   }
	
	
	  protected static synchronized RegistryService createFallBackQuery(URL endPoint, String contractVersion) {
		   URL contractEndpoint = null;
		      if(contractEndpoint.equals("0.1")) {
		    	  contractEndpoint = endPoint;
		      } else {
				  try {
					 contractEndpoint =  new URL(endPoint.toString() + "v" + contractVersion.replaceAll("[^\\w*]","_"));
				  }catch(java.net.MalformedURLException me) {
					  logger.error(me);
					  throw new RuntimeException("Error could not construct url " + me.getMessage());
				  }
		      }
			  //System.out.println("the endpoint constructoed = " + contractEndpoint);
			  if(contractVersion.equals("1.0")) {
				  return (org.astrogrid.registry.client.query.RegistryService) new org.astrogrid.registry.client.query.v1_0.QueryRegistry(contractEndpoint);
			  }else if(contractVersion.equals("0.1")) {
				  return (org.astrogrid.registry.client.query.v0_1.RegistryService) new org.astrogrid.registry.client.query.v0_1.QueryRegistry(contractEndpoint);
			  }else {
				  logger.warn("Could not find an AdminService for version = " + contractVersion + 
						  " Currently only 0.1 and 1.0 is available.  Defaulting to 0.1");
				  return (org.astrogrid.registry.client.query.v0_1.RegistryService) new org.astrogrid.registry.client.query.v0_1.QueryRegistry(contractEndpoint);
			  }	   
	  }   

}
