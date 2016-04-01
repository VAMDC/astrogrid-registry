<%@page contentType="text/plain"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean class="org.astrogrid.common.j2ee.environment.Environment" 
    id="environment" scope="application"/>
# Properties for context <c:out value="${environment.contextPath}"/>

<%
org.astrogrid.common.j2ee.environment.EnvEntry[] entries = environment.getEnvEntry();
pageContext.setAttribute("entries", entries);
%>
<c:forEach var="e" items="${entries}">
# <c:out value="${e.description}"/>
<c:out value="${e.name}"/>=<c:out value="${e.replacementValue}"/>
</c:forEach>