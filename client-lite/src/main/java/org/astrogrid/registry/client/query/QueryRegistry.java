package org.astrogrid.registry.client.query;

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

/** 
 * The QueryRegistry class is a delegate to a web service that submits an XML formatted
 * registry query to the to the server side web service also named the same RegistryService.
 * This delegate helps the user browse the registry and also the OAI. 
 * 
 * @see org.astrogrid.registry.common.RegistryInterface
 * @link http://www.ivoa.net/twiki/bin/view/IVOA/IVOARegWp03
 * @author Kevin Benson
 */
public abstract class QueryRegistry {

    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory.getLog(QueryRegistry.class);
    
    private static String reg_default_version = "1.0";
    
    private static String reg_transform_version = "1.0";
    
    private static boolean return_soap_body = false;
    
    protected final Map cache = new ReferenceMap(ReferenceMap.HARD,ReferenceMap.SOFT);


   /**
    * target end point is the location of the webservice. 
    */
   private URL endPoint = null;

   private boolean useDiskCache = false;

   private static final String QUERY_URL_PROPERTY =
      "org.astrogrid.registry.query.endpoint";

   public static Config conf = null;
   
   private static String cacheDir = null;
   
   protected static boolean useRefCache = true;

   //@todo don't think it's necessary to hang onto this object
   static {
      if (conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
         reg_default_version = conf.getString("org.astrogrid.registry.version",reg_default_version); 
         reg_transform_version = conf.getString("org.astrogrid.registry.result.version",reg_transform_version);
         
         cacheDir = conf.getString("registry.cache.dir",null);
         return_soap_body = conf.getBoolean("return.soapbody",false);
         useRefCache = conf.getBoolean("registry.useCache",true);
      }
   }
   

   /**
    * Main constructor to allocate the endPoint variable.
    * @param endPoint location to the web service.
    * @author Kevin Benson
    */
   public QueryRegistry(URL endPoint) {
      logger.debug("QueryRegistry(URL) - entered const(url) of RegistryService");
      this.endPoint = endPoint;
      if (this.endPoint == null) {
         logger.warn("endpoint is null, using disk cache if there is a registry.cache.dir set or properties file based on identifier");
         useDiskCache = true;
      }
      logger.debug("QueryRegistry(URL) - exiting const(url) of RegistryService");
   }
   
   public abstract String getSoapBodyNamespaceURI();
   public abstract String getContractVersion(); 
   public abstract String getDeclaredNamespacesForXQuery();

   /**
    * Method to establish a Service and a Call to the server side web service.
    * @return Call object which has the necessary properties set for an Axis message style.
    * @throws Exception
    * @todo there's code similar to this in eac of the delegate classes. could it be moved into a common baseclass / helper class.
    * @author Kevin Benson
    */
   private Call getCall() throws ServiceException {
       
      logger.debug("getCall() - entered getCall()");
      Call _call = null;
      Service service = new Service();
      _call = (Call)service.createCall();
      _call.setTargetEndpointAddress(this.endPoint);
      _call.setSOAPActionURI("");
      //_call.setOperationStyle(org.apache.axis.enum.Style.MESSAGE);
      //_call.setOperationUse(org.apache.axis.enum.Use.LITERAL);
      _call.setEncodingStyle(null);
      return _call;
   }

