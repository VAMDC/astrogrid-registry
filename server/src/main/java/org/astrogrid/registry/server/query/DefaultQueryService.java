package org.astrogrid.registry.server.query;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.codehaus.xfire.util.STAXUtils;
import java.io.StringReader;

import org.astrogrid.registry.common.NodeDescriber;

import javax.xml.stream.*;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.astrogrid.registry.server.SOAPFaultException;
import org.astrogrid.registry.server.ConfigExtractor;
import org.astrogrid.contracts.http.filters.ContractsFilter;

import org.astrogrid.util.DomHelper;
import org.astrogrid.config.Config;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.apache.commons.collections.map.ReferenceMap;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class: RegistryQueryService
 * Description: The main class for all queries to the Registry to go to via Web Service or via internal
 * calls such as jsp pages or other classes.  The main focus is Web Service Interface methods are here
 * such as Search, KeywordSearch, and GetResourceByIdentifier.
 *
 * @author Kevin Benson
 */
public abstract class DefaultQueryService {

   /**
   * Logging variable for writing information to the logs
   */
   private static final Log log = LogFactory.getLog(DefaultQueryService.class);
   
   //protected static final Map cache = new ReferenceMap(ReferenceMap.HARD,ReferenceMap.SOFT);
   protected static final Map cache = new HashMap();

   /**
    * conf - Config variable to access the configuration for the server normally
    * jndi to a config file.
    * @see org.astrogrid.config.Config
    */   
   public static Config conf = null;
   
   private String queryWSDLNS = null;
   
   private String contractVersion = null;
   
   private String voResourceVersion = null;
   
   private String collectionName = null;
   
   private XMLDBRegistry xdbRegistry = null;
   
   protected QueryHelper queryHelper = null;
   
   private static final String ASTROGRID_SCHEMA_BASE = "http://software.astrogrid.org/schema/";
   
   protected static String schemaLocationBase;
   private static  String serverCache;
   private static int serverCacheResourceCount;

   /**
    * Static to be used on the initiatian of this class for the config
    */   
   static {
      if(conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
         serverCache = conf.getString("server.cache","false");
         serverCacheResourceCount = conf.getInt("server.cache.resource.count",100);
         ///conf.g
         if(schemaLocationBase == null) {              
             schemaLocationBase = ContractsFilter.getContextURL() != null ? ContractsFilter.getContextURL() + "/schema/" :
                                  ASTROGRID_SCHEMA_BASE;
         }//if
         
      }
   }
   
   private SOAPFaultException sfe;
      
   public DefaultQueryService(String queryWSDLNS, String contractVersion, String voResourceVersion) {
       this.queryWSDLNS = queryWSDLNS;
       this.contractVersion = contractVersion;
       this.voResourceVersion = voResourceVersion;
       collectionName = "astrogridv" + voResourceVersion.replace('.','_');       
       xdbRegistry = new XMLDBRegistry();
       getQueryHelper();
   }
         
   public abstract XMLStreamReader processSingleResult(Node resultDBNode,String responseWrapper);
   
   public abstract XMLStreamReader processSingleResult(ResourceSet resultSet,String responseWrapper);
   
   public abstract XMLStreamReader processResults(ResourceSet resultSet,String responseWrapper);
   
   public abstract XMLStreamReader processResults(ResourceSet resultSet,String responseWrapper, String start, String max, String identOnly);
   
   public abstract NodeDescriber getXQuerySearchRootResourceNode();
      
