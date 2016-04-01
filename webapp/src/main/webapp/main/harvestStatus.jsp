<%@ page import="org.astrogrid.registry.server.harvest.*,
                 org.astrogrid.store.Ivorn,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.w3c.dom.Document,
                 org.astrogrid.io.Piper,
                 org.astrogrid.util.DomHelper,
                 org.apache.axis.utils.XMLUtils,
                 java.io.*"
   isThreadSafe="false"
   contentType="text/xml"                 
   session="false" %>
<%
   ISearch server = JSPHelper.getQueryService(request);
   String versionNumber = server.getResourceVersion();
   RegistryHarvestAdmin rha = new RegistryHarvestAdmin();
   //this version number is the actual resoruce version like 0.10  not the contract version.
   Document entry = rha.getStatus(request.getParameter("IVORN"),versionNumber);
   
   if (entry == null) {
       out.write("<Error>No entry returned</Error>");
   } else {
      out.write("<Description>Below you will find Status information about the harvesting of this particular " +
      " registry. Note the last harvest is generally at the bottom of the page. " +
      " StatsDate shows the date of the last harvests, Info elements normally contain Validation errors of resources that did not get harvested and a comma seperated " +
      " List of Resources harvested on that particular date. If no Harvest information is shown below this means no Harvesting occurred yet for this registry.");
      
      XMLUtils.ElementToWriter(entry.getDocumentElement(),out);
      out.write("</Description>");
   }
%>
