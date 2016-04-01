package org.astrogrid.registry.client.query.v1_0;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.XMLUtils;
import org.astrogrid.oldquery.sql.Sql2Adql;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Calendar;
import java.util.List;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.net.MalformedURLException;
import org.astrogrid.registry.RegistryException;
import org.astrogrid.registry.common.XSLHelper;
//import org.astrogrid.registry.common.InterfaceType;
import org.astrogrid.registry.common.RegistryDOMHelper;
import org.apache.commons.collections.map.ReferenceMap;

import org.astrogrid.util.DomHelper;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;

import org.xml.sax.SAXException;
import java.rmi.RemoteException;

import javax.wsdl.factory.WSDLFactory;

import org.astrogrid.config.Config;
import org.astrogrid.store.Ivorn;
import org.astrogrid.registry.common.RegistryDOMHelper;

/** 
 * The QueryRegistry class is a delegate to a web service that submits an XML formatted
 * registry query to the to the server side web service also named the same RegistryService.
 * This delegate helps the user browse the registry and also the OAI. 
 * 
 * @see org.astrogrid.registry.common.RegistryInterface
 * @link http://www.ivoa.net/twiki/bin/view/IVOA/IVOARegWp03
 * @author Kevin Benson
 */
public class QueryRegistry extends org.astrogrid.registry.client.query.QueryRegistry implements RegistryService {

	/*
declare namespace xsi = 'http://www.w3.org/2001/XMLSchema-instance';
declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0';
let $idents := (for $resource in //ri:Resource[contains(@xsi:type,'Registry') and @status='active'] return <resource>{$resource/identifier}{$resource/managedAuthority}</resource>)
return <resources>{$idents}</resources>	 
	 */
	
    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory.getLog(QueryRegistry.class);

    private String NAMESPACE_URI =
      "http://www.ivoa.net/wsdl/RegistrySearch/v1.0";
    
	private static final String CONTRACT_VERSION = "1.0";
	
	 public String getDeclaredNamespacesForXQuery() {
		 return "declare namespace xsi = 'http://www.w3.org/2001/XMLSchema-instance';" +
		 		" declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0';";
	 }	
	 
	   /**
	    * Performas a query to return all Resources of a type of Registry.
	    * @return XML DOM of Resources queried from the registry.
	    * @throws RegistryException problem during the query servor or client side.
	    */
	   public Document getRegistries() throws RegistryException {
		  String xquery = getDeclaredNamespacesForXQuery() + " let $resources := (//ri:Resource[contains(@xsi:type,'Registry') and @status='active']) return <VOResources>{$resources}</VOResources>";
		  System.out.println("xquery to be performed = " + xquery);
		  return xquerySearch(xquery);
	   }
	   
	   /**
	    * Queries for all the authorities managed by this registry. By loading this
	    * registries main registry resource type and looking for the ManagedAuthority
	    * elements, currently does not work with version 0.10.  But is slowly being
	    * factored out of use.
	    * 
	    * @return a hashmap of all the managed authority id's.
	    */
	   public HashMap managedAuthorities() throws RegistryException {
		   String xquery = getDeclaredNamespacesForXQuery() + " let $idents := (for $resource in //ri:Resource[contains(@xsi:type,'Registry') and @status='active'] return <resource>{$resource/identifier}{$resource/managedAuthority}</resource>)" +
	  		" return <resources>{$idents}</resources>";
		   //System.out.println("xquery to be performed = " + xquery);
	      Document doc =  xquerySearch(xquery);
	      logger.debug("managedAuthorities() - entered managedAuthorities");
	      HashMap hm = null;	      
	      String identTemp = null;
	      if (doc != null) {
	    	    //System.out.println("the doctostring = " + DomHelper.DocumentToString(doc));
	            NodeList nl = doc.getElementsByTagNameNS("*","resource");            
	            hm = new HashMap();
	            //System.out.println("there are " + nl.getLength() + " resource elements");
	            for (int i = 0; i < nl.getLength(); i++) {
	               NodeList childNodes = nl.item(i).getChildNodes();
	               //System.out.println("it has " + childNodes.getLength() + " child nodes");
	               for(int j = 0;j < childNodes.getLength();j++) {
	            	   //System.out.println("node name = " + childNodes.item(j).getNodeName());
	            	   if(childNodes.item(j).getLocalName() != null) {
		            	   //System.out.println("local name = " + childNodes.item(j).getLocalName());
		            	   if(childNodes.item(j).getLocalName().equals("identifier")) {
		            		   //System.out.println("yes found a identifier");
		            		   //identTemp = childNodes.item(j).getFirstChild().getNodeValue();
		            		   identTemp = RegistryDOMHelper.getAuthorityID((Element)childNodes.item(j).getParentNode());
		            	   }else if(childNodes.item(j).getLocalName().equals("managedAuthority")) {
		            		   hm.put(childNodes.item(j).getFirstChild().getNodeValue(), identTemp);
		            	   }
	            	   }//if
	               }//for
	            } //for
	      }       
	      logger.debug("managedAuthorities() - exiting managedAuthorities");
	      return hm;
	   }
   
