<%@ page import="org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,
                 org.w3c.dom.Document,
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.registry.server.http.servlets.Log4jInit,
                 org.astrogrid.xmldb.client.XMLDBManager,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.store.Ivorn,
                 org.apache.axis.utils.XMLUtils,
                 java.util.*,
                 java.io.*"
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Submit New Resource</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=iso-8859-1">
<style type="text/css" media="all">
    <%@ include file="/style/astrogrid.css" %>
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
    <jsp:useBean id="pa" class="org.astrogrid.registry.registration.PublishingAuthorities" scope="page"/>
      <div id='bodyColumn'>
        <h1>Create Entry</h1>
        <form action="NewIdentifier" method="post">
          <p>
            Formal name for the new entry:
            ivo://
            <select name="authority">
              <%String[] authorities = pa.getAuthorities();%>
              <%for (int i = 0; i < authorities.length; i++) {%>
              <option value="<%=authorities[i]%>"><%=authorities[i]%></option>
              <%}%>
            </select>
            /
            <input type="text" name="resourceKey" size="32">
            (<a href="../help/naming.jsp">help</a>)
          </p>
          <p>This entry describes:</p>
          <dl>
            <dt><dd><input type="radio" name="xsiType" value="vr:Service"> A virtual observatory service.
            <dt><dd><input type="radio" name="xsiType" value="vs:CatalogService"> Catalog service.
            <dt><dd><input type="radio" name="xsiType" value="vs:DataCollection"> A data collection.
            <dt><dd><input type="radio" name="xsiType" value="va:Application"> An application.
            <dt><dd><input type="radio" name="xsiType" value="vr:Organisation"> An organization.
            <dt><dd><input type="radio" name="xsiType" value="vr:Resource"> None of the above; just a generic resource.
	    	<!--
	    		User can do an applicatin and then put in a harvestvosi to turn it into cea if needed.
	    		But normally CEA will register everything the whole resource itself.
            <dt><dd><input type="radio" name="xsiType" value="cea:CeaApplication">A CEA application.
	    	-->
	  </dl>
          <p><input type="submit" value="Create this entry"></p>
        </form>
        <hr>
        <p>
          If the drop-down menu in the entry name has no choices, then you
          need to configure the registry before entering resources.
        </p>
      </div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
