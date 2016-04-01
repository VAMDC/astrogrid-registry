
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
*The Original Code is BadVerb.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package ORG.oclc.oai.server.verb;

import java.io.IOException;
// import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * This class represents an BadVerb response on either the server or
 * on the client
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class BadVerb extends ServerVerb {
    /**
     * Construct the xml response on the server side.
     *
     * @param context the servlet context
     * @param request the servlet request
     * @return a String containing the xml response
     */
    public static String construct(HashMap context,
                                   HttpServletRequest request, HttpServletResponse response,
                                   Transformer serverTransformer)
        throws TransformerException {
        Properties properties =
	    (Properties)context.get("OAIHandler.properties");
        StringBuffer sb = new StringBuffer();
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
	sb.append("<oai:responseDate>");
	sb.append(createResponseDate(new Date()));
	sb.append("</oai:responseDate>");
// 	sb.append("<requestURL>");
//         sb.append(getRequestURL(request));
// 	sb.append("</requestURL>");
	sb.append("<oai:request>");
	try {
	    sb.append(request.getRequestURL().toString());
	} catch (java.lang.NoSuchMethodError e) {
	    sb.append(HttpUtils.getRequestURL(request).toString());
	}
	sb.append("</oai:request>");
	sb.append("<oai:error code=\"badVerb\">Illegal verb</oai:error>");
        sb.append("</OAI-PMH>");
	return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}
