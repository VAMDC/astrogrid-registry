package org.astrogrid.registry.client.query.v0_1;

import java.net.URL;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.astrogrid.registry.RegistryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.astrogrid.registry.common.RegistryDOMHelper;

/** 
 * The QueryRegistry class is a delegate to a web service that submits an XML formatted
 * registry query to the to the server side web service also named the same RegistryService.
 * This delegate helps the user browse the registry and also the OAI. 
 * 
 * @see org.astrogrid.registry.common.RegistryInterface
 * @link http://www.ivoa.net/twiki/bin/view/IVOA/IVOARegWp03
 * @author Kevin Benson
 */
public class QueryRegistry extends org.astrogrid.registry.client.query.QueryRegistry implements RegistryService {
	 
	private String NAMESPACE_URI =
	      "http://www.ivoa.net/wsdl/RegistrySearch/v1.0";
	
	private static final String CONTRACT_VERSION = "1.0";
	
	  /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory.getLog(QueryRegistry.class);
	

	 public String getSoapBodyNamespaceURI() {
		   return this.NAMESPACE_URI;
	 }
	 
	 public String getContractVersion() {
		 return this.CONTRACT_VERSION;
	 }
	 
	 public String getDeclaredNamespacesForXQuery() {
		 return "declare namespace vr = \"http://www.ivoa.net/xml/VOResource/v0.10\";" +
		 		" declare namespace vor=\"http://www.ivoa.net/xml/RegistryInterface/v0.1\"; " +
		 		" declare namespace vs = \"http://www.ivoa.net/xml/VODataService/v0.5\";" +
		 		" declare namespace cs = \"http://www.ivoa.net/xml/ConeSearch/v0.3\"; " +
		 		" declare namespace sia = \"http://www.ivoa.net/xml/SIA/v0.7\";" +
		 		" declare namespace vg = \"http://www.ivoa.net/xml/VORegistry/v0.3\";" +
		 		" declare namespace vc = \"http://www.ivoa.net/xml/VORegistry/v0.3\";" +
		 		" declare namespace cea= \"http://www.ivoa.net/xml/CEAService/v0.2\";" +
		 		" declare namespace ceapd= \"http://www.astrogrid.org/schema/AGParameterDefinition/v1\";" +
		 		" declare namespace ag= \"http://www.astrogrid.org/schema/AstrogridDataService/v0.2\";" +
		 		" declare namespace tdb= \"urn:astrogrid:schema:vo-resource-types:TabularDB:v0.3\";" +
		 		" declare namespace xsi= \"http://www.w3.org/2001/XMLSchema-instance\"; ";
	 }
	 
	   
	   /**
	    * Performas a query to return all Resources of a type of Registry.
	    * @return XML DOM of Resources queried from the registry.
	    * @throws RegistryException problem during the query servor or client side.
	    */
	   public Document getRegistries() throws RegistryException {		   
		  String xquery = getDeclaredNamespacesForXQuery() + "  let $resources := (//vor:Resource[contains(@xsi:type,'Registry') and @status='active']) return <VOResources>{$resources}</VOResources>";
		  System.out.println("xquery to be performed = " + xquery);
		  return xquerySearch(xquery);
	       
	   }
	   
	   /**
	    * Queries for all the authorities managed by this registry. By loading this
	    * registries main registry resource type and looking for the ManagedAuthority
	    * elements, currently does not work with version 0.10.  But is slowly being
	    * factored out of use.
	    * 
	    * @return a hashmap of all the managed authority id's.
	    */
	   public HashMap managedAuthorities() throws RegistryException {
		   String xquery = getDeclaredNamespacesForXQuery() + " let $idents := (for $resource in //vor:Resource[contains(@xsi:type,'Registry') and @status='active'] return <resource>{$resource/vr:identifier}{$resource/vg:managedAuthority}</resource>)" +
	  		" return <resources>{$idents}</resources>";
		   System.out.println("xquery to be performed = " + xquery);
		      Document doc =  xquerySearch(xquery);
		      logger.debug("managedAuthorities() - entered managedAuthorities");
		      HashMap hm = null;	      
		      String identTemp = null;
		      if (doc != null) {
		    	    //System.out.println("the doctostring = " + DomHelper.DocumentToString(doc));
		            NodeList nl = doc.getElementsByTagNameNS("*","resource");            
		            hm = new HashMap();
		            //System.out.println("there are " + nl.getLength() + " resource elements");
		            for (int i = 0; i < nl.getLength(); i++) {
		               NodeList childNodes = nl.item(i).getChildNodes();
		               //System.out.println("it has " + childNodes.getLength() + " child nodes");
		               for(int j = 0;j < childNodes.getLength();j++) {
		            	   //System.out.println("node name = " + childNodes.item(j).getNodeName());
		            	   if(childNodes.item(j).getLocalName() != null) {
			            	   //System.out.println("local name = " + childNodes.item(j).getLocalName());
			            	   if(childNodes.item(j).getLocalName().equals("identifier")) {
			            		   //System.out.println("yes found a identifier");
			            		   //identTemp = childNodes.item(j).getFirstChild().getNodeValue();
			            		   identTemp = RegistryDOMHelper.getAuthorityID((Element)childNodes.item(j).getParentNode());
			            	   }else if(childNodes.item(j).getLocalName().equals("managedAuthority")) {
			            		   hm.put(childNodes.item(j).getFirstChild().getNodeValue(), identTemp);
			            	   }
		            	   }//if
		               }//for
		            } //for
		      }       
		      logger.debug("managedAuthorities() - exiting managedAuthorities");
		      return hm;
	   }
	 
	   /**
	    * Main constructor to allocate the endPoint variable.
	    * @param endPoint location to the web service.
	    * @author Kevin Benson
	    */
	   public QueryRegistry(URL endPoint) {
		   super(endPoint);
	   }
}