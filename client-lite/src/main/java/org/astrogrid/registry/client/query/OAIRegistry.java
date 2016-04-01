package org.astrogrid.registry.client.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
//import org.apache.axis.constants.Style;
//import org.apache.axis.constants.Use;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.XMLUtils;
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


public class OAIRegistry implements OAIService {

    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory.getLog(OAIRegistry.class);

    /**
     * target end point is the location of the webservice.
     */
    private URL endPoint = null;

    private static final String NAMESPACE_URI =
        "http://www.ivoa.net/schemas/services/QueryRegistry/wsdl";

    private static final String OAI_METADATA_PREFIX = "ivo_vor";

    private static String reg_default_version = "0.9";


    public static Config conf = null;

     //@todo don't think it's necessary to hang onto this object
     static {
        if (conf == null) {
           conf = org.astrogrid.config.SimpleConfig.getSingleton();
           reg_default_version = conf.getString("org.astrogrid.registry.version","0.9");
        }
     }

     /**
      * @todo - I think this is almost cyclic.
      * Empty constructor that defaults the end point to local host.
      * @author Kevin Benson
      */
     public OAIRegistry() {
        this(conf.getUrl(org.astrogrid.registry.client.RegistryDelegateFactory.OAI_URL_PROPERTY,null));
     }


     /**
      * Main constructor to allocate the endPoint variable.
      * @param endPoint location to the web service.
      * @author Kevin Benson
      */
     public OAIRegistry(URL endPoint) {
          logger
                  .info("QueryRegistry(URL) - entered const(url) of RegistryService");
        this.endPoint = endPoint;
     }

     /**
      * Method to establish a Service and a Call to the server side web service.
      * @return Call object which has the necessary properties set for an Axis message style.
      * @throws Exception
      * @todo there's code similar to this in eac of the delegate classes. could it be moved into a common baseclass / helper
      class.
      * @author Kevin Benson
      */
     private Call getCall() throws ServiceException {

        logger.info("getCall() - entered getCall()");
        Call _call = null;
        Service service = new Service();
        _call = (Call)service.createCall();
        _call.setTargetEndpointAddress(this.endPoint);
        _call.setSOAPActionURI("");
        //_call.setOperationStyle(Style.MESSAGE);
        //_call.setOperationUse(Use.LITERAL);
        _call.setEncodingStyle(null);
        return _call;
     }

     protected Document callService(Document soapBody, String name, String soapActionURI) throws RemoteException,
     ServiceException, Exception {
         Vector result = null;
         Document resultDoc = null;
             //get a call object
             Call call = getCall();
             call.setSOAPActionURI(soapActionURI);
             //create the soap body to be placed in the
             //outgoing soap message.
             SOAPBodyElement sbeRequest =
                new SOAPBodyElement(soapBody.getDocumentElement());
             //go ahead and set the name and namespace on the soap body
             //not sure if this is that important.
             sbeRequest.setName(name);
             sbeRequest.setNamespaceURI(NAMESPACE_URI);
             //call the web service, on axis-message style it
             //comes back as a vector of soabodyelements.
             result = (Vector)call.invoke(new Object[] { sbeRequest });
             SOAPBodyElement sbe = null;
             if (result.size() > 0) {
                sbe = (SOAPBodyElement)result.get(0);
                resultDoc = sbe.getAsDocument();
                return resultDoc;
             }
             return null;
     }


     /**
      * Identify - Queryies based on OAI-Identify verb, identifying the repository.
      * @return XML DOM of an OAI-PMH for the Identify.
      */
     public Document identify() throws RegistryException {
        Document doc = null;
        Document resultDoc = null;

        try {
           logger.debug("identify() - creating full soap element.");
           doc = DomHelper.newDocument();
           Element root = doc.createElementNS(NAMESPACE_URI, "Identify");
           doc.appendChild(root);
        } catch (ParserConfigurationException pce) {
           throw new RegistryException(pce);
        }

        try {
           return callService(doc,"Identify","Identify");
        } catch (RemoteException re) {
           throw new RegistryException(re);
        } catch (ServiceException se) {
           throw new RegistryException(se);
        } catch (Exception e) {
           throw new RegistryException(e);
        }
     }


     /**
      * ListRecords - OAI ListRecords query, the Registry server will default the
      * metadataPrefix to ivo_vor.
      * @return XML DOM of an OAI-PMH for the ListRecords.
      */
     public Document listRecords() throws RegistryException {
        return listRecords(null,null,null,null);
     }

