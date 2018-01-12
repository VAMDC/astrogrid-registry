<%@ page import="org.w3c.dom.Element,
                 org.w3c.dom.Document,
                 org.w3c.dom.NodeList,
                 org.astrogrid.registry.server.query.v1_0.RegistryQueryService,
                 org.apache.axis.utils.XMLUtils"
   isThreadSafe="false"
   contentType="text/xml; charset=UTF-8"
  pageEncoding="UTF-8"
   session="false"
%>
<%
  RegistryQueryService server = new RegistryQueryService();
  Document entry;   
  try {   
    entry = server.getResourceToDocument(request.getParameter("IVORN"));
  } catch (Exception e) {
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
