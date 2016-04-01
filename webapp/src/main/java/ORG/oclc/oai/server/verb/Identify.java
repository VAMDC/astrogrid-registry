
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
*The Original Code is Identify.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package ORG.oclc.oai.server.verb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
// import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import java.util.Properties;
import java.util.Date;
import java.util.Enumeration;
import org.astrogrid.util.DomHelper;
import org.astrogrid.registry.server.query.ISearch;
import org.astrogrid.registry.server.query.QueryFactory;
import org.xmldb.api.base.ResourceSet;
// import org.xml.sax.SAXException;

/**
 * This class represents an Identify response on either the server or
 * on the client
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class Identify extends ServerVerb {
    private static ArrayList validParamNames = new ArrayList();
    static {
	validParamNames.add("verb");
    }
    
    /**
     * Construct the xml response on the server side.
     *
     * @param context the servlet context
     * @param request the servlet request
     * @return a String containing the xml response
     */
    public static String construct(HashMap context,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Transformer serverTransformer)
        throws TransformerException {
	String version = (String)context.get("OAIHandler.version");
        Properties properties =
	    (Properties)context.get("OAIHandler.properties");
	String baseURL = (String)properties.getProperty("OAIHandler.baseURL");
	if (baseURL == null) {
	    try {
		baseURL = request.getRequestURL().toString();
	    } catch (java.lang.NoSuchMethodError f) {
		baseURL = HttpUtils.getRequestURL(request).toString();
	    }
	}
        StringBuffer sb = new StringBuffer();
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
// 	sb.append("<requestURL>");
//         sb.append(getRequestURL(request));
// 	sb.append("</requestURL>");
	sb.append(getRequestElement(request, validParamNames, baseURL));
	if (hasBadArguments(request, validParamNames.iterator(), validParamNames)) {
	    sb.append(new BadArgumentException().getMessage());
	} else {
	    sb.append("<oai:Identify>");
	    sb.append("<oai:repositoryName>");
	    sb.append(properties.getProperty("Identify.repositoryName",
					     "undefined"));
	    sb.append("</oai:repositoryName>");
	    sb.append("<oai:baseURL>");
	    sb.append(baseURL);
	    sb.append("</oai:baseURL>");
	    sb.append("<oai:protocolVersion>2.0</oai:protocolVersion>");
	    sb.append("<oai:adminEmail>");
	    sb.append(properties.getProperty("Identify.adminEmail", "undefined"));
	    sb.append("</oai:adminEmail>");
	    sb.append("<oai:earliestDatestamp>");
	    sb.append(properties.getProperty("Identify.earliestDatestamp", "undefined"));
	    sb.append("</oai:earliestDatestamp>");
	    sb.append("<oai:deletedRecord>");
	    sb.append(properties.getProperty("Identify.deletedRecord", "undefined"));
	    sb.append("</oai:deletedRecord>");
	    String granularity = properties.getProperty("AbstractCatalog.granularity");
	    if (granularity != null) {
		sb.append("<oai:granularity>");
		sb.append(granularity);
		sb.append("</oai:granularity>");
		  sb.append("<oai:compression>gzip</oai:compression>");
//	 	    sb.append("<compression>compress</compression>");
		    sb.append("<oai:compression>deflate</oai:compression>");
	    }
		//sb.append
		String contractVersion = properties.getProperty("registry_contract_version",null);
	    ISearch rsSearch = null;
	    sb.append("<oai:description>");
	    try {
	           rsSearch = QueryFactory.createQueryService(contractVersion);
	           ResourceSet resSet = rsSearch.getQueryHelper().loadMainRegistry();
	           //org.w3c.dom.NodeList nl = identityDoc.getElementsByTagNameNS("*","Resource");
	          
	           if(resSet.getSize() > 0) {
	        	   sb.append(resSet.getResource(0).getContent().toString());
	           }else {
	        	   sb.append("Could not find Resource, printing what was found");
	        	   //sb.append(DomHelper.DocumentToString(identityDoc));
	           }
	          
	    }catch(Exception e) {
	    	//throw new OAIInternalServerError("Could not get Query Service" + e.toString());
	    	sb.append("Could not get Query Service for some reason, contractVersion = " + contractVersion + " exception message = " + e.getMessage());
	    }
	    sb.append("</oai:description>");
	    
	    // 	String compression = properties.getProperty("Identify.compression");
	    // 	if (compression != null) {
	  
	    // 	}
	    /*
	    String repositoryIdentifier = properties.getProperty("Identify.repositoryIdentifier");
	    String sampleIdentifier = properties.getProperty("Identify.sampleIdentifier");
	    if (repositoryIdentifier != null && sampleIdentifier != null) {
		sb.append("<description>");
		sb.append("<oai-identifier xmlns=\"http://www.openarchives.org/OAI/2.0/oai-identifier\"");
		sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai-identifier http://www.openarchives.org/OAI/2.0/oai-identifier.xsd\">");
		sb.append("<scheme>oai</scheme>");
		sb.append("<repositoryIdentifier>");
		sb.append(repositoryIdentifier);
		sb.append("</repositoryIdentifier>");
		sb.append("<delimiter>:</delimiter>");
		sb.append("<sampleIdentifier>");
		sb.append(sampleIdentifier);
		sb.append("</sampleIdentifier>");
		sb.append("</oai-identifier>");
		sb.append("</description>");
	    }
	    String propertyPrefix = "Identify.description";
	    Enumeration propNames = properties.propertyNames();
	    while (propNames.hasMoreElements()) {
		String propertyName = (String)propNames.nextElement();
		if (propertyName.startsWith(propertyPrefix)) {
		    sb.append((String)properties.get(propertyName));
		    sb.append("\n");
		}
	    }
	    sb.append("<description><toolkit xsi:schemaLocation=\"http://oai.dlib.vt.edu/OAI/metadata/toolkit http://oai.dlib.vt.edu/OAI/metadata/toolkit.xsd\" xmlns=\"http://oai.dlib.vt.edu/OAI/metadata/toolkit\"><title>OCLC's OAICat Repository Framework</title><author><name>Jeffrey A. Young</name><email>jyoung@oclc.org</email><institution>OCLC</institution></author><version>");
	    sb.append(version);
	    sb.append("</version><toolkitIcon>http://alcme.oclc.org/oaicat/oaicat_icon.gif</toolkitIcon><URL>http://www.oclc.org/research/software/oai/cat.shtm</URL></toolkit></description>");
	    */
	    sb.append("</oai:Identify>");
	}
        sb.append("</oai:OAI-PMH>");
        return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}