     /**
      * ListRecords - OAI ListRecords query based on a fromDate, the recods
      * changed from that date The Registry server will default the
      * metadataPrefix to ivo_vor
      * @param fromDate - A from date for returning Resources from a date till now.
      * @return XML DOM of an OAI-PMH for the ListRecords.
      */
     public Document listRecords(Date fromDate) throws RegistryException {
        return listRecords(null,fromDate,null,null);
     }

     /**
      * ListRecords - OAI ListRecords query based on a fromDate, the recods
      * changed from that date The Registry server will default the
      * metadataPrefix to ivo_vor
      * @param fromDate - A from date for returning Resources from a date till now.
      * @return XML DOM of an OAI-PMH for the ListRecords.
      */
     public Document listRecords(String resumptionToken) throws RegistryException {
        return listRecords(null,null,null,resumptionToken);
     }


     /**
      * ListRecords - OAI ListRecords query. This will be the most used OAI verb for harvesting.
      * @param metadataPrefix - oai metadataPrefix string normally ivo_vor or oai_dc.
      * A null will let the Registry server default it to ivo_vor.
      * @param fromDate - A from date for returning Resources from a date till now.
      * @param untilDate - Returning resources from the beginning till a date.
      * @return XML DOM of an OAI-PMH for the ListRecords.
      */
     public Document listRecords(String metadataPrefix, Date fromDate, Date untilDate, String resumptionToken) throws
     RegistryException {
        Document doc = null;
        Document resultDoc = null;

        try {
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

           logger
                 .debug("listRecords(String, Date, Date) - creating full soap element.");
           doc = DomHelper.newDocument();
           Element root = doc.createElementNS(NAMESPACE_URI, "ListRecords");
           doc.appendChild(root);
           Element temp = null;
           if(resumptionToken != null && resumptionToken.trim().length() > 0) {
               temp = doc.createElement("resumptionToken");
               temp.appendChild(doc.createTextNode(resumptionToken));
               root.appendChild(temp);
           } else {
               if(metadataPrefix != null && metadataPrefix.trim().length() > 0) {
                   temp = doc.createElement("metadataPrefix");
                   temp.appendChild(doc.createTextNode(metadataPrefix));
                   root.appendChild(temp);
               } else {
                   temp = doc.createElement("metadataPrefix");
                   temp.appendChild(doc.createTextNode(OAI_METADATA_PREFIX));
                   root.appendChild(temp);
               }
               if(fromDate != null) {
                   temp = doc.createElement("from");
                   temp.appendChild(doc.createTextNode(sdf.format(fromDate)));
                   root.appendChild(temp);
               }
               if(untilDate != null) {
                   temp = doc.createElement("until");
                   temp.appendChild(doc.createTextNode(sdf.format(untilDate)));
                   root.appendChild(temp);
               }
           }//else
        } catch (ParserConfigurationException pce) {
           throw new RegistryException(pce);
        }

        try {
            return callService(doc,"ListRecords","ListRecords");
        } catch (RemoteException re) {
           throw new RegistryException(re);
        } catch (ServiceException se) {
           throw new RegistryException(se);
        } catch (Exception e) {
           throw new RegistryException(e);
        }
     }

     /**
      * ListMetadataFormats - OAI ListMetadtaFormats verb call.  With an optional
      * identifier string to list the metadata formats for a particular id.
      * @return XML DOM of an OAI-PMH for the ListMetadataFormats.
      */
     public Document listMetadataFormats(String identifier) throws RegistryException {
        Document doc = null;
        Document resultDoc = null;

        try {

           logger
                .debug("listMetadataFormats(String) - creating full soap element.");
           doc = DomHelper.newDocument();
           Element root = doc.createElementNS(NAMESPACE_URI, "ListMetadataFormats");
           doc.appendChild(root);
           if(identifier != null || identifier.trim().length() > 0) {
               Element temp = doc.createElement("identifier");
               temp.appendChild(doc.createTextNode(identifier));
               root.appendChild(temp);
           }
        } catch (ParserConfigurationException pce) {
           throw new RegistryException(pce);
        }

        try {
            return callService(doc,"ListMetadataFormats","ListMetadataFormats");
        } catch (RemoteException re) {
           throw new RegistryException(re);
        } catch (ServiceException se) {
           throw new RegistryException(se);
        } catch (Exception e) {
           throw new RegistryException(e);
        }
     }

     /**
      * OAI - Get a specific record from OAI given an identifier.
      * Defaults the metadataPrefix to ivo_vor.
      * @param identifier for a particular record ex: ivo_vor://astrogrid.org/Registry
      *
      * @return XML DOM of an OAI-PMH for the GetRecord.
      */
     public Document getRecord(String identifier) throws RegistryException {
        return getRecord(identifier,null);
     }

