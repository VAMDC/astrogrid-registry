package org.astrogrid.registry.server.query;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.StringReader;
import java.io.IOException;

import org.astrogrid.registry.server.SOAPFaultException;
import org.codehaus.xfire.util.STAXUtils;


import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.stream.*;

import org.astrogrid.util.DomHelper;
import org.astrogrid.config.Config;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class: OAIService
 * Description: The OAIService holds the web service interface methods for OAI (the open archives initiative).  OAI
 * is the standard interface for harvesting.  This method is actually just a wrapper for the OAI calls to the HTTP-GET
 * verison.
 * @see http://www.openarchives.org
 * 
 * @author Kevin Benson
 *
 */
public abstract class DefaultOAIService {
    
    /**
     * Logging variable for writing information to the logs
     */
     private static final Log log = LogFactory.getLog(DefaultOAIService.class);
     
     public String oaiWSDLNS = null;
     
     public String contractVersion = null;
     
     /**
      * conf - Config variable to access the configuration for the server normally
      * jndi to a config file.
      * @see org.astrogrid.config.Config
      */   
     public static Config conf = null;
     
     private static String oaiServletURL = null;
     
     /**
      * Static to be used on the initiatian of this class for the config
      */   
     static {
        if(conf == null) {
           conf = org.astrogrid.config.SimpleConfig.getSingleton();            
        }
     }
     
     public DefaultOAIService(String oaiWSDLNS, String contractVersion) {
         this.oaiWSDLNS = oaiWSDLNS;
         this.contractVersion = contractVersion;
         oaiServletURL = conf.getString("reg.amend.oaipublish." + contractVersion);
     }

    /**
     * Used by all the OAI required method interfaces to get the OAI
     * conformed Resources from a URL.  This URL by default with Astrogrid is a servlet to query
     * the eXist database and put the XML in a OAI form.  The XML DOM returned
     * are all the Resources managed by this Registry. Could be set in the properties if they have
     * another OAI url to use.  It can use any OAI via WebBrowser, again by default it is a
     * Astrogrid servlet using this 3rd party tool known as OAICat.
     * 
     * @param oaiServlet a url string 
     * @return OAI conformed DOM object of all the Resourced managed by this Registry.
     */
    private Document queryOAI(String oaiServlet) {
       try {
         log.info("the oaiservlet url = '" + oaiServlet + "'");
         //Document doc = DomHelper.newDocument(new URL(oaiServlet));
         //DomHelper.DocumentToStream(doc,out);
         return DomHelper.newDocument(new URL(oaiServlet));
        }catch(MalformedURLException me) {
         me.printStackTrace();
        }catch(ParserConfigurationException pce) {
          pce.printStackTrace();
        }catch(SAXException sax) {
          sax.printStackTrace();
        }catch(IOException ioe) {
          ioe.printStackTrace();
        }
        return null;
    }
        
    /**
     * OAI-Identify conformed Web service method.
     * 
     * @param query actually this OAI mehtod requires nothing. 
     * @return XML DOM object conforming to the OAI Identify.
     * @link http://www.openarchives.org 
     */
    public XMLStreamReader Identify(Document query) {
       String oaiServlet = oaiServletURL + "?verb=Identify";
       Document resultDoc = queryOAI(oaiServlet);
       return addWrapperElement("IdentifyResponse",resultDoc);
    }
    
    /**
     * Method: addWrapperElement
     * Description: Simply wraps some XML around the result for the Web Service.
     * Typically the internface name which is required by the soap body/wsdl.
     * @param wrapNodeString XML string to wrap around the results.
     * @param resultDoc the DOM Document containing the OAI XML.
     * @return stream reader created from the DOM to be returned to the client.
     */
    private XMLStreamReader addWrapperElement(String wrapNodeString,
                                       Document resultDoc) {
        if(resultDoc.getElementsByTagNameNS("*","error").getLength() > 0) {
            Document resDoc = (new SOAPFaultException("Server Error: " + "OAI Error",DomHelper.DocumentToString(resultDoc), oaiWSDLNS, SOAPFaultException.HARVESTSOAP_TYPE)).getFaultDocument();
            return STAXUtils.createXMLStreamReader(new StringReader(DomHelper.DocumentToString(resDoc)));            
        }
        Element currentRoot = resultDoc.getDocumentElement();
        Element root = resultDoc.createElementNS(oaiWSDLNS,"oairegws:" + wrapNodeString);
        root.appendChild(currentRoot);
        resultDoc.appendChild(root);
        return STAXUtils.createXMLStreamReader(new StringReader(DomHelper.DocumentToString(resultDoc)));
    }
    
    /**
     * OAI-ListMetadataFormats conformed Web service method.
     * 
     * @param query contains an optional identifier string. 
     * @return XML DOM object conforming to the OAI ListMetadataFormats.
     * @link http://www.openarchives.org 
     */
    public XMLStreamReader ListMetadataFormats(Document query) {
       String oaiServlet = oaiServletURL + "?verb=ListMetadataFormats";       
       NodeList nl = null;
       if( (nl = query.getElementsByTagNameNS("*","identifier")).getLength() > 0  )
            oaiServlet += "&identifier=" + nl.item(0).getFirstChild().getNodeValue(); 
       Document resultDoc = queryOAI(oaiServlet);
       return addWrapperElement("ListMetadataFormatsResponse",resultDoc);
    }
    
