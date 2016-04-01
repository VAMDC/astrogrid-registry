/*
 * The contents of this file, as updated from time to time by the OCLC Office
 * of Research, are subject to the OCLC Office of Research Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a current copy of the License at
 * http://purl.oclc.org/oclc/research/ORPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the OCLC Office of Research. For more information on the OCLC
 * Office of Research, please see http://www.oclc.org/oclc/research/.
 *
 * This is Original Code.
 *
 * The Initial Developer(s) of the Original Code is (are):
 *  - Ralph LeVan <levan@oclc.org>
 *
 * Portions created by OCLC are Copyright (C) 2001.
 *
 * 2002-04-09 Created
 */

//package ORG.oclc.oai.server.catalog;
package astrogrid.registry.oai;

import org.w3c.dom.Text;
import ORG.oclc.oai.server.catalog.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
//import java.io.ByteArrayInputStream;
import org.xml.sax.InputSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.HashMap;
import java.util.Set;


import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.astrogrid.xmldb.client.XMLDBFactory;
import org.astrogrid.xmldb.client.XMLDBService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.net.MalformedURLException;

import ORG.oclc.oai.server.verb.BadResumptionTokenException;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;
import ORG.oclc.oai.server.verb.IdDoesNotExistException;
import ORG.oclc.oai.server.verb.BadArgumentException;
import ORG.oclc.oai.server.verb.NoMetadataFormatsException;
import ORG.oclc.oai.server.verb.NoSetHierarchyException;
import ORG.oclc.oai.server.verb.NoItemsMatchException;
import ORG.oclc.oai.server.catalog.helpers.RecordStringHandler;
// // import ORG.oclc.oai.util.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.astrogrid.config.Config;
import org.astrogrid.util.DomHelper;
import org.astrogrid.registry.server.admin.AuthorityList;
import org.astrogrid.registry.server.admin.AuthorityListManager;
import org.astrogrid.registry.server.query.QueryConfigExtractor;
import org.astrogrid.registry.RegistryException;
import org.astrogrid.registry.server.query.ISearch;
import org.astrogrid.registry.server.query.QueryFactory;
import org.astrogrid.registry.server.XSLHelper;
import org.astrogrid.registry.server.SOAPFaultException;

/**
 * XMLFileOAICatalog is an implementation of AbstractCatalog interface
 * with the data sitting in a directory on a filesystem.
 *
 * @author Jeff Young, OCLC Online Computer Library Center
 */

public class XMLExistOAICatalog extends AbstractCatalog {
    private static boolean debug=false;

   private SortedMap nativeMap = null;
   private HashMap          resumptionResults=new HashMap();
   private int              maxListSize;
   private HashMap sets = null;
   private Transformer getMetadataTransformer = null;
   private boolean schemaLocationIndexed = false;
   private static String returnVersionNumber = null;
   public static Config conf = null;
   private Properties props = null;
   
   /**
    * Logging variable for writing information to the logs
    */
    private static final Log log = LogFactory.getLog(XMLExistOAICatalog.class);


   static {
      if(conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
      }
   }

   public XMLExistOAICatalog(Properties properties) throws IOException {
         this.props = properties;         
         sets = getSets(properties);
   }
   
