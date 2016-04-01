
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
*The Original Code is RecordFactory.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/


package ORG.oclc.oai.server.catalog;

// import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import ORG.oclc.oai.server.verb.NoMetadataFormatsException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;
import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.crosswalk.CrosswalkItem;
import ORG.oclc.oai.server.crosswalk.Crosswalks;
import ORG.oclc.oai.util.OAIUtil;

/**
 * RecordFactory is responsible for pulling various pieces of information from native records
 * such as the identifier, datestamp, and sets. Note that the Crosswalk implementations
 * are responsible for the creating the &lt;metadata&gt; part of the response from
 * native records.
 */
public abstract class RecordFactory {
    public static final boolean debug=false;
//     private boolean encodeSetSpec = true;

    /**
     * Container for the crosswalk(s) supported by this factory
     */
    private Crosswalks crosswalks;

    /**
     * Construct a RecordFactory capable of producing the Crosswalk(s)
     * specified in the properties file.
     *
     * @param properties Contains information to configure the factory;
     *                   specifically, the names of crosswalk(s) supported
     */
    public RecordFactory(Properties properties) {
//         if ("false".equals(properties.getProperty("RecordFactory.encodeSetSpec"))) {
//             encodeSetSpec = false;
//         }
   crosswalks = new Crosswalks(properties);
    }

    public RecordFactory(HashMap crosswalkMap) {
   crosswalks = new Crosswalks(crosswalkMap);
    }

    /**
     * Retrieve the crosswalk(s) container
     *
     * @return Crosswalk(s) container
     */
    public Crosswalks getCrosswalks() { return crosswalks; }

    /**
     * Utility method to parse the 'local identifier' from the OAI identifier
     *
     * @param identifier OAI identifier
     * @return local identifier
     */
    protected abstract String fromOAIIdentifier(String identifier);

    public Object getMetaDataFromMap(Object nativeItem) {
      return ((HashMap)nativeItem).get("recordString");
    }

    /**
     * Get a list of supported schemaLocations for the specified native record.
     * @param nativeItem native database record
     * @return A Vector containing all the schemaLocations this record can support.
     * @exception NoMetadatdaFormatsException This record doesn't support any of the
     * available schemaLocations for this repository.
     */
    public Vector getSchemaLocations(Object nativeItem)
   throws NoMetadataFormatsException {
   if (isDeleted(nativeItem)) {
       throw new NoMetadataFormatsException();
   }
   Vector v = new Vector();
   Iterator iterator = getCrosswalks().iterator();
   while (iterator.hasNext()) {
       Map.Entry entry = (Map.Entry)iterator.next();
       CrosswalkItem crosswalkItem = (CrosswalkItem)entry.getValue();
       Crosswalk crosswalk = crosswalkItem.getCrosswalk();
       if (crosswalk.isAvailableFor(getMetaDataFromMap(nativeItem))) {
      v.add(crosswalk.getSchemaLocation());
       }
   }
   return v;
    }

    /**
     * Convert a native "item" to a "record" String. Use this version of createHeader
     * if the setSpecs are derived from the nativeItem itself.
     *
     * @param nativeItem the native record.
     * @return String[0] = "header" XML string String[1] = oai-identifier.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     */
    public String[] createHeader(Object nativeItem)
   throws IllegalArgumentException, OAIInternalServerError {
       return createHeader(
            getOAIIdentifier(nativeItem),
            getDatestamp(nativeItem),
            getSetSpecs(nativeItem),
            isDeleted(nativeItem));
   }

    /**
     * Convert a native "item" to a "record" String. Use this version of createHeader
     * if the setSpecs are supplied from a source other than the nativeItem itself.
     *
     * @param nativeItem the native record.
     * @param setSpecs a setSpec iterator
     * @return String[0] = "header" XML string String[1] = oai-identifier.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     */
    public String[] createHeader(Object nativeItem, Iterator setSpecs)
   throws IllegalArgumentException, OAIInternalServerError {
       if (setSpecs == null) {
      return createHeader(nativeItem);
       } else {
      return createHeader(
                getOAIIdentifier(nativeItem),
                getDatestamp(nativeItem),
                setSpecs,
                isDeleted(nativeItem));
       }
   }

