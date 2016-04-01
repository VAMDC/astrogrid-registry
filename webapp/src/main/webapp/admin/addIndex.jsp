<%@ page import="org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.common.RegistryValidator,
                 org.astrogrid.registry.server.admin.*,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,
                 org.w3c.dom.Document,
                 org.astrogrid.io.Piper,
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.registry.server.http.servlets.Log4jInit,
                 org.astrogrid.xmldb.client.XMLDBManager,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.store.Ivorn,
                 org.apache.axis.utils.XMLUtils,
                 org.apache.commons.fileupload.*,
                 java.util.*,
                 java.net.*,
                 java.io.*"
   isThreadSafe="false"
   session="false"
%>
<%
  boolean validateError = false;
  boolean doValidate = false;
  Document doc = null;
  boolean update = false;
  String errorTemp = "";
  if(request.getParameter("Index").trim().length() > 0) {
	  doc = DomHelper.newDocument(request.getParameter("Index").trim());
	  update = true;
  }
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Adding Index</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=iso-8859-1">
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<div id='bodyColumn'>

<p>Service returns:</p>

<pre>
   <font color="red"><%=errorTemp %></font>
<%
   if(update) {
      Document result = null;
	  IAdmin server = JSPHelper.getAdminService(request);
      out.write("<p>Attempt at updating Registry Indexes, if any errors occurred it will be printed below<br /></p>");
      result = server.updateIndex(doc);
	      if (result != null) {
	        DomHelper.DocumentToWriter(result, out);
	      }
   }else {
     out.write("Did not find anything to add/update");
   }
%>
</pre>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
