<%@ page import="java.io.*,
      org.astrogrid.registry.server.query.*,
       org.w3c.dom.*,
       org.astrogrid.io.*,
	    org.astrogrid.registry.server.http.servlets.helper.JSPHelper,       
       org.astrogrid.util.DomHelper,
       org.apache.axis.utils.XMLUtils"
   isThreadSafe="false"
   session="false"
   contentType="text/xml"
%>
<%@ page language="java" %><%
   ISearch server = JSPHelper.getQueryService(request);
   Document entry = server.getQueryHelper()getRegistriesQuery();
   
   if (entry == null) {
      out.write("<Error>No entry returned</Error>");
   } else {
      XMLUtils.ElementToWriter(entry.getDocumentElement(),out);
   }
%>