   private void populateNativeMapFromIdentifier(String oaiIdentifier) throws OAIInternalServerError, IdDoesNotExistException {
       String contractVersion = props.getProperty("registry_contract_version",null);
       ISearch rsSearch = null;
       try {
           rsSearch = QueryFactory.createQueryService(contractVersion);
       }catch(Exception e) {
           throw new OAIInternalServerError("Could not get Query Service" + e.toString());
       }
       String versionNumber = rsSearch.getResourceVersion();
       
       maxListSize = conf.getInt("XMLFileOAICatalog.maxListSize",200);
       Document sourceFile;
       Document oaiDoc;
       SAXParser saxParser;
       String xmlDoc;
       StringReader xmlReader = null;
       try {
           sourceFile = rsSearch.getQueryHelper().getResourceByIdentifier(oaiIdentifier);
           if(sourceFile.getElementsByTagNameNS("*","Resource").getLength() == 0)
               throw new IdDoesNotExistException(oaiIdentifier);

           //Document sourceFile = qs.runQuery(collectionName,xqlQuery);
           //Document resultDoc = null;
           //System.out.println("the verisonNumber = " + versionNumber);
           XSLHelper xsh = new XSLHelper();
           //resultDoc = sourceFile;
           //Document oaiDoc = xsh.transformToOAI(resultDoc,versionNumber);
           oaiDoc = xsh.transformToOAI(sourceFile,versionNumber);
        
           //System.out.println("the oai from xmlexistoaicatalog = " + DomHelper.DocumentToString(oaiDoc));
           xmlDoc = DomHelper.DocumentToString(oaiDoc);
           //ByteArrayInputStream bas = new ByteArrayInputStream(xmlDoc.getBytes());
           InputSource inputs = new InputSource((xmlReader = new StringReader(xmlDoc)));
           RecordStringHandler rsh = new RecordStringHandler();
           SAXParserFactory factory = SAXParserFactory.newInstance();
           factory.setNamespaceAware(true);
           factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
           saxParser = factory.newSAXParser();
           //saxParser.parse(new File(sourceFile), rsh);
           //saxParser.parse(bas, rsh);
           saxParser.parse(inputs, rsh);
           nativeMap = rsh.getNativeRecords();
      }catch(SOAPFaultException e) {
    	  e.printStackTrace();
    	  System.out.println("exc message for soapfaultexc = " + e.getMessage());
    	  if(e.getSoapFaultType() == SOAPFaultException.NOTFOUNDSOAP_TYPE)
    		  throw new IdDoesNotExistException(oaiIdentifier);
    	  else
    		  throw new OAIInternalServerError(e.getMessage());
      }catch (SAXException e) {
          e.printStackTrace();
          throw new OAIInternalServerError(e.getMessage());
      }catch (ParserConfigurationException e) {
          e.printStackTrace();
          throw new OAIInternalServerError(e.getMessage());
      }catch(MalformedURLException e) {
          e.printStackTrace();
          throw new OAIInternalServerError(e.getMessage());
      }catch(IOException ioe) {
          ioe.printStackTrace();
          throw new OAIInternalServerError(ioe.getMessage());
      }catch(RegistryException re) {
          re.printStackTrace();
          throw new OAIInternalServerError(re.getMessage());
      }finally {
    	  xmlDoc = null;
    	  oaiDoc = null;
    	  sourceFile = null;
    	  saxParser = null;
    	  if(xmlReader != null) {
    		  xmlReader.close();
    	  }
    	  xmlReader = null;
      }
       
   }
   
   
   private void populateNativeMap(String from, String until, String set,int fromSequence) throws OAIInternalServerError, NoItemsMatchException {
       Node sourceFile;
       Document oaiDoc;
       SAXParser saxParser;
       String xmlDoc;
       StringReader xmlReader = null;
       ResourceSet rs;
       try {
           String contractVersion = props.getProperty("registry_contract_version",null);
           ISearch rsSearch = null;
           try {
               rsSearch = QueryFactory.createQueryService(contractVersion);
           }catch(Exception e) {
               throw new OAIInternalServerError("Could not get Query Service" + e.toString());
           }
           String versionNumber = rsSearch.getResourceVersion();
           
           //versionNumber = versionNumber.replace('.','_');
           String collectionName = "astrogridv" + versionNumber.replace('.','_');
           String temp;
           debug = false;

           maxListSize = conf.getInt("XMLFileOAICatalog.maxListSize",200);
           if(debug)
              System.out.println("in XMLFileOAICatalog(): maxListSize=" + maxListSize);
           
           HashMap manageAuths = null;
           boolean managedSet = false;
           if("ivo_managed".equals(set)) {
               managedSet = true;
           }
           String xqlQuery = QueryConfigExtractor.getXQLDeclarations(versionNumber);
           xqlQuery += " let $hits := (" + QueryConfigExtractor.getStartQuery(versionNumber);
           
           if(managedSet) {               
               try {
                   //manageAuths = RegistryServerHelper.getManagedAuthorities(collectionName, versionNumber);
                   AuthorityListManager alm = new AuthorityListManager();
                   manageAuths = alm.getManagedAuthorities(collectionName,versionNumber);
               }catch(XMLDBException xdbe) {
                   log.error(xdbe);
               }
               String authorityID = conf.getString("reg.amend.authorityid");
               //Set keys = manageAuths.keySet();
               java.util.Collection authList = manageAuths.values();
               Iterator keyIter = authList.iterator();
               //System.out.println("The manageAuths Full Size = " + authList.size());
               if(authList.size() == 0) {
                  throw new OAIInternalServerError("Could not find any authorites");
               }
               
               xqlQuery += " ( ";
               String identWhere = null;
               if(contractVersion.equals("1.0"))
                  identWhere = " starts-with($x/identifier,'ivo://";
               else
            	  identWhere = " starts-with($x/vr:identifier,'ivo://";
               
               String wildCard = "";
               AuthorityList al = null;
               while(keyIter.hasNext()) {
                   al = (AuthorityList)keyIter.next();
                   if(authorityID.equals(al.getOwner())) {               
                       xqlQuery +=  identWhere + al.getAuthorityID() + "') ";
                       while(keyIter.hasNext()) {
                           al = (AuthorityList)keyIter.next();
                           if(authorityID.equals(al.getOwner())) {                           
                               xqlQuery += " or " + identWhere + al.getAuthorityID() + "') ";
                           }//if
                       }//while
                   }//if
               }//while
               xqlQuery += " ) ";
           }else if("ivo_standard".equals(set) || set == null || set.trim().length() == 0) {
        	   if(contractVersion.equals("1.0"))
        		   xqlQuery += " exists($x/identifier) ";
        	   else
        		   xqlQuery += " exists($x/vr:identifier) ";
           }else {
        	   if(set.startsWith("ivo_")) {
        		   xqlQuery += " matches(@xsi:type,'" + set.substring(4) + "') ";
        	   }else {
        		   xqlQuery += " matches(@xsi:type,'" + set + "') ";
        	   }
           }
           
           if(from != null && from.trim().length() > 0) {
               xqlQuery += "and  $x/@updated >= '" + from + "' ";
           }
           if(until != null && until.trim().length() > 0) {
               xqlQuery += "and $x/@updated <= '" + until + "' ";
           }           
           //xqlQuery += " return $x";
           xqlQuery += " return $x) return subsequence($hits," + fromSequence + "," + (maxListSize + /*fromSequence*/ + 2) + ")";
           
           System.out.println("xql for OAI = " + xqlQuery);
           log.info("the build xql = " + xqlQuery);
           
           XMLDBService xdb = XMLDBFactory.createXMLDBService();
           sourceFile = null;
           Collection coll = null;
           try {
               long currentTimeInMillis = System.currentTimeMillis();
               coll = xdb.openCollection(collectionName);            
               rs = xdb.queryXQuery(coll, xqlQuery);
               log.info("OAI query time = " + (System.currentTimeMillis() - currentTimeInMillis));
               log.info("OAI query performed resulted in returns/size = " + rs.getSize());
               if(rs.getSize() > 0) {
                   Resource xmlr = rs.getMembersAsResource();
                   sourceFile = DomHelper.newDocument(xmlr.getContent().toString());
                   //System.out.println("here is the getcontent in oai = " + xmlr.getContent().toString());
               } else {
                   throw new NoItemsMatchException();
               }
           }catch(XMLDBException xdbe) {
               log.error(xdbe);
               xdbe.printStackTrace();
           }finally {
               try {
                   xdb.closeCollection(coll);
               }catch(XMLDBException xmldb) {
                   log.error(xmldb);
                   xmldb.printStackTrace();
               }
           }
           
           XSLHelper xsh = new XSLHelper();
           //Document oaiDoc = xsh.transformToOAI(resultDoc,versionNumber);
           oaiDoc = xsh.transformToOAI(sourceFile,versionNumber);

           //System.out.println("the oai from xmlexistoaicataling populatenativerecords = " + DomHelper.DocumentToString(oaiDoc));
           xmlDoc = DomHelper.DocumentToString(oaiDoc);
           //System.out.println("here is the xmlDOC = " + xmlDoc);
           //ByteArrayInputStream bas = new ByteArrayInputStream(xmlDoc.getBytes());
           System.out.println("trying inputSource");
           InputSource inputs = new InputSource((xmlReader = new StringReader(xmlDoc)));
           RecordStringHandler rsh = new RecordStringHandler();
           SAXParserFactory factory = SAXParserFactory.newInstance();
           factory.setNamespaceAware(true);
           factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
           saxParser = factory.newSAXParser();
           //saxParser.parse(bas, rsh);
           saxParser.parse(inputs, rsh);
           nativeMap = rsh.getNativeRecords();
          } catch (SAXException e) {
              e.printStackTrace();
              throw new OAIInternalServerError(e.getMessage());
          } catch (ParserConfigurationException e) {
              e.printStackTrace();
              throw new OAIInternalServerError(e.getMessage());
          }catch(MalformedURLException e) {
              e.printStackTrace();
              throw new OAIInternalServerError(e.getMessage());
          } catch(IOException ioe) {
              ioe.printStackTrace();
              throw new OAIInternalServerError(ioe.getMessage());
          } catch(RegistryException re) {
              re.printStackTrace();
              throw new OAIInternalServerError(re.getMessage());
          }finally {
        	  xmlDoc = null;
        	  oaiDoc = null;
        	  sourceFile = null;
        	  saxParser = null;
        	  rs = null;
        	  if(xmlReader != null) {
        		  xmlReader.close();
        	  }
        	  xmlReader = null;
          }
   }

