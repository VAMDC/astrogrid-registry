<%@ page 
    session="false"
    	contentType="text/html; charset=UTF-8"
    		   pageEncoding="UTF-8"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Registry keyword search</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=UTF-8">
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>

<div id='bodyColumn'>

<h1>Keyword query</h1>

<p>
The registry may be searched using keywords that are matched against selected 
fields of the registration. To try it, type one or more keywords into the box
and press submit.
</p>
<form action="keyword-search-results.jsp" method="post">
<p>
<textarea name="keywords" rows="5" cols="80"></textarea>
</p>
<p>
<input type="submit" name="button" value="Submit"><br>
</p>
</form>
<p>These fields are searched:</p>
<ul>
  <li>identifier;</li>
  <li>title;</li>
  <li>description;</li>
  <li>subject;</li>
  <li>type of registered resource (so that one can search, e.g. for "application");</li>
  <li>type of capability (so that one can search, e.g., for "VAMDC-TAP").</li>
</ul>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
