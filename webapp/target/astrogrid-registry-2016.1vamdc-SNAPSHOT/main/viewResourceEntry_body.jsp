<%@ page import="org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,
                 org.w3c.dom.Document,
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.registry.server.http.servlets.Log4jInit,
                 org.astrogrid.xmldb.client.XMLDBManager,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.store.Ivorn,
                 org.apache.axis.utils.XMLUtils,
                 java.util.*,
                 java.io.*"
   isThreadSafe="false"
   contentType="text/xml; charset=UTF-8"
  pageEncoding="UTF-8"
   session="false"
%>
<%
   ISearch server = JSPHelper.getQueryService(request);
   Document entry;   try {   entry = server.getQueryHelper().getResourceByIdentifier(request.getParameter("IVORN"));
   }catch(Exception e) {
    entry = null;
   }

   if(entry == null) {
       out.write("<Error>No entry returned</Error>");
   } else {
	   NodeList nl = entry.getElementsByTagNameNS("*","Resource");
	   if (nl.getLength() == 0) {
	       out.write("<Error>No entry returned</Error>");
	   } else {
	      if(request.getParameter("XML") == null ||
	         !request.getParameter("XML").equals("true")) {
			  out.write("<?xml-stylesheet type='text/xsl' href='../ResourceToDublinCoreDisplay.xsl'?>");
		  }
	      XMLUtils.ElementToWriter((Element)nl.item(0),out);
          }
     }
%>
