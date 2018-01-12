<%@page contentType="text/html"%>
<%@page pageEncoding="iso-8859-1"%>
<%@page import="java.net.URLEncoder"
   isThreadSafe="false"
   session="false"
%>
<%
String ivorn = request.getParameter("IVORN");
String encodedIvorn = URLEncoder.encode(ivorn, "iso-8859-1");
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Registry Access Pages</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=iso-8859-1">
<style type="text/css" media="all">
    <%@ include file="../style/astrogrid.css" %>
</style>
<%@ include file="../style/link_options.xml" %>
</head>
<body>
<%@ include file="../style/header.xml" %>
<%@ include file="../style/navigation.xml" %>
      <div id='bodyColumn'>
        <h1>Editing options for <%=ivorn%></h1>
        <ul>
          <li><a href="../registration/DublinCore.jsp?IVORN=<%=encodedIvorn%>">Edit core information</a></li>
          <li><a href="../registration/ServiceMetadataForm.jsp?IVORN=<%=encodedIvorn%>">Define service capabilities via VOSI</a></li>
          <li><a href="../admin/editEntry.jsp?IVORN=<%=encodedIvorn%>">Edit XML text</a> (low level)</li>
        </ul>
     </div>
    <%@ include file="../style/footer.xml" %>
   </body>
</html>
