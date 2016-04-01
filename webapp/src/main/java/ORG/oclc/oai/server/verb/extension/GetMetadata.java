
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
*The Original Code is GetMetadata.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package ORG.oclc.oai.server.verb.extension;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
// import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.crosswalk.Crosswalks;
import ORG.oclc.oai.server.verb.*;

/**
 * This class represents a GetMetadata response on either the server or
 * the client.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class GetMetadata extends ServerVerb {
    private static final boolean debug = false;
    private static ArrayList validParamNames = new ArrayList();
    static {
	validParamNames.add("verb");
	validParamNames.add("identifier");
	validParamNames.add("metadataPrefix");
    }
    
    /**
     * Construct the xml response on the server-side.
     *
     * @param context the servlet context
     * @param request the servlet request
     * @return a String containing the XML response
     * @exception OAIBadRequestException an http 400 status error occurred
     * @exception OAINotFoundException an http 404 status error occurred
     * @exception OAIInternalServerError an http 500 status error occurred
     */
    public static String construct(HashMap context,
                                   HttpServletRequest request, HttpServletResponse response,
                                   Transformer serverTransformer)
        throws FileNotFoundException, TransformerException {
        Properties properties = (Properties)context.get("OAIHandler.properties");
	AbstractCatalog abstractCatalog =
	    (AbstractCatalog)context.get("OAIHandler.catalog");
	String baseURL = (String)properties.getProperty("OAIHandler.baseURL");
	if (baseURL == null) {
	    try {
		baseURL = request.getRequestURL().toString();
	    } catch (java.lang.NoSuchMethodError f) {
		baseURL = HttpUtils.getRequestURL(request).toString();
	    }
	}
        StringBuffer sb = new StringBuffer();
        String identifier = request.getParameter("identifier");
        String metadataPrefix = request.getParameter("metadataPrefix");

        if (debug) {
            System.out.println("GetMetadata.constructGetMetadata: identifier=" +
                               identifier);
            System.out.println("GetMetadata.constructGetMetadata: metadataPrefix="
                               + metadataPrefix);
        }
        Crosswalks crosswalks = abstractCatalog.getCrosswalks();
        sb.append("<?xml version=\"1.0\" encoding=\"");
        String encoding = crosswalks.getEncoding(metadataPrefix);
        if (encoding != null)
            sb.append(encoding);
        else
            sb.append("UTF-8");
        sb.append("\" ?>\n");
        
        String docType = crosswalks.getDocType(metadataPrefix);
        if (docType != null) {
            sb.append(docType);
            sb.append("\n");
        }
	try {
	    if (metadataPrefix == null || metadataPrefix.length() == 0
		|| identifier == null || identifier.length() == 0) {
                if (debug) System.out.println("Bad argument");
		throw new BadArgumentException();
	    }
	    else if (!crosswalks.containsValue(metadataPrefix)) {
                if (debug) System.out.println("crosswalk not present: " + metadataPrefix);
		throw new CannotDisseminateFormatException(metadataPrefix);
	    } else {
 		String metadata = abstractCatalog.getMetadata(identifier, metadataPrefix);
 		if (metadata != null) {
                    sb.append(metadata);
 		} else {
                    if (debug) System.out.println("ID does not exist");
 		    throw new IdDoesNotExistException(identifier);
 		}
	    }
	} catch (BadArgumentException e) {
            if (debug) e.printStackTrace();
            throw new FileNotFoundException();
	} catch (CannotDisseminateFormatException e) {
            if (debug) e.printStackTrace();
            throw new FileNotFoundException();
	} catch (IdDoesNotExistException e) {
            if (debug) e.printStackTrace();
            throw new FileNotFoundException();
	} catch (OAIInternalServerError e) {
            e.printStackTrace();
            return BadVerb.construct(context, request, response, serverTransformer);
	}
        if (debug) {
            System.out.println("GetMetadata.construct: contentType=" + crosswalks.getContentType(metadataPrefix));
            System.out.println("GetMetadata.construct: prerendered sb=" + sb.toString());
        }
        return render(response, crosswalks.getContentType(metadataPrefix), sb.toString(), (Transformer)null);
    }
}
