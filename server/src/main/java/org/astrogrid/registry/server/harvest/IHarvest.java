package org.astrogrid.registry.server.harvest;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.w3c.dom.Node;
import org.astrogrid.registry.RegistryException;

/**
 * Interface: IHarvest
 * Description: Basic harvest interface that is used for performing harvests
 * of other Registries or Services.
 * 
 * @author kevinbenson
 *
 */
public interface IHarvest {
    
	/**
	 * Method: harvestAll
	 * Description: Starts a harvest of all Registries and uses dates if possible.
	 *
	 */
    public void harvestAll();
    
    /**
     * Method: harvestAll
     * Description: starts a harvest of all Registries.
     * @param onlyRegistries Should always pass in true, currently does nothing if false.  In the future
     * a client/user can harvest services as well not just registries.
     * @param useDates use dates from the Status Documents to determine when the last harvest was done and
     * get contents 'from' that last harvest date.  If false it will harvest everything from Registries.
     * @throws RegistryException
     */
    public void harvestAll(boolean onlyRegistries,boolean useDates) throws RegistryException;
    
    /**
     * Method: harvestURL
     * Description: Currently not used allows manual harvest via a url.
     * @param url url of a OAI interface.
     * @param invocationType Soap or HTTP-GET based.  Needs to be WebService or WebBrowser, ParamHTTP
     * @param dt from date used for the harvest.  May be null to do all.
     * @param setName special OAI set on the Registry
     * @throws RegistryException
     * @throws IOException
     */
    public void harvestURL(URL url, String invocationType, Date dt, String setName) throws RegistryException, IOException;
    
    /**
     * Method: beginHarvest
     * Description: Begins a Harvest for a Registrly Element DOM that came from the DB.
     * @param resource DOM of a Registry type.
     * @param dt from date used to perform a harvest from a particular date.
     * @param lastResumptionToken to start from a previous token.  Used in huge about of Resources and have to 
     * page through many Resources.
     * @throws RegistryException
     * @throws IOException
     */
    public void beginHarvest(Node resource, Date dt, String lastResumptionToken) throws RegistryException, IOException;
    
    /**
     * Method: beginHarvest
     * Description: Begins a Harvest for a particular URL.
     * @param resource DOM of a Registry type.
     * @param accessURL OAI harvest url
     * @param invocationType Soap or HTTP-GET based.  Needs to be WebService or WebBrowser, ParamHTTP
     * @param lastResumptionToken to start from a previous token.  Used in huge about of Resources and have to 
     * page through many Resources.
     * @param identifier unique identifier string mainly used for logging or checking properties file for anything
     * special to use during the harvest such as a unique set.
     * @param setName a Set that can be used on the harvest.
     * @throws RegistryException
     * @throws IOException
     */
    public String beginHarvest(String accessURL, String invocationType, Date dt, String lastResumptionToken, String identifier, String setName) throws RegistryException, IOException;
    
}