<%@ page import="org.astrogrid.registry.server.admin.v1_0.RegistryAdminService,
                 org.w3c.dom.Document,
                 org.astrogrid.registry.common.DomHelper"
   isThreadSafe="false"
   session="false"
%>
<%
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
    RegistryAdminService server = new RegistryAdminService();
    out.write("<p>Attempt at updating Registry Indexes, if any errors occurred it will be printed below<br /></p>");
    Document result = server.updateIndex(doc);
	  if (result != null) {
	    DomHelper.DocumentToWriter(result, out);
	  }
  } else {
    out.write("Did not find anything to add/update");
  }
%>
</pre>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
