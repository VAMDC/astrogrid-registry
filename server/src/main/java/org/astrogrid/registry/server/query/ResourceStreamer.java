package org.astrogrid.registry.server.query;

import org.astrogrid.registry.server.query.ResourceStreamDelegate;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import java.util.List;

/**
 * Class: ResultStreamer
 * Description: Currently passes the streaming to be done by the 
 * RegistryXMLStreamDelegate.  Along with returning a Resource conent.
 * 
 * @author kevinbenson
 *
 */
public class ResourceStreamer extends ResourceStreamDelegate {
	
	/**
	 * Constructor:
	 * Description: Basic ResultStreamer that passes all the streaming up
	 * to the Delegate which is what other ResultStreamer classes use as well.
	 * @param resSet Resource Set of XML Resource content.  This set does not
	 * contain the XML yet.  Acts much like a ResultSet need a cursor to go through
	 * the set and get the XML.
	 * @param xmlWrapper Small xml string that will be used as the wrapper around the XML in
	 * the ResourceSet.
	 */
	public ResourceStreamer(List resSet, String xmlWrapper) {
		super(resSet, xmlWrapper);
	}
		
	/**
	 * Method: getResourceContent
	 * Description:  Just returns a string version of one XML Resource.
	 * @param res XML Resource object
	 * @param identOnly not used at the moment.
	 * @return String version of the XML Resource.
	 * @throws org.xmldb.api.base.XMLDBException
	 */
	public String getResourceContent(Resource res, boolean identOnly) throws org.xmldb.api.base.XMLDBException {
		return res.getContent().toString();
	}	
}