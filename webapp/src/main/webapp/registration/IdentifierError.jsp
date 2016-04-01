<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="org.astrogrid.registry.registration.*"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Error in choosing an IVO identifier</title>
  </head>
  <body>

    <h1>Error in choosing an IVO identifier</h1>
    
      <p>
        Sorry, you can't use the name '<%=request.getAttribute("IVORN")%>'.
      </p>
      
      <%
      Exception e = (Exception) request.getAttribute("exception");
      if (e instanceof IdentifierAlreadyUsedException ) {
      %>
      <p>
        There is already a resource with that name. If you want to reuse the
        name, delete the other resource first.
      </p>
      <%
      }
      else if (e instanceof IdentifierSyntaxException) {
      %>
      <p>
        The syntax of the name isn't right. The syntax checker says:
      </p>
      <blockquote>"<%=e%>"</blockquote>
      <p>
        Please go back and try again.
      </p>
      <%
      }
      else {
      %>
      <p>
        There's a programming error inside the registry. Please report this:
      </p>
      <blockquote>"<%=e%>"</blockquote>
      <%
      }
      %>
    
  </body>
</html>
