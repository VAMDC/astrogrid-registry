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
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Tree View of Registry</title>
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
          <h1>Registry Tree</h1>
          <p>
            This is a browseable view of registry as a tree
            structure. Links ending in a slash lead to branches; 
            links with no slash at the end lead to resource documents for the
            things registered.
          </p>
          <p>
            The branches are represented as web pages
            with further links while the resource documents are in XML. If you
            get a resource document with a utility such as <i>wget</i> you
            get the actual XML text from the registry. If you view it in a web
            browser you see a view of the text turned into HTML for ease of
            reading; use the <i>show source</i> function in your browser to
            see the original XML.
          </p>
          <ul>
            <li><a href="<%=request.getContextPath()%>/main/tree/">ivo://</a></li>
          </ul>
       </div>
    </div>
    <%@ include file="/style/footer.xml" %>
    </body>
</html>
