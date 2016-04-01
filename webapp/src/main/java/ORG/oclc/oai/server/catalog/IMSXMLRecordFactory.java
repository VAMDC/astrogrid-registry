
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
*The Original Code is IMSXMLRecordFactory.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package ORG.oclc.oai.server.catalog;

import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * IMSXMLRecordFactory converts native XML items to Record objects and vice versa.
 * This factory assumes the native XML item looks exactly like the <record>
 * element of an OAI GetRecord response, with the possible exception that the
 * <metadata> element contains multiple metadataFormats from which to choose.
 */
public class IMSXMLRecordFactory extends RecordFactory {
    /**
     * Construct an IMSXMLRecordFactory capable of producing the Crosswalk(s)
     * specified in the properties file.
     * @param properties Contains information to configure the factory:
     *                   specifically, the names of the crosswalk(s) supported
     */
    public IMSXMLRecordFactory(Properties properties)
	throws IllegalArgumentException {
	super(properties);
    }

    /**
     * Utility method to parse the 'local identifier' from the OAI identifier
     *
     * @param identifier OAI identifier
     * @return local identifier
     */
    protected String fromOAIIdentifier(String identifier) {
	StringTokenizer tokenizer = new StringTokenizer(identifier, ":");
	tokenizer.nextToken();
	tokenizer.nextToken();
	return tokenizer.nextToken();
    }

    /**
     * Construct an OAI identifier from the native item
     *
     * @param nativeItem native Item object
     * @return OAI identifier
     */
    public String getOAIIdentifier(Object nativeItem)
	throws IllegalArgumentException  {
	throw new IllegalArgumentException("Identifier isn't available in native item");
    }

    /**
     * get the datestamp from the item
     *
     * @param nativeItem a native item presumably containing a datestamp somewhere
     * @return a String containing the datestamp for the item
     */
    public String getDatestamp(Object nativeItem)
	throws IllegalArgumentException  {
	throw new IllegalArgumentException("Datestamp isn't available in native item");
    }

    /**
     * get the setspec from the item
     *
     * @param nativeItem a native item presumably containing a setspec somewhere
     * @return a String containing the setspec for the item
     */
    public Iterator getSetSpecs(Object nativeItem)
	throws IllegalArgumentException  {
	throw new IllegalArgumentException("SetSpecs aren't available in native item");
    }

    public boolean isDeleted(Object nativeItem)
	throws IllegalArgumentException {
	throw new IllegalArgumentException("IsDeleted isn't available in native item");
    }

    public String quickCreate(Object nativeItem, String schemaLocation, String metadataFormat) {
	// Can't do quickCreates?
	return null;
    }

    public Iterator getAbouts(Object nativeItem) throws IllegalArgumentException {
	throw new IllegalArgumentException("getAbouts isn't available in native item");
    }
}
