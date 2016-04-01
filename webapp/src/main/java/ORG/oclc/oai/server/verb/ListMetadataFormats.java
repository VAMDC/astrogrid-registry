
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
*The Original Code is ListMetadataFormats.java.
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
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map;
// import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.crosswalk.CrosswalkItem;
import ORG.oclc.oai.server.crosswalk.Crosswalks;

/**
 * This class represents a ListMetadataFormats verb on either
 * the client or on the server.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class ListMetadataFormats extends ServerVerb {
    private static ArrayList validParamNames = new ArrayList();
    static {
	validParamNames.add("verb");
	validParamNames.add("identifier");
    }
    private static ArrayList requiredParamNames = new ArrayList();
    static {
	requiredParamNames.add("verb");
    }

    /**
     * Server-side construction of the xml response
     *
     * @param context the servlet context
     * @param request the servlet request
     * @exception OAIBadRequestException an http 400 status code problem
     * @exception OAINotFoundException an http 404 status code problem
     * @exception OAIInternalServerError an http 500 status code problem
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
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
	String styleSheet = properties.getProperty("OAIHandler.styleSheet");
	if (styleSheet != null) {
	    sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
	    sb.append(styleSheet);
	    sb.append("\"?>");
	}
        sb.append("<OAI-PMH xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
        sb.append("<responseDate>");
	sb.append(createResponseDate(new Date()));
	sb.append("</responseDate>");
// 	sb.append("<requestURL>");
//         sb.append(getRequestURL(request));
// 	sb.append("</requestURL>");
	sb.append(getRequestElement(request, validParamNames, baseURL));
	if (hasBadArguments(request, requiredParamNames.iterator(),
			    validParamNames)) {
	    sb.append(new BadArgumentException().getMessage());
	} else {
	    Crosswalks crosswalks = abstractCatalog.getCrosswalks();
	    if (identifier == null || identifier.length() == 0) {
		Iterator iterator = crosswalks.iterator();
		sb.append("<ListMetadataFormats>");
		while (iterator.hasNext()) {
		    Map.Entry entry = (Map.Entry)iterator.next();
		    String oaiSchemaLabel = (String)entry.getKey();
		    CrosswalkItem crosswalkItem = (CrosswalkItem)entry.getValue();
		    Crosswalk crosswalk = crosswalkItem.getCrosswalk();
		    StringTokenizer tokenizer = new StringTokenizer(crosswalk.getSchemaLocation());
		    String namespaceURI = tokenizer.nextToken();
		    String schemaURI = tokenizer.nextToken();
		    sb.append("<metadataFormat>");
		    sb.append("<metadataPrefix>");
		    sb.append(oaiSchemaLabel);
		    sb.append("</metadataPrefix>");
		    sb.append("<schema>");
		    sb.append(schemaURI);
		    sb.append("</schema>");
		    sb.append("<metadataNamespace>");
		    sb.append(namespaceURI);
		    sb.append("</metadataNamespace>");
		    sb.append("</metadataFormat>");
		}
		sb.append("</ListMetadataFormats>");
	    } else {
		try {
		    Vector schemaLocations = abstractCatalog.getSchemaLocations(identifier);
		    sb.append("<ListMetadataFormats>");
		    for (int i=0; i<schemaLocations.size(); ++i) {
			String schemaLocation = (String)schemaLocations.elementAt(i);
			StringTokenizer tokenizer = new StringTokenizer(schemaLocation);
			String namespaceURI = tokenizer.nextToken();
			String schemaURL = tokenizer.nextToken();
			sb.append("<metadataFormat>");
			sb.append("<metadataPrefix>");
			// make sure it's a space that separates them
			sb.append(crosswalks.getMetadataPrefix(namespaceURI, schemaURL));
			sb.append("</metadataPrefix>");
			sb.append("<schema>");
			sb.append(schemaURL);
			sb.append("</schema>");
			sb.append("<metadataNamespace>");
			sb.append(namespaceURI);
			sb.append("</metadataNamespace>");
			sb.append("</metadataFormat>");
		    }
		    sb.append("</ListMetadataFormats>");
		} catch (IdDoesNotExistException e) {
		    sb.append(e.getMessage());
		} catch (NoMetadataFormatsException e) {
		    sb.append(e.getMessage());
		}
	    }
        }
        sb.append("</OAI-PMH>");
        return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}
