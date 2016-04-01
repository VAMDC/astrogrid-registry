<%@ page import="org.astrogrid.registry.server.query.*,
                 org.w3c.dom.*,
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 java.net.*,
                 java.util.*,
                 java.io.*"
    session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Sample area of xml for updates</title>
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
<h1>Sample area of xml for updates</h1>
<h2>Directory v0_10</h2>
<table cellPadding="2" border="0" width="100%">
<tr><th align="left" width="50%">Filename</th><th align="left" width="15%">Size</th><th align="left" width="35%">Last Modified</th></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_10/AGServiceKindsv10.xml">AGServiceKindsv10.xml</a></td><td>4.2 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_10/ARegistryv10.xml">ARegistryv10.xml</a></td><td>1.1 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_10/AstrogridStandardAuthorityv10.xml">AstrogridStandardAuthorityv10.xml</a></td><td>1.0 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_10/CEAMSSLRegistryEntryv10.xml">CEAMSSLRegistryEntryv10.xml</a></td><td>4.1 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_10/INT-WFS-SIAPv10.xml">INT-WFS-SIAPv10.xml</a></td><td>1.8 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_10/MSSLRegistryEntryv10.xml">MSSLRegistryEntryv10.xml</a></td><td>6.4 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_10/TabularSkyService_MSSL_TRACEv10.xml">TabularSkyService_MSSL_TRACEv10.xml</a></td><td>2.0 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_10/filestore-onev10.xml">filestore-onev10.xml</a></td><td>1.0 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_10/myspacev10.xml">myspacev10.xml</a></td><td>1.3 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
</table>
<br>
<h2>Directory v0_9</h2>
<table cellPadding="2" border="0" width="100%">
<tr><th align="left" width="50%">Filename</th><th align="left" width="15%">Size</th><th align="left" width="35%">Last Modified</th></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/AGServiceKinds.xml">AGServiceKinds.xml</a></td><td>6.5 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/ARegistry.xml">ARegistry.xml</a></td><td>1.5 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_9/AstrogridStandardAuthority.xml">AstrogridStandardAuthority.xml</a></td><td>2.0 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_9/AuthorityTest.xml">AuthorityTest.xml</a></td><td>1.3 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/CEADataCentreCDSRegistryEntry.xml">CEADataCentreCDSRegistryEntry.xml</a></td><td>3.9 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/CEADataCentreRegistryEntry.xml">CEADataCentreRegistryEntry.xml</a></td><td>6.9 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/CEAHttpRealapps.xml">CEAHttpRealapps.xml</a></td><td>9.7 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/CEATestapps.xml">CEATestapps.xml</a></td><td>3.4 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/CeaRegistry.xml">CeaRegistry.xml</a></td><td>10.8 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/Community.xml">Community.xml</a></td><td>5.5 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/CommunityAuthority.xml">CommunityAuthority.xml</a></td><td>1.1 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr>
<tr><td><a href="../reg_xml_samples/updates/v0_9/INT-WFS-SIAP.xml">INT-WFS-SIAP.xml</a></td><td>2.1 kb</td><td> Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_9/MSSL_Registry_Entries.xml">MSSL_Registry_Entries.xml</a></td><td>19.6 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_9/MySpace.xml">MySpace.xml</a></td><td>2.7 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
<tr><td><a href="../reg_xml_samples/updates/v0_9/TabularSkyService_Fits_TRACE.xml">TabularSkyService_Fits_TRACE.xml</a></td><td>3.1 kb</td><td>Fri, 07 Jan 2005 14:14:24 GMT</td></tr> 
</table>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
