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
   session="false"
	   contentType="text/html; charset=UTF-8"
		   pageEncoding="UTF-8"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Resource IDs beginning with <%=request.getAttribute("ivorn")%></title>
<meta http-equiv="Content-type" content="text/xhtml;charset=UTF-8"/>
<style type="text/css" media="all">
    <%@ include file="/style/astrogrid.css" %>
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
      <div id="bodyColumn">
        <div class="contentBox">
          <h1>Resource IDs beginning with <%=request.getAttribute("ivorn")%></h1>
          <ul>
            <%
            java.util.Collection c = (java.util.Collection) request.getAttribute("hrefs");
            for (Object o : c) {
            %>
            <li><a href="<%=o%>"><%=o%></a></li>
            <%
            }
            %>
          </ul>
        </div>
      </div>
<%@ include file="/style/footer.xml" %>
</body>
</html>

