package org.astrogrid.registry.client.query;

import java.util.Date;

import org.astrogrid.registry.RegistryException;
import org.w3c.dom.Document;



/**
 * 
 * Class: RegistryService
 * Type: Interface
 * Description: A interface used to be given to clients from the Delegate Factory for querying the registry.
 * Essentially this is the interface to the delegate. See QueryRegistry for the implemented class of this
 * interface.
 * 
 * @see org.astrogrid.registry.client.query.QueryRegistry
 * 
 * @link http://www.ivoa.net/twiki/bin/view/IVOA/IVOARegWp03
 * @author Kevin Benson
 */
public interface OAIService  {
       
    public Document identify() throws RegistryException;
    
    public Document listRecords() throws RegistryException;
    
    public Document listRecords(String resumptionToken) throws RegistryException;    
   
    public Document listRecords(Date fromDate) throws RegistryException;
   
    public Document listRecords(String metadataPrefix, Date fromDate, Date untilDate, String resumptionToken) throws RegistryException;
   
    public Document listMetadataFormats(String identifier) throws RegistryException;
   
    public Document getRecord(String identifier) throws RegistryException;
   
    public Document getRecord(String identifier, String metadataPrefix) throws RegistryException;
   
    public Document listIdentifiers() throws RegistryException;

    public Document listIdentifiers(String resumptionToken) throws RegistryException;
   
    public Document listIdentifiers(String metadataPrefix, Date fromDate, Date untilDate, String resumptionToken) throws RegistryException;   
}