   /**
    * To perform a query with ADQL, using adqls.
    * @param adql string form of adqls
    * @
    * @return XML DOM of Resources queried from the registry.
    * @todo throw registry exception until this method is implemented.
    * @throws RegistryException problem during the query servor or client side.
    */
   public Document searchFromSADQL(String adql) throws RegistryException {
      //send to sadql->adql parser.
       try {
           Document doc = (Document)cache.get(adql);
           if(doc != null) return doc;
           logger.debug("not in cache");
           String adqlString = Sql2Adql.translateToAdql074(adql);
           doc = search(DomHelper.newDocument(adqlString));
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
   public Document search(String xadql) throws RegistryException {
      //search using adqlx. Catch any exceptions and throw them as RegistryExceptions
      try {
         return search(DomHelper.newDocument(xadql));
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
   
   /**
    * To perform a query on the Registry using a DOM conforming of ADQL.
    * Uses a Axis-Message type style so wrap the DOM in the method name conforming
    * to the WSDL.
    * @param adql string form of adqls
    * @return XML DOM of Resources queried from the registry.
    * @throws RegistryException problem during the query servor or client side.
    */   
   public Document search(Document adql) throws RegistryException {
      //wrap a Search element around the dom.
      //Element currentRoot = adql.getDocumentElement();
      Document resultDoc = null;
      searchDOM(adql);
      try {
          return callService(adql,"Search","Search");
      } catch (RemoteException re) {
    	  URL backupEndpoint = conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.ALTQUERY_URL_PROPERTY,null);
          if(backupEndpoint != null && !backupEndpoint.equals(this.endPoint)) {
              RegistryService rs = QueryFallBackDelegate.createFallBackQueryv1_0(backupEndpoint);
              return rs.search(adql);
          }//if 
         logger.error(re);         
         throw new RegistryException(re);
      } catch(ServiceException se) {
          URL backupEndpoint = conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.ALTQUERY_URL_PROPERTY,null);
          if(backupEndpoint != null && !backupEndpoint.equals(this.endPoint)) {
              RegistryService rs = QueryFallBackDelegate.createFallBackQueryv1_0(backupEndpoint);
              return rs.search(adql);
          }//if 
          logger.error(se);
          throw new RegistryException(se);
      } catch(Exception e) {
          logger.error(e); 
          e.printStackTrace();
          throw new RegistryException(e);
      }
   }
   
   protected Document searchDOM(Document searchDoc) {
       Element newRoot = searchDoc.createElementNS(getSoapBodyNamespaceURI(), "reg:Search");
       Element whereElem = null;
       NodeList nl = searchDoc.getElementsByTagNameNS("*","Where");       
       if(getContractVersion().equals("0.1")) {
       	whereElem = searchDoc.createElementNS(nl.item(0).getNamespaceURI(), "reg:Where");
       }else {
       	whereElem = searchDoc.createElementNS(getSoapBodyNamespaceURI(), "reg:Where");
       }       
       Element currentRoot = null;
       if(nl.getLength() > 0) {
           currentRoot = (Element)nl.item(0);
           NodeList childNodes = currentRoot.getChildNodes();
           //System.out.println("child nodes of where length = " + childNodes.getLength());
           for(int i = 0;i < childNodes.getLength();i++) {
        	   whereElem.appendChild(childNodes.item(i).cloneNode(true));
           }
       }//if
       //System.out.println("whereelem toString = " + DomHelper.ElementToString(whereElem));
       newRoot.appendChild(whereElem);
       //System.out.println("New Root before checking on removal = " + DomHelper.ElementToString(newRoot));
       //System.out.println("SearchDoc documnt to string = " + DomHelper.DocumentToString(searchDoc));
       /*if(searchDoc.getDocumentElement() != null && 
          searchDoc.getDocumentElement().getNodeName().indexOf("Where") == -1) {
        */
       if(searchDoc.getDocumentElement() != null) {
    	   System.out.println("removing the documentElement now.");
           searchDoc.removeChild(searchDoc.getDocumentElement());
           
       } 
       System.out.println("now append the newroot");
       searchDoc.appendChild(newRoot);
       System.out.println("SearchDoc2 documnt to string = " + DomHelper.DocumentToString(searchDoc));
       
       logger.debug("THE ADQL IN SEARCH = " + DomHelper.DocumentToString(searchDoc));
       return searchDoc;
   }
   
   protected Document callService(Document soapBody, String name, String soapActionURI) throws RemoteException, ServiceException, Exception {
       Vector result = null;
       Document resultDoc = DomHelper.newDocument();
       Document wsDoc = null;
       NodeList vResources = null;
           //get a call object
           Call call = getCall();
           call.setSOAPActionURI(soapActionURI);
           //create the soap body to be placed in the
           //outgoing soap message.
           SOAPBodyElement sbeRequest =
              new SOAPBodyElement(soapBody.getDocumentElement());
           logger.debug("SOAP Body element = " + DomHelper.ElementToString(soapBody.getDocumentElement()));
           //go ahead and set the name and namespace on the soap body
           //not sure if this is that important.
           sbeRequest.setName(name);
           sbeRequest.setNamespaceURI(getSoapBodyNamespaceURI());
           //call the web service, on axis-message style it
           //comes back as a vector of soabodyelements.
           result = (Vector)call.invoke(new Object[] { sbeRequest });
           SOAPBodyElement sbe = null;
           if (result.size() > 0) {
              sbe = (SOAPBodyElement)result.get(0);
              //resultDoc = sbe.getAsDocument();
              wsDoc = sbe.getAsDocument();
              logger.debug("THE RESULTDOC FROM SERVICE = " + DomHelper.DocumentToString(wsDoc));
//              if(resultDoc.getElementsByTagNameNS("*","Resource").getLength() == 0)
//                  return DomHelper.newDocument();
              if(wsDoc.getDocumentElement() == null) {
                  logger.error("NO DOCUMENT ELEMENT SHOULD NOT HAPPEN");
                  throw new Exception("Error Nothing returned in the Message from the Server, should always be something.");
              }
              if(return_soap_body) {
                  resultDoc = wsDoc;
              }else {
                  NodeList soapChildNodes = wsDoc.getDocumentElement().getChildNodes();
                  boolean importedNodes = false;
                  if( soapChildNodes.getLength() == 0) {
                      //logger.error("NO VOResources found, SHOULD NOT HAPPEN THERE SHOULD ALWAYS BE AN ELEMENT");
                      //throw new Exception("Error No VOResources found, should always be one.");
                      /*
                       * Okay to be compatible with old restry servers which may just return
                       * an empty message signature element back like <searchResponse/>
                       * we can't throw ane exception here just yet.  so just return the dom for now.
                       * No need to transform it either.
                       * 
                       */
                      return wsDoc;
                  }else {
                      for(int i = 0;i < soapChildNodes.getLength();i++) {
                          if(soapChildNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                              Node importNode = resultDoc.importNode(soapChildNodes.item(i),true);
                              resultDoc.appendChild(importNode);
                              importedNodes = true;
                              i = soapChildNodes.getLength();
                          }
                      }//for
                      if(!importedNodes) {
                          //logger.error("NO VOResources found, SHOULD NOT HAPPEN THERE SHOULD ALWAYS BE AN ELEMENT (Child Nodes were found though)");
                          //throw new Exception("Error No VOResources found, should always be one. (Child Nodes were found in the soap body)");
                          /*
                           * Don't throw an exception just yet, need to rethink this later.
                           * XQuerySearches won't have VOResources.
                           */
                      }//if
                  }//else
              }//else

              if(!reg_default_version.equals(reg_transform_version)) {
                  //hmmm more of a hack for auto-integration than anything else.
                  //lets grab the registry version from the result and only transform
                  //if it is different from the reg_transform_version.
                  String versionNumber = null;
                  if(resultDoc.getDocumentElement().hasChildNodes())
                      versionNumber = RegistryDOMHelper.getRegistryVersionFromNode(resultDoc.getDocumentElement().getFirstChild());
                  if(versionNumber == null) {
                      logger.error("Could not find vr namespace from return of a query; SHOULD NOT HAPPEN: Using default = " + reg_default_version);
                      versionNumber = reg_default_version;
                  }
                  if(!versionNumber.equals(reg_transform_version)) {
                      logger.debug("performing tranformation = " + reg_transform_version + 
                              " version from query = " + versionNumber);                      
                      XSLHelper xslHelper = new XSLHelper();
                      return xslHelper.transformResourceToResource(resultDoc.getDocumentElement(),
                             versionNumber,reg_transform_version);
                  }//if
              }//if
              makeIdentifierCache(resultDoc); 
              
              return resultDoc;
           }
           logger.error("ERROR RESULT FROM WEB SERVICE WAS LITERALLY NOTHING IN THE MESSAGE SHOULD NOT HAPPEN.");
           throw new Exception("ERROR RESULT FROM WEB SERVICE WAS LITERALLY NOTHING IN THE MESSAGE SHOULD NOT HAPPEN.");
           //return null;
   }
   
   private void makeIdentifierCache(Document results) throws ParserConfigurationException {
       NodeList nl = results.getElementsByTagNameNS("*","Resource");
       String identifier = null;
       NodeList identList = null;
       if(nl.getLength() > 0 && nl.item(0).getParentNode() != null && 
          nl.item(0).getParentNode().getNodeType() == Node.ELEMENT_NODE) {
           //NodeList resourceNodes = nl.item(0).getChildNodes();
           final int resourceNum = nl.getLength();
           for(int i = 0;i < resourceNum;i++) {
               identList = ((Element)nl.item(i)).getElementsByTagNameNS("*","identifier");
               if(identList.getLength() > 0) {
                   identifier = DomHelper.getValue((Element)identList.item(0));
                   if(cache.get(identifier) == null) {
                       Document doc = DomHelper.newDocument();
                       Node importNode = doc.importNode(nl.item(i).getParentNode(),false);
                       doc.appendChild(importNode);
                       Node resourceNode = doc.importNode(nl.item(i),true);
                       doc.getDocumentElement().appendChild(resourceNode);
                       logger.debug("placing in cache dom for identifier = " + identifier);
                       if(useRefCache)
                           cache.put(identifier,doc);
                   }//if
               }//if
           }//for
       }//if
   }
   
   /**
    * Performas a query to return all Resources of a type of Registry.
    * @return XML DOM of Resources queried from the registry.
    * @throws RegistryException problem during the query servor or client side.
    */
   public Document getRegistries() throws RegistryException {
	   throw new RegistryException("Not implemented");
       
   }
   
   /**
    * Loads this registry type resource for the registry. Essentially querying
    * for one resource that defines the Registry.
    * 
    * @return XML DOM of Resources queried from the registry. 
    */
   public Document loadRegistry() throws RegistryException {
       
      logger.debug("loadRegistry() - loadRegistry");
      Document doc = null;
      Document resultDoc = null;
      try {

         DocumentBuilder registryBuilder = null;
         registryBuilder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
         doc = registryBuilder.newDocument();
         Element root = doc.createElementNS(getSoapBodyNamespaceURI(), "reg:loadRegistry");
         //String value = "http://www.ivoa.net/xml/VOResource/v" + reg_default_version;
         //root.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:vr",value);         
         doc.appendChild(root);
      } catch (ParserConfigurationException pce) {
         throw new RegistryException(pce);
      }
      try {
          return callService(doc,"loadRegistry","loadRegistry");
      } catch (RemoteException re) {
         logger.error(re);          
         throw new RegistryException(re);
      } catch (ServiceException se) {
         logger.error(se);          
         throw new RegistryException(se);
      } catch (Exception e) {
         logger.error(e);          
         throw new RegistryException(e);
      }
   }
   
   /**
    * Loads this registry type resource for the registry. Essentially querying
    * for one resource that defines the Registry.
    * 
    * @return XML DOM of Resources queried from the registry. 
    */
   public Document getIdentity() throws RegistryException {
       
      logger.debug("loadRegistry() - loadRegistry");
      Document doc = null;
      Document resultDoc = null;
      try {

         DocumentBuilder registryBuilder = null;
         registryBuilder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
         doc = registryBuilder.newDocument();
         Element root = doc.createElementNS(getSoapBodyNamespaceURI(), "reg:GetIdentity");
         //String value = "http://www.ivoa.net/xml/VOResource/v" + reg_default_version;
         //root.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:vr",value);         
         doc.appendChild(root);
      } catch (ParserConfigurationException pce) {
         throw new RegistryException(pce);
      }
      try {
          return callService(doc,"GetIdentity","GetIdentity");
      } catch (RemoteException re) {
         logger.error(re);          
         throw new RegistryException(re);
      } catch (ServiceException se) {
         logger.error(se);          
         throw new RegistryException(se);
      } catch (Exception e) {
         logger.error(e);          
         throw new RegistryException(e);
      }
   }   

   
   protected void addChildSoap(Document doc, String elemName, String uri, String val) {
       Element elem = doc.createElementNS(uri,"rs:" + elemName);
       if(val != null  && val.trim().length() > 0)
           elem.appendChild(doc.createTextNode(val));
       if(doc.hasChildNodes())
           doc.getDocumentElement().appendChild(elem);
       else 
           doc.appendChild(elem);
   }
   
   /*
   protected void addAttribute(Element elem, String attrNS, String name, String value ) {
       if(attrNS == null) {
           elem.setAttribute(name, value);           
       }else {
           elem.setAttributeNS(attrNS, name, value);
       }
   }   
   */
   
   /**
    * Do a XQuery on the database, this is supported since the astrogrid registry uses a xmldb backend.
    * 
    * @param xquery string, string in a xquery syntax.
    * 
    * @return XML DOM of what was querying in the registry not necessarily a full Resource.. 
    */
   public Document xquerySearch(String xquery) throws RegistryException {    
       Document doc = null;
       Document resultDoc = null;
       logger.debug("entered xquerySearch");
       resultDoc = (Document)cache.get(xquery);
       if(resultDoc != null) return resultDoc;
       logger.debug("not in cache");
       try {
           DocumentBuilder registryBuilder = null;
           registryBuilder =
             DocumentBuilderFactory.newInstance().newDocumentBuilder();
           	 doc = registryBuilder.newDocument();
             Element root = doc.createElementNS(getSoapBodyNamespaceURI(), "reg:XQuerySearch");
             //String value = "http://www.ivoa.net/xml/VOResource/v" + reg_default_version;
             //root.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:vr",value);       
             Element xqueryElem = doc.createElementNS(getSoapBodyNamespaceURI(),"reg:xquery");
             xqueryElem.appendChild(doc.createTextNode(xquery));
             root.appendChild(xqueryElem);
             doc.appendChild(root);
          } catch (ParserConfigurationException pce) {
             logger.error(pce);              
             throw new RegistryException(pce);
          }
          try {
              resultDoc =  callService(doc,"XQuerySearch","XQuerySearch");
              if(useRefCache)
                  cache.put(xquery,resultDoc);
              return resultDoc;
          } catch (RemoteException re) {
        	  URL backupEndpoint = conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.ALTQUERY_URL_PROPERTY,null);
              if(backupEndpoint != null && !backupEndpoint.equals(this.endPoint)) {
                  RegistryService rs = QueryFallBackDelegate.createFallBackQueryv1_0(backupEndpoint);
                  resultDoc = rs.xquerySearch(xquery);
                  if(useRefCache)
                 	 cache.put(xquery,resultDoc);
                  return resultDoc;
              }       
        	  logger.error(re);         
              throw new RegistryException(re);
          } catch (ServiceException se) {
             URL backupEndpoint = conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.ALTQUERY_URL_PROPERTY,null);
             if(backupEndpoint != null && !backupEndpoint.equals(this.endPoint)) {
                 RegistryService rs = QueryFallBackDelegate.createFallBackQueryv1_0(backupEndpoint);
                 resultDoc = rs.xquerySearch(xquery);
                 if(useRefCache)
                	 cache.put(xquery,resultDoc);
                 return resultDoc;
             }        	  
             logger.error(se);              
             throw new RegistryException(se);
          } catch (Exception e) {
        	  System.out.println("a regular exception called " + e.getMessage());
             logger.error(e);              
             throw new RegistryException(e);
          }
   } 
   
   
   protected Document getResourceByIdentifierDOM(String ident) throws ParserConfigurationException {
       DocumentBuilder registryBuilder = null;
       registryBuilder =
          DocumentBuilderFactory.newInstance().newDocumentBuilder();
       Document doc = registryBuilder.newDocument();
       
       addChildSoap(doc,"GetResource",getSoapBodyNamespaceURI(),null);
       //String value = "http://www.ivoa.net/xml/VOResource/v" + reg_default_version;
       //addAttribute(doc.getDocumentElement(),"http://www.w3.org/2000/xmlns/","xmlns:vr",value);
       addChildSoap(doc,"identifier",getSoapBodyNamespaceURI(),ident);
       return doc;
   }