    private static HashMap getSets(Properties properties) {
        //TreeMap treeMap = new TreeMap();
        HashMap hashMap = new HashMap();
        String propertyPrefix = "Sets.";
        Enumeration propNames = properties.propertyNames();
        while (propNames.hasMoreElements()) {
            String propertyName = (String)propNames.nextElement();
            if (propertyName.startsWith(propertyPrefix)) {
                hashMap.put(propertyName, properties.get(propertyName));
            }
        }
        //return new ArrayList(treeMap.values());
        return hashMap;
    }

   /**
    * Retrieve the specified metadata for the specified oaiIdentifier
    *
    * @param     oaiIdentifier the OAI identifier
    * @param     metadataPrefix the OAI metadataPrefix
    * @return    the Record object containing the result.
    * @exception CannotDisseminateFormatException signals an http status
    *                code 400 problem
    * @exception IdDoesNotExistException signals an http status code 404
    *                problem
    * @exception OAIInternalServerError signals an http status code 500
    *                problem
    */
   public String getRecord(String oaiIdentifier, String metadataPrefix)
       throws IdDoesNotExistException, CannotDisseminateFormatException,
              OAIInternalServerError {
       populateNativeMapFromIdentifier(oaiIdentifier);
       String localIdentifier
           = ((XMLExistRecordFactory)getRecordFactory()).fromOAIIdentifier(oaiIdentifier);
       String recordid = localIdentifier;
       if (schemaLocationIndexed) {
           recordid=recordid + "/" + metadataPrefix;
       }
       if (debug) System.out.println("XMLFileOAICatalog.getRecord: recordid=" + recordid);
       Object nativeRecord = nativeMap.get(recordid.toLowerCase());
       if (nativeRecord == null)
           throw new IdDoesNotExistException(oaiIdentifier);
       return constructRecord(nativeRecord, metadataPrefix);
   }