   /**
    * Main constructor to allocate the endPoint variable.
    * @param endPoint location to the web service.
    * @author Kevin Benson
    */
    public QueryRegistry(URL endPoint) {
	   super(endPoint);
    }

	 public String getSoapBodyNamespaceURI() {
		   return this.NAMESPACE_URI;
	 }
	 
	 public String getContractVersion() {
		 return this.CONTRACT_VERSION;
	 }	 
   
   
   /**
    * To perform a query with ADQL, using adqls.
    * @param adql string form of adqls
    * @
    * @return XML DOM of Resources queried from the registry.
    * @todo throw registry exception until this method is implemented.
    * @throws RegistryException problem during the query servor or client side.
    */
   public Document searchFromSADQL(String adql, int from,  boolean identifiersOnly) throws RegistryException {
       return searchFromSADQL(adql,from,-1,identifiersOnly);
   }


   /**
    * To perform a query with ADQL, using adqls.
    * @param adql string form of adqls
    * @
    * @return XML DOM of Resources queried from the registry.
    * @todo throw registry exception until this method is implemented.
    * @throws RegistryException problem during the query servor or client side.
    */
   public Document searchFromSADQL(String adql, int from, int max, boolean identifiersOnly) throws RegistryException {
      //send to sadql->adql parser.
       try {
           Document doc = (Document)cache.get(adql);
           if(doc != null) return doc;
           logger.debug("not in cache");
           String adqlString = Sql2Adql.translateToAdql074(adql);
           doc = search(DomHelper.newDocument(adqlString),from, max, identifiersOnly);
           if(useRefCache)
               cache.put(adqlString,doc);
           return doc;
       }catch(Exception e) {
           logger.error(e);
           throw new RegistryException(e);
       }
       //throw new RegistryException("Cannot get to this point");
   }
   
   /**
    * To perform a query with ADQL, using adql string.
    * @param adql string form of adql (xml)
    * @return XML DOM of Resources queried from the registry.
    * @throws RegistryException problem during the query servor or client side.
    */   
   public Document search(String xadql,int from, boolean identifiersOnly) throws RegistryException {
       return search(xadql,from, -1, identifiersOnly);
   }


   /**
    * To perform a query with ADQL, using adql string.
    * @param adql string form of adql (xml)
    * @return XML DOM of Resources queried from the registry.
    * @throws RegistryException problem during the query servor or client side.
    */   
   public Document search(String xadql,int from, int max, boolean identifiersOnly) throws RegistryException {
      //search using adqlx. Catch any exceptions and throw them as RegistryExceptions
      try {
         return search(DomHelper.newDocument(xadql), from, max,identifiersOnly);
      } catch (ParserConfigurationException pce) {
         logger.error(pce);
         throw new RegistryException(pce);
      } catch (IOException ioe) {
          logger.error(ioe);          
         throw new RegistryException(ioe);
      } catch (SAXException sax) {
         logger.error(sax);          
         throw new RegistryException(sax);
      }
   }
   
   public Document search(Document adql, int from,  boolean identifiersOnly) throws RegistryException {
       return search(adql, from, -1, identifiersOnly);
   }
   
