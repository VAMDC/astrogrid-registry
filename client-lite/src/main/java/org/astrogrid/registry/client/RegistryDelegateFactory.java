/*
 * $Id: RegistryDelegateFactory.java,v 1.23 2009-06-17 12:56:04 KevinBenson Exp $
 *
 * (C) Copyright Astrogrid...
 */

package org.astrogrid.registry.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.io.IOException;

import org.astrogrid.registry.client.query.RegistryService;
//import org.astrogrid.registry.client.query.v0_1.RegistryService;
//import org.astrogrid.registry.client.query.v1_0.RegistryService;
import org.astrogrid.registry.client.query.OAIService;
import org.astrogrid.registry.client.admin.RegistryAdminService;



import java.net.URL;

import org.astrogrid.config.Config;

/**
 * Creates the appropriate delegates to access the registry.
 * @author Kevin Benson
 */

public class RegistryDelegateFactory {
    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory
            .getLog(RegistryDelegateFactory.class);


   public static Config conf = null;
   
   private static final String DEFAULT_CONTRACT_VERSION = "0.1";
   
   public static final String QUERY_URL_PROPERTY = "org.astrogrid.registry.query.endpoint";
   public static final String OAI_URL_PROPERTY = "org.astrogrid.registry.oai.query.endpoint";
   public static final String ALTQUERY_URL_PROPERTY = "org.astrogrid.registry.query.altendpoint";
   public static final String ADMIN_URL_PROPERTY = "org.astrogrid.registry.admin.endpoint";   
   /**
    * @todo - why is a static reference to the config necessary? wouldn't it be simpler to call config directly each timie
    */
   static {
      if(conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
      }      
   }
    
   /**
    * Method: createQueryv1_0
    * Description: Creates a RegistryService interface for querying
    * the registry based on a property 'org.astrogrid.registry.query.endpoint'
    * Note the RegistryService interface will be the default generic interface
    * and will not contain the added methods in the org.astrogrid.registry.client.query.v1_0.RegistryService
    * 
    * @return RegistryService interface object connected to query the registry.
    */
   public static synchronized org.astrogrid.registry.client.query.v1_0.RegistryService createQueryv1_0() {
	  URL queryCheckURL = conf.getUrl(QUERY_URL_PROPERTY);
	  URL queryURL = queryCheckURL;
	  if(queryCheckURL.toString().endsWith("RegistryQuery")) {
		  try {
			  queryURL =  new URL(queryCheckURL.toString() + "v1_0");
		  }catch(java.net.MalformedURLException me) {
			  logger.error(me);
			  throw new RuntimeException("Error could not construct url " + me.getMessage());
		  }
	  }
	  logger.debug("url endpoing set to " + queryURL.toString());
      return createQueryv1_0(queryURL);
   }
   
   /**
    * Method: createQueryv1_0
    * Description: Creates a 1.0 RegistryService interface object for querying 1.0
    * Resources.  Allowing the ability to query based on some new interface methods plus the
    * default RegistryService methods as well. Normally astrogrid endpoints end in RegistryQueryv1_0
    * @param endPoint url end point to a 1.0 web service.
    * @return RegistryService interface object connected to query the registry.
    */
   public static synchronized org.astrogrid.registry.client.query.v1_0.RegistryService createQueryv1_0(URL endPoint) {
     logger.info("createQuery(URL) - the ENDPOINT AT DELEGATE = "
               + "'" + endPoint + "'");
     return new org.astrogrid.registry.client.query.v1_0.QueryRegistry(endPoint);
       //return new org.astrogrid.registry.client.query.v0_1.QueryRegistry(contractEndpoint);
  }

   
   /**
    * Method: createAdmin
    * Description: Creates a (0.1,0.10) RegistryAdminService interface for updating
    * the registry based on a property 'org.astrogrid.registry.admin.endpoint'
    * The Default contract at this moment is 0.1 which is for 0.10 VOResources, this may
    * change in the near future.
    * 
    * @return RegistryAdminService interface object connected to query the registry.
    */
   public static synchronized RegistryAdminService createAdmin() {      
      return createAdmin(conf.getUrl(ADMIN_URL_PROPERTY),DEFAULT_CONTRACT_VERSION);      
   }

