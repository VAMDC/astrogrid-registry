package org.astrogrid.registry.server.query;

import java.util.ArrayList;

import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.astrogrid.registry.server.SOAPFaultException;
import org.astrogrid.util.DomHelper;
import org.astrogrid.config.Config;
import org.astrogrid.registry.server.XSLHelper;
import org.astrogrid.store.Ivorn;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

/**
 * Class: QueryHelper
 * Description: Helper Class that will form the appropriate queries 
 * usually from the properties file.  Perform the Query and return back
 * normally a ResourceSet or a Resource object from the database to be
 * used.
 * 
 * @author kevinbenson
 *
 */
public class QueryHelper {
    
    /**
     * conf - Config variable to access the configuration for the server normally
     * jndi to a config file.
     * @see org.astrogrid.config.Config
     */   
    public static Config conf = null;
    
    private static int queryLimit;
    
    private String queryWSDLNS = null;
    
    private String contractVersion = null;
    
    private String voResourceVersion = null;
    
    private String collectionName = null;
    
    private XMLDBRegistry xdbRegistry = null;    
    
    /**
     * Logging variable for writing information to the logs
     */
     private static final Log log = LogFactory.getLog(DefaultQueryService.class);    
    
    /**
     * Static to be used on the initiatian of this class for the config
     */   
    static {
       if(conf == null) {
          conf = org.astrogrid.config.SimpleConfig.getSingleton();
          queryLimit = conf.getInt("reg.amend.returncount",100);
       }
    }
    
    /**
     * Constructor:
     * Description: Constructor for the Helper Class takes in a ISearch interface
     * which has the necessary information such as contract versions, namespaces to 
     * perform the Query to the database.
     * 
     * @param search ISearch interface.
     */
    public QueryHelper(ISearch search) {
        this(search.getWSDLNameSpace(),search.getContractVersion(),search.getResourceVersion());
    }
    
    /**
     * Constructor:
     * Description: Constructor for the Helper Class takes in the necessary parameters
     * such as contract versions, namespaces to perform the Query to the database.
     *
     * @param queryWSDLNS wsdl namespace used for constructing soap fault exceptions if they happen.
     * @param contractVersion the contract version
     * @param voResourceVersion version number for the VOResource, commonly used for extracting an xquery from
     * the properties file.
     */
    public QueryHelper(String queryWSDLNS, String contractVersion, String voResourceVersion) {
        this.queryWSDLNS = queryWSDLNS;
        this.contractVersion = contractVersion;
        this.voResourceVersion = voResourceVersion;
        collectionName = "astrogridv" + voResourceVersion.replace('.','_');
        xdbRegistry = new XMLDBRegistry();
    }
    
    /**
     * Constructor
     * Description: old constructor that only gives the version number of the VOResource
     * to perform xqueries.
     * @deprecated
     * @param voResourceVersion version number for the VOResource, commonly used for extracting an xquery from
     * the properties file.
     */
    public QueryHelper(String voResourceVersion) {
        this(null, null, voResourceVersion);        
    }
    
    
    
    /**
     * Method: loadMainRegistry
     * Description: Queries for the Registry Resource element that is tied to this Registry.
     * All Astrogrid Registries have one Registry Resource tied to the Registry.
     * Which defines the AuthorityID's it manages and how to access the Registry.
     * 
     * @param version of the schema to be queryied on (the vr namespace); hence the collection
     * @return XML docuemnt object representing the result of the query.
     */
    public ResourceSet loadMainRegistry()  throws SOAPFaultException {
        String xqlString = QueryConfigExtractor.queryForMainRegistry(voResourceVersion);
        //log.info("XQL String = " + xqlString);
        //Document resultDoc = queryRegistry(xqlString);        
        log.debug("end loadRegistry");
        return queryRegistry(xqlString);
        
        //return resultDoc;
    }
    
    /**
     * Method: keywordQuery
     * Description: A Keyword search method. Splits the keywords and forms a xql query for the key word search.
     * The paths used for comparison with the keywords are obtained from the properties file they are a comma
     * seperated xpath form.
     * 
     * @param keywords - String of keywords seperated by spaces.
     * @param orKeywords - Are the key words to be or'ed together
     * @return Set of XML Resources which is normally iterated through and streamed out to the client.
     */     
    public ResourceSet keywordQuery(String keywords, boolean orKeywords)  throws SOAPFaultException {
        return keywordQuery(keywords, orKeywords, null, null);
    }
        