   /**
    * To perform a query on the Registry using a DOM conforming of ADQL.
    * Uses a Axis-Message type style so wrap the DOM in the method name conforming
    * to the WSDL.
    * @param adql string form of adqls
    * @return XML DOM of Resources queried from the registry.
    * @throws RegistryException problem during the query servor or client side.
    */   
   public Document search(Document adql, int from, int max, boolean identifiersOnly) throws RegistryException {
      //wrap a Search element around the dom.
      //Element currentRoot = adql.getDocumentElement();
      Document resultDoc = null;
      adql = searchDOM(adql);
      addChildSoap(adql,"from",NAMESPACE_URI,java.lang.String.valueOf(from));
      if(max != -1)
          addChildSoap(adql,"max",NAMESPACE_URI,java.lang.String.valueOf(max));
      if(identifiersOnly) 
          addChildSoap(adql,"identifiersOnly",NAMESPACE_URI,java.lang.String.valueOf(identifiersOnly));
      //return doQuery(adql,"Search", "Search");
      try {
    	  return callService(adql,"Search", "Search");
      }catch (RemoteException re) {
              logger.error(re);                 
              throw new RegistryException(re);
      } catch (ServiceException se) {
          logger.error(se);                 
          throw new RegistryException(se);
      }catch (Exception e) {
          logger.error(e);                 
          throw new RegistryException(e);
      }  
   }
   
   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search that lets you put in word(s) seperated by spaces and then by default is and or you can set to true
    * to look for all the workds.
    * @param keywords a space seperated string
    * @param ovValue do you want to look for all the words, by default false. 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */      
   public Document keywordSearch(String keywords,boolean orValues, int from, boolean identifiersOnly) throws RegistryException {
       return keywordSearch(keywords,orValues,from, -1, identifiersOnly);
   }
   
   
   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search (with default setting of querying for all words).
    * @param keywords a space seperated string 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */   
   public Document keywordSearch(String keywords, int from, boolean identifiersOnly) throws RegistryException {
       return keywordSearch(keywords, from, -1, identifiersOnly);
   }

   
   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search (with default setting of querying for all words).
    * @param keywords a space seperated string 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */   
   public Document keywordSearch(String keywords, int from, int max, boolean identifiersOnly) throws RegistryException { 
       return keywordSearch(keywords, false, from, max, identifiersOnly);
   }
   
   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search that lets you put in word(s) seperated by spaces and then by default is and or you can set to true
    * to look for all the workds.
    * @param keywords a space seperated string
    * @param ovValue do you want to look for all the words, by default false. 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */  
   public Document keywordSearch(String keywords,boolean orValue, int from, int max, boolean identifiersOnly) throws RegistryException {    
       Document doc = null;
       Document resultDoc = null;
       logger.debug("entered keywordSearch");
      
           resultDoc = (Document)cache.get(keywords);
           if(resultDoc != null) return resultDoc;
           logger.debug("KeywordSearch() not using cache");
           try {
               doc = keywordSearchDOM(keywords, orValue);
               addChildSoap(doc,"from",NAMESPACE_URI,java.lang.String.valueOf(from));
               if(max != -1)
                   addChildSoap(doc,"max",NAMESPACE_URI,java.lang.String.valueOf(max));
               if(identifiersOnly)
                   addChildSoap(doc,"identifiersOnly",NAMESPACE_URI,java.lang.String.valueOf(identifiersOnly));
               //resultDoc = doQuery(doc,"KeywordSearch","KeywordSearch");
               resultDoc = callService(doc,"KeywordSearch","KeywordSearch");
               if(useRefCache)
                   cache.put(keywords,resultDoc);
          }catch (ParserConfigurationException pce) {
              logger.error(pce);                   
              throw new RegistryException(pce);
          }catch (RemoteException re) {
              logger.error(re);                 
              throw new RegistryException(re);
          }catch (ServiceException se) {
              logger.error(se);                 
              throw new RegistryException(se);
          }catch (Exception e) {
              logger.error(e);                 
              throw new RegistryException(e);
          }  
          return resultDoc;
   }
   
