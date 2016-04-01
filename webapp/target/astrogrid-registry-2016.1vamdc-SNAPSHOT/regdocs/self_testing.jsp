<%@ page import="org.astrogrid.config.SimpleConfig"
   isThreadSafe="false"
   session="false"
%>

<html>
<head>
<title>AstroGrid Registry FAQ</title>
<style type="text/css" media="all">
	<%@ include file="/style/astrogrid.css" %>          
</style>
</title>
</head>

<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<div id='bodyColumn'>
<h1>Self Testing</h1>
      <p>
      <b>Self Testing</b><br />
         There is no specific jsp page for self tests.  By going to the Home page makes a connection to the database
         and queries for your current self registered entry which is required for all Registries.  If there are problems it
         should report an error on this 'Home' page.  
      </p>
   </body>
</html>