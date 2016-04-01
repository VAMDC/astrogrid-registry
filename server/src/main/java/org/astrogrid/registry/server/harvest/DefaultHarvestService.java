package org.astrogrid.registry.server.harvest;

import java.rmi.RemoteException;

import java.io.IOException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.server.query.QueryHelper;
import org.astrogrid.registry.server.query.QueryFactory;
import org.astrogrid.registry.server.query.ISearch;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.astrogrid.util.DomHelper;
import org.astrogrid.config.Config;
import org.astrogrid.registry.RegistryException;
import org.astrogrid.xmldb.client.XMLDBFactory;
import org.astrogrid.registry.server.InvalidStorageNodeException;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Vector;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

/**
 * Class: DefaultHarvestService
 * Description: Main abstract Harvest service that can perform harvests
 * on a Registry and be called by an external servlet for harvesting many/all Registries.
 * RegistryHarvestService class is what is instantiated to use this class.
 * RegistryHarvestService is based around contract versions e.g. - 0.1, 1.0
 */
public abstract class DefaultHarvestService {

   /**
    * logging service.
    */
   private static final Log log =
                          LogFactory.getLog(DefaultHarvestService.class);
   
   /**
    * Harvest Set used for calling an OAI harvest.
    */
   private static final String MAIN_HARVEST_SET = "ivo_managed";
   
   /**
    * URI soap action used for OAI web service calls.
    */
   private static final String SOAPACTION_URI = "http://www.openarchives.org/OAI/2.0/ListRecords";
   
   /**
    * OAI webservice method called.
    */
   private static final String OAIINTERFACE_METHOD = "ListRecords";
   //private static final String NAMESPACE_URI = "http://www.ivoa.net/wsdl/RegistryHarvest/v0.1";
   
   
   /**
    * Date format used for OAI HTTP-GET and WebService calls when using the 'from'.
    */
   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

   /**
    * config/property variable.
    */
   public static Config conf = null;
   
   // initialize to use a maximum of 8 threads.
   //static PooledExecutor pool = new PooledExecutor(5);
   

   static {
      if(conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
      }
   }
  
   //Not used anymore.
   //protected XMLDBRegistry xdbRegistry;
   
   /**
    * Harvest Admin service for storage of XML Resources from
    * harvesting along with status information stored.
    */
   protected RegistryHarvestAdmin rha = null;
   
   /**
    * Contract version of the service.  Not the VOResource
    * version.
    */
   private String contractVersion = null;
   
   /**
    * VOResource version number noted from the xmlns:vr namespace
    */
   private String versionNumber = null;
   
   /**
    * DefaultHarvestService constructor used by subclasses to call.
    */
   public DefaultHarvestService(String contractVersion, String versionNumber) {
       //xdbRegistry = new XMLDBRegistry();
   	   //instantiate admin service.
       rha = new RegistryHarvestAdmin();
       //set the contract and version number       
       this.contractVersion = contractVersion;
       this.versionNumber = versionNumber;
       log.debug("DefaultHarvestService constructor contract = " + contractVersion + " version number = " + versionNumber);
   }
   
   /**
    * Method: harvestAll
    * Description: called to kick off a full harvest of all
    * Registries.
    * 
    */
   public void harvestAll()  {
       try {
           harvestAll(true,true);
       }catch(RegistryException re) {
         re.printStackTrace();
         log.error(re);
         //now update the stat info with this exception.
       }
   }
   
   /**
    * Method: harvestURL
    * Description: harvest a particular url.  Currently not
    * used by any class/jsp/servlet.
    */
   public void harvestURL(URL url, String invocationType, Date dt, String setName) throws RegistryException, IOException {
       beginHarvest(url.toString(), invocationType, dt, null, null,setName);
   }
   