    /**
     * Create a "record" string from the specified components.
     *
     * @param identifier the OAI identifier
     * @param datestamp the datestamp
     * @param setSpec An interator containing setSpec Strings
     * @param isDeleted a flag indicating the status of the record.
     * @return String[0] = "header" XML string String[1] = oai-identifier.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     */
    public static String[] createHeader(String identifier,
             String datestamp, Iterator setSpecs, boolean isDeleted)
   throws IllegalArgumentException, OAIInternalServerError {
       StringBuffer xmlHeader = new StringBuffer();
       xmlHeader.append("<oai:header");
       if (isDeleted) {
      xmlHeader.append(" status=\"deleted\"");
       }
       xmlHeader.append("><oai:identifier>");
       xmlHeader.append(OAIUtil.xmlEncode(identifier));
       xmlHeader.append("</oai:identifier><oai:datestamp>");
       xmlHeader.append(datestamp);
       xmlHeader.append("</oai:datestamp>");
       if (setSpecs != null) {
      while (setSpecs.hasNext()) {
          xmlHeader.append("<oai:setSpec>");
//                     try {
//                         if (encodeSetSpec) {
//                             xmlHeader.append(URLEncoder.encode((String)setSpecs.next(), "UTF-8"));
//                         } else {
                            xmlHeader.append((String)setSpecs.next());
//                         }

//                     } catch (UnsupportedEncodingException e) {
//                         e.printStackTrace();
//                         throw new OAIInternalServerError(e.getMessage());
//                     }
          xmlHeader.append("</oai:setSpec>");
      }
       }
       xmlHeader.append("</oai:header>");
       String resultString = xmlHeader.toString();
       xmlHeader = null;
       return new String[] {
      resultString,
      identifier
       };
   }

    /**
     * Create the &lt;record&gt; for the given record and metadataFormat selection. Use this
     * version of create if the setSpecs can be derived from the nativeItem itself.
     *
     * @param nativeItem the native record
     * @param schemaURL the schemaURL desired for the response
     * @param the metadataPrefix from the request
     * @return a String containing the OAI record response.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     * @exception This nativeItem doesn't support the specified metadataPrefix
     */
    public String create(Object nativeItem, String schemaURL, String metadataPrefix)
   throws IllegalArgumentException, CannotDisseminateFormatException {
   return create(nativeItem, schemaURL, metadataPrefix, (Iterator)null, (Iterator)null);
    }

    /**
     * Create the &lt;record&gt; for the given record and metadataFormat selection.
     * Use this version of create if the setSpecs are derived from a source other
     * other than the nativeItem itself.
     *
     * @param nativeItem the native record
     * @param schemaURL the schemaURL desired for the response
     * @param the metadataPrefix from the request
     * @return a String containing the OAI record response.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     * @exception This nativeItem doesn't support the specified metadataPrefix
     */
    public String create(Object nativeItem, String schemaURL, String metadataPrefix,
          Iterator setSpecs, Iterator abouts)
   throws IllegalArgumentException, CannotDisseminateFormatException {
        try {
            if (isDeleted(nativeItem)) {
                StringBuffer sb = new StringBuffer("<oai:record>");
                sb.append(createHeader(nativeItem)[0]);
                sb.append("</oai:record>");
                return sb.toString();
            }
            String result = quickCreate(nativeItem, schemaURL, metadataPrefix);
            if (result == null) {
                if (setSpecs == null) {
                    setSpecs = getSetSpecs(nativeItem);
                }
                if (abouts == null) {
                    abouts = getAbouts(nativeItem);
                }
                result = create(nativeItem, schemaURL,
                                getOAIIdentifier(nativeItem),
                                getDatestamp(nativeItem),
                                setSpecs,
                                abouts,
                                isDeleted(nativeItem));
            }

            return result;
        } catch (OAIInternalServerError e) {
            e.printStackTrace();
            throw new CannotDisseminateFormatException(e.getMessage());
        }
    }

    /**
     * Allows classes that implement RecordFactory to override the default create() method.
     * This is useful, for example, if the entire &lt;record&gt; is already packaged as the native
     * record.
     * @param nativeItem the native record
     * @param schemaURL the schemaURL desired for the response
     * @param the metadataPrefix from the request
     * @return a String containing the OAI record response.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     * @exception This nativeItem doesn't support the specified metadataPrefix
     */
    public abstract String quickCreate(Object nativeItem, String schemaURL, String metadataPrefix)
   throws IllegalArgumentException, CannotDisseminateFormatException;

