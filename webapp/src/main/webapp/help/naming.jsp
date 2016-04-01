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
<title>New registry entry: choosing the name</title>
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
        <h1>Naming a new registry entry</h1>

        <p>The name for a registry entry is in two parts: <i>naming authority</i>
          and <i>resource key</i>. The authority identifies a publisher, typically a
          university department or a research group. The resource key identifies
        an individual service, data collection, application, etc.</p>
        
        <p>E.g. the authority <i>uk.ac.cam.ast</i> (IoA Cambridge) and the
          resource key <i>INT-WFS/images/siap-atlas</i> (image-access service for
        INT Wide-Field Survey) generates the IVO identifier</p>
        
        <blockquote><i>ivo://uk.ac.cam.ast/INT-WFS/images/siap-atlas</i></blockquote>
        
        <p>
          You must choose the naming authority from those managed by this registry.
          (Or add a new one; see the "manage new authority"
          link in the sidebar.)
          You may choose the resource key freely, but you can't use one that is
          already allocated unless you delete that entry first.
        </p>
        <p>
          If you delete a resource from your registry then you <em>can</em>
          reuse that name for a different resource, but you should consider
          carefully the effects of that change. If the resource has been in
          use for some time and is well known to users, then it would be 
          confusing to reassign its name. Conversely, if the resource is newly
          registered, has not yet been discovered by the users, and you want
          to correct some mistake in the registration then replacing it entirely
          is reasonable. Similarly, it is reasonable to replace a registry
          entry with one that describes the same real-work resource differently.
        </p>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>