   /**
       * Will start a harvest of all the Registries known to this registry.
       * Only harvests if harvesting is enabled.
       *
       * @param onlyRegistries harvest only Registry type services, currently
       * this is normally true.  Later harvesting of services may happen.
       * @param useDates - harvest via dates found in a status document.  Used as
       * a 'from' date on the harvest call to get recent resources.
       */
   public void harvestAll(boolean onlyRegistries, boolean useDates) throws RegistryException  {
      log.debug("start harvestAll");
      Document harvestDoc = null;

      Date dt = null;
      //make sure harvest is enabled.
      boolean harvestEnabled = conf.getBoolean("reg.amend.harvest",false);

      //return if harvesting is not enabled.
      if(!harvestEnabled) {
          return;
      }

      //get the main authority id of this registry.
      String authorityID = conf.getString("reg.amend.authorityid");

      String tempIdent = null;
      String resKey = null;
      String lastResumptionToken = null;
      
      //harvest only registries, at the moment always true.
          if(onlyRegistries) {
             //query for all the Registry types which should be all of them with an xsi:type="RegistryType"
             //xqlQuery = "declare namespace vr = \"http://www.ivoa.net/xml/VOResource/v0.9\"; //vr:Resource[@xsi:type='RegistryType']";
                 QueryHelper queryHelper = null;
                 log.debug("Start processing Registry Types from version = " + contractVersion);
                 try {
                 	//Get the search service
                     ISearch search = QueryFactory.createQueryService(contractVersion);
                     queryHelper = search.getQueryHelper();
                     //query for all registries.
                     harvestDoc = DomHelper.newDocument(queryHelper.getRegistriesQuery().getMembersAsResource().getContent().toString());
                 }catch(Exception e) {
                     log.error(e);
                     harvestDoc = null;
                 }
                 //make sure we have some DOM of registry types.
                 if(harvestDoc != null) {
                     NodeList nl = harvestDoc.getElementsByTagNameNS("*","Resource");
                     log.debug("Harvest All found this number of resources = " + nl.getLength());
                     //go through all the Resource Registry types
                     //and begin harvesting.
                     for(int i = 0; i < nl.getLength();i++) {
                       Element elem = (Element) nl.item(i);
                       //get authority id and resource key (which makes up the identifier).
                       //later tempIdent will go back as a identifier.
                       tempIdent = RegistryDOMHelper.getAuthorityID(elem);
                       resKey = RegistryDOMHelper.getResourceKey(elem);
                   
                       //se if we need to use dates.
                       if(useDates) {
                          String dateString = null;
                          try {
                              if(resKey != null && resKey.trim().length() > 0) tempIdent += "/" + resKey;
                              //get the status document where the date can be found
                              Document statDoc = rha.getStatus(tempIdent, versionNumber);
                              log.debug("StatDoc toString = " + DomHelper.DocumentToString(statDoc));
                              //dateString = DomHelper.getNodeTextValue(statDoc,"StatsDateMillis");
                              dateString = DomHelper.getNodeTextValue(statDoc,"MostRecentResourceUpdateMillis");
                              
                              //lastResumptionToken = DomHelper.getNodeTextValue(statDoc,"LastResumptionToken");
                              log.debug("dateString found in statDoc to be used for harvesting = " + dateString);                              
                          } catch(IOException ioe) {
                                  log.error(ioe);
                                  log.info("xmldb exception thrown when trying to obtain stat and date information. Continue on and do a full harvest of the Registry Type");
                                  dateString = null;
                                  //Forgot to comment out this throws
                                  //clause in the latest release it should
                                  //not throw anything.
                                  //throw new RegistryException(ioe);
                              }
                          //found a date string so place it as a Date object.
                          if(dateString != null && dateString.trim().length() > 0) {
                              dt = new Date(Long.parseLong(dateString));
                          }//if
                       }//if
                       try {                           
                           if(tempIdent.startsWith(authorityID)) {
                               log.debug("This is our main Registry type do not do a harvest of it");
                           } else {
                               //rha.clearOAILastUpdateInfo(tempIdent + "/" + resKey,versionNumber);
                           	  //call beginHarvest which is in the subclass (contract version RegistryHarvestService)
                               beginHarvest(elem,dt, lastResumptionToken);
                               dt = null;
                               //rha.addStatNewDate(tempIdent + "/" + resKey, versionNumber);
                               //rha.harvestingUpdate(DomHelper.newDocument(DomHelper.ElementToString(elem)), versionNumber);
                           }
                       }catch(IOException ioe) {
                           log.error(ioe);
                           log.info("still continue to the next Registry type if there are any");
                           //update this reg stat here
                           rha.addStatError(tempIdent,versionNumber,ioe.getMessage());
                           //re = new RegistryException(ioe);
                       }/*catch(XMLDBException xmldbe) {
                           log.error(xmldbe);
                           rha.addStatError(tempIdent + "/" + resKey,versionNumber,xmldbe.getMessage());
                           //update this reg stat here
                       }*/catch(InvalidStorageNodeException isne) {
                           log.error(isne);
                           rha.addStatError(tempIdent,versionNumber,isne.getMessage());
                           //update this reg stat here
                       }catch(RegistryException ree) {
                           log.error(ree);
                           log.debug("still continue to the next Registry type if there are any");
                           rha.addStatError(tempIdent,versionNumber,ree.getMessage());
                           //update this reg stat here
                       }
                     }//for
                 }//if
          }//if
   }

