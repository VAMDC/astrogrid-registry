package org.astrogrid.registry.client.admin;


import java.net.URL; 
import java.util.HashMap;
import java.util.Vector; 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException; 
import org.apache.axis.client.Call; 
import org.apache.axis.client.Service; 
import org.apache.axis.message.SOAPBodyElement; 
import org.apache.axis.utils.XMLUtils; 
import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;

import javax.xml.rpc.ServiceException;
import org.xml.sax.SAXException;
import java.rmi.RemoteException;

import org.astrogrid.registry.RegistryException;

import java.io.File;
import java.io.IOException;
import org.astrogrid.util.DomHelper;
import org.astrogrid.config.Config;

import org.astrogrid.registry.common.XSLHelper;

/**
 * Class Name: RegistryAdminService
 * Description: This class represents the client webservice delegate to the Administration piece of the
 * web service.  It uses the same Interface as the server side webservice so they both implement and handle
 * the same web service method names.  The primary goal of this class is to establish a Axis-Message style
 * webservice call to the server.
 * 
 * @see org.astrogrid.registry.common.RegistryAdminInterface
 * @author Kevin Benson
 *
 * 
 */
public interface RegistryAdminService { 


   /**
    * Takes an XML Document to send to the update server side web service call.  Establishes
    * a service and a call to the web service and call it's update method, using an Axis-Message
    * style.  Then updates this document onto the registry.
    * @param query Document a XML document dom object to be updated on the registry.
    * @return the document updated on the registry is returned.
    * @author Kevin Benson
    * 
    */   
   public Document update(Document update) throws RegistryException;
   
   public Document updateFromFile(File fi) throws RegistryException;
   
   public Document updateFromURL(URL location) throws RegistryException;
   
   public Document updateFromString(String voresources) throws RegistryException;
   
   public String getCurrentStatus();
   
   public Document getStatus();
   
   
} 