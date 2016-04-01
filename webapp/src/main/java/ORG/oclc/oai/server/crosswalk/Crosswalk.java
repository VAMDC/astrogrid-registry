
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
*The Original Code is Crosswalk.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package ORG.oclc.oai.server.crosswalk;

import java.util.StringTokenizer;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

/**
 * Converts a native "item" to an OAI metadataFormat.
 */
public abstract class Crosswalk {
    /**
     * The schemaLocation supported by this crosswalk
     */
    private String schemaLocation;
    private String contentType;
    private String docType;
    private String encoding;

    /**
     * Constructor
     *
     * @param schemaLocation the schemaLocation supported by this crosswalk
     */
    public Crosswalk(String schemaLocation) {
        this(schemaLocation, (String)null);
    }

    /**
     * Constructor
     *
     * @param schemaLocation the schemaLocation supported by this crosswalk
     */
    public Crosswalk(String schemaLocation, String contentType) {
        this(schemaLocation, contentType, (String)null);
    }

    public Crosswalk(String schemaLocation, String contentType, String docType) {
        this(schemaLocation, contentType, docType, (String)null);
    }
    /**
     * Constructor
     *
     * @param schemaLocation the schemaLocation supported by this crosswalk
     */
    public Crosswalk(String schemaLocation, String contentType, String docType, String encoding) {
        this.schemaLocation = schemaLocation;
        if (contentType == null)
            contentType = "text/xml; charset=UTF-8";
        this.contentType = contentType;
        this.docType = docType;
        this.encoding = encoding;
    }

    /**
     * returns the schemaLocation
     *
     * @return the schemaLocation
     */
    public String getSchemaLocation() { return schemaLocation; }

    public String getContentType() { return contentType; }

    public String getDocType() { return docType; }

    public String getEncoding() { return encoding; }

    /**
     * parse the schemaURL from the schemaLocation
     *
     * @return the schemaURL portion of the schemaLocation
     */
    public String getSchemaURL() {
	StringTokenizer tokenizer = new StringTokenizer(schemaLocation, " ");
	tokenizer.nextToken();
	return tokenizer.nextToken();
    }

    /**
     * parse the namespaceURL from the schemaLocation
     *
     * @return the namespaceURL portion of the schemaLocation
     */
    public String getNamespaceURL() {
	StringTokenizer tokenizer = new StringTokenizer(schemaLocation, " ");
	return tokenizer.nextToken();
    }

    /**
     * Can this nativeItem be represented in ETDMS format?
     * @param nativeItem a record in native format
     * @return true if ETDMS format is possible, false otherwise.
     */
    public abstract boolean isAvailableFor(Object nativeItem);
    
    /**
     * Perform the actual crosswalk.
     *
     * @param nativeItem the native "item". In this case, it is
     * already formatted as an OAI <record> element, with the
     * possible exception that multiple metadataFormats are
     * present in the <metadata> element.
     * @return a String containing the XML to be stored within the <metadata> element.
     * @exception CannotDisseminateFormatException nativeItem doesn't support this format.
     */
    public abstract String createMetadata(Object nativeItem)
	throws CannotDisseminateFormatException;

    /**
     * returns the schemaLocation for this crosswalk.
     * @return a String containing the schemaLocation.
     */
    public String toString() {
	return schemaLocation;
    }
}
