
/**
*Copyright (c) 2000-2002 OCLC Online Computer Library Center,
*Inc. and other contributors. All rights reserved.  The contents of this file, as updated
*from time to time by the OCLC Office of Research, are subject to OCLC Research
*Public License Version 2.0 (the "License"); you may not use this file except in
*compliance with the License. You may obtain a current copy of the License at
*http://purl.oclc.org/oclc/research/ORPL/.  Software distributed under the License is
*distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
*or implied. See the License for the specific language governing rights and limitations
*under the License.  This software consists of voluntary contributions made by many
*individuals on behalf of OCLC Research. For more information on OCLC Research,
*please see http://www.oclc.org/oclc/research/.
*
*The Original Code is OAIHandler.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/


package ORG.oclc.oai.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.IllegalAccessException;
import java.lang.NoSuchMethodException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Enumeration;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.*;

// import org.apache.log4j.BasicConfigurator;
// import org.apache.log4j.Logger;

/**
 * OAIHandler is the primary Servlet for OAICat.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class OAIHandler extends HttpServlet {
    private static final String VERSION = "1.5.26";
    private Transformer transformer = null;
    private static boolean debug = false;
    private boolean serviceUnavailable = false;
    private boolean monitor = false;
    private boolean forceRender = false;
    private HashMap attributes = null;
    private HashMap serverVerbs = null;
    private HashMap extensionVerbs = null;
    private String extensionPath = null;

//     private static Logger logger = Logger.getLogger(OAIHandler.class);
//     static {
//         BasicConfigurator.configure();
//     }
    
    /**
     * Get the VERSION number
     */
    public static String getVERSION() { return VERSION; }

    /**
     * init is called one time when the Servlet is loaded. This is the
     * place where one-time initialization is done. Specifically, we
     * load the properties file for this application, and create the
     * AbstractCatalog object for subsequent use.
     *
     * @param config servlet configuration information
     * @exception ServletException there was a problem with initialization
     */
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	String fileName =
	    config.getServletContext().getInitParameter("properties");
    System.out.println("inside init trying to get version");
    String versionNumberTemp =
        config.getInitParameter("version");
    
    System.out.println("the version number in oaihandler = " + versionNumberTemp);
	try {
	    ServletContext context = getServletContext();
	    attributes = new HashMap();
	    Enumeration attrNames = context.getAttributeNames();
	    while (attrNames.hasMoreElements()) {
		String attrName = (String)attrNames.nextElement();
		attributes.put(attrName, context.getAttribute(attrName));
	    }
	    FileInputStream in = new FileInputStream(fileName);
	    Properties properties = new Properties();
	    properties.load(in);
	    System.out.println("is there a externalURL in the properties = " + properties.containsKey("OAIHandler.externalURL"));
	    if(properties.containsKey("OAIHandler.externalURL")) {
	    	System.out.println("setting baseURL property to = " + properties.getProperty("OAIHandler.externalURL") + "/OAIHandlerv" + versionNumberTemp.replaceAll(".", "_"));
	    	properties.setProperty("OAIHandler.baseURL", properties.getProperty("OAIHandler.externalURL") + "/OAIHandlerv" + versionNumberTemp.replaceAll(".", "_"));
	    }
	    attributes.put("OAIHandler.properties", properties);
            String temp = properties.getProperty("OAIHandler.debug");
            if ("true".equals(temp)) debug = true;
            extensionPath = properties.getProperty("OAIHandler.extensionPath", "/extension");
            if (debug)
                System.out.println("OAIHandler.init: fileName=" + fileName);
            if (properties.getProperty("OAIHandler.serviceUnavailable") != null) {
                this.serviceUnavailable = true;
            } else {
                attributes.put("OAIHandler.version", VERSION);
                AbstractCatalog abstractCatalog = AbstractCatalog.factory(properties);
                attributes.put("OAIHandler.catalog", abstractCatalog);
            }
            if (properties.getProperty("OAIHandler.monitor") != null) {
                this.monitor = true;
            }
            if ("true".equals(properties.getProperty("OAIHandler.forceRender"))) {
                this.forceRender = true;
            }
            String xsltName = properties.getProperty("OAIHandler.styleSheet");
            String appBase = properties.getProperty("OAIHandler.appBase");
            if (appBase == null) appBase = "webapps";
            if (xsltName != null
                && ("true".equalsIgnoreCase(properties.getProperty("OAIHandler.renderForOldBrowsers"))
                    || this.forceRender)) {
                StreamSource xslSource = new StreamSource(new FileInputStream(appBase + "/"
                                                                              + xsltName));
                TransformerFactory tFactory = TransformerFactory.newInstance();
                transformer = tFactory.newTransformer(xslSource);
            }

	    serverVerbs = ServerVerb.getVerbs(properties);
            extensionVerbs = ServerVerb.getExtensionVerbs(properties);
	} catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        } catch (ClassNotFoundException e) {
	    e.printStackTrace();
            throw new ServletException(e.getMessage());
        } catch (IllegalArgumentException e) {
	    e.printStackTrace();
            throw new ServletException(e.getMessage());
	} catch (IOException e) {
	    e.printStackTrace();
            throw new ServletException(e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
	}
    }

    /**
     * Peform the http GET action. Note that POST is shunted to here as well.
     * The verb widget is taken from the request and used to invoke an
     * OAIVerb object of the corresponding kind to do the actual work of the verb.
     *
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception ServletException a servlet error occurred
     * @exception IOException an I/O error occurred
     */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
//         try {
            request.setCharacterEncoding("UTF-8");
