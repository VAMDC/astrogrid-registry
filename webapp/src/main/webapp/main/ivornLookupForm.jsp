<%@ page
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Registry Access Pages</title>
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

<h1>View Resource</h1>

<p>
Enter the Identifier for the entry you want to view.
</p>

<form action="viewResourceEntry.jsp" method="post">
<p>
 <input name="IVORN" type="text" value="ivo://" size="60">
 <input type="submit" name="button" value="Find">
</p>
<p>E.g.: ivo://vamdc/cdms</p>
</form>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
