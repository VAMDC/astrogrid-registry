<%@ page import="org.astrogrid.registry.server.admin.*,
                 org.astrogrid.store.Ivorn,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.registry.server.*,
                 org.astrogrid.registry.common.RegistryValidator,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 junit.framework.AssertionFailedError,
                 javax.xml.stream.*,
                 org.codehaus.xfire.util.STAXUtils,
                 javax.xml.parsers.DocumentBuilderFactory,
                 javax.xml.parsers.DocumentBuilder,
                 org.w3c.dom.Document,
                 org.w3c.dom.Element,
                 org.w3c.dom.Node,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Attr,
                 org.astrogrid.io.Piper,
                 org.astrogrid.util.DomHelper,
                 java.net.*,                 
                 java.util.*,
                 org.apache.axis.utils.XMLUtils, 
                 org.apache.commons.fileupload.*, 
                 java.io.*"
    session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Status Change Result</title>
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

<p>Service returns (Any Errors in the update will be reported below):</p>

<pre>
<%
    Hashtable ivornArray = new Hashtable();
    String param;
    String changeAllVal = request.getParameter("changeAll");
   
    for (Enumeration e = request.getParameterNames() ; e.hasMoreElements() ;) {
        param = (String)e.nextElement();
        if(param.startsWith("status_")) {
   	    if(changeAllVal.equals("NONE")) {
   	        System.out.println("placing in ivornArr = ivo://" + param.substring(7) + " with val = " + request.getParameter(param));
   	    	if(!request.getParameter(param).equals("NONE")) {
                    ivornArray.put("ivo://" + param.substring(7),request.getParameter(param));
                }
            } else {
                System.out.println("placing in ivornArr = ivo://" + param.substring(7) + " with val = " + changeAllVal);
                ivornArray.put("ivo://" + param.substring(7),changeAllVal);
            }
        }
    }//for
    if(ivornArray.size() > 0) {
        String key = null;
        String val = null;
        ISearch querySearch = JSPHelper.getQueryService(request);
        IAdmin server = JSPHelper.getAdminService(request);
        Document doc;
        for (Enumeration e = ivornArray.keys() ; e.hasMoreElements() ;) {
            key = (String)e.nextElement();
            val = (String)ivornArray.get(key);
            doc = querySearch.getQueryHelper().getResourceByIdentifier(key);
            Element elem = (Element)(doc.getElementsByTagNameNS("*","Resource")).item(0);
            Attr attribute = elem.getAttributeNode("status");
            attribute.setValue(val);
            elem.setAttributeNode(attribute);
            XMLStreamReader reader = querySearch.processSingleResult(elem,null);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document updateDoc = STAXUtils.read(builder,reader,true);
            System.out.println("the updateDoc in changeStatusResult = " + DomHelper.DocumentToString(updateDoc));
            //hmmm do I need to show errors. Probably.
            Document result = server.updateInternal(updateDoc);
            if (result != null) {
                DomHelper.DocumentToWriter(result, out);
      	    }//if
        }//for
    }//if
%>
</pre>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
