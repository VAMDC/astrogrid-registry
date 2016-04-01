
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
*The Original Code is ListIdentifiers.java.
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
import java.util.Map;
import java.util.Properties;
// import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.crosswalk.Crosswalks;

/**
 * This class represents a ListIdentifiers verb on either the server or
 * on the client side.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class ListIdentifiers extends ServerVerb {
    private static final boolean debug = false;
    private static ArrayList validParamNames1 = new ArrayList();
    static {
	validParamNames1.add("verb");
	validParamNames1.add("from");
	validParamNames1.add("until");
	validParamNames1.add("set");
	validParamNames1.add("metadataPrefix");
    }
    private static ArrayList validParamNames2 = new ArrayList();
    static {
	validParamNames2.add("verb");
	validParamNames2.add("resumptionToken");
    }
    private static ArrayList requiredParamNames1 = new ArrayList();
    static {
	requiredParamNames1.add("verb");
	requiredParamNames1.add("metadataPrefix");
    }
    private static ArrayList requiredParamNames2 = new ArrayList();
    static {
	requiredParamNames2.add("verb");
	requiredParamNames2.add("resumptionToken");
    }

    public  static String construct(HashMap context,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Transformer serverTransformer)
	throws OAIInternalServerError, TransformerException {
        Properties properties =
	    (Properties)context.get("OAIHandler.properties");
        //System.out.println("dropping static construct for listidentifiers");
	AbstractCatalog abstractCatalog =
	    (AbstractCatalog)context.get("OAIHandler.catalog");
	boolean xmlEncodeSetSpec = "true".equalsIgnoreCase((String)properties.getProperty("OAIHandler.xmlEncodeSetSpec"));
	boolean urlEncodeSetSpec = !"false".equalsIgnoreCase((String)properties.getProperty("OAIHandler.urlEncodeSetSpec"));
	String baseURL = (String)properties.getProperty("OAIHandler.baseURL");
    Map listIdentifiersMap = null;
	if (baseURL == null) {
	    try {
		baseURL = request.getRequestURL().toString();
	    } catch (java.lang.NoSuchMethodError f) {
		baseURL = HttpUtils.getRequestURL(request).toString();
	    }
	}
	StringBuffer sb = new StringBuffer();
	String oldResumptionToken = request.getParameter("resumptionToken");
	String metadataPrefix = request.getParameter("metadataPrefix");

	if (metadataPrefix != null && metadataPrefix.length() == 0)
	    metadataPrefix = null;
	
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
// 	sb.append(getRequestURL(request));
// 	sb.append("</requestURL>");
            
	if (!abstractCatalog.isHarvestable()) {
	    sb.append("<oai:request verb=\"ListIdentifiers\">");
	    sb.append(baseURL);
	    sb.append("</oai:request>");
	    sb.append("<oai:error code=\"badArgument\">Database is unavailable for harvesting</oai:error>");
	} else {
	    ArrayList validParamNames = null;
	    ArrayList requiredParamNames = null;
	    if (oldResumptionToken == null) {
		validParamNames = validParamNames1;
		requiredParamNames = requiredParamNames1;
		String from = request.getParameter("from");
		String until = request.getParameter("until");
		try {
		    if (from != null && from.length() > 0 && from.length() < 10) {
			throw new BadArgumentException();
		    }
		    if (until != null && until.length() > 0 && until.length() < 10) {
			throw new BadArgumentException();
		    }
		    if (from != null && until != null && from.length() != until.length()) {
			throw new BadArgumentException();
		    }
		    //if (from == null || from.length() == 0) {
			//from = "0001-01-01";
		    //}
		    //if (until == null || until.length() == 0) {
			//until = "9999-12-31";
		    //}
            if(from != null)
                from = abstractCatalog.toFinestFrom(from);
            if(until != null)
                until = abstractCatalog.toFinestUntil(until);
          if(from != null && until != null) {
              if (from.compareTo(until) > 0)
                  throw new BadArgumentException();
          }
		    String set = request.getParameter("set");
                    if (set != null) {
                        if (set.length() == 0) set = null;
                        else if (urlEncodeSetSpec) set = set.replace(' ', '+');
                    }
		    Crosswalks crosswalks = abstractCatalog.getCrosswalks();
		    if (metadataPrefix != null
			&& !crosswalks.containsValue(metadataPrefix)) {
			throw new CannotDisseminateFormatException(metadataPrefix);
		    } else {
			listIdentifiersMap = abstractCatalog.listIdentifiers(from, until, set,
									     metadataPrefix);
		    }
		} catch (NoItemsMatchException e) {
		    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
		    sb.append(e.getMessage());
		} catch (BadArgumentException e) {
		    sb.append("<request verb=\"ListIdentifiers\">");
// 		    sb.append(HttpUtils.getRequestURL(request));
		    sb.append(baseURL);
		    sb.append("</request>");
		    sb.append(e.getMessage());
// 		} catch (BadGranularityException e) {
// 		    sb.append(getRequestElement(request));
// 		    sb.append(e.getMessage());
		} catch (CannotDisseminateFormatException e) {
		    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
		    sb.append(e.getMessage());
		} catch (NoSetHierarchyException e) {
		    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
		    sb.append(e.getMessage());
		}
	    } else {
		validParamNames = validParamNames2;
		requiredParamNames = requiredParamNames2;
		if (hasBadArguments(request, requiredParamNames.iterator(), validParamNames)) {
		    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
		    sb.append(new BadArgumentException().getMessage());
		} else {
		    try {
			listIdentifiersMap = abstractCatalog.listIdentifiers(oldResumptionToken);
		    } catch (BadResumptionTokenException e) {
		        sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
			    sb.append(e.getMessage());
		    }
		}
	    }

	    if (listIdentifiersMap != null) {
		sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
		if (hasBadArguments(request, requiredParamNames.iterator(),
				    validParamNames)) {
		    sb.append(new BadArgumentException().getMessage());
		} else {
		    sb.append("<ListIdentifiers>");
		    Iterator identifiers = (Iterator)listIdentifiersMap.get("headers");
		    while (identifiers.hasNext()) {
			sb.append((String)identifiers.next());
		    }
		    
		    Map newResumptionMap = (Map)listIdentifiersMap.get("resumptionMap");
		    if (newResumptionMap != null) {
			String newResumptionToken = (String)newResumptionMap.get("resumptionToken");
			String expirationDate = (String)newResumptionMap.get("expirationDate");
			String completeListSize = (String)newResumptionMap.get("completeListSize");
			String cursor = (String)newResumptionMap.get("cursor");
			sb.append("<resumptionToken");
			if (expirationDate != null) {
			    sb.append(" expirationDate=\"");
			    sb.append(expirationDate);
			    sb.append("\"");
			}
			if (completeListSize != null) {
			    sb.append(" completeListSize=\"");
			    sb.append(completeListSize);
			    sb.append("\"");
			}
			if (cursor != null) {
			    sb.append(" cursor=\"");
			    sb.append(cursor);
			    sb.append("\"");
			}
			sb.append(">");
			sb.append(newResumptionToken);
			sb.append("</resumptionToken>");
		    } else if (oldResumptionToken != null) {
			sb.append("<resumptionToken />");
		    }
		    sb.append("</ListIdentifiers>");
		}
	    }
	}
	sb.append("</OAI-PMH>");
	if (debug) {
	    System.out.println("ListIdentifiers.constructListIdentifiers: returning: "
			       + sb.toString());
	}
	//System.out.println("cleaning listidentmap from ListIdentifiers Verb");
	/*
	if(listIdentifiersMap != null) {
		listIdentifiersMap.clear();
	}
	*/
	listIdentifiersMap = null;
	String resultReturn = sb.toString();
	sb = null;
    return render(response, "text/xml; charset=UTF-8", resultReturn, serverTransformer);
    }
}