    /**
     * OAI-ListSets conformed Web service method. Currently not implemented.
     * 
     * @param query 
     * @return XML DOM object conforming to the OAI OAI-ListSets.
     * @link http://www.openarchives.org 
     */   
    public XMLStreamReader ListSets(Document query) {
        String oaiServlet = oaiServletURL + "?verb=ListSets";
        Document resultDoc = queryOAI(oaiServlet);
        return addWrapperElement("ListSetsResponse",resultDoc);
    }
    
    /**
     * OAI-ResumeListSets conformed Web service method. Currently not implemented.
     * 
     * @param query 
     * @return XML DOM object conforming to the OAI OAI-ResumeListSets.
     * @link http://www.openarchives.org 
     */   
    public XMLStreamReader ResumeListSets(Document query) {
      Document resultDoc = (new SOAPFaultException("Server Error: " + "Resume List Sets not supported","Resume List Sets not supported",oaiWSDLNS, SOAPFaultException.HARVESTSOAP_TYPE)).getFaultDocument();
      return STAXUtils.createXMLStreamReader(new StringReader(DomHelper.DocumentToString(resultDoc)));
    }
    
    /**
     * OAI-GetRecord conformed Web service method.
     * 
     * @param query contains an identifier string and metadataPrefix. The prefix
     * is defaulted to the standard registry ivo_vor if not given. 
     * @return XML DOM object conforming to the OAI GetRecord.
     * @link http://www.openarchives.org 
     */   
    public XMLStreamReader GetRecord(Document query) {
        String oaiServlet = oaiServletURL + "?verb=GetRecord";       
        NodeList nl = null;
        Document resultDoc;
        if( (nl = query.getElementsByTagNameNS("*","identifier")).getLength() > 0  ) 
           oaiServlet += "&identifier=" + nl.item(0).getFirstChild().getNodeValue();
        else {
        	 resultDoc = (new SOAPFaultException("Server Error: " + "No Identifier given","No Identifier given",oaiWSDLNS, SOAPFaultException.HARVESTSOAP_TYPE)).getFaultDocument();
             return STAXUtils.createXMLStreamReader(new StringReader(DomHelper.DocumentToString(resultDoc)));        	 
        }
        if( (nl = query.getElementsByTagNameNS("*","metadataPrefix")).getLength() > 0  )
         oaiServlet += "&metadataPrefix=" + nl.item(0).getFirstChild().getNodeValue();
        else
         oaiServlet += "&metadataPrefix=ivo_vor";
        resultDoc = queryOAI(oaiServlet);
        return addWrapperElement("GetRecordResponse",resultDoc);
    }
    
    /**
     * OAI-ListIdentifiers conformed Web service method.
     * 
     * @param query contains a metadataPrefix, and optional from and until 
     * @return XML DOM object conforming to the OAI ListIdentifiers.
     * @link http://www.openarchives.org 
     */   
    public XMLStreamReader ListIdentifiers(Document query) {
       String oaiServlet = oaiServletURL + "?verb=ListIdentifiers";
       NodeList nl = null;      
       if( (nl = query.getElementsByTagNameNS("*","metadataPrefix")).getLength() > 0  )
        oaiServlet += "&metadataPrefix=" + nl.item(0).getFirstChild().getNodeValue();
       else 
         oaiServlet += "&metadataPrefix=ivo_vor";        
       if( (nl = query.getElementsByTagNameNS("*","from")).getLength() > 0  ) 
         oaiServlet += "&from=" + nl.item(0).getFirstChild().getNodeValue();
       if( (nl = query.getElementsByTagNameNS("*","until")).getLength() > 0  )
         oaiServlet += "&until=" + nl.item(0).getFirstChild().getNodeValue();
       if( (nl = query.getElementsByTagNameNS("*","set")).getLength() > 0  )
           oaiServlet += "&set=" + nl.item(0).getFirstChild().getNodeValue();       
       if( (nl = query.getElementsByTagNameNS("*","resumtptionToken")).getLength() > 0  )
           oaiServlet += "&resumptionToken=" + nl.item(0).getFirstChild().getNodeValue();       
       Document resultDoc = queryOAI(oaiServlet);
       return addWrapperElement("ListIdentifiersResponse",resultDoc);
    
    }
    
    
    /**
     * OAI-ListRecords conformed Web service method.
     * 
     * @param query contains a metadataPrefix, optional from&until elements. 
     * @return XML DOM object conforming to the OAI ListRecords.
     * @link http://www.openarchives.org 
     */   
    public XMLStreamReader ListRecords(Document query) {
         String oaiServlet = oaiServletURL + "?verb=ListRecords";
         NodeList nl = null;        
         if( (nl = query.getElementsByTagNameNS("*","metadataPrefix")).getLength() > 0  )
          oaiServlet += "&metadataPrefix=" + nl.item(0).getFirstChild().getNodeValue();
         else
          oaiServlet += "&metadataPrefix=ivo_vor";
         if( (nl = query.getElementsByTagNameNS("*","from")).getLength() > 0  ) 
           oaiServlet += "&from=" + nl.item(0).getFirstChild().getNodeValue();
         if( (nl = query.getElementsByTagNameNS("*","until")).getLength() > 0  )
           oaiServlet += "&until=" + nl.item(0).getFirstChild().getNodeValue();
         if( (nl = query.getElementsByTagNameNS("*","set")).getLength() > 0  )
             oaiServlet += "&set=" + nl.item(0).getFirstChild().getNodeValue();         
         if( (nl = query.getElementsByTagNameNS("*","resumptionToken")).getLength() > 0  )
             oaiServlet += "&resumptionToken=" + nl.item(0).getFirstChild().getNodeValue();                
         Document resultDoc = queryOAI(oaiServlet);
         return addWrapperElement("ListRecordsResponse",resultDoc);
    }    
}