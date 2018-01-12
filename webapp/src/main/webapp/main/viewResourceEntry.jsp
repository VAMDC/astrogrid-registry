<%@ page 
   isThreadSafe="false"
   session="false"
   contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>VAMDC Registry Access Pages</title>
<meta http-equiv="Content-type" content="text/xhtml;UTF-8" />
<style type="text/css" media="all">
    <%@ include file="/style/astrogrid.css" %>
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<div id='bodyColumn'>

<SCRIPT LANGUAGE="JavaScript" type="text/javascript">
<!--
      document.write('<iframe src="viewResourceEntry_body.jsp?IVORN=<%=request.getParameter("IVORN")%>" name="main" scrolling="yes" FRAMEBORDER="0" width="90%" height="700"><\/iframe>');
// -->
</SCRIPT>

</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
