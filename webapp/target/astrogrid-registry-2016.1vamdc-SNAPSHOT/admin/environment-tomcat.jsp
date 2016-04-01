<?xml version="1.0"?>
<%@page contentType="application/xml"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<jsp:useBean class="org.astrogrid.common.j2ee.environment.Environment" 
    id="environment" scope="application"/>
<%
org.astrogrid.common.j2ee.environment.EnvEntry[] entries = environment.getEnvEntry();
pageContext.setAttribute("entries", entries);
%>
<Context path="<c:out value="${environment.contextPath}"/>">
  <c:forEach var="e" items="${entries}">
    <Environment
      description="<c:out value="${e.description}"/>"
      name="<c:out value="${e.name}"/>"
      override="false"
      type="<c:out value="${e.type}"/>"
      value="<c:out value="${e.replacementValue}"/>"
    />
  </c:forEach>
</Context>