    /**
     * Method: keywordQuery
     * Description: A Keyword search method. Splits the keywords and forms a xql query for the key word search.
     * The paths used for comparison with the keywords are obtained from the JNDI/properties file they are a comma
     * seperated xpath form.
     * 
     * @param keywords - String of keywords seperated by spaces.
     * @param orKeywords - Are the key words to be or'ed together
     * @param start - Resource Number to start from.
     * @param max - how many Resources to return. 
     * @return Set of XML Resources which is normally iterated through and streamed out to the client.
     */   
    public ResourceSet keywordQuery(String keywords, boolean orKeywords, String start, String max)  throws SOAPFaultException {
        long beginQ = System.currentTimeMillis();
        //split the keywords from there spaces
        String []keyword = keywords.split(" ");
        //get all the xpaths for the query.
        String xqlPaths = conf.getString("reg.custom.keywordxpaths." + voResourceVersion);
        //the xpaths are comma seperated split that as well.
        String []xqlPath = xqlPaths.split(",");
        
        //get the first part of the query which is basically to query on the
        //Resource element.
        String xqlString = "//" + QueryConfigExtractor.getRootNodeName(voResourceVersion) + "[(@status = 'active') and (";
        //go through all the xpaths and buildup a keyword string.
        for(int j = 0;j < keyword.length;j++) {
            xqlString += " (";        	
	        for(int i = 0;i < xqlPath.length;i++) {
	        	xqlString += " near(" + xqlPath[i].trim() + ",'" + keyword[j] + "')";
	            if(i != (xqlPath.length - 1)) {
                    xqlString += " or ";	            	
	            }//if
	         }//for
	         xqlString += ") ";
	         if(j != (keyword.length-1)) {
	        	 if(orKeywords) {
	        		 xqlString += " or ";	                	  
	             }else {
	            	 xqlString += " and ";
	             }
	         }//if
        }//for
        xqlString += ")] ";
        
        //Document resultDoc = queryRegistry(xqlString, start, max); 
        log.info("Time taken to complete keywordsearch on server = " +
                (System.currentTimeMillis() - beginQ));
        log.debug("end keywordsearch");         
        return queryRegistry(xqlString,start,max);
    }
    
    /**
     * Method: getAll
     * Description: Convenient method for the 'browse all jsp page' which queries the entire
     * collection in the registry based on a version number.
     * @return Set of XML Resources which is normally iterated through and streamed out to the client.
     */
    public ResourceSet getAll()  throws SOAPFaultException {    
        String xqlString = QueryConfigExtractor.getAllQuery(voResourceVersion);
        return queryRegistry(xqlString);
        //Document resultDoc = queryRegistry(xqlString);
        //return resultDoc;
    }

