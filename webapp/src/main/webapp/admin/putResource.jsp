<%@ page import="org.astrogrid.registry.server.admin.*,
                 org.w3c.dom.Document,
                 org.astrogrid.util.DomHelper,
                 java.io.*"
    session="false" %>

<html>
<head>
<title>Put Resource</title>
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
</head>

<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>

<div id='bodyColumn'>
<h1>Put Resource</h1>
<p>Inserting/Updating Resource to Registry...
<p>
<%
   String resource = request.getParameter("Resource");
   String forwardTo = request.getParameter("forwardTo");

   if ((resource == null) || (resource.trim().length() == 0)) {
      out.write("No Resource given (Parameter 'Resource' empty)");
   }
   else {
	  IAdmin server = JSPHelper.getAdminService(request);
	  Document serverResponse = server.updateResource(DomHelper.newDocument(resource));
      out.write("<b>Server Response:</b><p><pre>");
      if (serverResponse != null) {
         out.write(DomHelper.DocumentToString(serverResponse).replaceAll("<","&lt;").replaceAll(">","&gt;"));
      }
      out.write("</pre>");

      //forward if everything has gone OK (?)
      if ((forwardTo != null) && (forwardTo.trim().length() != 0) ) {
         out.write("<p>Forwarding to "+forwardTo);
         out.flush();
         request.getRequestDispatcher(forwardTo).forward(request, response);
      }
   
   }
   
%>
</div>
<%@ include file="/style/footer.xml" %>

</body>
</html>
