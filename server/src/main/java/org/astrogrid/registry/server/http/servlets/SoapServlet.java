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
package org.astrogrid.registry.server.http.servlets;

import org.astrogrid.config.Config;
import org.astrogrid.config.SimpleConfig;
import org.astrogrid.registry.server.soap.RegistrySearchSoap;
import org.astrogrid.registry.server.soap.SoapEnvelope;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to implement the search functions as a SOAP service. This class
 connects the inbound and outbound messages, represented as SoapEnvelope
 objects, with the input and output stream; it also deals with the WSDL.
 Actual implementation of the service is delegated to the RegistrySearchSoap
 class.
 <p>
 * To make the WSDL work, the URL for the home page of the registry must be
 * set as the configuration item org.vamdc.registry.url (e.g. it must be present
 * as a system property of that name).
 * 
 * @author Guy Rixon
 */
public class SoapServlet extends HttpServlet {
  
  /**
   * The URL for the home page of the web application.
   * This is used to form the correct service address in the WSDL.
   */
  protected String registryUrl = null;
  
  /**
   * Configures the service URL for the WSDL.
   * 
   * @throws javax.servlet.ServletException If the superclass throws.
   */
  @Override
  public void init() throws ServletException {
    super.init();
    Config config = SimpleConfig.getSingleton();
    registryUrl = config.getString("org.vamdc.registry.url");
  }

  /**
   * Handles a GET request for the WSDL. All other GET requests are refused.
   * 
   * @param request HTTP request
   * @param response HTTP response
   * @throws IOException If the WSDL cannot be written out.
   * @throws ServletException If the request is not for the WSDL.
   */
  @Override
  public void doGet(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException, ServletException {
    String query = request.getQueryString();
    if ("wsdl".equals(query) || "WSDL".equals(query)) {
      response.setContentType("text/xml");
      emitWsdl(registryUrl, response.getWriter());
    } else {
      throw new ServletException("GET is allowed only for ?wsdl queries");
    }
  }

  /**
   * Handles a POSTed request to a SOAP endpoint. As a favour to confused
   * clients, also handles POSTed requests for the WSDL.
   * 
   * @param request HTTP request.
   * @param response HTTP response.
   * @throws IOException If the results cannot be written out.
   * @throws ServletException If a problem occurs that cannot be signalled as a SOAP fault.
   */
  @Override
  public void doPost(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException, ServletException {
    String query = request.getQueryString();
    if ("wsdl".equals(query) || "WSDL".equals(query)) {
      response.setContentType("text/xml");
      emitWsdl(registryUrl, response.getWriter());
    } else {
      dispatch(request.getInputStream(), response.getWriter());
    }
  }

  /**
   * Handles an exchange of SOAP messages. Parses the given stream (in normal
 operation, the body of the HTTP request) to construct the inbound
 envelope. Creates an empty envelope for the response. Calls the 
 RegistrySearchSoap class to get the request actioned and the response message
 built. Serializes the response envelope to the given stream.
   * 
   * @param reader Source of inbound message.
   * @param writer Target for outbound message.
   * @throws IOException If the results cannot be written out.
   * @throws ServletException If a problem occurs that cannot be signalled as a SOAP fault.
   */
  protected void dispatch(
      InputStream reader,
      Writer      writer
  ) throws ServletException, IOException {
    try {
      SoapEnvelope request = new SoapEnvelope(reader);
      SoapEnvelope response = new SoapEnvelope();

      // Invoke the implementing method for the operation.
      RegistrySearchSoap.dispatch(request, response);

      // Serialize the response to the client.
      response.print(writer);

    } catch (IOException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  /**
   * Forms the WSDL for the service, with the correct URL, and writes it out
   * to the given stream.
   * 
   * @param address The URL for the home page of the registry application.
   * @param out The stream to receive the WSDL.
   * @throws IOException If the WSDL cannot be written to the stream.
   */
  private void emitWsdl(String address, PrintWriter out) throws IOException {
    InputStream in = this.getClass().getResourceAsStream("/wsdl/reg.wsdl");
    int c;
    while ((c = in.read()) != -1) {
      if (c == '%') {
        out.write(address);
      } else {
        out.write(c);
      }
    }
  }  

}
