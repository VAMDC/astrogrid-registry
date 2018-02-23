<%@page import="org.w3c.dom.Document,
                org.w3c.dom.Element,
                org.astrogrid.registry.common.DomHelper,
                org.astrogrid.registry.server.query.v1_0.RegistryQueryService,
                org.apache.commons.lang.StringEscapeUtils"
   isThreadSafe="false"
   session="false"
	   contentType="text/html; charset=UTF-8"
		   pageEncoding="UTF-8"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Add or Update Entry</title>
<meta http-equiv="Content-type" content="text/xhtml; charset=UTF-8">
<style type="text/css" media="all">
    <%@ include file="../style/astrogrid.css" %>
</style>
<%@ include file="../style/link_options.xml" %>
</head>
<body>
<%@ include file="../style/header.xml" %>
<%@ include file="../style/navigation.xml" %>
<div id='bodyColumn'>

<%
  String resource = "";
  Document resourceDoc = null;
  if(request.getParameter("IVORN") != null && request.getParameter("IVORN").trim().length() > 0) {
    RegistryQueryService server = new RegistryQueryService();
    resourceDoc = server.getQueryHelper().getResourceByIdentifier(request.getParameter("IVORN"));
           if (resourceDoc != null) {
               StringBuffer resContent = new StringBuffer(DomHelper.ElementToString(((Element)(resourceDoc.getDocumentElement().getElementsByTagNameNS("*","Resource").item(0)))));
               String xml = resContent.toString();
               resource = StringEscapeUtils.escapeXml(xml);
           }//if
       }//if
%>

<h1>Edit Entry</h1>
<p>Here you can update the XML structure of resources.</p>
<p>Upload from a local file:</p>
<form enctype="multipart/form-data" method="post" action="addResourceEntry.jsp">
<p>
<input type="file" name="docfile">
<input type="hidden" name="addFromFile" value="true">
<input type="submit" name="uploadFromFile" value="upload">
</p>
</form>

<p>Upload from a url:</p>
<p>
<form method="post" action="addResourceEntry.jsp">
<input type="text" name="docurl">
<input type="hidden" name="addFromURL" value="true">
<input type="submit" name="uploadFromURL" value="upload">
</p>
</form>

<p>Upload from text:</p>
<form action="addResourceEntry.jsp" method="post">
<input type="hidden" name="addFromText" value="true">
<p>
<textarea name="Resource" rows="24" cols="80">
<%= resource %>
</textarea>
</p>
<p>
<input type="submit" name="button" value="Submit">
</p>
</form>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