   /**
    * get a DocumentFragment containing the specified record
    */
   public String getMetadata(String oaiIdentifier, String metadataPrefix)
       throws IdDoesNotExistException, IdDoesNotExistException, CannotDisseminateFormatException,
         OAIInternalServerError {
       populateNativeMapFromIdentifier(oaiIdentifier);
       String localIdentifier
           = ((XMLExistRecordFactory)getRecordFactory()).fromOAIIdentifier(oaiIdentifier);
       String recordid = localIdentifier;
       if (schemaLocationIndexed) {
           recordid=recordid + "/" + metadataPrefix;
       }
       if (debug) System.out.println("XMLFileOAICatalog.getRecord: recordid=" + recordid);
       HashMap nativeRecord = (HashMap)nativeMap.get(recordid.toLowerCase());
       if (nativeRecord == null)
           throw new IdDoesNotExistException(oaiIdentifier);
       if (debug) {
           Iterator keys = nativeRecord.keySet().iterator();
           while (keys.hasNext())
               System.out.println(keys.next());
       }
       String result = (String)nativeRecord.get("recordString");
       if (getMetadataTransformer != null) {
           StringReader stringReader = new StringReader(result);
           StreamSource streamSource = new StreamSource(stringReader);
           StringWriter stringWriter = new StringWriter();
           try {
               synchronized (getMetadataTransformer) {
                   getMetadataTransformer.transform(streamSource, new StreamResult(stringWriter));
               }
           } catch (TransformerException e) {
               e.printStackTrace();
               throw new OAIInternalServerError(e.getMessage());
           }
           result = stringWriter.toString();
       }
       return result;
   }

