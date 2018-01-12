<%@ page import="org.astrogrid.registry.server.query.v1_0.RegistryQueryService"
         contentType="text/plain"                  
    	 session="false"
%>
<%
  if(request.getParameter("ResourceXQuery").trim().length() > 0) {
    RegistryQueryService server = new RegistryQueryService();
    String xql = request.getParameter("ResourceXQuery").trim();
    try {
      String results = server.xQueryToText(xql);
    out.write(results.trim());
    } catch (Exception e) {
      out.write(e.toString());
    }
	}else {
	  out.write("Did not find anything in the request, no xquery given.");
	}
%>
