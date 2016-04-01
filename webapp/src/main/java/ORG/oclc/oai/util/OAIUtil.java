
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
*The Original Code is OAIUtil.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/

package ORG.oclc.oai.util;

import java.net.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.xml.sax.*;

/**
 * Utility methods for OAICat and OAIHarvester
 */
public class OAIUtil {
    /**
     * XML encode a string.
     * @param s any String
     * @return the String with &amp;, &lt;, and &gt; encoded for use in XML.
     */
    public static String xmlEncode(String s) {
        StringBuffer sb = new StringBuffer();

        for (int i=0; i<s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
            case '&':
                sb.append("&amp;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Convert a packed LCCN String to MARC display format.
     * @param packedLCCN an LCCN String in packed storage format (e.g. 'n&nbsp;&nbsp;2001050268').
     * @return  an LCCN String in MARC display format (e.g. 'n2001-50268').
     */
    public static String toLCCNDisplay(String packedLCCN) {
	StringBuffer sb = new StringBuffer();
	if (Character.isDigit(packedLCCN.charAt(2))) {
	    sb.append(packedLCCN.substring(0, 2).trim());
	    sb.append(packedLCCN.substring(2, 6));
	    sb.append("-");
	    int i = Integer.parseInt(packedLCCN.substring(6).trim());
	    sb.append(Integer.toString(i));
	} else {
	    sb.append(packedLCCN.substring(0, 3).trim());
	    sb.append(packedLCCN.substring(3, 5));
	    sb.append("-");
	    int i = Integer.parseInt(packedLCCN.substring(5).trim());
	    sb.append(Integer.toString(i));
	}
	return sb.toString();
    }

    /**
     * convert a packed LCCN to display format.
     * @param packedLCCN an LCCN String in packed storage format (e.g. 'n&nbsp;&nbsp;2001050268').
     * @return  an LCCN String in MARC display format (e.g. 'n2001-50268').
     * @deprecated use toLCCNDisplay() instead.
     */
    public static String getLCCN(String packedLCCN) {
	return toLCCNDisplay(packedLCCN);
    }
}
