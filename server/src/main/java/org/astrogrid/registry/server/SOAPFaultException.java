package org.astrogrid.registry.server;

import org.w3c.dom.Document;

import org.astrogrid.util.DomHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class: SOAPFaultException
 * Description: Small helper class to help construct a Soap Fault Exception
 * DOM to be returned as a response in the case of errors. 
 * 
 * @author Kevin Benson
 */
public class SOAPFaultException extends Exception {
	
	public static final int QUERYSOAP_TYPE = 1;
	public static final int ADMINSOAP_TYPE = 2;
	public static final int HARVESTSOAP_TYPE = 3;
	public static final int NOTFOUNDSOAP_TYPE = 4;
	
	private Document excDoc = null;
	private int soapFaultType = -1;

   /**
    * Constructor
    * Takes parameters passed into the constructor and creates a 'Fault' DOM object to be returned
    * for Soap Requests in the case of errors.  This constructor deals with an errorString for the description.
    * @param faultString Fault Message string
    * @param errorString Error description in the Fault.
    * @param faultNamespace Namespace for this Fault.  Each wsdl contract has there own fault namespace used.
    * @param soapType Type to determine which createSoapFaultMethod to call since different types have a slightly differnt
    * DOM.
    */
   public SOAPFaultException(String faultString, String errorString, String faultNamespace, int soapType) {
	   super(errorString);
	   this.soapFaultType = soapType;
	   if(soapType == QUERYSOAP_TYPE) {
		   this.excDoc = createSOAPFaultException(faultString, errorString,faultNamespace);
	   }else if(soapType == ADMINSOAP_TYPE) {
		   this.excDoc = createSOAPFaultException(faultString, errorString,faultNamespace);
	   }else if(soapType == HARVESTSOAP_TYPE) {
		   this.excDoc = createSOAPFaultException(faultString, errorString,faultNamespace);
	   }else if(soapType == NOTFOUNDSOAP_TYPE) {
		   this.excDoc = createQueryNotFoundSOAPFaultException(faultString, errorString,faultNamespace);
	   }
   }
   
   /**
    * Constructor
    * Takes parameters passed into the constructor and creates a 'Fault' DOM object to be returned
    * for Soap Requests in the case of errors.  This constructor deals with an Exception object for the description and
    * can handle chained exceptions.
    * @param faultString Fault Message string
    * @param errorString Error description in the Fault.
    * @param faultNamespace Namespace for this Fault.  Each wsdl contract has there own fault namespace used.
    * @param soapType Type to determine which createSoapFaultMethod to call since different types have a slightly differnt
    * DOM.
    */   
   public SOAPFaultException(String faultString, Exception errorException, String faultNamespace, int soapType) {
	   super(faultString,errorException);
	   this.soapFaultType = soapType;
	   
	   if(soapType == QUERYSOAP_TYPE) {
		   this.excDoc = createSOAPFaultException(faultString, errorException,faultNamespace);
	   }else if(soapType == ADMINSOAP_TYPE) {
		   this.excDoc = createSOAPFaultException(faultString, errorException,faultNamespace);
	   }else if(soapType == HARVESTSOAP_TYPE) {
		   this.excDoc = createSOAPFaultException(faultString, errorException,faultNamespace);
	   }else if(soapType == NOTFOUNDSOAP_TYPE) {
		   this.excDoc = createQueryNotFoundSOAPFaultException(faultString, errorException,faultNamespace);
	   }
   }
   
   /**
    * Method: getFaultDocument
    * Description: simple get method to return the DOM.
    * @return Document DOM of the Fault.
    */
   public Document getFaultDocument() {
	   return this.excDoc;
   }
   
   /**
    * Method: getSoapFaultType
    * Description: Returns what type of Fault this is in regards to the Registry.
    * @return integer of the Soap fault type. e.g. Query, Admin, Harvest, NotFound.
    */
   public int getSoapFaultType() {
	   return this.soapFaultType;
   }
	
   
   /**
    * Logging variable for writing information to the logs
    */   
   private static final Log log = LogFactory.getLog(SOAPFaultException.class);
      
