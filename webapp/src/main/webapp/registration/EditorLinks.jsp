<%@page contentType="text/html"%>
<%@page pageEncoding="iso-8859-1"%>
<%@page import=" org.astrogrid.config.SimpleConfig,
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
                 java.net.*,
                 java.util.*,
                 java.io.*"
   isThreadSafe="false"
   session="false"
%>
<%
String ivorn = request.getParameter("IVORN");
String encodedIvorn = URLEncoder.encode(ivorn, "iso-8859-1");
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Registry Access Pages</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=iso-8859-1">
<style type="text/css" media="all">
    <%@ include file="../style/astrogrid.css" %>
</style>
<%@ include file="../style/link_options.xml" %>
</head>
<body>
<%@ include file="../style/header.xml" %>
<%@ include file="../style/navigation.xml" %>
      <div id='bodyColumn'>
        <h1>Editing options for <%=ivorn%></h1>
        <ul>
          <% if(!contractVersion.equals("0.1")) { %>
          <!--<li><a href="DublinCore.jsp?IVORN=<%=encodedIvorn%>">Edit core information</a></li>-->
          <li><a href="DublinCore?IVORN=<%=encodedIvorn%>">Edit core information</a></li>
          <li><a href="ServiceMetadataForm.jsp?IVORN=<%=encodedIvorn%>">Edit metadata (service/application) via VOSI</a></li>
          <li><a href="CoverageForm.jsp?IVORN=<%=encodedIvorn%>">Edit coverage</a></li>
          <% } %>
          <li><a href="../admin/editEntry.jsp?IVORN=<%=encodedIvorn%>">Edit XML text</a> (low level)</li>
        </ul>
     </div>
    <%@ include file="../style/footer.xml" %>
   </body>
</html>