   /**
    * Method: beginHarvest
    * Description: main abstract to analyze the Registry type DOM to get
    * the url for harvesting.
    *
    * @param resource Node Resource DOM of the Registry type.
    * @param dt An optional date used to harvest from a particular date
    * @param resources Set of Resources to harvest on, normally a Registry Resource.
    * 
    */
   public abstract void beginHarvest(Node resource, Date dt, String lastResumptionToken) throws RegistryException, IOException;
   
   /**
    * Method: getNameSpaceURI
    * Description: abstract method to return the namespace uri for
    * the harvesting service.
    */
   public abstract String getNameSpaceURI();
   
   /**
    * Method: beginHarvest
    * Description: starts the harvesting of a url.  Several various parameters to determine how to
    * harvest.
    * 
    * @param accessURL OAI harvesting url.
    * @param invocationType such as WebService or ParamHTTP for HTTP-GET or Soap based calls.
    * @param dt from date.
    * @lastResumptionToken a token for paging through many Resources using OAI calls.
    * @identifier used to see if there is a property that could be set fot calling the OAI
    * with a particular Set.  Normally none which uses the default ivo_managed.
    * @setName set to be used for OAI harvest call.
    * 
    */
   public String beginHarvest(String accessURL, String invocationType, Date dt, String lastResumptionToken, String identifier, String setName) throws RegistryException, IOException  {
       log.debug("entered beginharvest(url,invocation)");      
       //boolean isRegistryType;
       //Document doc = null;
       String results = null;

       //if no set name then verify there is not a property
       //setting for this identifier to use a particular set for harvesting.
       if(setName == null) {
           setName = conf.getString("reg.custom.harvest.set-" + identifier.substring(6,identifier.indexOf('/', 6)),MAIN_HARVEST_SET);
       }

       //had an external class somehow passing in NONE so make sure that
       //sets the resumption token to null.
       if(lastResumptionToken != null && lastResumptionToken.equals("NONE"))
           lastResumptionToken = null;
      
      
      if(invocationType != null && (invocationType.endsWith("WebService") || invocationType.endsWith("OAISOAP"))) {
         //call the service
         //remember to look at the date
      	  log.debug("its a webservice call it via soap");
          results =  runHarvestSoap(accessURL, lastResumptionToken, setName, dt, identifier, "");
          //rha.addResumptionToken(identifier,versionNumber,"NONE");
          return results;
      }else if(invocationType != null && 
               (invocationType.endsWith("WebBrowser") || 
                invocationType.endsWith("Extended") ||
                invocationType.endsWith("ParamHTTP") ||
                invocationType.endsWith("OAIHTTP"))
               ) {
         //its a web browser so assume for oai.
         try {
            String ending = "";
            //might need to put some oai date stuff on the end.  This is
            //unknown.
            log.debug("A web browser invocation not a web service");
            String httpSet = "";
            
            if(!setName.equals("NONE") && hasSetViaHTTP(setName, accessURL)) {
                httpSet = "&set=" + setName;
            }
            ending += "&metadataPrefix=ivo_vor" + httpSet;
            if(dt != null) {
                ending += "&from=" + sdf.format(dt);
            }
            //log.debug("Grabbing Document from this url = " + accessURL + ending);

            results =  runHarvestGet(accessURL + "?verb=ListRecords", ending, lastResumptionToken, identifier, "");
            //rha.addResumptionToken(identifier,versionNumber,"NONE");
            return results;
         }catch(ParserConfigurationException pce) {
            log.error(pce);
            throw new RegistryException(pce);
         }catch(SAXException sax) {
            log.error(sax);
            throw new RegistryException(sax);
         }catch(IOException ioe){
            log.error(ioe);
            throw new RegistryException(ioe);
         }
      }//elseif
      log.debug("end beginHarvest");
      return null;
   }//beginHarvest
       