   /**
    * Query for a specific resource in the Registry based on its identifier element(s).
    * 
    * @param identifier IVORN object.
    * @return XML DOM of Resource queried from the registry. 
    * @see org.astrogrid.store.Ivorn
    */
   public Document getResourceByIdentifier(Ivorn ident)
      throws RegistryException {
      if (ident == null) {
         logger.error("Cannot call this method with a null ivorn identifier");          
         throw new RegistryException("Cannot call this method with a null ivorn identifier");
      }
      return getResourceByIdentifier(ident.toRegistryString());
   }
   
   /**
    * Query for a specific resource in the Registry based on its identifier element(s).
    * 
    * @param identifier string.
    * 
    * @return XML DOM of Resource queried from the registry. 
    */
   public Document getResourceByIdentifier(String ident)   
      throws RegistryException {    
       Document doc = null;
       Document resultDoc = null;
       
       if(!Ivorn.isIvorn(ident)) {
           //hmmm okay old code lets put a small hack for and put  a ivo:// in front
           ident = Ivorn.SCHEME + "://" + ident;
       }
       
       logger.debug("entered getResourceByIdentifier");
       if (ident == null) {
           logger.error("Cannot call this method with a null ivorn identifier");          
           throw new RegistryException("Cannot call this method with a null identifier");
       }       
       if (!useDiskCache) {
           logger.debug("GetResourceByIdentifier() not using local filestore cache grab from web service");
           resultDoc = (Document)cache.get(ident);
           if(resultDoc != null) return resultDoc;
           logger.debug("not in cache");
           
           try {
        	 doc = getResourceByIdentifierDOM(ident);
           } catch (ParserConfigurationException pce) {
              logger.error(pce);                   
              throw new RegistryException(pce);
           }
           try {
              resultDoc =  callService(doc,"GetResource","GetResource");
              //cache.put(ident,resultDoc); //kmb -- no need to do this callService caches via identifier now automatically.
           } catch (RemoteException re) {
        	   URL backupEndpoint = conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.ALTQUERY_URL_PROPERTY,null);
               if(backupEndpoint != null && !backupEndpoint.equals(this.endPoint)) {
                   RegistryService rs = QueryFallBackDelegate.createFallBackQueryv1_0(backupEndpoint);
                   resultDoc = rs.getResourceByIdentifier(ident);
               }        	  
             
              logger.error(re);                 
              throw new RegistryException(re);
          } catch (ServiceException se) {
        	  URL backupEndpoint = conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.ALTQUERY_URL_PROPERTY,null);
              if(backupEndpoint != null && !backupEndpoint.equals(this.endPoint)) {
                  RegistryService rs = QueryFallBackDelegate.createFallBackQueryv1_0(backupEndpoint);
                  resultDoc = rs.getResourceByIdentifier(ident);
              }        	  
              logger.error(se);                 
              //throw new RegistryException(se);
          } catch (Exception e) {
              logger.error(e);                 
              throw new RegistryException(e);
          }      
            NodeList resultList = resultDoc.getElementsByTagNameNS("*","Resource");            
            if(resultList.getLength() == 0) {
                logger.error("No Resource Found for ident = " + ident);                   
                throw new RegistryException("No Resource Found for ident = " + ident);   
            }          
            if(resultList.getLength() > 1) {
                logger.error("Found more than one Resource for Ident = " + ident);
                throw new RegistryException("Found more than one Resource for Ident = " + ident);   
            }
         return resultDoc;
       } else {
            logger.debug("getResourceByIdentifier using cache");
            if(Ivorn.isIvorn(ident))
                ident = ident.substring((Ivorn.SCHEME + "://").length());            
            if(cacheDir != null) {
                ident = ident.replaceAll("[^\\w*]","_") + ".xml";
                try {
                    return DomHelper.newDocument(new File(cacheDir,ident));
                } catch (ParserConfigurationException pce) {
                    logger.error(pce);                    
                    throw new RegistryException(pce);
                } catch (SAXException sax) {
                    logger.error(sax);                    
                    throw new RegistryException(sax);
                } catch (IOException ioe) {
                    logger.error(ioe);                    
                    throw new RegistryException(ioe);
                }
            } else {            
                return conf.getDom(ident);
            }
       }
   }
   
   
   protected Document keywordSearchDOM(String keywords, boolean orValues) throws ParserConfigurationException {
       DocumentBuilder registryBuilder = null;
       registryBuilder =
          DocumentBuilderFactory.newInstance().newDocumentBuilder();
       Document doc = registryBuilder.newDocument();
       
       addChildSoap(doc,"KeywordSearch",getSoapBodyNamespaceURI(),null);
       //String value = "http://www.ivoa.net/xml/VOResource/v" + reg_default_version;
       //addAttribute(doc.getDocumentElement(),"http://www.w3.org/2000/xmlns/","xmlns:vr",value);
       addChildSoap(doc,"keywords",getSoapBodyNamespaceURI(),keywords);
       addChildSoap(doc,"orValues",getSoapBodyNamespaceURI(),java.lang.String.valueOf(orValues));
       return doc;
       
   }
   
   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search (with default setting of querying for all words).
    * @param keywords a space seperated string 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */   
   public Document keywordSearch(String keywords) throws RegistryException { 
       return keywordSearch(keywords,false);
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
   public Document keywordSearch(String keywords,boolean orValues) throws RegistryException {    
       Document doc = null;
       Document resultDoc = null;
       logger.debug("entered keywordSearch");
      
           resultDoc = (Document)cache.get(keywords);
           if(resultDoc != null) return resultDoc;
           logger.debug("KeywordSearch() not using cache");
           try {
        	 doc = keywordSearchDOM(keywords, orValues);  
          } catch (ParserConfigurationException pce) {
              logger.error(pce);                   
              throw new RegistryException(pce);
          }
          try {
              resultDoc =  callService(doc,"KeywordSearch","KeywordSearch");
              if(useRefCache)
                  cache.put(keywords,resultDoc);
          } catch (RemoteException re) {
        	  URL backupEndpoint = conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.ALTQUERY_URL_PROPERTY,null);
              if(backupEndpoint != null && !backupEndpoint.equals(this.endPoint)) {
                  RegistryService rs = QueryFallBackDelegate.createFallBackQueryv1_0(backupEndpoint);
                  resultDoc = rs.keywordSearch(keywords,orValues);
                  if(useRefCache)
                	  cache.put(keywords,resultDoc);
                  return resultDoc;
              }        	  
              logger.error(re);                 
              throw new RegistryException(re);
          } catch (ServiceException se) {
              URL backupEndpoint = conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.ALTQUERY_URL_PROPERTY,null);
              if(backupEndpoint != null && !backupEndpoint.equals(this.endPoint)) {
                  RegistryService rs = QueryFallBackDelegate.createFallBackQueryv1_0(backupEndpoint);
                  resultDoc = rs.keywordSearch(keywords,orValues);
                  if(useRefCache)
                	  cache.put(keywords,resultDoc);
                  return resultDoc;
              }        	  
              logger.error(se);                 
          } catch (Exception e) {
              logger.error(e);                 
              throw new RegistryException(e);
          }
       return resultDoc;
   }
   
   
   /**
    * Method: getQueryForInterfacetype
    * Description: Query for a particular relationship type and only query for active resources.
    * @param ident identifier of the related resource.
    * @return sql type query.
    */
   public String getQueryForInterfaceType(String ident) {
       String selectQuery = null;
       
       selectQuery = "Select * from Registry where @status='active' and vr:content/vr:relationship/vr:relationshipType = 'derived-from' and " +
                     "vr:content/vr:relationship/vr:relatedResource/@ivo-id = '" + ident + "'";
       return selectQuery;
   }
   
   /**
    * Method: getResourceDataByIdentifier
    * Description: Get Resource data based on a relationship identifier and populate that data into a common
    * object that contains the most common referenced data.
    * @param ident Ivorn object of an identifier for the related resource identifier.
    * @return a ResourceData object containing the most common referenced data from a Resource.
    * @throws RegistryException
    */
   public ResourceData getResourceDataByIdentifier(Ivorn ident) throws RegistryException {
       return getResourceDataByIdentifier(ident.toRegistryString());
   }
   
   /**
    * Method: getResourceDataByIdentifier
    * Description: Get Resource data based on a relationship identifier and populate that data into a common
    * object that contains the most common referenced data.
    * @param ident String object of an identifier for the related resource identifier.
    * @return a ResourceData object containing the most common referenced data from a Resource.
    * @throws RegistryException
    */   
   public ResourceData getResourceDataByIdentifier(String ident) throws RegistryException {
       return createSingleResourceData(getResourceByIdentifier(ident));
   }
   
   public ResourceData[] getResourceDataByRelationship(Ivorn ident) throws RegistryException  {
       return getResourceDataByRelationship(ident.toRegistryString());
   }
      
   public ResourceData[] getResourceDataByRelationship(String ident) throws RegistryException  {
      Document doc = null;
      logger
           .debug("getResourcesDataByRelationship - " + ident);
       
      String selectQuery = getQueryForInterfaceType(ident);
      doc = searchFromSADQL(selectQuery);
      logger.debug("exiting getResourcesDataByRelationship");         
      return createResourceData(doc);
   }
   
   private ResourceData createSingleResourceData(Document doc) {
       return createResourceData(doc)[0]; 
   }
   
   private ResourceData[] createResourceData(Document doc) {
       NodeList nl = doc.getElementsByTagNameNS("*","Resource");
       ResourceData[] rd = new ResourceData[nl.getLength()];
       NodeList serviceNodes = null;
       String resKey = null;
       String ident = null;
       for(int i = 0;i < nl.getLength(); i++) {
           rd[i] = new ResourceData();
           serviceNodes = ((Element)nl.item(i)).getElementsByTagNameNS("*","title");
           if(serviceNodes.getLength() > 0)
               rd[i].setTitle(DomHelper.getValue((Element)serviceNodes.item(0)));
           serviceNodes = ((Element)nl.item(i)).getElementsByTagNameNS("*","description");
           if(serviceNodes.getLength() > 0)
               rd[i].setDescription(DomHelper.getValue((Element)serviceNodes.item(0)));
           serviceNodes = ((Element)nl.item(i)).getElementsByTagNameNS("*","accessURL");
           if(serviceNodes.getLength() > 0) {
               try {
                   rd[i].setAccessURL(new URL(DomHelper.getValue((Element)serviceNodes.item(0))));
               }catch(MalformedURLException mfe) {
                   logger.error(mfe);
               }
           }
           serviceNodes = ((Element)nl.item(i)).getElementsByTagNameNS("*","identifier");
           try{
               rd[i].setIvorn(new Ivorn(DomHelper.getValue((Element)serviceNodes.item(0))));
           }catch(java.net.URISyntaxException use) {
               logger.error(use);
           }
           //lets skip getting all the interface types for now.
           /*
           serviceNodes = ((Element)nl.item(i)).getElementsByTagNameNS("*","relatedResource");           
           if(serviceNodes.getLength() > 0 ) {               
               for(int j = 0; j < serviceNodes.getLength();j++) {
                 ident = ((Element)serviceNodes.item(j)).getAttribute("ivo-id");
                 resKey = ident.substring(ident.lastIndexOf(ident.lastIndexOf("/")));
                 //InterfaceType iType = (InterfaceType)Class.forName("org.astrogrid.registry.common." + resKey);
                 //rd[i].addInterfaceType(iType);
               }
           } 
           */          
       }//for
//       printResourceData(rd);
       return rd;
   }
   
   private void printResourceData(ResourceData []rd) {
       for(int i = 0;i < rd.length;i++) {
           System.out.println("the rd[" + i + "] = " + rd.toString());
       }
   }

   /**
    * Query for a specific resource in the Registry based on its identifier element(s), Then extracts out
    * the AccessURL element to find the endpoint. If the endpoint is a web service and has a "?wsdl" ending
    * then attempts to parse the wsdl for the end point of the service.
    * 
    * @param ivorn object.
    * @see org.astrogrid.store.Ivorn 
    * @return String of a url. 
    */
   public String getEndPointByIdentifier(Ivorn ident)
      throws RegistryException {
      return getEndPointByIdentifier(ident.toRegistryString());
   }

   /**
    * Query for a specific resource in the Registry based on its identifier element(s), Then extracts out
    * the AccessURL element to find the endpoint. If the endpoint is a web service and has a "?wsdl" ending
    * then attempts to parse the wsdl for the end point of the service.
    * 
    * @param string identifer of the resource. 
    * @return String of a url. 
    */
   public String getEndPointByIdentifier(String ident)
      throws RegistryException {
       logger
             .debug("enter getEndPointByIdentifier with ident = " + ident);
      //check for an AccessURL
      //if AccessURL is their and it is a web service then get the wsdl
      //into a DOM object and run an XSL on it to get the endpoint.
      String returnVal, invocation = null;
      Document doc = getResourceByIdentifier(ident);
      try {
         returnVal = DomHelper.getNodeTextValue(doc, "accessURL", "*");
      } catch (IOException ioe) {
          logger.error(ioe);          
          throw new RegistryException("Could not parse xml to get AcessURL or Invocation");
      }
      if (returnVal == null) {
          logger.error("Found Resource Document, but had no AccessURL");          
          throw new RegistryException("Found Resource Document, but had no AccessURL");
      }
      logger.debug("getEndPointByIdentifier(String) - The AccessURL = " + returnVal);
      if (returnVal.endsWith("wsdl")) {
         logger.debug("getEndPointByIdentifier(String) - has ?wsdl stripping off");
         returnVal = returnVal.substring(0,returnVal.indexOf("?wsdl"));
      }
      return returnVal;
   }
}
