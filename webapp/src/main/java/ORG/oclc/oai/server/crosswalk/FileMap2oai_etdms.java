
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
*The Original Code is FileMap2oai_etdms.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package ORG.oclc.oai.server.crosswalk;

import java.util.HashMap;
import java.util.Properties;
import java.io.UnsupportedEncodingException;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

/**
 * Convert native "item" to oai_etdms. In this case, the native "item"
 * is assumed to already be formatted as an OAI <record> element,
 * with the possible exception that multiple metadataFormats may
 * be present in the <metadata> element. The "crosswalk", merely
 * involves pulling out the one that is requested.
 */
public class FileMap2oai_etdms extends Crosswalk {
    /**
     * The constructor assigns the schemaLocation associated with this crosswalk. Since
     * the crosswalk is trivial in this case, no properties are utilized.
     *
     * @param properties properties that are needed to configure the crosswalk.
     */
    public FileMap2oai_etdms(Properties properties) {
	super("http://www.ndltd.org/standards/metadata/etdms/1.0/ http://www.ndltd.org/standards/metadata/etdms/1.0/etdms.xsd");
    }

    /**
     * Can this nativeItem be represented in ETDMS format?
     * @param nativeItem a record in native format
     * @return true if ETDMS format is possible, false otherwise.
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
	HashMap recordMap = (HashMap)nativeItem;
        try {
            String xmlRec = (new String((byte[])recordMap.get("recordBytes"), "UTF-8")).trim();
            if (xmlRec.startsWith("<?")) {
                int offset = xmlRec.indexOf("?>");
                xmlRec = xmlRec.substring(offset+2);
            }
            return xmlRec;
        } catch (UnsupportedEncodingException e) {
            throw new CannotDisseminateFormatException("An error occurred");
        }
    }
}