       public String runHarvestGet(String accessURL, String urlQuery, String resumptionToken, String identifier, String resultInfo) throws RegistryException {
           Document doc = null;
           int failureCount = 0;           
           boolean resumptionSuccess = false;
           String results = resultInfo;
           while(failureCount <= 2 && !resumptionSuccess) {
               try {
                   log.debug("harvest call url = " + accessURL + urlQuery);
                   if(resumptionToken != null) {
                	   log.info("and full url to oai = " + accessURL + "&resumptionToken=" + java.net.URLEncoder.encode(resumptionToken,"UTF-8"));
                       doc = DomHelper.newDocument(new URL(accessURL + "&resumptionToken=" + java.net.URLEncoder.encode(resumptionToken,"UTF-8")));
                       if(doc.getDocumentElement().getElementsByTagNameNS("*","metadata").getLength() > 0)
                           urlQuery = null;
                       else {
                           if(doc.getDocumentElement().getElementsByTagNameNS("http://www.openarchives.org/OAI/2.0","error").getLength() == 0 &&
                              doc.getDocumentElement().getElementsByTagNameNS("oai","error").getLength() == 0) {
                               urlQuery = null;
                           }
                       }
                   }
                   if(urlQuery != null) {
                	   log.debug("Grabbing doc from this harvest url = " + accessURL + urlQuery);
                       doc = DomHelper.newDocument(new URL(accessURL + urlQuery));
                   }
                   resumptionSuccess = true;                   
               }catch(Exception e) {
               	    //failed 3 times now, throw the exception
               	    //back up to be reported.
               		if(failureCount == 2) {
               			throw new RegistryException(e);
               		}
               		log.error("Seemed to fail for = " + accessURL + urlQuery);
               		log.error("Exception: " + e.getMessage());
               		log.info("try another in case web server has not caught up");
               		failureCount++;
               		resumptionSuccess = false;
               }                   
           }
           try {
               if(doc.getDocumentElement() != null && doc.getDocumentElement().getElementsByTagNameNS("*","metadata").getLength() > 0) {
                   results += rha.harvestingUpdate(doc, identifier, versionNumber);
                   /*
                   if(versionNumber.equals(("0.10")) {
                	   log.debug("transform to 1.0 version and do an update for 1.0");                	   
                	   rha.harvestingUpdate(doc, "0.10_1.0");
                   }
                   */
                   
               }
               //rha.addStatInfo()
           }catch(Exception e) {
        	   e.printStackTrace();
               throw new RegistryException(e);
           }
           
           NodeList moreTokens = null;
           //if there are more paging(next) then keep calling them.
           if( doc != null && (moreTokens = doc.getElementsByTagNameNS("*","resumptionToken")).
                                    getLength() > 0 && moreTokens.item(0).hasChildNodes()) {
              Node nd = moreTokens.item(0);
              /*
              if(accessURL.indexOf("?") != -1) {
                 accessURL = accessURL.substring(0,accessURL.indexOf("?"));
              }
              */
              urlQuery = null;
              //might use later idea is to put the resumption token in the
              //status document and try to start off from a resumption token.  This would
              //be good in CDS case when there is a huge about of Resources being harvested.
              //Though technically resumptionToken do not guarantee to save state and give you the same
              //results.
              //rha.addResumptionToken(identifier,versionNumber,nd.getFirstChild().getNodeValue());
              //System.out.println("RESUMPTIONTOKEN RESULTS:====" + results);
              results += runHarvestGet(accessURL, urlQuery, nd.getFirstChild().getNodeValue(), identifier, "");
           }//if
           return results;
       }//runHarvestGet
          
