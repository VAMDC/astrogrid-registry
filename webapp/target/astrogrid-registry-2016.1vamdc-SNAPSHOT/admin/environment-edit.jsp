<%@ page import="org.astrogrid.config.SimpleConfig,
                 java.io.File,
      	         org.astrogrid.registry.server.http.servlets.helper.JSPHelper"
   isThreadSafe="false"
   session="false"
%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Editing environment entries</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=iso-8859-1">
<style type="text/css" media="all">
    <%@ include file="/style/astrogrid.css" %>
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<jsp:useBean class="org.astrogrid.common.j2ee.environment.Environment" 
    id="environment" scope="application"/>
<div id='bodyColumn'>
<h1>Editing environment entries</h1>
<p>
<font color='blue'>
<%
String contextFile = SimpleConfig.getSingleton().getString("reg.custom.contextFile", "");
if(contextFile == null || contextFile.trim().length() == 0) {
File confLocalhost = new File(System.getProperty("catalina.home"), "conf/Catalina/localhost");
contextFile = new File(confLocalhost, environment.getTomcatContextFileName()).toString();
out.write("Your contextFile is located here:" + contextFile + "if this is not right then " +
"please go to WEB-INF/classes/astrogrid.properties and edit reg.custom.contextFile to " +
"a full path including filename");
}
%>
</font>
</p>
<p>
These are the environment entries for the current web-application.
</p>
<p>
There are three values for each environment entry:
</p>
<ul>
<li>The default values are those defined in the deployment descriptor (web.xml)
that came with the web-application code.</li>
<li>The operational values are those that the running web-application is currently using.</li>
<li>The replacement values, which you may edit, are those that you will set if you apply this
configuration.</li>
</ul>
<p>
To reconfigure the web application, edit the replacement values, press the submit button,
and then follow the instructions on the next page.
</p>
<form action="EnvironmentServlet" method="post">
<dl>
<c:set var="avoid" value="cea.component.manager.class"/>
<%
org.astrogrid.common.j2ee.environment.EnvEntry[] entries = environment.getEnvEntry();
pageContext.setAttribute("entries", entries);
%>
<c:forEach var="e" items="${entries}">
  <dt class="envEntryName"><c:out value="${e.name}"/></dt>
  <dd>
    <table>
    <tr>
      <td>Usage</td>
      <td><c:out value="${e.description}"/></td>
    </tr>
    <tr>
      <td>Type</td>
      <td><c:out value="${e.type}"/></td>
    </tr>
    <tr>
      <td>Default value</td>
      <td><c:out value="${e.defaultValue}"/></td>
    </tr>
    <tr>
      <td>Operational value</td>
      <td><c:out value="${e.operationalValue}"/></td>
    </tr>
    <c:if test="${e.name ne avoid}">
    <tr>
        <td>Replacement value</td>
        <td><input name="<c:out value="${e.name}"/>" value="<c:out value="${e.replacementValue}"/>" size="96"></td>
    </tr>
    </c:if>
    </table>
  </dd>
</c:forEach>
</dl>
<p><input type="submit" value="Submit"></p>
</form>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