    /**
     * Convert a native "item" to a "record" object.
     *
     * @param nativeItem native "Item"
     * @param schemaURL the schemaURL of the desired metadataFormat
     *                  or NULL for a Record object containing all
     *                  the supported Crosswalk(s)
     * @return "record" String
     * @exception IllegalArgumentException One of the header components for this record is bad.
     * @exception This nativeItem doesn't support the specified metadataPrefix
     */
    public String create(Object nativeItem, String schemaURL, String identifier,
          String datestamp, Iterator setSpecs, Iterator abouts, boolean isDeleted)
   throws IllegalArgumentException, CannotDisseminateFormatException, OAIInternalServerError {
        if (debug) System.out.println("RecordFactory.create");
   StringBuffer xmlRec = new StringBuffer();
   xmlRec.append("<oai:record><oai:header");
   if (isDeleted) {
       xmlRec.append(" status=\"deleted\"");
   }
   xmlRec.append("><oai:identifier>");
   xmlRec.append(OAIUtil.xmlEncode(identifier));
   xmlRec.append("</oai:identifier><oai:datestamp>");
   xmlRec.append(datestamp);
   xmlRec.append("</oai:datestamp>");
   if (setSpecs != null) {
       while (setSpecs.hasNext()) {
      xmlRec.append("<oai:setSpec>");
//                 try {
//                     if (encodeSetSpec) {
//                         xmlRec.append(URLEncoder.encode((String)setSpecs.next(), "UTF-8"));
//                     } else {
                        xmlRec.append((String)setSpecs.next());
//                     }
//                 } catch (UnsupportedEncodingException e) {
//                     e.printStackTrace();
//                     throw new OAIInternalServerError(e.getMessage());
//                 }
      xmlRec.append("</oai:setSpec>");
       }
   }
   xmlRec.append("</oai:header>");
      if (debug) System.out.println("RecordFactory.create: header finished");
   if (!isDeleted) {
            if (debug) System.out.println("RecordFactory.create: starting metadata");
       xmlRec.append("<oai:metadata>");
       Iterator iterator = getCrosswalks().iterator();
       while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry)iterator.next();
      CrosswalkItem crosswalkItem = (CrosswalkItem)entry.getValue();
      Crosswalk crosswalk = crosswalkItem.getCrosswalk();
                if (debug) ;
                    //System.out.println("RecordFactory.create: crosswalk=" + crosswalk);
      if (schemaURL == null
          || crosswalk.getSchemaURL().equals(schemaURL)) {

          xmlRec.append(crosswalk.createMetadata(getMetaDataFromMap(nativeItem)));
      }
       }
       xmlRec.append("</oai:metadata>");
            if (debug) System.out.println("RecordFactory.create: finished metadata");
       if (abouts != null) {
      while (abouts.hasNext()) {
          xmlRec.append("<oai:about>");
          xmlRec.append((String)abouts.next());
          xmlRec.append("</oai:about>");
      }
       }
   }
   xmlRec.append("</oai:record>");
        if (debug) System.out.println("RecordFactory.create: return=" + xmlRec.toString());
   return xmlRec.toString();
    }

    /**
     * Create the &lt;record&gt; for the given record and metadataFormat selection. Use this
     * version of create if the setSpecs can be derived from the nativeItem itself.
     *
     * @param nativeItem the native record
     * @param schemaURL the schemaURL desired for the response
     * @param the metadataPrefix from the request
     * @return a String containing the OAI record response.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     * @exception This nativeItem doesn't support the specified metadataPrefix
     */
    public String createMetadata(Object nativeItem, String schemaURL, String metadataPrefix)
   throws IllegalArgumentException, CannotDisseminateFormatException {
   return createMetadata(nativeItem, schemaURL, metadataPrefix, (Iterator)null, (Iterator)null);
    }

    /**
     * Create the &lt;record&gt; for the given record and metadataFormat selection.
     * Use this version of create if the setSpecs are derived from a source other
     * other than the nativeItem itself.
     *
     * @param nativeItem the native record
     * @param schemaURL the schemaURL desired for the response
     * @param the metadataPrefix from the request
     * @return a String containing the OAI record response.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     * @exception This nativeItem doesn't support the specified metadataPrefix
     */
    public String createMetadata(Object nativeItem, String schemaURL, String metadataPrefix,
          Iterator setSpecs, Iterator abouts)
   throws IllegalArgumentException, CannotDisseminateFormatException {
        try {
            if (isDeleted(nativeItem)) {
                throw new CannotDisseminateFormatException("Record is deleted.");
            }
            String result = quickCreateMetadata(nativeItem, schemaURL, metadataPrefix);
            if (result == null) {
                if (setSpecs == null) {
                    setSpecs = getSetSpecs(nativeItem);
                }
                if (abouts == null) {
                    abouts = getAbouts(nativeItem);
                }
                result = createMetadata(nativeItem, schemaURL,
                                isDeleted(nativeItem));
            }

            return result;
        } catch (OAIInternalServerError e) {
            e.printStackTrace();
            throw new CannotDisseminateFormatException(e.getMessage());
        }
    }

    /**
     * Allows classes that implement RecordFactory to override the default createMetadata() method.
     * This is useful, for example, if the entire &lt;record&gt; is already packaged as the native
     * record.
     * @param nativeItem the native record
     * @param schemaURL the schemaURL desired for the response
     * @param the metadataPrefix from the request
     * @return a String containing the OAI record response.
     * @exception IllegalArgumentException One of the header components for this record is bad.
     * @exception This nativeItem doesn't support the specified metadataPrefix
     */
    public String quickCreateMetadata(Object nativeItem, String schemaURL, String metadataPrefix)
   throws IllegalArgumentException, CannotDisseminateFormatException {
        return null;
    }

    /**
     * Convert a native "item" to a "record" object.
     *
     * @param nativeItem native "Item"
     * @param schemaURL the schemaURL of the desired metadataFormat
     *                  or NULL for a Record object containing all
     *                  the supported Crosswalk(s)
     * @return "record" String
     * @exception IllegalArgumentException One of the header components for this record is bad.
     * @exception This nativeItem doesn't support the specified metadataPrefix
     */
    public String createMetadata(Object nativeItem, String schemaURL, boolean isDeleted)
   throws IllegalArgumentException, CannotDisseminateFormatException, OAIInternalServerError {
   StringBuffer xmlRec = new StringBuffer();
   if (isDeleted) {
            throw new CannotDisseminateFormatException("Record is deleted");
   }
        Iterator iterator = getCrosswalks().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            CrosswalkItem crosswalkItem = (CrosswalkItem)entry.getValue();
       Crosswalk crosswalk = crosswalkItem.getCrosswalk();
            if (schemaURL == null
                || crosswalk.getSchemaURL().equals(schemaURL)) {
                xmlRec.append(crosswalk.createMetadata(getMetaDataFromMap(nativeItem)));
            }
        }
   return xmlRec.toString();
    }

    /**
     * Construct an OAI identifier from the native record
     *
     * Caveat: This brazenly assumes the record's local identifier
     * exists somewhere within the native, or can be
     * generated on the fly.
     *
     * @param nativeItem the native record
     * @return String OAI identifier
     */
    protected abstract String getOAIIdentifier(Object nativeItem);

    /**
     * Extract the local identifier from the native record.
     * Since I forgot to add this originally, it is a roundabout
     * way to get this. It's a good idea to override this method
     * to do it more directly.
     *
     * @param nativeItem the native record
     * @return String local identifier
     */
    public String getLocalIdentifier(Object nativeItem) {
   return fromOAIIdentifier(getOAIIdentifier(nativeItem));
    }

    /**
     * Construct an OAI datestamp from the native record
     *
     * Caveat: This brazenly assumes the record's datestamp
     * exists somewhere within the native record, or can be
     * generated on the fly.
     *
     * @param nativeItem the native record
     * @return String OAI identifier
     */
    protected abstract String getDatestamp(Object nativeItem);

    /**
     * Get an Iterator containing all the setSpecs for this native record.
     * Return null if the setSpecs are derived from
     * a source other than the nativeItem itself. In this case, the AbstractCatalog
     * is responsible for obtaining the setSpecs which can be passed to the
     * create() and createHeader() methods along with the nativeItem.
     *
     * @param nativeItem the native record
     * @return an Iterator containing setSpec String
     */
    protected abstract Iterator getSetSpecs(Object nativeItem)
   throws IllegalArgumentException;
    /**
     * Get a boolean indicating if this native record is deleted
     *
     * @param nativeItem the native record
     * @return true if record is deleted, false otherwise.
     */
    protected abstract boolean isDeleted(Object nativeItem);
    /**
     * Get an Iterator containing all the &lt;about&gt; entries for this native record
     * Return null if the abouts are derived from a source outside the native record itself.
     * In this case, the AbstractCatalog is responsible for obtaining the abouts which
     * can be passed to the create() method along with the nativeItem.
     *
     * @param nativeItem the native record
     * @return an Iterator containing &lt;about&gt; XML String
     */
    protected abstract Iterator getAbouts(Object nativeItem);
}
