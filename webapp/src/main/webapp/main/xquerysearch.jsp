<%@ page 
    session="false"
    	contentType="text/html; charset=UTF-8"
    		   pageEncoding="UTF-8"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Advanced XQuery of Registry</title>
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

<h1>Reqistry XQuery</h1>

<p>
The registry may be searched using XQuery. To try it, type an XQuery in
the box and press submit.
</p>
<form action="xqueryresults.jsp" method="post">
<p>
<input type="hidden" name="performquery" value="true">
<textarea name="ResourceXQuery" rows="24" cols="80"></textarea>
</p>
<p>
<input type="submit" name="button" value="Submit"><br>
</p>
</form>
<p>
If your query uses namespaces, you will have to declare them in the query.
</p>
<p>
To refer to whole registration documents (without need to know
the formal name and namespace), use the special token <i>RootResource</i>. E.g.
<i>RootResource</i> by itself returns every registration document in the 
registry, <i>RootResource/identifier</i> lists all their IVORNs 
(i.e. formal names) and <i>RootResource/title</i> lists all the titles. To find
a resource by its formal name, try <i>RootResource[identifier='ivo://vamdc/cdms']</i>.
</p>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