   public String[] getEndpoints(String capabilityStandardID) throws RegistryException {
	   /*
	    * declare namespace RootResource = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; 
declare namespace xsi = 'http://www.w3.org/2001/XMLSchema-instance'; 
declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0';
let $idents := (//ri:Resource[@status='active' and 
identifier='ivo://uk.ac.le.star/6df-dsa/wsa']) 
return <resources>{for $resource in $idents/capability/interface where @version="0.3" and ../@standardID='ivo://ivoa.net/std/ConeSearch' and  contains(../@xsi:type,'ConeSearch') return $resource}</resources> 

	    */
	   
	   String xquery = getDeclaredNamespacesForXQuery() + 
	   " let $idents := (//ri:Resource[@status='active') " +
	   " return <resources>{for $resource in $idents/capability/interface where ../@standardID='" + capabilityStandardID + "' return $resource}</resources>";
	   Document doc = xquerySearch(xquery);
	   NodeList nl = doc.getElementsByTagNameNS("*","accessURL");
	   String []endpoints = new String[nl.getLength()];
	   for(int i = 0;i < nl.getLength();i++) {
		   endpoints[i] = nl.item(i).getFirstChild().getNodeValue();
	   }
	   return endpoints;
   }
   
   
  
   public String[] getEndpoints(String capabilityStandardID, String interfaceVersion) throws RegistryException {
	   String xquery = getDeclaredNamespacesForXQuery() + 
	   " let $idents := (//ri:Resource[@status='active') " +
	   " return <resources>{for $resource in $idents/capability/interface where @version='" + interfaceVersion + "' ../@standardID='" + capabilityStandardID + "'  return $resource}</resources>";
	   Document doc = xquerySearch(xquery);
	   NodeList nl = doc.getElementsByTagNameNS("*","accessURL");
	   String []endpoints = new String[nl.getLength()];
	   for(int i = 0;i < nl.getLength();i++) {
		   endpoints[i] = nl.item(i).getFirstChild().getNodeValue();
	   }
	   return endpoints;
   }   
   
   public String[] getEndpointsByIdentifier(String identifier, String capabilityStandardID) throws RegistryException {
	   /*
	    * declare namespace RootResource = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; 
declare namespace xsi = 'http://www.w3.org/2001/XMLSchema-instance'; 
declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0';
let $idents := (//ri:Resource[@status='active' and 
identifier='ivo://uk.ac.le.star/6df-dsa/wsa']) 
return <resources>{for $resource in $idents/capability/interface where @version="0.3" and ../@standardID='ivo://ivoa.net/std/ConeSearch' and  contains(../@xsi:type,'ConeSearch') return $resource}</resources> 

	    */
	   
	   String xquery = getDeclaredNamespacesForXQuery() + 
	   " let $idents := (//ri:Resource[@status='active' and identifier='" + identifier + "']) " +
	   " return <resources>{for $resource in $idents/capability/interface where ../@standardID='" + capabilityStandardID + "'  return $resource}</resources>";
	   Document doc = xquerySearch(xquery);
	   NodeList nl = doc.getElementsByTagNameNS("*","accessURL");
	   String []endpoints = new String[nl.getLength()];
	   for(int i = 0;i < nl.getLength();i++) {
		   endpoints[i] = nl.item(i).getFirstChild().getNodeValue();
	   }
	   return endpoints;
   }
   
   public String[] getEndpointsByIdentifier(String identifier, String capabilityStandardID, String interfaceVersion) throws RegistryException {
	   String xquery = getDeclaredNamespacesForXQuery() + 
	   "let $idents := (//ri:Resource[@status='active' and identifier='" + identifier + "']) " +
	   " return <resources>{for $resource in $idents/capability/interface where  @version='" + interfaceVersion + "' and ../@standardID='" + capabilityStandardID + "'  return $resource}</resources>";

	   Document doc = xquerySearch(xquery);
	   NodeList nl = doc.getElementsByTagNameNS("*","accessURL");
	   String []endpoints = new String[nl.getLength()];
	   for(int i = 0;i < nl.getLength();i++) {
		   endpoints[i] = nl.item(i).getFirstChild().getNodeValue();
	   }
	   return endpoints;
   }

   public String getEndpointByIdentifier(String identifier, String capabilityStandardID) throws RegistryException {
	   String []urls =  getEndpointsByIdentifier(identifier, capabilityStandardID);
	   if(urls.length > 0)
		   return urls[0];
	   else
		   return null;
   }
   
   public String getEndpointByIdentifier(String identifier, String capabilityStandardID, String interfaceVersion) throws RegistryException {
	   String []urls = getEndpointsByIdentifier(identifier, capabilityStandardID, interfaceVersion);
	   if(urls.length > 0)
		   return urls[0];
	   else
		   return null;
   }
   
   
   
}