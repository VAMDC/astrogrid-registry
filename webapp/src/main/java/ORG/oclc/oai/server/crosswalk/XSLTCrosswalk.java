
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
*The Original Code is XSLTCrosswalk.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/


package ORG.oclc.oai.server.crosswalk;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;

/**
 * Convert native "item" to oai_dc. In this case, the native "item"
 * is assumed to already be formatted as an OAI <record> element,
 * with the possible exception that multiple metadataFormats may
 * be present in the <metadata> element. The "crosswalk", merely
 * involves pulling out the one that is requested.
 */
public class XSLTCrosswalk extends Crosswalk {
    private boolean debug = false;
    protected Transformer transformer = null;

    public XSLTCrosswalk(Properties properties)
        throws OAIInternalServerError {
        this(properties, properties.getProperty("XSLTCrosswalk.schemaLocation"), (String)null);
    }

    public XSLTCrosswalk(Properties properties, String schemaLocation, String contentType)
        throws OAIInternalServerError {
        this(properties, schemaLocation, contentType, (String)null);
    }
    public XSLTCrosswalk(Properties properties, String schemaLocation, String contentType, String docType)
        throws OAIInternalServerError {
        this(properties, schemaLocation, contentType, docType, (String)null);
    }
    /**
     * The constructor assigns the schemaLocation associated with this crosswalk. Since
     * the crosswalk is trivial in this case, no properties are utilized.
     *
     * @param properties properties that are needed to configure the crosswalk.
     */
    public XSLTCrosswalk(Properties properties, String schemaLocation, String contentType, String docType, String encoding)
        throws OAIInternalServerError {
// 	super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
        super(schemaLocation, contentType, docType, encoding);
        if ("true".equals(properties.getProperty("XSLTCrosswalk.debug"))) debug = true;
        try {
            String xsltName = properties.getProperty("XSLTCrosswalk.xsltName");
            if (xsltName != null) {
                StreamSource xslSource = new StreamSource(new FileInputStream(xsltName));
                TransformerFactory tFactory = TransformerFactory.newInstance();
                this.transformer = tFactory.newTransformer(xslSource);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
    }

    /**
     * Can this nativeItem be represented in DC format?
     * @param nativeItem a record in native format
     * @return true if DC format is possible, false otherwise.
     */
    public boolean isAvailableFor(Object nativeItem) {
        return true;
    }

    /**
     * Perform the actual crosswalk.
     *
     * @param nativeItem the native "item". In this case, it is
     * already formatted as an OAI <record> element, with the
     * possible exception that multiple metadataFormats are
     * present in the <metadata> element.
     * @return a String containing the FileMap to be stored within the <metadata> element.
     * @exception CannotDisseminateFormatException nativeItem doesn't support this format.
     */
    public String createMetadata(Object nativeItem)
	throws CannotDisseminateFormatException {
        try {
            String xmlRec = null;
            if (nativeItem instanceof HashMap) {
                HashMap recordMap = (HashMap)nativeItem;
                xmlRec = (String)recordMap.get("recordString");
                if (xmlRec == null) {
                    xmlRec = new String((byte[])recordMap.get("recordBytes"), "UTF-8");
                }
                xmlRec = xmlRec.trim();
            } else if (nativeItem instanceof String) {
                xmlRec = (String)nativeItem;
            } else {
                throw new Exception("Unrecognized nativeItem");
            }
        
            if (debug) {
                System.out.println("XSLTCrosswalk.createMetadata: xmlRec=" + xmlRec);
            }
            if (xmlRec.startsWith("<?")) {
                int offset = xmlRec.indexOf("?>");
                xmlRec = xmlRec.substring(offset+2);
            }
            if (debug) {
                System.out.println("XSLTCrosswalk.createMetadata: transformer=" + transformer);
            }
            if (transformer != null) {
                StringReader stringReader = new StringReader(xmlRec);
                StreamSource streamSource = new StreamSource(stringReader);
                StringWriter stringWriter = new StringWriter();
                synchronized (transformer) {
                    transformer.transform(streamSource, new StreamResult(stringWriter));
                }
                if (debug) {
                    System.out.println("XSLTCrosswalk.createMetadata: return=" + stringWriter.toString());
                }
                return stringWriter.toString();
            } else {
                return xmlRec;
            }
        } catch (Exception e) {
            if (debug) {
                e.printStackTrace();
            }
            throw new CannotDisseminateFormatException(e.getMessage());
        }
    }
}