   /**
    * Method: createAdmin
    * Description: Creates a (0.1,0.10) RegistryAdminService interface for updating
    * the registry from the url endpoint passed in.
    * The Default contract at this moment is 0.1 which is for 0.10 VOResources, this may
    * change in the near future.
    * 
    * @param endPoint url object to the registry web service endpoint
    * @return RegistryAdminService interface object connected to query the registry.
    */
   public static synchronized RegistryAdminService createAdmin(URL endPoint) {
	   return createAdmin(endPoint,DEFAULT_CONTRACT_VERSION);
   }
   

   /**
    * Method: createAdmin
    * Description: Creates a RegistryAdminService interface for updating
    * the registry based on the contract version passed in and the 
    * property 'org.astrogrid.registry.admin.endpoint' url that is set.  At the time of this writing
    * only two contracts are supported: 0.1 which updates the 0.10 VOResource Records and
    * 1.0 which updates the 1.0 VOResource records.
    * Note all astrogrid urls should just have the ending 'RegistryAdmin' nothing else the 
    * contract version will change the url slightly to point to the correct location for that 
    * VOResource version.
    * 
    * @param contractVersion The wsdl contract version number.  
    * Currently 0.1 for updating 0.10 VOResource records and 1.0 for updating 1.0 VOResource Records.
    * @param endPoint url object to the registry web service endpoint
    * @return RegistryService interface object connected to query the registry.
    */
   public static synchronized RegistryAdminService createAdmin(String contractVersion) {      
      return createAdmin(conf.getUrl(ADMIN_URL_PROPERTY),contractVersion);      
   }
   
   /**
    * Method: createAdmin
    * Description: Creates a RegistryAdminService interface for updating
    * the registry based on the contract version passed in and the 
    * url that is set.  At the time of this writing
    * only two contracts are supported: 0.1 which updates the 0.10 VOResource Records and
    * 1.0 which updates the 1.0 VOResource records.
    * Note all astrogrid urls should just have the ending 'RegistryAdmin' nothing else; the 
    * contract version will change the url slightly to point to the correct location for that 
    * VOResource version.
    * 
    * @param contractVersion The wsdl contract version number.  
    * Currently 0.1 for updating 0.10 VOResource records and 1.0 for updating 1.0 VOResource Records.
    * @param endPoint url object to the registry web service endpoint
    * @return RegistryService interface object connected to query the registry.
    */
   public static synchronized RegistryAdminService createAdmin(URL endPoint, String contractVersion) {
	  URL contractEndpoint = null;
	  try {
		 contractEndpoint =  new URL(endPoint.toString() + "v" + contractVersion.replaceAll("[^\\w*]","_"));
	  }catch(java.net.MalformedURLException me) {
		  logger.error(me);
		  throw new RuntimeException("Error could not construct url " + me.getMessage());
	  }
	  System.out.println("the endpoint constructoed = " + contractEndpoint);
	  if(contractVersion.equals("1.0")) {
		  return new org.astrogrid.registry.client.admin.v1_0.UpdateRegistry(contractEndpoint);
	  }else if(contractVersion.equals("0.1")) {
		  return new org.astrogrid.registry.client.admin.v0_1.UpdateRegistry(contractEndpoint);
	  }else {
		  logger.warn("Could not find an AdminService for version = " + contractVersion + 
				  " Currently only 0.1 and 1.0 is available.  Defaulting to 0.1");
		  return new org.astrogrid.registry.client.admin.v0_1.UpdateRegistry(contractEndpoint);
	  }
   }
   
   /**
    * Method: createOAI
    * Description: creates an OAIService interface object for access
    * to the OAI methods and to perform the OAI queries on the registry.  Uses
    * the property set 'org.astrogrid.registry.oai.query.endpoint' as the endpoint.
    * @return  OAIService to query the registry OAI.
    */
   public static synchronized OAIService createOAI() {
      return createOAI(conf.getUrl(OAI_URL_PROPERTY));      
   }

   
   /**
    * Method: createOAI
    * Description: creates an OAIService interface object for access
    * to the OAI methods and to perform the OAI queries on the registry.  
    * 
    * @param endpoint URL endpoint to the OAI webservice. Normally end with
    * RegistryHarvest for the 0.10 VOResource records or RegistryHarvest1_0 for the 1.0 VOResource records.
    * @return  OAIService to query the registry OAI.
    */
   public static synchronized OAIService createOAI(URL endPoint) {
      return new org.astrogrid.registry.client.query.OAIRegistry(endPoint);
   }

}