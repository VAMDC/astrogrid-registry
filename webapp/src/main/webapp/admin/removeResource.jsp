<%@ page import="org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.server.admin.*,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.*,
                 java.util.ArrayList"
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Remove Resource</title>
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
<h1>Remove Resource</h1>
<p>
<font color="red">WARNING -- Delete only if you know this particular
resource has not been harvested to any other registry.</font><br>
If a resource has already been harvested to other registries, then DO
NOT REMOVE the resource just change the status attribute to "deleted".
To remove you must put the full Resource Identifier.
</p>
<form method="post" action="removeResource.jsp">
<p>
Remove Resource:
 <br>
 <input type="hidden" name="remove_resource" value="true">
 <input type ="text" name="id">
 <input type="submit" name="button" value="remove">
</p>
</form>
<%
String removeRes = request.getParameter("remove_resource");
String id = request.getParameter("id");
if("true".equals(removeRes)) {
    if(id == null || id.trim().length() == 0) {
        out.println("<font color='red'>You need to provide a Resource Identifier.</font>");
    } else {
        if(removeRes != null && removeRes.equals("true")) {
            IAdmin server = JSPHelper.getAdminService(request);
            server.remove(id);
            out.print("<font color='blue'>Removed resource</font>");
        }
    }
}
%>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
