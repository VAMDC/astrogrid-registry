/**
 *Copyright (c) 2000-2002 OCLC Online Computer Library Center, Inc. and
 *other contributors. All rights reserved.  The contents of this file, as
 *updated from time to time by the OCLC Office of Research, are subject to
 *OCLC Research Public License Version 2.0 (the "License"); you may not
 *use this file except in compliance with the License. You may obtain a
 *current copy of the License at http://purl.oclc.org/oclc/research/ORPL/.
 *Software distributed under the License is distributed on an "AS IS"
 *basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 *License for the specific language governing rights and limitations under
 *the License.  This software consists of voluntary contributions made by
 *many individuals on behalf of OCLC Research. For more information on
 *OCLC Research, please see http://www.oclc.org/oclc/research/.

 *The Original Code is CrosswalkItem.java.
 *The Initial Developer of the Original Code is Jeff Young.
 *Portions created by ______________________ are
 *Copyright (C) _____ _______________________. All Rights Reserved.
 *Contributor(s):______________________________________.
 */

package ORG.oclc.oai.server.crosswalk;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import ORG.oclc.oai.server.verb.OAIInternalServerError;

public class CrosswalkItem {
    private String contentType = null;
    private String docType = null;
    private String encoding = null;
    private String nativeMetadataPrefix = null;
    private String metadataPrefix = null;
    private String schema = null;
    private String metadataNamespace = null;
    private String xsltName = null;
    private Crosswalk crosswalk = null;
    private int rank = -1;
    
    public static final int RANK_DIRECTLY_AVAILABLE = 0;
    public static final int RANK_DERIVED = 1;

    public CrosswalkItem(String metadataPrefix, String schema, String metadataNamespace, Class crosswalkClass)
    throws OAIInternalServerError {
	this.nativeMetadataPrefix = metadataPrefix;
	this.metadataPrefix = metadataPrefix;
	this.schema = schema;
	this.metadataNamespace = metadataNamespace;
	this.rank = RANK_DIRECTLY_AVAILABLE;
	try {
	    Constructor constructor = crosswalkClass.getConstructor(new Class[] {CrosswalkItem.class});
	    this.crosswalk = (Crosswalk)constructor.newInstance(new Object[] {this});
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new OAIInternalServerError(e.getMessage());
	}
    }
	
    public CrosswalkItem(String metadataPrefix, String schema, String metadataNamespace, Crosswalk crosswalk)
    throws OAIInternalServerError {
	this.nativeMetadataPrefix = metadataPrefix;
	this.metadataPrefix = metadataPrefix;
	this.schema = schema;
	this.metadataNamespace = metadataNamespace;
	this.rank = RANK_DIRECTLY_AVAILABLE;
	this.crosswalk = crosswalk;
    }
	
    public CrosswalkItem(String nativeMetadataPrefix, String metadataPrefix, String schema, String metadataNamespace, Class crosswalkClass, String xsltName)
    throws OAIInternalServerError {
	this.nativeMetadataPrefix = nativeMetadataPrefix;
	this.metadataPrefix = metadataPrefix;
	this.schema = schema;
	this.metadataNamespace = metadataNamespace;
	this.rank = RANK_DERIVED;
	this.xsltName = xsltName;
	try {
	    Constructor constructor = crosswalkClass.getConstructor(new Class[] {CrosswalkItem.class});
	    this.crosswalk = (Crosswalk)constructor.newInstance(new Object[] {this});
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new OAIInternalServerError(e.getMessage());
	}
    }

    public String getNativeMetadataPrefix() { return nativeMetadataPrefix; }
    public String getMetadataPrefix() { return metadataPrefix; }
    public String getMetadataNamespace() { return metadataNamespace; }
    public String getSchema() { return schema; }
    public Crosswalk getCrosswalk() { return crosswalk; }
    public String getContentType() { return contentType; }
    public String getDocType() { return docType; }
    public String getEncoding() { return encoding; }
    public String getXSLTName() { return xsltName; }
    
    public int getRank() { return rank; }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("CrosswalkItem: " );
	sb.append(getNativeMetadataPrefix());
	sb.append(":");
 	sb.append(getMetadataPrefix());
	sb.append(":");
	sb.append(getMetadataNamespace());
	sb.append(":");
	sb.append(getSchema());
	sb.append(":");
	sb.append(getCrosswalk());
	sb.append(":");
	sb.append(getRank());
	return sb.toString();
    }
}