    /**
     * Method: GetResourcesByIdentifier
     * Description: Used by JSP pages and the GetResourcesByIdentifier soap call. Currently
     * this method will query for part of the Identifier. From the client perspective it is passing an entire identifier
     * each time and only receiving one resource, but it could pass in ivo://{authorityid} and get all Resources that
     * have that AuthorityID.  Reason it is currently used is eXist seems to have a problem in embedded mode where
     * plainly I have seen it lose some elements.
     * 
     * @deprecated
     * 
     * @param ivorn - Identifier String.
     * @return Set of XML Resources which is normally iterated through and streamed out to the client.
     */
    public ResourceSet getResourcesByIdentifier(String ivorn) throws SOAPFaultException {
        if(ivorn == null || ivorn.trim().length() <= 0) {
            throw new SOAPFaultException("Server Error: Cannot have empty or null identifier","Cannot have empty or null identifier",queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
        }
        String queryIvorn = ivorn;
        //this is a hack for now delete later.  Some old client delegates might not pass the ivorn
        //the new delegates do.
        if(!Ivorn.isIvorn(ivorn))
            queryIvorn = "ivo://" + ivorn;
        String xqlString = QueryConfigExtractor.queryForResource(queryIvorn,voResourceVersion);
        return queryRegistry(xqlString);
        //Document resultDoc = queryRegistry(xqlString);
        //return resultDoc;
    }

    /**
     * Method: GetResourceByIdentifier
     * Description: (No query is actually performed). Grabs the Resource from the database based on
     * identifier, this is because the identifier is the primary key (or id) in the db.
     * 
     * @param ivorn - identifier string
     * @return XML docuemnt object representing the result of the query.
     */
    public Document getResourceByIdentifier(String ivorn) throws SOAPFaultException {
        if(ivorn == null || ivorn.trim().length() <= 0) {
            throw new SOAPFaultException("Server Error: Cannot have empty or null identifier","Cannot have empty or null identifier",queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
        }
        //return getResourcesByIdentifier(ivorn);

        
        String queryIvorn = ivorn;
        if(Ivorn.isIvorn(ivorn)) { 
            queryIvorn = ivorn.substring(6);
        }
        
        String id = queryIvorn.replaceAll("[^\\w*]","_");
        try {
            
            XMLResource xmr = xdbRegistry.getResource(id, collectionName);              
            if(xmr == null || xmr.getContentAsDOM() == null) {
                throw new SOAPFaultException("Resource Not Found ivorn = " + ivorn,
                                                                        "Resource Not Found ivorn = " + ivorn,queryWSDLNS, SOAPFaultException.NOTFOUNDSOAP_TYPE);
            }
            //Document resDoc = (Document)xmr.getContentAsDOM();
            /*
            Node resultNode = xmr.getContentAsDOM();
            if(resultNode instanceof Element) {
                
            }else if(resultNode instanceof Document) {
            }
            */
            return DomHelper.newDocument(xmr.getContent().toString());
            //return (Document)xmr.getContentAsDOM();
            //return processQueryResults(resDoc.getDocumentElement(), "GetResourceByIdentifier");
        }catch(XMLDBException xdbe) {
            throw new SOAPFaultException(xdbe.getMessage(),xdbe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
            throw new SOAPFaultException("Server Error: " + pce.getMessage(),pce,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
        }catch(SAXException sax) {
            sax.printStackTrace();
            throw new SOAPFaultException("Server Error: " + sax.getMessage(),sax,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
        }catch(IOException ioe) {
            ioe.printStackTrace();
            throw new SOAPFaultException("Server Error: " + ioe.getMessage(),ioe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
        }
        
    }
    
    /**
     * Method: getResourcesByAnyIdentifier
     * Description: Used by JSP pages. Currently
     * this method will query for part of the Identifier. From the client perspective it is passing an entire identifier
     * each time and only receiving one resource, but it could pass in ivo://{authorityid} and get all Resources that
     * have that AuthorityID.  Reason it is currently used is eXist seems to have a problem in embedded mode where
     * plainly I have seen it lose some elements.
     * 
     * @deprecated
     * 
     * @param ivorn - Identifier String.
     * @return Set of XML Resources which is normally iterated through and streamed out to the client.
     */
    public ResourceSet getResourcesByAnyIdentifier(String ivorn) throws SOAPFaultException  {
        String xqlString = QueryConfigExtractor.queryForAllResource(ivorn,voResourceVersion);
        return queryRegistry(xqlString);
        //Document resultDoc = queryRegistry(xqlString);
        //return resultDoc;
    }
    
    /**
     * Method: GetRegistries
     * Description: Queries and returns all the Resources that are Registry type resources.
     * Used by the harvester to find Registry types for harvesting.
     * 
     * @param versionNumber - String table or collection
     * @return Resource entries of type Registries.
     * @see org.astrogrid.registry.server.harvest.RegistryHarvestService
     */   
    public ResourceSet getRegistriesQuery()  throws SOAPFaultException {
        String xqlString = QueryConfigExtractor.queryForRegistries(voResourceVersion);
        return queryRegistry(xqlString);
        //Document resultDoc = queryRegistry(xqlString);
        //return resultDoc;       
    }
    
    /**
     * Method: GetRegistries
     * Description: Queries and returns all the Resources that have VOSI capability resources.
     * Used currently only by jsp page but harvester in the near future.
     * 
     * @param versionNumber - String table or collection
     * @return Resource entries of type Registries.
     * @see org.astrogrid.registry.server.harvest.RegistryHarvestService
     */   
    public ResourceSet getVOSIQuery()  throws SOAPFaultException {
        String xqlString = QueryConfigExtractor.queryForVOSI(voResourceVersion);
        return queryRegistry(xqlString);
        //Document resultDoc = queryRegistry(xqlString);
        //return resultDoc;       
    }    
    
    
    
    
    /**
     * Method: getAstrogridVersions
     * Description: Does not actually do a query, it opens the main root colleciton /db and finds all the child collections
     * associated with astrogridv?? (??=version number) and puts them as strings in an array list to be returned.
     * 
     * @return an ArrayList of Strings containging the versions number supported by this registry (or in the xml db).
     */
    public static ArrayList getAstrogridVersions() throws XMLDBException {
        XMLDBRegistry xdbRegistry = new XMLDBRegistry();
        ArrayList al = new ArrayList();
        String []childCollections = xdbRegistry.listRootCollections();
        for(int i = 0;i < childCollections.length;i++) {
            if(childCollections[i].startsWith("astrogridv")) {
                al.add(((String)childCollections[i].substring(10).replace('_','.')));    
            }
        }
        return al;
    }
    
    /**
     * Method: queryRegistry
     * Description: Queries the Registry with no start or max parameters and returns the results
     * as a Set to be iterated through.
     * @param xqlString an xquery string
     * @return Resource Set
     * @throws SOAPFaultException
     */    
    public ResourceSet queryRegistry(String xqlString) throws SOAPFaultException   {
        return queryRegistry(xqlString,null,null);
    }
    
    
    /**
     * Method: queryRegistry
     * Description: Queries the xml database, on the collection of the registry. This method
     * will read from properties a xql expression and fill out the expression then perform the query. This
     * is a convenience in case of customization for other xml databases.
     * @param xqlString an XQuery to query the database
     * @param collectionName the location in the database to query (sort of like a table)
     * @return xml DOM object returned from the database, which are Resource elements
     */
    public ResourceSet queryRegistry(String xqlString, String start, String max) throws SOAPFaultException {
        
       log.debug("start queryRegistry");
       int tempIndex = 0;
       
       try {
    	   int maxInt = -1;
    	   int startInt = 1;
    	   if(max != null && max.trim().length() > 0)
    		   maxInt = Integer.parseInt(max);
    	   if(start != null && start.trim().length() > 0)
    		   startInt = Integer.parseInt(start);
    	   if(maxInt > queryLimit || maxInt < 0)
    		   maxInt = queryLimit + 1;
           
           String startCount = String.valueOf(startInt);
           //String returnCount = max == null ? String.valueOf((queryLimit+startInt)) : String.valueOf(maxInt + startInt);
           String returnCount = max == null ? String.valueOf((queryLimit)) : String.valueOf(maxInt+1);
           
           //get the xquery expression.
           String xqlExpression = conf.getString("reg.custom.query.expression"); 
           xqlExpression = xqlExpression.replaceAll("__declareNS__", QueryConfigExtractor.getXQLDeclarations(voResourceVersion));
           //log.info(" the xqlExpression = " + xqlExpression);
           //xqlExpression = xqlExpression.replaceAll("regquery", xqlString);
           //log.info("the xqlString = " + xqlString);
           //xqlExpression = xqlExpression.replaceAll("__query__", xqlString);
           tempIndex = xqlExpression.indexOf("__query__");
           if(tempIndex == -1) {
               throw new  SOAPFaultException("Server Error: XQL Expression has no placement for a Query",
                                                                       "XQL Expression has no placement for a Query",queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
           }
           //todo: check into this again, for some reason could not do a replaceAll so currently placing
           //in the string the hard way.
           String endString = xqlExpression.substring(tempIndex+9);
           xqlExpression = xqlExpression.substring(0,tempIndex);
           xqlExpression += xqlString + endString;
           xqlExpression = xqlExpression.replaceAll("__startCount__", startCount);
           xqlExpression = xqlExpression.replaceAll("__returnCount__", returnCount);           
           //System.out.println("here is the xql for the query = " + xqlExpression + " and collectionnae = " + collectionName);           
           ResourceSet xmlrSet = xdbRegistry.query(xqlExpression, collectionName);
           //log.debug("Query Performed = " + xqlExpression + " For collection/table = " + collectionName + " And Resulting Size = " + xmlrSet.getSize());
           //System.out.println("here is the ressetsize of the query = " + xmlrSet.getSize());
           return xmlrSet;
           /*
           if(xmlrSet.getSize() == 0) {
               return DomHelper.newDocument();
           }
           */
           
           //Resource xmlr = xmlrSet.getMembersAsResource();          
           //return DomHelper.newDocument(xmlr.getContent().toString());
       }catch(XMLDBException xdbe) {
           xdbe.printStackTrace();
           throw new SOAPFaultException("Server Error: " + xdbe.getMessage(),xdbe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
       }
       /*
       catch(ParserConfigurationException pce) {
           pce.printStackTrace();
           throw new SOAPFaultException("Server Error: " + pce.getMessage(),pce,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
       }catch(SAXException sax) {
           sax.printStackTrace();
           throw new SOAPFaultException("Server Error: " + sax.getMessage(),sax,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
       }catch(IOException ioe) {
           ioe.printStackTrace();
           throw new SOAPFaultException("Server Error: " + ioe.getMessage(),ioe,queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
       }*/
    }
    
    /**
     * Method: getQuery
     * Description: Transforms ADQL to XQuery, uses the namespace of ADQL to allow the
     * transformations to handle different versions.  Transformations are done
     * by XSL stylesheets. XSL is customizable in case you need to change the XQuery or
     * some other type of Query for the database.
     * 
     * @param query ADQL DOM object 
     * @return xquery string
     */   
    public String getQuery(Document query) throws Exception {                                               
        //String adqlVersion = org.astrogrid.registry.common.RegistryDOMHelper.findADQLVersionFromNode((Node)query.getDocumentElement());
        String adqlVersion = RegistryDOMHelper.findADQLVersionFromNode(query.getDocumentElement());
        
        //throw an error if no version was found.
        if(adqlVersion == null || adqlVersion.trim().length() == 0) {
            throw new Exception("No ADQL version found, hence do not know how to translate the adql to a xquery");           
        }
        
        XSLHelper xslHelper = new XSLHelper();
        return xslHelper.transformADQLToXQL(query, adqlVersion, 
                         QueryConfigExtractor.getRootNodeName(voResourceVersion),contractVersion);
    }
}