   /**
    * Method: Search
    * Description: Web Service method to take ADQL DOM and perform a query on the
    * registry.  Takes in a DOM so it can handle multiple versions of ADQL.
    * 
    * @param query - DOM object containing ADQL. Which is xsl'ed into XQuery language for the query.
    * @return - Resource DOM object of the Resources from the query of the registry. 
    * 
    */
   public XMLStreamReader Search(Document query) {
      log.debug("start Search");
      long beginQ = System.currentTimeMillis();

      //transform the ADQL to an XQuery for the registry.
      String xqlQuery = null;
      String start, max, identOnly;
      try {
          xqlQuery = queryHelper.getQuery(query);
          start = DomHelper.getNodeTextValue(query,"from");
          max = DomHelper.getNodeTextValue(query,"max");
          if(max == null && contractVersion.equals("0.1")) {
              max = conf.getString("reg.amend.returncount","100");
          }
          identOnly = DomHelper.getNodeTextValue(query,"identifiersOnly");
      }catch(Exception e) {
          e.printStackTrace();
          sfe = new SOAPFaultException("Server Error: " + e.getMessage(),e, queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
          return processSingleResult(sfe.getFaultDocument(),null);
      }
      log.debug("The XQLQuery From ADQLSearch = " + xqlQuery);

      try {
	      //perform the query and log how long it took to query.
	      ResourceSet resultSet = queryHelper.queryRegistry(xqlQuery, start, max);
	      log.info("Time taken to complete search on server = " +
	              (System.currentTimeMillis() - beginQ));
	      log.debug("end Search");
	            
	      //To be correct we need to transform the results, with a correct response element 
	      //for the soap message and for the right root element around the resources.
	      return processResults(resultSet,"SearchResponse", start, max, identOnly);
      }catch(SOAPFaultException soapexc) {
    	  return processSingleResult(soapexc.getFaultDocument(),null);
      }
   }
   

   
   /**
    * Method: Query
    * Description: More of a convenience method to do direct Xqueries on the registry
    * Gets the XQuery out of the XQLString element which is the wrapped method
    * name in the SOAP body. Currently Not in Use.
    *  
    * @param query - XQuery string to be used directly on the registry.
    * @return - Resource DOM object of the Resources from the query of the registry.
    */
   public XMLStreamReader XQuerySearch(Document query) {
         log.debug("start XQuerySearch");         
         try {
        	 NodeDescriber nd = getXQuerySearchRootResourceNode();
             String xql = DomHelper.getNodeTextValue(query,"xquery");
             int tempIndexCheck1 = 0;
             int tempIndexCheck2;
             ResourceSet rs;
             String wrapper = ("<ri:XQuerySearchResponse xmlns:ri=\"" + queryWSDLNS +"\"></ri:XQuerySearchResponse>");
             if(xql == null || xql.trim().length() == 0)
            	 xql = DomHelper.getNodeTextValue(query,"XQuery");
             log.debug("Found XQuery in XQuerySearch = " + xql);
             /*
              * Hmmmm right now Astrogrid knows it is vor:Resource in our db, but others do not and
              * might send vr:Resource we will need to translate/replace those and possibly
              * add a vor namespace. New RI spec now says "//Resource" is a special keyword for the root.
              * @todo use a full path this actually replaces things always to //vor:Resource be nice to be
              * Astrogrid/vor:Resource, but hard to do till we move up to 1.0
             */
            
             if(xql.indexOf("//RootResource") != -1) {
                 xql = xql.replaceAll("//RootResource","//RootResource:" + nd.getLocalName());
             }
             else if(xql.indexOf("/RootResource") != -1) {
                 xql = xql.replaceAll("/RootResource","//RootResource:" + nd.getLocalName());
             }
             else if(xql.indexOf("RootResource") != -1) {
                xql = xql.replaceAll("RootResource","//RootResource:" + nd.getLocalName());
             }
             
             boolean cont = true;
             String []paramCheck;
             String xqlTemp = null;
             /*
              * Hack: Task Launcher and Resource queries will typically send a 'matches' xquery with
              * a lot of 'or' statements these tend to come back in around 6 seconds, but if I switch it to
              * using eXist specefic text method then it comes back in about 2 seconds sometimes 1 second.
              * Problem: Changes things to use eXist near() function but near() does not have full
              * regular expression support like xqueryspec-matches or eXistspecefic-match-all.  A user
              * will be able to do wildcards'*' and '?' and thats about it which is all I ever see anybody
              * do anyways.  We can't quite use match-all because of the way eXist tokenizes strings
              * whereby match-all(node,'x-ray') will find every word 'x' and every word 'ray' :(  The
              * near() function works better it is similiar on the tokenizing of match-all but requires
              * the words to be next to one another unles a user puts in a 3rd parameter integer to let it
              * be further seperated.
              */
             if(xql.split(" or ").length > 2) {
	             while(cont) {
	            	 //use split.length to get how many
		             if((tempIndexCheck1 = xql.indexOf("contains(",tempIndexCheck1+10)) != -1) {            	 
		            	 paramCheck = xql.substring(tempIndexCheck1,(tempIndexCheck2 = xql.indexOf(')',tempIndexCheck1))).split(",");
		            	 //System.out.println("it had a contains and paramcheck = " + paramCheck.length);
		            	 if(paramCheck.length == 2) {
		            		 xql = xql.replaceAll("contains\\("," near(");	 
		            	 }
		             }else {
		            	 cont = false;
		             }
	             }
	             tempIndexCheck1 = 0;
	             cont = true;
	             while(cont) {
		             if((tempIndexCheck1 = xql.indexOf("matches(",tempIndexCheck1+10)) != -1) {
		            	 paramCheck = xql.substring(tempIndexCheck1,(tempIndexCheck2 = xql.indexOf(')',tempIndexCheck1))).split(",");
		            	 if(paramCheck.length == 2) {
		            		 //same thing as a near and its case insensitive which is what registry is supposed to be.
		            		 xql = xql.replaceAll("matches\\("," near(");	 
		            	 }
		            	 if(paramCheck.length == 3) {
		            		 if(paramCheck[2].trim().charAt(1) == 's' || paramCheck[2].trim().charAt(1) == 'i' ||
		            			paramCheck[2].trim().charAt(1) == 'm' || paramCheck[2].trim().charAt(1) == 'x') {
		            				//ok there doing the matches with just a regular expression setting lets
		            				//drop it and use near.
		            				xqlTemp = xql.substring(0,tempIndexCheck1) + paramCheck[0].replaceAll("matches", "near") + "," + paramCheck[1] + ") " + xql.substring(tempIndexCheck2+1);
		            				xql = xqlTemp;
		            		 }//if
		            	 }//if
		             }else {
		            	 cont = false;
		             }//else
	             }//while
	             xql = xql.replaceAll("\\.\\*:", "*:");
             }//if
             
             /*
              * Hack for older clients there is a bug with eXist with a particular query using the older
              * slower style of where clause with variable and wildcards and lots of constraints.
              * Very odd kind of bug hard to really see what is wrong.  You can use the 
              * variable just fine on smaller type queries but bigger queries really seem to have a problem
              * with it in the where clause.  So change all of them if there is a where caluse.
              * Only one that is different is the full-text search workbench sometimes does that needs
              * to stay as $r//*
              * :( bummer i have to do this.
              * When more updated workbenches come alive then we need to remove this hack. 
              */
             if(xql.indexOf("where") != -1 && (xql.indexOf("&=") != -1 || xql.indexOf("&amp;=") != -1)) {
            	 //ok using older eXist style functions lets to a relaceAll on $r/ and
            	 //hoopefully will be good after that. With the exception of $r//* it needs to stay.
            	 if(xql.indexOf("$r//*") == -1) {
            		 xql = xql.replaceAll("\\$r/", "");
            	 }else {
            		 xql = xql.replaceAll("\\$r/", "./");
            	 }
             }
             xql = "declare namespace RootResource = '" + nd.getNameSpace() + "'; " + xql;
             int hashC = xql.hashCode();
             log.info("0.a The hashcode = " + hashC);   

             if(serverCache != null && serverCache.equals("true")) {
            	 if(cache.containsKey(String.valueOf(hashC))) {
            		 log.info("2.a Found in the cache.");
            		 return new ResourceStreamer(cloneResources((List)cache.get(String.valueOf(hashC))), wrapper);
            	 }
             }
             //log.info("Query to be ran = " + xql);
             rs = xdbRegistry.query(xql,collectionName);
             List resSet = cloneResources(rs);
             //log.info("0.b The hashcode = " + hashC);   
             
             if(serverCache != null && serverCache.equals("true")) {
	             if(rs.getSize() >= serverCacheResourceCount) {
	            	 //Hmmm at least 100 Resources on this query.
	            	 //lets go ahead and cache it.
	        		 cache.put(String.valueOf(hashC), resSet );
	             }
             }
             return new ResourceStreamer(resSet, wrapper);
         }catch(XMLDBException xdbe) {
             xdbe.printStackTrace();
             sfe = new SOAPFaultException("Server Error: " + xdbe.getMessage(),xdbe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
             return STAXUtils.createXMLStreamReader(new StringReader(DomHelper.DocumentToString(sfe.getFaultDocument())));
         }catch(IOException ioe) {
             ioe.printStackTrace();
             sfe = new SOAPFaultException("Server Error: " + ioe.getMessage(),ioe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
             return STAXUtils.createXMLStreamReader(new StringReader(DomHelper.DocumentToString(sfe.getFaultDocument())));             
         }         
   }
   
   private void debugCache() {
	   Object []keyArr = cache.keySet().toArray();
	   log.info("In debugCache number of keys = " + keyArr.length);
	   for(int i = 0;i < keyArr.length;i++) {
		   log.info("KeyArr i = " + i + " val = " + (String)keyArr[i]);
	   }
	   
   }
   
   private List cloneResources(List inputResources) {
	   List clonedResult = new ArrayList(inputResources.size());
	   clonedResult.addAll(inputResources);
	   return clonedResult;
   }
   
   private List cloneResources(ResourceSet inputResources) throws XMLDBException {
	   List clonedResult = new ArrayList();
	   for(int i = 0;i < (int)inputResources.getSize();i++) {
		   clonedResult.add(inputResources.getResource(i));
	   }
	   return clonedResult;
   }
      
   /**
    * Method: loadRegistry
    * Description: Grabs the versionNumber from the DOM if possible and call the
    * loadMainRegistry method. If versionNumber is not in the DOM then use the default
    * version number in the properties.  The versionNumber comes from the vr namespace.
    * 
    * @param query actually normally empty/null and is ignored.
    * @return XML docuemnt object representing the result of the query.
    */
   public XMLStreamReader loadRegistry(Document query) {
      log.debug("start loadRegistry");
      try {
    	  return processResults((ResourceSet)queryHelper.loadMainRegistry(),"SearchResponse");
      }catch(SOAPFaultException soapexc) {
    	  return processSingleResult(soapexc.getFaultDocument(),null);
      }    	  
   }
   
   public XMLStreamReader GetIdentity(Document query) {
	   try {
		   ResourceSet rs = queryHelper.loadMainRegistry();
		   return processSingleResult(rs,"ResolveResource");
	   }catch(SOAPFaultException soapexc) {
	      return processSingleResult(soapexc.getFaultDocument(),null);
	   }
	   
   }      

   /**
    * Method KeywordSearch
    * Description: A Keyword search web service method.  Gets the keywords from the soap body (also if the keywords are to be 'or' together)
    * The paths used for comparison with the keywords are obtained from the JNDI/properties file. The keywords are seperated by spaces.
    * Once data is obtained called the other keywordQuery method below to perform the query.
    * 
    * @param query - The soap body of the web service call, containing sub elements of keywords.
    * @return XML docuemnt object representing the result of the query.
    */
   public XMLStreamReader KeywordSearch(Document query) {
       log.debug("start keywordsearch");                   
       String keywords = null;
       String orValue = null;
       String start = null;
       String max = null;
       String identOnly = null;
       try {
           keywords = DomHelper.getNodeTextValue(query,"keywords");
           orValue = DomHelper.getNodeTextValue(query,"orValues");
           if(orValue == null) {
        	   orValue = DomHelper.getNodeTextValue(query,"orValue");
           }
           start = DomHelper.getNodeTextValue(query,"from");
           max = DomHelper.getNodeTextValue(query,"max");
           if(max == null && contractVersion.equals("0.1")) {
               max = conf.getString("reg.amend.returncount","100");
           }           
           identOnly = DomHelper.getNodeTextValue(query,"identifiersOnly");
       }catch(IOException ioe) {
           sfe = new SOAPFaultException("Server Error: " + "IO problem trying to get keywords and orValue",ioe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
       }
       
       
       boolean orKeywords = Boolean.valueOf(orValue).booleanValue();
       try {
    	   ResourceSet resultSet =  queryHelper.keywordQuery(keywords, orKeywords, start, max);
    	   return processResults(resultSet, "SearchResponse", start, max, identOnly);
       }catch(SOAPFaultException soapexc) {
     	  return processSingleResult(soapexc.getFaultDocument(),null);
       }
   }
   
   
   
   /**
    * Method: GetResourcesByIdentifier
    * Description: This is the currently used web service method from client (Iteration 0.9).  But is expected to be
    * deprecated.  And to use GetResourceByIdentifier, because an identifier can only return one Resource only.  Currently
    * this method will query for part of the Identifier. From the client perspective it is passing an entire identifier
    * each time and only receiving one resource, but it could pass in ivo://{authorityid} and get all Resources that
    * have that AuthorityID.  Reason it is currently used is eXist seems to have a problem in embedded mode where
    * plainly I have seen it lose some elements.  After getting the identifier call GetResourcesByIdentifier(String,versionNumber).
    * 
    * @param query - A Soap body request containing an identifier element holding the identifier to be queries on.
    * @return XML docuemnt object representing the result of the query.
    */
   public XMLStreamReader GetResource(Document query) {
       log.debug("start GetResource");                   
       String ident = null;
       try {
//           log.info("The soapbody in regserver1 = " + DomHelper.DocumentToString(query));
           ident = DomHelper.getNodeTextValue(query,"identifier");
           
           log.info("found identifier in web service request = " + ident);
           return processSingleResult((Node)queryHelper.getResourceByIdentifier(ident), "ResolveResource");
       }catch(IOException ioe) {
           sfe = new SOAPFaultException("Server Error: " + "IO problem trying to get identifier",ioe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
           return processSingleResult(sfe.getFaultDocument(),null);
       }catch(SOAPFaultException soapexc) {
    	   return processSingleResult(soapexc.getFaultDocument(),null);
       }
   }   
   
   /**
    * 
    * Method: GetResourcesByIdentifier
    * Description: This is the currently used web service method from client (Iteration 0.9).  But is expected to be
    * deprecated.  And to use GetResourceByIdentifier, because an identifier can only return one Resource only.  Currently
    * this method will query for part of the Identifier. From the client perspective it is passing an entire identifier
    * each time and only receiving one resource, but it could pass in ivo://{authorityid} and get all Resources that
    * have that AuthorityID.  Reason it is currently used is eXist seems to have a problem in embedded mode where
    * plainly I have seen it lose some elements.  After getting the identifier call GetResourcesByIdentifier(String,versionNumber).
    * 
    * @deprecated
    * @param query - A Soap body request containing an identifier element holding the identifier to be queries on.
    * @return XML docuemnt object representing the result of the query.
    */
   public XMLStreamReader GetResourcesByIdentifier(Document query) {
       log.debug("start GetResourcesByIdentifier");                   
       String ident = null;
       try {
  //         log.info("The soapbody in regserver2 = " + DomHelper.DocumentToString(query));
           ident = DomHelper.getNodeTextValue(query,"identifier");
           log.info("found identifier in web service request = " + ident);
           return processResults((ResourceSet)queryHelper.getResourcesByIdentifier(ident), "GetResourcesByIdentifier");           
       }catch(IOException ioe) {
           sfe = new SOAPFaultException("Server Error: " + "IO problem trying to get identifier",ioe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
           return processSingleResult(sfe.getFaultDocument(),null);
       }catch(SOAPFaultException soapexc) {
    	   return processSingleResult(soapexc.getFaultDocument(),null);
       }
   }
   

   /**
    * Method: GetResourceByIdentifier
    * Description: Web Service interface method. Gets the identifier from a Soap body and extracts
    * it out of the xml database based on the primary key or id. (No query is performed). Actually calls the
    * getResoruceByIdentifier(string,string).
    * 
    * @param query - soab body containing a identifier element for the identifier to query on.
    * @return XML docuemnt object representing the result of the query.
    */
   public XMLStreamReader GetResourceByIdentifier(Document query) {
       log.debug("start GetResourcesByIdentifier");                   
       String ident = null;
       try {
           ident = DomHelper.getNodeTextValue(query,"identifier");
           log.info("found identifier in web service request = " + ident);
           return processSingleResult((Node)queryHelper.getResourceByIdentifier(ident), "GetResourceByIdentifier");
       }catch(IOException ioe) {
           sfe = new SOAPFaultException("Server Error: " + "IO problem trying to get identifier",ioe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
           return processSingleResult(sfe.getFaultDocument(),null);
       }catch(SOAPFaultException soapexc) {
    	   return processSingleResult(soapexc.getFaultDocument(),null);
       }
   }


   /**
    * Method: GetRegistries
    * Description: Queries and returns all the Resources that are Registry type resources.
    * Calls teh getRegistriesQuery(String)
    * 
    * @param query normally empty and is ignored, it is required though for the
    * Axis Document style method.  At most it will contain nothing more than the method
    * name.
    * @return Resource entries of type Registries.
    */
   public XMLStreamReader GetRegistries(Document query) {
	   try {
		   return processResults((ResourceSet)queryHelper.getRegistriesQuery(), "SearchResponse");
       }catch(SOAPFaultException soapexc) {
    	   return processSingleResult(soapexc.getFaultDocument(),null);
       }		   
   }
   
   public abstract QueryHelper getQueryHelper();  
}