       private SOAPBodyElement createSoapBody(String accessURL, String resumptionToken, String setName, Date fromDate) throws Exception {
           SOAPBodyElement sbeRequest = null;
           
           try {
               Element childElem = null;
               Element root = null;               
               Document doc = DomHelper.newDocument();
               log.debug("Calling harvest service for url = " + accessURL + 
                        " interface Method = " + OAIINTERFACE_METHOD + 
                        " with soapactionuri = " + SOAPACTION_URI +
                        " resumptionToken = " + resumptionToken +
                        " setName = " + setName);
               if(fromDate != null)
                log.debug(" fromDate = " + sdf.format(fromDate));
               root = doc.createElementNS(getNameSpaceURI(),OAIINTERFACE_METHOD);
               if(resumptionToken != null) {
                   childElem = doc.createElementNS(getNameSpaceURI(),"resumptionToken");
                   childElem.appendChild(doc.createTextNode(resumptionToken));
                   root.appendChild(childElem);                   
               } else {
                   if(!setName.equals("NONE") && hasSetViaWebService(setName, accessURL)) {
                       childElem = doc.createElementNS(getNameSpaceURI(),"set");
                       childElem.appendChild(doc.createTextNode(setName));
                       root.appendChild(childElem);                   
                   }
                   if(fromDate != null) {
                      childElem = doc.createElementNS(getNameSpaceURI(),"from");
                      childElem.appendChild(doc.createTextNode(sdf.format(fromDate)));
                      root.appendChild(childElem);
                   }//if
               }
               doc.appendChild(root);
               //log.info("FULL SOAP REQUEST FOR HARVEST = " + DomHelper.DocumentToString(doc));
               sbeRequest = new SOAPBodyElement(
                                                doc.getDocumentElement());
               //sbeRequest.setName("harvestAll");
               sbeRequest.setName(OAIINTERFACE_METHOD);
               sbeRequest.setNamespaceURI(getNameSpaceURI());
           } catch(ParserConfigurationException pce) {
               pce.printStackTrace();
               log.error(pce);
               //throw new RegistryException(pce);
           }
           return sbeRequest;           
       }
              
