<%@ page
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Registry Setup Pages</title>
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

<h1>Add/Update Indexes</h1>

<%
   String authorityID = request.getParameter("AuthorityID");
%>

<p>
This page is new to the registry release and allows you to edit or add indexes to the database.  
Below in the text box is the default indexes, you may wish to add to it, but advise not to remove various paths 
especially the 'fulltext' element.
<br>
Indexes take effect only on new or updated Resources, you may wish to use the WebStart client GUI to re-index your collection to make certain all
Resources are indexed if you make index changes with existing Resources in the database..<br>
<p>
<form action="addIndex.jsp" method="post">
<p>
<input type="hidden" name="addFromText" value="true">
<textarea name="Index" cols='80' rows='24'>
<%@ include file="indexCollection1_0.jsp" %>
</textarea>
<p>
<input name="button" value="Submit" type="submit">
</form>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
