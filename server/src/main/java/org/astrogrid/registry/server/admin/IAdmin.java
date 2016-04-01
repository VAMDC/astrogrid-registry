package org.astrogrid.registry.server.admin;

import org.w3c.dom.Document;
import javax.xml.stream.*;
import org.astrogrid.registry.RegistryException;
import org.xmldb.api.base.XMLDBException;

/**
 * Interface: IAdmin
 * Description: Admin interface used for any administration to the database
 * such as storing, removing, indexing into the database.  Implemented by
 * all RegistryAdminService classes.
 * @author kevinbenson
 *
 */
public interface IAdmin {
	
	/**
	 * Method: Update
	 * Description: stores into the database if possible the Resources in the 
	 * Document DOM object and returns any Errors or Successes in the Stream Reader
	 * which is used via XFire to return to the client.  Should note it just uses
	 * updateInternal and takes the returned DOM and streams that.
	 * @param update Document DOM containing 1 or many Resources based on the IVOA Resource schemas.
	 * @return Stream to the client of the results.
	 */
	public XMLStreamReader Update(Document update);
	
	/**
	 * Method: updateInternal
	 * Description: stores into the database if possible the Resources in the 
	 * Document DOM object and returns any Errors or Successes in a Document DOM.

	 * @param update Document DOM containing 1 or many Resources based on the IVOA Resource schemas.
	 * @return Document DOM of the results.
	 */	
	public Document updateInternal(Document update);	
	
	/**
	 * Method: remove
	 * Description: Removes an XML Resource from the databased.  All data in the database
	 * has a unique string called an identifier for a name, this is passed in for removal.
	 * @param id unique string in the database, happens to be the same as the identifier element in 
	 * the Resource Schema.
	 * @throws RegistryException
	 * @throws XMLDBException
	 */
	public void remove(String id) throws RegistryException, XMLDBException;
	
	public Document updateIndex(Document doc) throws XMLDBException, RegistryException;
	
}