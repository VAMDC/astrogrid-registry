<%@ page import="org.astrogrid.config.SimpleConfig"
   isThreadSafe="false"
   session="false"
%>

<html>
<head>
<title>Register Page</title>
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
</title>
</head>

<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>

<div id='bodyColumn'>

<h1>Register URL</h1>
<p>If the Resource document can be reached with a URL, then type/paste that URL
in here and the Registry will 'pull' that document in and enter it.
</p>
<form action="pullResources.jsp" method="post">
<p>
Resource URL: <input name="ResourceUrl" type="text" value="" size="60"/>
</p>
<p>
<input type="submit" name="button" value="Submit"/>
</p>
</form>
</div>
<%@ include file="/style/footer.xml" %>


</body>
</html>

