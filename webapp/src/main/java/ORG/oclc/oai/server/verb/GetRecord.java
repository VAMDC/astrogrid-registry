
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
*The Original Code is GetRecord.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package ORG.oclc.oai.server.verb;

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
// import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.crosswalk.Crosswalks;

/**
 * This class represents a GetRecord response on either the server or
 * the client.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class GetRecord extends ServerVerb {
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
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Transformer serverTransformer)
        throws OAIInternalServerError, TransformerException {
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
            System.out.println("GetRecord.constructGetRecord: identifier=" +
                               identifier);
            System.out.println("GetRecord.constructGetRecord: metadataPrefix="
                               + metadataPrefix);
        }
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
	String styleSheet = properties.getProperty("OAIHandler.styleSheet");
	if (styleSheet != null) {
	    sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
	    sb.append(styleSheet);
	    sb.append("\"?>");
	}
        sb.append("<oai:OAI-PMH xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
        sb.append("<oai:responseDate>");
        sb.append(createResponseDate(new Date()));
        sb.append("</oai:responseDate>");
//         sb.append("<requestURL>");
//         sb.append(getRequestURL(request));
//         sb.append("</requestURL>");
        Crosswalks crosswalks = abstractCatalog.getCrosswalks();
	try {
	    if (metadataPrefix == null || metadataPrefix.length() == 0
		|| identifier == null || identifier.length() == 0) {
		throw new BadArgumentException();
	    }
	    else if (!crosswalks.containsValue(metadataPrefix)) {
		throw new CannotDisseminateFormatException(metadataPrefix);
	    } else {
		String record = abstractCatalog.getRecord(identifier, metadataPrefix);
		if (record != null) {
		    sb.append(getRequestElement(request, validParamNames, baseURL));
		    if (hasBadArguments(request, validParamNames.iterator(), validParamNames)) {
			sb.append(new BadArgumentException().getMessage());
		    } else {
			sb.append("<oai:GetRecord>");
			sb.append(record);
			sb.append("</oai:GetRecord>");
		    }
		} else {
		    throw new IdDoesNotExistException(identifier);
		}
	    }
	} catch (BadArgumentException e) {
	    sb.append("<oai:request verb=\"GetRecord\">");
	    sb.append(baseURL);
	    sb.append("</oai:request>");
	    sb.append(e.getMessage());
	} catch (CannotDisseminateFormatException e) {
	    sb.append(getRequestElement(request, validParamNames, baseURL));
	    sb.append(e.getMessage());
	} catch (IdDoesNotExistException e) {
	    sb.append(getRequestElement(request, validParamNames, baseURL));
	    sb.append(e.getMessage());
	}
        sb.append("</oai:OAI-PMH>");
        return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}
