/*
 * Copyright 2018 University of Cambridge.
 *
 * This class is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.astrogrid.registry.server.soap;

import org.astrogrid.registry.server.query.v1_0.RegistryQueryService;
import org.astrogrid.registry.server.query.v1_0.XQueryException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of the RegistrySearchSoap interface as a SOAP service.
 * This is message-base SOAP: the request and response messages are handled
 * as DOM trees, each embedded in a SoapEnvelope object. The {
 * @link #dispatch(org.astrogrid.registry.server.soap.SoapEnvelope, 
 * org.astrogrid.registry.server.soap.SoapEnvelope)} method determines the 
 * operation from the request message and dispatches to the appropriate
 * method. This interface works withe the registry's SoapServlet class.
 * 
 * @author Guy Rixon
 */
public class RegistrySearchSoap {
  
  /**
   * The namespace of the operation elements in the SOAP messages.
   */
  public static final String RS_NS = "http://www.ivoa.net/wsdl/RegistrySearch/v1.0";
  
  /**
   * The namespace of the returned registrations.
   */
  public static final String RI_NS = "http://www.ivoa.net/xml/RegistryInterface/v1.0";
  
  /**
   * Collection to be queried in the XML database.
   */
  public static final String COLLECTION_NAME = "astrogridv1_0";
  
  /**
   * Executes the request if it is correctly formed. Any errors, including
   * unrecognised operations, are signalled by returning a SOAP fault
   * in the response message.
   * 
   * @param request SOAP request message.
   * @param response  SOAP response method.
   */
  public static void dispatch(
      SoapEnvelope request,
      SoapEnvelope response
  ) {
    String opName = request.getOperationLocalName();
    if (opName.equals("XQuerySearch")) {
        xQuerySearch(request, response);
      } else if (opName.equals("GetResource")) {
        getResource(request, response);
      } else {
        response.setFault(false, "Requested operation is unexpected: " + opName);
      }
  }

  /**
   * Performs an XQuery search on the registry.
   * 
   * @param request SOAP request message.
   * @param response  SOAP response method.
   */
  protected static void xQuerySearch(
      SoapEnvelope request,
      SoapEnvelope response
  ) {
    String xql;
    try {
      Element requestOperation = request.getOperation();
      Element xquery = request.findChildElement(requestOperation, "xquery");
      xql = xquery.getTextContent().trim();
    } catch (Exception ex) {
      response.setFault(false, ex.getMessage());
      return;
    }
    
    try {
      Element responseOperation = response.setOperation(RS_NS, "rs:XQuerySearchResponse");
      RegistryQueryService server = new RegistryQueryService();
      for (Document d : server.xQueryToDocuments(xql)) {
        response.addExternalChild(responseOperation, d.getDocumentElement());
      }
    } catch (XQueryException ex) {
      response.setFault(true, ex.getMessage());
    }
  }

  /**
   * Raises the registration for a single resource.
   * 
   * @param request SOAP request message.
   * @param response  SOAP response method.
   */
  public static void getResource(
      SoapEnvelope request,
      SoapEnvelope response
  ) {
    String ivorn;
    try {
      Element requestOperation = request.getOperation();
      Element identifier = request.findChildElement(requestOperation, "identifier");
      ivorn = identifier.getTextContent().trim();
    } catch (Exception ex) {
      response.setFault(false, ex.toString());
      return;
    }
    
    try {
      
      // Query the database. Note that the database uses an internal
      // system of identifiers for resources based on their IVORNs.
      RegistryQueryService server = new RegistryQueryService();
      Document resourceDocument = server.getResourceToDocument(ivorn);
      if (resourceDocument == null) {
        response.setFault(false, ivorn + " is not registered here");
        return;
      }
      
      // Extract useable DOM from the results. The document element is
      // a wrapper containing the actual registrion, which we extract.
      Element agrResource = resourceDocument.getDocumentElement();
      Element riResource = response.findChildElement(agrResource, RI_NS, "Resource");
      
      // Transcribe the results from the intermediate document into the response document.
      Element responseOperation = response.setOperation(RS_NS, "rs:ResolveResponse");
      response.addExternalChild(responseOperation, riResource);
      
    } catch (Exception ex) {
      response.setFault(true, ex.toString());
    }
  }
  
}
