<%@ page 
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
            <dt><dd><input type="radio" name="xsiType" value="vs:CatalogService"/> Catalog service (<em>use this for VAMDC data-nodes</em>)</dd>
            <dt><dd><input type="radio" name="xsiType" value="vr:Service"/> A virtual observatory service ((<em>use this for XSAMS-consuming services</em>)</dd>
            <dt><dd><input type="radio" name="xsiType" value="va:Application"/> An application (<em>Use this to register software associated with a node</em>)</dd>
            <dt><dd><input type="radio" name="xsiType" value="vs:DataCollection"/> A data collection.</dd>
            <dt><dd><input type="radio" name="xsiType" value="vr:Organisation"/> An organization.</dd>
            <dt><dd><input type="radio" name="xsiType" value="vr:Resource"/> None of the above; just a generic resource.</dd>
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
