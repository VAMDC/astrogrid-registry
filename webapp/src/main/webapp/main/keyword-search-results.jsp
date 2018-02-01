<%@ page import="org.astrogrid.registry.server.query.v1_0.RegistryQueryService,
                 java.net.URLDecoder"
         contentType="text/plain"
         pageEncoding="UTF-8"
    	   session="false"
%>
<%
  // Default character encoding to UTF-8. The partner JSP to this one
  // uses URF-8, but the browser sometimes forgets to set the encoding
  // in the HTTP headers.
  if (request.getCharacterEncoding() == null) {
    request.setCharacterEncoding("UTF-8");
  }
  
  String encodedKeywords = request.getParameter("keywords");
  String keywords = (encodedKeywords == null)? "" : 
      URLDecoder.decode(encodedKeywords, request.getCharacterEncoding());
  
  RegistryQueryService server = new RegistryQueryService();
  out.write(server.keywordQueryToText(keywords));
%>