//         } catch (UnsupportedEncodingException e) {
//             e.printStackTrace();
//             throw new IOException(e.getMessage());
//         }
        Date then = null;
        if (monitor) then = new Date();
	if (debug) {
	    Enumeration headerNames = request.getHeaderNames();
	    System.out.println("OAIHandler.doGet: ");
	    while (headerNames.hasMoreElements()) {
		String headerName = (String)headerNames.nextElement();
		System.out.print(headerName);
		System.out.print(": ");
		System.out.println(request.getHeader(headerName));
	    }
	}
        if (serviceUnavailable) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                               "Sorry. This server is down for maintenance");
        } else {
            try {
                String userAgent = request.getHeader("User-Agent");
                if (userAgent == null) {
                    userAgent = "";
                } else {
                    userAgent = userAgent.toLowerCase();
                }
                Transformer serverTransformer = null;
                if (transformer != null) {
                    
                    // return HTML if the client is an old browser
                    if (this.forceRender
			|| userAgent.indexOf("opera") != -1
			|| (userAgent.startsWith("mozilla")
			    && userAgent.indexOf("msie 6") == -1
                        /* && userAgent.indexOf("netscape/7") == -1 */)) {
                        serverTransformer = transformer;
                    }
                }
		String result = getResult(attributes, request, response, serverTransformer, serverVerbs, extensionVerbs, extensionPath);
//                 logger.debug("result=" + result);
                
//                 if (serverTransformer) { // render on the server
//                     response.setContentType("text/html; charset=UTF-8");
//                     StringReader stringReader = new StringReader(getResult(request));
//                     StreamSource streamSource = new StreamSource(stringReader);
//                     StringWriter stringWriter = new StringWriter();
//                     transformer.transform(streamSource, new StreamResult(stringWriter));
//                     result = stringWriter.toString();
//                 } else { // render on the client
//                     response.setContentType("text/xml; charset=UTF-8");
//                     result = getResult(request);
//                 }
        
                Writer out = getWriter(request, response);
                out.write(result);
                out.close();
            } catch (FileNotFoundException e) {
                if (debug) {
                    e.printStackTrace();
                    System.out.println("SC_NOT_FOUND: " + e.getMessage());
                }
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } catch (TransformerException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (OAIInternalServerError e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (Throwable e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        if (monitor) {
            System.out.println(request.getHeader("Accept-Encoding") + ": " + ((new Date()).getTime()-then.getTime()) + "ms: "
                               + request.getQueryString());
        }
    }

    public static String getResult(HashMap attributes,
				   HttpServletRequest request,
				   HttpServletResponse response,
				   Transformer serverTransformer,
				   HashMap serverVerbs,
				   HashMap extensionVerbs,
				   String extensionPath)
        throws Throwable {
        try {
            boolean isExtensionVerb = extensionPath.equals(request.getPathInfo());
            String verb = request.getParameter("verb");
            if (debug) {
                System.out.println("OAIHandler.getResult: verb=>" + verb + "<");
            }
            String result;
            Class verbClass = null;
            if (isExtensionVerb) {
                verbClass = (Class)extensionVerbs.get(verb);
            } else {
                verbClass = (Class)serverVerbs.get(verb);
            }
            if (verbClass == null) {
                if (debug) {
                    System.out.println("verb not found among:");
                    java.util.Iterator keySet = null;
                    if (isExtensionVerb) {
                        keySet = extensionVerbs.keySet().iterator();
                    } else {
                        keySet = serverVerbs.keySet().iterator();
                    }
                    while(keySet.hasNext()) {
                        System.out.println(keySet.next());
                    }
                }
                result=BadVerb.construct(attributes, request, response, serverTransformer);
            } else {
                Method construct = verbClass.getMethod("construct",
                                                       new Class[] {HashMap.class,
                                                                    HttpServletRequest.class,
                                                                    HttpServletResponse.class,
                                                                    Transformer.class});
                try {
                    result = (String)construct.invoke(null,
                                                  new Object[] {attributes,
                                                                request,
                                                                response,
                                                                serverTransformer});
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
            if (debug) {
                System.out.println(result);
            }
            return result;
        } catch (NoSuchMethodException e) {
            throw new OAIInternalServerError(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new OAIInternalServerError(e.getMessage());
        }
    }

    /**
     * Get a response Writer depending on acceptable encodings
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception IOException an I/O error occurred
     */
    public static Writer getWriter(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
	Writer out;
	response.setContentType("UTF-8");
	String encodings = request.getHeader("Accept-Encoding");
        if (debug) {
            System.out.println("encodings=" + encodings);
        }
	if (encodings != null && encodings.indexOf("gzip") != -1) {
//  	    System.out.println("using gzip encoding");
//             logger.debug("using gzip encoding");
	    response.setHeader("Content-Encoding", "gzip");
	    out = new OutputStreamWriter(new GZIPOutputStream(response.getOutputStream()),
					 "UTF-8");
// 	} else if (encodings != null && encodings.indexOf("compress") != -1) {
// //  	    System.out.println("using compress encoding");
// 	    response.setHeader("Content-Encoding", "compress");
// 	    ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
// 	    zos.putNextEntry(new ZipEntry("dummy name"));
// 	    out = new OutputStreamWriter(zos, "UTF-8");
	} else if (encodings != null && encodings.indexOf("deflate") != -1) {
//  	    System.out.println("using deflate encoding");
//             logger.debug("using deflate encoding");
	    response.setHeader("Content-Encoding", "deflate");
	    out = new OutputStreamWriter(new DeflaterOutputStream(response.getOutputStream()),
					 "UTF-8");
	} else {
//             logger.debug("using no encoding");
	    out = response.getWriter();
	}
	return out;
    }

    /**
     * Peform a POST action. Actually this gets shunted to GET
     *
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception ServletException a servlet error occurred
     * @exception IOException an I/O error occurred
     */
    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {
        doGet(request, response);
    }
}
