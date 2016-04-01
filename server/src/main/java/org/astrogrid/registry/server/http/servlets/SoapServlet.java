package org.astrogrid.registry.server.http.servlets;

import java.net.URL;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.binding.MessageBindingProvider;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.http.XFireServlet;

import org.astrogrid.registry.server.SoapDispatcher;
import org.astrogrid.registry.server.SoapAdminDispatcher;
import org.astrogrid.registry.server.SoapHarvestDispatcher;

import javax.servlet.ServletException;

public class SoapServlet extends XFireServlet
{
	
  @Override
  public void init() throws ServletException
  {
    super.init();
    System.out.println("done with init() doing objectffservicefactory");

    //make an xfire factory class and bind it to a message style for us to do the Soap ourselves
    ObjectServiceFactory factory = new ObjectServiceFactory(new MessageBindingProvider());
    factory.setStyle("message");
    
    //now create the linkup with the SoapDispather to the namespace and endpoint being "RegistryQueryv1_0"
    //this means when the soapservlet is called with a RegistryQueryv1_0 the factory will know
    //to use the SoapDispatcher class.
    Service searchServicev1_0 = factory.create(SoapDispatcher.class,"RegistryQueryv1_0",
    		org.astrogrid.registry.server.query.v1_0.RegistryQueryService.QUERY_WSDL_NS,null);
    

    //do the same for my Update and Harvest webservice clases.
    Service adminServicev1_0 = factory.create(SoapAdminDispatcher.class,"RegistryUpdatev1_0",
    		org.astrogrid.registry.server.admin.v1_0.RegistryAdminService.ADMIN_WSDL_NS,null);
    
    
    Service searchHarvestv1_0 = factory.create(SoapHarvestDispatcher.class,"RegistryHarvestv1_0",
    		org.astrogrid.registry.server.query.v1_0.OAIService.OAI_WSDL_NS,null);
         
    
    //we register the service with the controller that handles soap requests
    getController().getServiceRegistry().register(searchServicev1_0);
    getController().getServiceRegistry().register(adminServicev1_0);
    getController().getServiceRegistry().register(searchHarvestv1_0);
    
  }
}