    /**
     * Retrieve a list of schemaLocation values associated with the specified
     * oaiIdentifier.
     *
     * We get passed the ID for a record and are supposed to return a list
     * of the formats that we can deliver the record in.  Since we are assuming
     * that all the records in the directory have the same format, the
     * response to this is static;
     *
     * @param oaiIdentifier the OAI identifier
     * @return a Vector containing schemaLocation Strings
     * @exception OAIBadRequestException signals an http status code 400
     *            problem
     * @exception OAINotFoundException signals an http status code 404 problem
     * @exception OAIInternalServerError signals an http status code 500
     *            problem
     */
    public Vector getSchemaLocations(String oaiIdentifier)
      throws IdDoesNotExistException, OAIInternalServerError, NoMetadataFormatsException {
        Vector v = new Vector();
        String localIdentifier
            = ((XMLExistRecordFactory)getRecordFactory()).fromOAIIdentifier(oaiIdentifier);
        Iterator iterator = nativeMap.entrySet().iterator();
        int numRows = nativeMap.entrySet().size();
        for (int i=0; i<numRows; ++i) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
       if (((XMLExistRecordFactory)getRecordFactory()).getOAIIdentifier(nativeRecord).equals(oaiIdentifier)) {
      Vector schemaLocations = ((XMLExistRecordFactory)getRecordFactory()).getSchemaLocations(nativeRecord);
      Iterator itemIterator = schemaLocations.iterator();
      while (itemIterator.hasNext())
          v.add(itemIterator.next());
       }
        }
        if (v.size() > 0) {
            return v;
        } else {
            throw new IdDoesNotExistException(oaiIdentifier);
        }
    }


    /**
     * Retrieve a list of Identifiers that satisfy the criteria parameters
     *
     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
     * date is desired
     * @param until ending date in the form of YYYY-MM-DD or null if latest
     * date is desired
     * @param set set name or null if no set is desired
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and an "identifiers" Map object. The "identifiers" Map contains OAI
     * identifier keys with corresponding values of "true" or null depending on
     * whether the identifier is deleted or not.
     * @exception OAIBadRequestException signals an http status code 400
     *            problem
     * @exception OAIInternalServerError signals an http status code 500
     *            problem
     */
    public Map listIdentifiers(String from, String until, String set, String metadataPrefix)
        throws BadArgumentException, CannotDisseminateFormatException, OAIInternalServerError,
               NoItemsMatchException {
        populateNativeMap(from, until, set, 1);
        purge(); // clean out old resumptionTokens
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();
   Iterator iterator = nativeMap.entrySet().iterator();
   int numRows = nativeMap.entrySet().size();
   int count = 0;
   while (count < maxListSize && iterator.hasNext()) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
            String recordDate = ((XMLExistRecordFactory)getRecordFactory()).getDatestamp(nativeRecord);
            String schemaLocation = (String)nativeRecord.get("schemaLocation");
            //List setSpecs = (List)nativeRecord.get("setSpecs");
            if ((!schemaLocationIndexed || schemaLocation.equals(getCrosswalks().getSchemaLocation(metadataPrefix)))) {
                /*
                && (set==null || setSpecs.contains(set))) {
                */
                String[] header = ((XMLExistRecordFactory)getRecordFactory()).createHeader(nativeRecord);
                headers.add(header[0]);
                identifiers.add(header[1]);
                count++;
            }
   }
   if (count == 0)
       throw new NoItemsMatchException();

   /* decide if you're done */
   if (iterator.hasNext()) {
       String resumptionId = getRSName();
       resumptionResults.put(resumptionId, " ");

       /*****************************************************************
        * Construct the resumptionToken String however you see fit.
        *****************************************************************/
       StringBuffer resumptionTokenSb = new StringBuffer();
       resumptionTokenSb.append(resumptionId);
       resumptionTokenSb.append(":");
       resumptionTokenSb.append(Integer.toString(count));
       resumptionTokenSb.append(":");
       resumptionTokenSb.append(Integer.toString(numRows));
       resumptionTokenSb.append(":");
       if(from != null)
           resumptionTokenSb.append(from);
       else
           resumptionTokenSb.append(" ");
       resumptionTokenSb.append(":");
       if(until != null)
           resumptionTokenSb.append(until);
       else
           resumptionTokenSb.append(" ");       
       resumptionTokenSb.append(":");
       if(set != null)
           resumptionTokenSb.append(set);
       else
           resumptionTokenSb.append(" ");       
       resumptionTokenSb.append(":");       
       resumptionTokenSb.append(metadataPrefix);

       /*****************************************************************
        * Use the following line if you wish to include the optional
        * resumptionToken attributes in the response. Otherwise, use the
        * line after it that I've commented out.
        *****************************************************************/
       //listIdentifiersMap.put("resumptionMap",
       //        getResumptionMap(resumptionTokenSb.toString(),
       //               numRows,
       //               0));
        listIdentifiersMap.put("resumptionMap",
                getResumptionMap(resumptionTokenSb.toString()));
   }
        listIdentifiersMap.put("headers", headers.iterator());
        listIdentifiersMap.put("identifiers", identifiers.iterator());
        //System.out.println("cleaning out nativeMap should be done with it");
        //nativeMap.clear();
        //nativeMap = null;
        return listIdentifiersMap;
    }

    /**
     * Retrieve the next set of Identifiers associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listIdentifiers() Map result.
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and an "identifiers" Map object. The "identifiers" Map contains OAI
     * identifier keys with corresponding values of "true" or null depending on
     * whether the identifier is deleted or not.
     * @exception OAIBadRequestException signals an http status code 400
     *            problem
     * @exception OAIInternalServerError signals an http status code 500
     *            problem
     */
    public Map listIdentifiers(String resumptionToken)
      throws BadResumptionTokenException, OAIInternalServerError {
        purge(); // clean out old resumptionTokens
        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();

        /**********************************************************************
         * parse your resumptionToken and look it up in the resumptionResults,
         * if necessary
         **********************************************************************/
        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
        String resumptionId;
        int oldCount;
        String metadataPrefix;
        String from, until, set;
        int numRows;
        try {
            resumptionId = tokenizer.nextToken();
            oldCount = Integer.parseInt(tokenizer.nextToken());
            numRows = Integer.parseInt(tokenizer.nextToken());
            from = tokenizer.nextToken();
            if(from != null && from.trim().length() == 0)
                from = null;
            until = tokenizer.nextToken();
            if(until != null && until.trim().length() == 0)
                until = null;            
            set = tokenizer.nextToken();
            if(set != null && set.trim().length() == 0)
                set = null;            
            metadataPrefix = tokenizer.nextToken();
        } catch (NoSuchElementException e) {
            throw new BadResumptionTokenException();
        }
        try {
            populateNativeMap(from, until, set, oldCount);
        } catch(NoItemsMatchException nime) {
            throw new OAIInternalServerError(nime.getMessage());
        }
        
        Iterator iterator = nativeMap.entrySet().iterator();
        

        numRows = nativeMap.entrySet().size();
        int count = 0;
        while (count < maxListSize && iterator.hasNext()) {
                 Map.Entry entryNativeMap = (Map.Entry)iterator.next();
                 HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
                 String recordDate = ((XMLExistRecordFactory)getRecordFactory()).getDatestamp(nativeRecord);
                 String schemaLocation = (String)nativeRecord.get("schemaLocation");
                 //List setSpecs = (List)nativeRecord.get("setSpecs");
                 if ((!schemaLocationIndexed || schemaLocation.equals(getCrosswalks().getSchemaLocation(metadataPrefix)))) {
                     String[] header = ((XMLExistRecordFactory)getRecordFactory()).createHeader(nativeRecord);
                     headers.add(header[0]);
                     identifiers.add(header[1]);
                     count++;
                 }
        }

        /* decide if you're done */
        if (iterator.hasNext()) {
            /*****************************************************************
             * Construct the resumptionToken String however you see fit.
             *****************************************************************/
            StringBuffer resumptionTokenSb = new StringBuffer();
            resumptionTokenSb.append(resumptionId);
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString((count+oldCount)));
            resumptionTokenSb.append(":");
            resumptionTokenSb.append(Integer.toString(numRows));
            resumptionTokenSb.append(":");
            if(from != null)
                resumptionTokenSb.append(from);
            else
                resumptionTokenSb.append(" ");
            resumptionTokenSb.append(":");
            if(until != null)
                resumptionTokenSb.append(until);
            else
                resumptionTokenSb.append(" ");       
            resumptionTokenSb.append(":");
            if(set != null)
                resumptionTokenSb.append(set);
            else
                resumptionTokenSb.append(" ");
            resumptionTokenSb.append(":");            
            resumptionTokenSb.append(metadataPrefix);

            /*****************************************************************
             * Use the following line if you wish to include the optional
             * resumptionToken attributes in the response. Otherwise, use the
             * line after it that I've commented out.
             *****************************************************************/
            //listIdentifiersMap.put("resumptionMap",
            //        getResumptionMap(resumptionTokenSb.toString(),
            //               numRows,
            //               0));
             listIdentifiersMap.put("resumptionMap",
                     getResumptionMap(resumptionTokenSb.toString()));
        }
             listIdentifiersMap.put("headers", headers.iterator());
             listIdentifiersMap.put("identifiers", identifiers.iterator());

             //System.out.println("cleaning out nativeMap should be done with it (with resumptiontoken)");
             //nativeMap.clear();
             //nativeMap = null;             
             return listIdentifiersMap;
    }


    /**
     * Utility method to construct a Record object for a specified
     * metadataFormat from a native record
     *
     * @param nativeRecord native item from the dataase
     * @param metadataPrefix the desired metadataPrefix for performing the crosswalk
     * @return the <record/> String
     * @exception CannotDisseminateFormatException the record is not available
     * for the specified metadataPrefix.
     */
    private String constructRecord(Object nativeRecord, String metadataPrefix)
        throws CannotDisseminateFormatException, OAIInternalServerError {
        String schemaURL = null;

        Iterator setSpecs = getSetSpecs(nativeRecord);
        Iterator abouts = getAbouts(nativeRecord);

        if (metadataPrefix != null) {
            if (debug) {
                System.out.println(getCrosswalks());
            }
            if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null)
                throw new CannotDisseminateFormatException(metadataPrefix);
        }
        //System.out.println("calling create for metataDataPrefix = " + metadataPrefix);
        //System.out.println("the nativeRecord CHECK = " + nativeRecord);
        return ((XMLExistRecordFactory)getRecordFactory()).create(nativeRecord, schemaURL, metadataPrefix, setSpecs, abouts);
    }

    /**
     * get an Iterator containing the setSpecs for the nativeRecord
     *
     * @param rs ResultSet containing the nativeRecord
     * @return an Iterator containing the list of setSpec values for this nativeRecord
     */
    private Iterator getSetSpecs(Object nativeRecord)
   throws OAIInternalServerError {
   try {
       return ((XMLExistRecordFactory)getRecordFactory()).getSetSpecs(nativeRecord);
   } catch (Exception e) {
       e.printStackTrace();
       throw new OAIInternalServerError(e.getMessage());
   }
    }

    /**
     * get an Iterator containing the abouts for the nativeRecord
     *
     * @param rs ResultSet containing the nativeRecord
     * @return an Iterator containing the list of about values for this nativeRecord
     */
    private Iterator getAbouts(Object nativeRecord)
   throws OAIInternalServerError {
        return null;
    }

    /**
     * Retrieve a list of records that satisfy the specified criteria
     *
     * @param from beginning date in the form of YYYY-MM-DD or null if earliest
     * date is desired
     * @param until ending date in the form of YYYY-MM-DD or null if latest
     * date is desired
     * @param set set name or null if no set is desired
     * @param metadataPrefix the OAI metadataPrefix
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and a "records" Iterator object. The "records" Iterator contains a
     * set of Records objects.
     * @exception OAIBadRequestException signals an http status code 400
     *            problem
     * @exception OAIInternalServerError signals an http status code 500
     *            problem
     */
    public Map listRecords(String from, String until, String set,
                                    String metadataPrefix)
      throws BadArgumentException, CannotDisseminateFormatException,
      OAIInternalServerError, NoItemsMatchException {
        populateNativeMap(from, until, set, 1);
        String requestedSchemaLocation = getCrosswalks().getSchemaLocation(metadataPrefix);
        purge(); // clean out old resumptionTokens
        Map listRecordsMap = new HashMap();
        LinkedList records = new LinkedList();
        Iterator iterator = nativeMap.entrySet().iterator();
        int numRows = nativeMap.entrySet().size();
        if (debug) {
            //System.out.println("XMLFileOAICatalog.listRecords: numRows=" + numRows);
        }
        int count = 0;
        while (count < maxListSize && iterator.hasNext()) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            HashMap nativeRecord = (HashMap)entryNativeMap.getValue();
            String recordDate = ((XMLExistRecordFactory)getRecordFactory()).getDatestamp(nativeRecord);
            String schemaLocation = (String)nativeRecord.get("schemaLocation");
            List setSpecs = (List)nativeRecord.get("setSpecs");
            if (debug) {
                //System.out.println("XMLFileOAICatalog.listRecord: recordDate=" + recordDate);
                //System.out.println("XMLFileOAICatalog.listRecord: requestedSchemaLocation=" + requestedSchemaLocation);
                //System.out.println("XMLFileOAICatalog.listRecord: schemaLocation=" + schemaLocation);
            }
            if ((!schemaLocationIndexed || requestedSchemaLocation.equals(schemaLocation))) {
                String record = constructRecord(nativeRecord, metadataPrefix);
                if (debug) {
                    //System.out.println("XMLFileOAICatalog.listRecords: record=" + record);
                }
                records.add(record);
                count++;
            }
   }

        if (count == 0)
            throw new NoItemsMatchException();

   /* decide if you're done */
   if (iterator.hasNext()) {
       String resumptionId = getRSName();
       resumptionResults.put(resumptionId, " ");

       /*****************************************************************
        * Construct the resumptionToken String however you see fit.
        *****************************************************************/
       StringBuffer resumptionTokenSb = new StringBuffer();
       resumptionTokenSb.append(resumptionId);
       resumptionTokenSb.append(":");
       resumptionTokenSb.append(Integer.toString(count));
       resumptionTokenSb.append(":");
       resumptionTokenSb.append(Integer.toString(numRows));
       resumptionTokenSb.append(":");
       if(from != null)
           resumptionTokenSb.append(from);
       else
           resumptionTokenSb.append(" ");
       resumptionTokenSb.append(":");
       if(until != null)
           resumptionTokenSb.append(until);
       else
           resumptionTokenSb.append(" ");

       resumptionTokenSb.append(":");
       if(set != null)
           resumptionTokenSb.append(set);
       else
           resumptionTokenSb.append(" ");       
       resumptionTokenSb.append(":");
       resumptionTokenSb.append(metadataPrefix);

       /*****************************************************************
        * Use the following line if you wish to include the optional
        * resumptionToken attributes in the response. Otherwise, use the
        * line after it that I've commented out.
        *****************************************************************/
       //listRecordsMap.put("resumptionMap",
       //        getResumptionMap(resumptionTokenSb.toString(),
       //               numRows, 0));
        listRecordsMap.put("resumptionMap",
                getResumptionMap(resumptionTokenSb.toString()));
   }
        listRecordsMap.put("records", records.iterator());
        return listRecordsMap;
    }


    /**
     * Retrieve the next set of records associated with the resumptionToken
     *
     * @param resumptionToken implementation-dependent format taken from the
     * previous listRecords() Map result.
     * @return a Map object containing an optional "resumptionToken" key/value
     * pair and a "records" Iterator object. The "records" Iterator contains a
     * set of Records objects.
     * @exception OAIBadRequestException signals an http status code 400
     *            problem
     * @exception OAIInternalServerError signals an http status code 500
     *            problem
     */
    public Map listRecords(String resumptionToken)
      throws BadResumptionTokenException, OAIInternalServerError {
        purge(); // clean out old resumptionTokens
        Map listRecordsMap = new HashMap();
        LinkedList records = new LinkedList();

        /**********************************************************************
         * parse your resumptionToken and look it up in the resumptionResults,
         * if necessary
         **********************************************************************/
        StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
        String resumptionId;
        int oldCount;
        String metadataPrefix;
        String from, until, set, tempDT;
        int numRows;
        System.out.println("Begin tokenize of resumptionToken = " + resumptionToken);
        try {
            resumptionId = tokenizer.nextToken();
            oldCount = Integer.parseInt(tokenizer.nextToken());
            numRows = Integer.parseInt(tokenizer.nextToken());
            System.out.println("resid = " + resumptionId + " oldCount = " + oldCount + " numrows = " + numRows);
            from = tokenizer.nextToken();
            System.out.println("from = " + from);
            if(from != null && from.trim().length() == 0) {
                from = null;
            }
            if(from != null  && from.indexOf("T") != -1) {
            	//it seems on resumption tokens these from and until will have a T##:##:##Z which
            	//screws things up some so lets tokenize again to the Z
            	from += ":" + tokenizer.nextToken() + ":";
            	from += tokenizer.nextToken();
            }
            until = tokenizer.nextToken();
            System.out.println("until = " + until);
            if(until != null && until.trim().length() == 0)
                until = null;     
            if(until != null) {
            	//really should not have to deal with until but much like From make sure it is
            	//parsed to the 'Z'
            	until += ":" + tokenizer.nextToken() + ":";
            	until += tokenizer.nextToken();
            }
            
            set = tokenizer.nextToken();
            System.out.println("set = " + set);
            if(set != null && set.trim().length() == 0)
                set = null;            
            metadataPrefix = tokenizer.nextToken();
            System.out.println("metadataPrefix = " + metadataPrefix);
        } catch (NoSuchElementException e) {
            System.out.println("throwing badresumptiontokenexception");
            throw new BadResumptionTokenException();
        }

        /* Get some more records from your database */
        try {
            populateNativeMap(from, until, set, oldCount);
        } catch(NoItemsMatchException nime) {
            throw new OAIInternalServerError(nime.getMessage());
        }
        
        Iterator iterator = nativeMap.entrySet().iterator();

        /* load the records ArrayLists. */
        int count = 0;
        while (count < maxListSize && iterator.hasNext()) {
            Map.Entry entryNativeMap = (Map.Entry)iterator.next();
            try {
                Object nativeRecord = nativeMap.get((String)entryNativeMap.getKey());
                String record = constructRecord(nativeRecord, metadataPrefix);
                records.add(record);
                count++;
            } catch (CannotDisseminateFormatException e) {
                /* the client hacked the resumptionToken beyond repair */
                throw new BadResumptionTokenException();
       }
   }

   /* decide if you're done. */
   if (iterator.hasNext()) {
       resumptionId = getRSName();
       resumptionResults.put(resumptionId, " ");

       /*****************************************************************
        * Construct the resumptionToken String however you see fit.
        *****************************************************************/
       StringBuffer resumptionTokenSb = new StringBuffer();
       resumptionTokenSb.append(resumptionId);
       resumptionTokenSb.append(":");
       resumptionTokenSb.append(Integer.toString(oldCount + count));
       resumptionTokenSb.append(":");
       resumptionTokenSb.append(Integer.toString(numRows));
       resumptionTokenSb.append(":");              
       if(from != null)
           resumptionTokenSb.append(from);
       else
           resumptionTokenSb.append(" ");
       resumptionTokenSb.append(":");
       if(until != null)
           resumptionTokenSb.append(until);
       else
           resumptionTokenSb.append(" ");       
       resumptionTokenSb.append(":");
       if(set != null)
           resumptionTokenSb.append(set);
       else
           resumptionTokenSb.append(" ");       
       resumptionTokenSb.append(":");       
       resumptionTokenSb.append(metadataPrefix);

       /*****************************************************************
        * Use the following line if you wish to include the optional
        * resumptionToken attributes in the response. Otherwise, use the
        * line after it that I've commented out.
        *****************************************************************/
       //listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
       //                      numRows,
       //                      oldCount));
                 listRecordsMap.put("resumptionMap",
                                        getResumptionMap(resumptionTokenSb.toString()));
   }

        listRecordsMap.put("records", records.iterator());
        return listRecordsMap;
    }


    public Map listSets() throws OAIInternalServerError,
             NoSetHierarchyException {
        if (sets.size() == 0)
            throw new NoSetHierarchyException();
        /*
         Map listSetsMap = new LinkedHashMap();
         listSetsMap.put("sets", sets.iterator());
         return listSetsMap;
        */
        return sets;
    }


    public Map listSets(String resumptionToken)
      throws BadResumptionTokenException, OAIInternalServerError {
   throw new BadResumptionTokenException();
    }


    /**
     * close the repository
     */
    public void close() { }


    /**
     * Purge tokens that are older than the time-to-live.
     */
    private void purge() {
        ArrayList old = new ArrayList();
        Date      then, now = new Date();
        Iterator  keySet = resumptionResults.keySet().iterator();
        String    key;

        while (keySet.hasNext()) {
            key=(String)keySet.next();
            then=new Date(Long.parseLong(key)+getMillisecondsToLive());
            if (now.after(then)) {
                old.add(key);
            }
        }
        Iterator iterator = old.iterator();
        while (iterator.hasNext()) {
            key = (String)iterator.next();
            resumptionResults.remove(key);
        }
    }


    /**
     * Use the current date as the basis for the resumptiontoken
     *
     * @return a long integer version of the current time
     */
    private synchronized static String getRSName() {
        Date now = new Date();
        return Long.toString(now.getTime());
    }
}