   /**
    * Method: createSOAPFaultException
    * Description: Creates a Soap Fault DOM.
    * @param faultString fault string message
    * @param errorString error description string in the fault
    * @param faultNS namespace to be used for the ErrorResponse in the fault
    * @return Document DOM Fault to be returned on soap requests.
    */
   private Document createSOAPFaultException(String faultString, String errorString, String faultNS ) {
     String faultMessage = "<env:Fault xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
     "<faultcode>env:Server</faultcode><faultstring>"+faultString+"</faultstring><detail>" +
     "<tns:ErrorResponse xmlns:tns=\"" + faultNS + "\">" +
     "<tns:errorMessage>" + errorString + "</tns:errorMessage>" +
     "</tns:ErrorResponse>" +
     "</detail></env:Fault>";            
   
     try {
         return DomHelper.newDocument(faultMessage);
     }catch(Exception e) {
         log.error("Could not create soap fault xml, problem on server making xml from string");
         log.error(e);
     }
     return null;
   }
   
   /**
    * Method: createSOAPFaultException
    * Description: Creates a Soap Fault DOM.
    * @param faultString fault string message
    * @param exception Exception object to be used for populating the Fault description.
    * @param faultNS namespace to be used for the ErrorResponse in the fault
    * @return Document DOM Fault to be returned on soap requests.
    */   
   private Document createSOAPFaultException(String faultString, Exception exception, String faultNS ) {
       StackTraceElement []ste = exception.getStackTrace();
       String faultMessage = "<env:Fault xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
       "<faultcode>env:Server</faultcode><faultstring>"+faultString+"</faultstring><detail>" +
       "<tns:ErrorResponse xmlns:tns=\"" + faultNS + "\">" +
       "<tns:errorMessage>";
       //Loop through the stack trace and get the message/tostring of each exception.
       for(int i = 0;i < ste.length;i++) 
           faultMessage += ste[i].toString() + " ";
       //Place all the messages together in the Fault Error message dom.
       faultMessage += "</tns:errorMessage>" +
       "</tns:ErrorResponse>" +
       "</detail></env:Fault>";            
       //construct the DOM and return it.
       try {
           return DomHelper.newDocument(faultMessage);
       }catch(Exception e) {
           log.error("Could not create soap fault xml, problem on server making xml from string");
           log.error(e);
       }
       return null;
   }
   
   /**
    * Method: createQueryNotFoundSOAPFaultException
    * Description: Creates a Soap Fault DOM for a NotFound Query which has a different
    * Element (NotFound instead of ErrorResponse) based on the Query wsdl contract.
    * @param faultString fault string message
    * @param errorString error description string in the fault
    * @param faultNS namespace to be used for the ErrorResponse in the fault
    * @return Document DOM Fault to be returned on soap requests.
    */   
   private Document createQueryNotFoundSOAPFaultException(String faultString, String errorString, String faultNS ) {
	     String faultMessage = "<env:Fault xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
	     "<faultcode>env:Server</faultcode><faultstring>"+faultString+"</faultstring><detail>" +
	     "<tns:NotFound xmlns:tns=\"" + faultNS + "\">" +
	     "<tns:errorMessage>" + errorString + "</tns:errorMessage>" +
	     "</tns:NotFound>" +
	     "</detail></env:Fault>";            
	     
	     try {
	         return DomHelper.newDocument(faultMessage);
	     }catch(Exception e) {
	         log.error("Could not create soap fault xml, problem on server making xml from string");
	         log.error(e);
	     }
	     return null;
	   }   
   
   /**
    * Method: createQueryNotFoundSOAPFaultException
    * Description: Creates a Soap Fault DOM for a NotFound Query which has a different
    * Element (NotFound instead of ErrorResponse) based on the Query wsdl contract.
    * @param faultString fault string message
    * @param exception Exception object to be used for populating the Fault description.
    * @param faultNS namespace to be used for the ErrorResponse in the fault
    * @return Document DOM Fault to be returned on soap requests.
    */    
   private Document createQueryNotFoundSOAPFaultException(String faultString, Exception exception, String faultNS ) {
       StackTraceElement []ste = exception.getStackTrace();
       String faultMessage = "<env:Fault xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
       "<faultcode>env:Server</faultcode><faultstring>"+faultString+"</faultstring><detail>" +
       "<tns:NotFound xmlns:tns=\"" + faultNS + "\">" +
       "<tns:errorMessage>";
       
       for(int i = 0;i < ste.length;i++) 
           faultMessage += ste[i].toString() + " ";
       faultMessage += "</tns:errorMessage>" +
       "</tns:NotFound>" +
       "</detail></env:Fault>";            
       try {
           return DomHelper.newDocument(faultMessage);
       }catch(Exception e) {
           log.error("Could not create soap fault xml, problem on server making xml from string");
           log.error(e);
       }
       return null;
   }  
   
}