     /**
      * OAI - Get a specefic record for an identifier and metadataprefix
      * @param identifier for a particular record ex: ivo_vor://astrogrid.org/Registry
      * @param metadataPrefix is the oai prefix/id to be used, currently only ivo_vor and oai_dc.
      * @return XML DOM of an OAI-PMH for the GetRecord.
      */
     public Document getRecord(String identifier, String metadataPrefix) throws RegistryException {
        Document doc = null;
        Document resultDoc = null;

        try {
           logger
                .debug("getRecord(String, String) - creating full soap element.");
           doc = DomHelper.newDocument();
           Element root = doc.createElementNS(NAMESPACE_URI, "GetRecord");
           doc.appendChild(root);
           Element temp = null;
           if(identifier == null || identifier.trim().length() <= 0)
              throw new RegistryException("Error From Client: No identifier found for calling GetRecord");

           temp = doc.createElement("identifier");
           temp.appendChild(doc.createTextNode(identifier));
           root.appendChild(temp);

           if(metadataPrefix != null && metadataPrefix.trim().length() > 0) {
              temp = doc.createElement("metadataPrefix");
              temp.appendChild(doc.createTextNode(metadataPrefix));
              root.appendChild(temp);
           }

        } catch (ParserConfigurationException pce) {
           throw new RegistryException(pce);
        }

        try {
            return callService(doc,"GetRecord","GetRecord");
        } catch (RemoteException re) {
           throw new RegistryException(re);
        } catch (ServiceException se) {
           throw new RegistryException(se);
        } catch (Exception e) {
           throw new RegistryException(e);
        }
     }

     /**
      * OAI - ListIdentifiers call, similiar to ListRecords but only returns
      * the identifiers (unique ids) for the records. Defaults the metadataPrefix to
      * ivo_vor.
      *
      * @return XML DOM of an OAI-PMH for the ListIdentifiers.
      */
     public Document listIdentifiers() throws RegistryException {
        return listIdentifiers(null,null,null,null);
     }

     /**
      * OAI - ListIdentifiers call, similiar to ListRecords but only returns
      * the identifiers (unique ids) for the records. Defaults the metadataPrefix to
      * ivo_vor.
      *
      * @return XML DOM of an OAI-PMH for the ListIdentifiers.
      */
     public Document listIdentifiers(String resumptionToken) throws RegistryException {
        return listIdentifiers(null,null,null,resumptionToken);
     }


     /**
      * OAI - ListIdentifiers call, similiar to ListRecords but only returns
      * the identifiers (unique ids) for the records. Defaults the metadataPrefix to
      * ivo_vor.
      * @param metadataPrefix the oai prefix; normally ivo_vor.  Also available is oai_dc.
      * @param fromDate - A from date for returning Resources from a date till now.
      * @param untilDate - Returning resources from the beginning till a date.
      * @return XML DOM of an OAI-PMH for the ListIdentifiers.
      */
     public Document listIdentifiers(String metadataPrefix, Date fromDate, Date untilDate, String resumptionToken) throws
     RegistryException {
        Document doc = null;
        Document resultDoc = null;

        try {
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

           logger
                .debug("listIdentifiers(String, Date, Date) - creating full soap element.");
           doc = DomHelper.newDocument();
           Element root = doc.createElementNS(NAMESPACE_URI, "ListIdentifiers");
           doc.appendChild(root);
           Element temp = null;
           if(resumptionToken != null && resumptionToken.trim().length() > 0) {
               temp = doc.createElement("resumptionToken");
               temp.appendChild(doc.createTextNode(resumptionToken));
               root.appendChild(temp);
           }else {
               if(metadataPrefix != null && metadataPrefix.trim().length() > 0) {
                  temp = doc.createElement("metadataPrefix");
                  temp.appendChild(doc.createTextNode(metadataPrefix));
                  root.appendChild(temp);
               } else {
                   temp = doc.createElement("metadataPrefix");
                   temp.appendChild(doc.createTextNode(OAI_METADATA_PREFIX));
                   root.appendChild(temp);
               }
               if(fromDate != null) {
                  temp = doc.createElement("from");
                  temp.appendChild(doc.createTextNode(sdf.format(fromDate)));
                  root.appendChild(temp);
               }
               if(untilDate != null) {
                  temp = doc.createElement("until");
                  temp.appendChild(doc.createTextNode(sdf.format(untilDate)));
                  root.appendChild(temp);
               }
           }//else
        } catch (ParserConfigurationException pce) {
           throw new RegistryException(pce);
        }

        try {
            return callService(doc,"ListIdentifiers","ListIdentifiers");
        } catch (RemoteException re) {
           throw new RegistryException(re);
        } catch (ServiceException se) {
           throw new RegistryException(se);
        } catch (Exception e) {
           throw new RegistryException(e);
        }
     }
}
