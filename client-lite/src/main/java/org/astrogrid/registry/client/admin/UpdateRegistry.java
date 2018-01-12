package org.astrogrid.registry.client.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.net.URL; 
import java.util.Vector; 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException; 
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call; 
import org.apache.axis.client.Service; 
import org.astrogrid.registry.common.RegistryValidator;
import org.apache.axis.message.SOAPBodyElement; 
import org.w3c.dom.Document; 
import org.w3c.dom.Element;

import java.io.IOException;;
import org.xml.sax.SAXException;
import java.rmi.RemoteException;

import org.astrogrid.registry.RegistryException;

import java.io.*;
import org.astrogrid.util.DomHelper;
import org.astrogrid.config.Config;
import org.astrogrid.store.Ivorn;
import org.astrogrid.registry.common.RegistryDOMHelper;

/**
 * Class Name: RegistryAdminService
 * Description: This class represents the client webservice delegate to the Administration piece of the
 * web service.  It uses the same Interface as the server side webservice so they both implement and handle
 * the same web service method names.  The primary goal of this class is to establish a Axis-Message style
 * webservice call to the server.
 * 
 * @see org.astrogrid.registry.common.RegistryAdminInterface
 * @author Kevin Benson
 * @todo document the method bodies
 *
 * 
 * 
 */
//Dropping the abstract because several other components to instantiate
//this class directly instead of the DelegateFactory.
public  abstract class UpdateRegistry  {
    /**
     * Commons Logger for this class
     */
   private static final Log logger = LogFactory.getLog(UpdateRegistry.class); 


   public static final String ADMIN_URL_PROPERTY = "org.astrogrid.registry.admin.endpoint";
   
   private static String cacheDir = null;
   
   private boolean validated = false;   

    /**
    * target end point  is the location of the webservice. 
    */
   private URL endPoint = null;
   
   public static Config conf = null;    

   
   /** @todo shouldn't be necessary to take a local reference to this static - call simple config.getSingleton each time instead */
   static {
      if(conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
         cacheDir = conf.getString("registry.cache.dir",null);
      }      
   }
   
   public abstract String getSoapBodyNamespaceURI();
   
   
   /**
    * Main constructor to allocate the endPoint variable.
    * @todo what happens with nulls?
    * @param endPoint location to the web service.
    * @author Kevin Benson
    */ 
   public UpdateRegistry(URL endPoint) {
      this.endPoint = endPoint;
   }
   
   public UpdateRegistry() {
		  //super(endPoint);
		  //do nothing
	  }  
    