       public String runHarvestSoap(String accessURL, String resumptionToken, String setName, Date fromDate, String identifier, String resultInfo) throws RegistryException {
           
           //create a call object
           String results = resultInfo;
           Call callObj = getCall(accessURL);
                 //log.info("FULL SOAP REQUEST FOR HARVEST = " + DomHelper.DocumentToString(doc));                 
                 try {
                     SOAPBodyElement sbeRequest = createSoapBody(accessURL, resumptionToken, setName, fromDate);
                     log.debug("Calling invoke on service");
                     Vector result = (Vector) callObj.invoke
                                              (new Object[] {sbeRequest});
                     //Take the results and harvest.
                     if(result.size() > 0) {
                         SOAPBodyElement sbe = (SOAPBodyElement) result.get(0);
                         Document soapDoc = sbe.getAsDocument();
                         if(resumptionToken != null)
                         if(soapDoc.getDocumentElement().getElementsByTagNameNS("http://www.openarchives.org/OAI/2.0","error").getLength() > 0 ||
                            soapDoc.getDocumentElement().getElementsByTagNameNS("oai","error").getLength() > 0) {
                             runHarvestSoap(accessURL, null, setName, fromDate,identifier,results);
                         }
                         
                         //(new HarvestThread(ras,soapDoc.getDocumentElement())).start();
                         if(soapDoc.getDocumentElement().getElementsByTagNameNS("*","metadata").getLength() > 0)
                             results += rha.harvestingUpdate(soapDoc, identifier, versionNumber);
                         //if(isRegistryType) {
                            NodeList nl = DomHelper.getNodeListTags(soapDoc,"resumptionToken");
                            if(nl.getLength() > 0) {
                                runHarvestSoap(accessURL, nl.item(0).getFirstChild().getNodeValue(), setName, fromDate, identifier, results);
                            }//if
                     }//if
                  } catch(RemoteException re) {
                      //log error
                      re.printStackTrace();
                      log.error(re);
                      throw new RegistryException(re);   
                  } catch(Exception e) {
                	e.printStackTrace();
                	log.error(e);
                    throw new RegistryException(e);
                  }
                  return results;
       }
   
   
   private boolean hasSetViaHTTP(String setName, String accessURL) throws ParserConfigurationException, SAXException, IOException {
       String ending = "";
       if(accessURL.indexOf("?") == -1) {
           ending = "?verb=ListSets";
       }
       log.debug("grabbing document at: " + accessURL + ending);
       Document doc = DomHelper.newDocument(new URL(accessURL + ending));
       log.debug("the spec doc = " + DomHelper.DocumentToString(doc));
       NodeList nl = doc.getElementsByTagNameNS("*","setSpec");
       for(int i = 0;i < nl.getLength();i++) {
           if(setName.equals(nl.item(i).getFirstChild().getNodeValue()))
               return true;
       }//for
       return false;
   }
   
   protected boolean hasSetViaWebService(String setName, String accessURL)  throws ParserConfigurationException, RemoteException, Exception  {
       Element root = null;
       Call callObj = getCall(accessURL);       
       Document doc = DomHelper.newDocument();
       String interfaceMethod = "ListSets";

       log.debug("Calling harvest service for url = " + accessURL + 
                " interface Method = " + interfaceMethod + 
                " with soapactionuri = " + SOAPACTION_URI);
       
       root = doc.createElementNS(getNameSpaceURI(),interfaceMethod);       
       doc.appendChild(root);
       //log.info("FULL SOAP REQUEST FOR HARVEST = " + DomHelper.DocumentToString(doc));
       SOAPBodyElement sbeRequest = new SOAPBodyElement(
                                        doc.getDocumentElement());
       //sbeRequest.setName("harvestAll");
       sbeRequest.setName(interfaceMethod);
       sbeRequest.setNamespaceURI(getNameSpaceURI());
       //invoke the web service call
       log.debug("Calling invoke on service");
       Vector result = (Vector) callObj.invoke
                                (new Object[] {sbeRequest});
       //Take the results and harvest.
       if(result.size() > 0) {
           SOAPBodyElement sbe = (SOAPBodyElement) result.get(0);
           Document soapDoc = sbe.getAsDocument();
           NodeList nl = soapDoc.getElementsByTagNameNS("*","setSpec");
           for(int i = 0;i < nl.getLength();i++) {
               if(setName.equals(nl.item(i).getFirstChild().getNodeValue()))
                   return true;
           }//for
       }
       return false;
   }

   /**
    * Method to establish a Service and a Call to the server side web service.
    * @return Call object which has the necessary properties set for an Axis message style.
    * @throws Exception
    * @author Kevin Benson
    */
   protected Call getCall(String endPoint) {
      log.debug("start getCall");
      Call _call = null;
      try {
         Service  service = new Service();
         _call = (Call) service.createCall();
         _call.setTargetEndpointAddress(endPoint);
         _call.setSOAPActionURI(SOAPACTION_URI);
         _call.setEncodingStyle(null);
      } catch(ServiceException se) {
         se.printStackTrace();
         log.error(se);
         _call = null;
      }finally {
         log.debug("end getCall");
      }
      return _call;
   }//getCall
   
}