   /**
    * Method to establish a Service and a Call to the server side web service.
    * @return Call object which has the necessary properties set for an Axis message style.
    * @throws Exception
    * @author Kevin Benson
    */     
   private Call getCall() throws ServiceException {
      Call _call = null;
         Service  service = new Service();      
         _call = (Call) service.createCall();
         _call.setTargetEndpointAddress(this.endPoint);
         _call.setSOAPActionURI("");
//         _call.setOperationStyle(org.apache.axis.enum.Style.MESSAGE);
//         _call.setOperationUse(org.apache.axis.enum.Use.LITERAL);        
         _call.setEncodingStyle(null);
         return _call;       
    }
   
   
   /**
    * Takes an XML Document to send to the update server side web service call.  Establishes
    * a service and a call to the web service and call it's update method, using an Axis-Message
    * style.  Then updates this document onto the registry.
    * @param query Document a XML document dom object to be updated on the registry.
    * @return the document updated on the registry is returned.
    * @author Kevin Benson
    * 
    */   
   public Document update(Document update) throws RegistryException {

      if(update == null) {
          throw new RegistryException("Cannot update 'null' found as the document to update");
      }
      //System.out.println("THE NAMESPACE URI USED = " + getSoapBodyNamespaceURI());
      
      boolean validateXML = conf.getBoolean("registry.validate.onupdates",false);
      if(validateXML) {
        RegistryValidator validator = new RegistryValidator();
        if (validator.isInvalid(update.getDocumentElement())) {
          logger.error("Error invalid document still attempting to process resources = " + validator.getErrorMessages());
          if(conf.getBoolean("registry.quiton.invalid",false)) {
            throw new RegistryException("Quitting: Invalid document, Message: " + validator.getErrorMessages());
          }
        }          
      }
      
      if(cacheDir != null) {
          try {
              logger.debug("cachedir found updaing to cache directory = " + cacheDir);
              String ident = RegistryDOMHelper.getIdentifier(update.getDocumentElement());
              if(Ivorn.isIvorn(ident))
                  ident = ident.substring((Ivorn.SCHEME + "://").length());
              ident = ident.replaceAll("[^\\w*]","_") + ".xml";
              logger.debug("filename will be = " + ident);
              FileOutputStream fos = new FileOutputStream(new File(cacheDir,ident));
              DomHelper.DocumentToStream(update,fos);
              return DomHelper.newDocument("<UpdateResponse/>");
          }catch(IOException ioe) {
              logger.error(ioe);
              throw new RegistryException(ioe);
          } catch (ParserConfigurationException pce) {
              logger.error(pce);
              throw new RegistryException(pce);
          } catch (SAXException sax) {
              logger.error(sax);
              throw new RegistryException(sax);
          }
      }
      DocumentBuilder registryBuilder = null;
      Document doc = null;
      Document resultDoc = null;
          //Element root = doc.createElementNS(NAMESPACE_URI,"update");

          Element root = update.createElementNS(getSoapBodyNamespaceURI(),"Update");
          root.appendChild(update.getDocumentElement());
          update.appendChild(root);
          
      try {   
      //Get the CAll.  
      Call call = getCall(); 
      
      //Build up a SoapBodyElement to be sent in a Axis message style.
      //Go ahead and reset a name and namespace to this as well.
      logger.debug("update(Document) - the endpoint = "+ this.endPoint);
      logger.debug("update(Document) - okay calling update service with doc = "
            + DomHelper.DocumentToString(update));
      //SOAPBodyElement sbeRequest = new SOAPBodyElement(doc.getDocumentElement());      
      SOAPBodyElement sbeRequest = new SOAPBodyElement(update.getDocumentElement());
      sbeRequest.setName("Update");
      sbeRequest.setNamespaceURI(getSoapBodyNamespaceURI());
      
    
         Vector result = (Vector) call.invoke (new Object[] {sbeRequest});
         if(result.size() > 0) {
            SOAPBodyElement sbe = (SOAPBodyElement) result.get(0);
            resultDoc =  sbe.getAsDocument();
         }
      }catch(RemoteException re) {
         resultDoc = null;
         logger.error(re);
         //@todo shouldn't catch this one - let it through.
         throw new RegistryException(re);
      }catch (Exception e) {
         resultDoc = null;
         logger.error(e);
         throw new RegistryException(e);
      }
      return resultDoc;
   }
   
   /**
    * Method: updateFromFile
    * Purpose: Update from a File object a local XML file on the system.  Mainly makes a Document DOM object and calls
    * update.
    * @param fi - java.io.File object.
    * @return the document updated on the registry is returned. 
    */
   public Document updateFromFile(File fi) throws RegistryException {
      try {
         return update(DomHelper.newDocument(fi));
      }catch(IOException ioe) {
          logger.error(ioe);
         throw new RegistryException(ioe);      
      }catch(SAXException sax) {
          logger.error(sax);
         throw new RegistryException(sax);   
      }catch(ParserConfigurationException pce) {
          logger.error(pce);
         throw new RegistryException(pce);
      }
   }

   /**
    * Method: updateFromURL
    * Purpose: Update from a URL consisting of XML for updating the Registry.  Mainly makes a Document DOM object and calls
    * update.
    * @param location - java.net.URL
    * @return the document updated on the registry is returned. 
    */   
   public Document updateFromURL(URL location) throws RegistryException {
      try {
         return update(DomHelper.newDocument(location));
      }catch(IOException ioe) {
          logger.error(ioe);
         throw new RegistryException(ioe);      
      }catch(SAXException sax) {
          logger.error(sax);
         throw new RegistryException(sax);   
      }catch(ParserConfigurationException pce) {
          logger.error(pce);
         throw new RegistryException(pce);
      }
   }  

   /**
    * Method: updateFromString
    * Purpose: Update from a String of a XML.  Mainly makes a Document DOM object and calls
    * update.
    * @param voresources - a String of XML.
    * @return the document updated on the registry is returned. 
    */   
   public Document updateFromString(String voresources) throws RegistryException {
      try {
         return update(DomHelper.newDocument(voresources));
      }catch(IOException ioe) {  
          logger.error(ioe);
         throw new RegistryException(ioe);      
      }catch(SAXException sax) {
          logger.error(sax);
         throw new RegistryException(sax);   
      }catch(ParserConfigurationException pce) {
          logger.error(pce);
         throw new RegistryException(pce);
      }      
   }   
   
   public String getCurrentStatus() {
       return "";
   }
   
   public Document getStatus() {
       return null;
   }
}