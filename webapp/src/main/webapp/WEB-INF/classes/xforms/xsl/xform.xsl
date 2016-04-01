<?xml version="1.0" encoding="utf-8" ?>
<!--
Project name : xslt2Xforms
Last update : $LastChangedDate$
Version : 0.7.6.$Rev$
Author : Sebastien CRAMATTE <contact@zeninteractif.com> with the help of the community
Site : http://xforms.zeninteractif.com
Abstract : "xslt2xforms" is a cross browser W3C Xforms processor. This xsl stylesheet add the W3C Xforms support in your web project using Xhtml, Javascript and Css.
License: GNU GPL (GNU General Public License. See LICENSE file) for non profit use / Commercial license for business use
Platform  : Independent
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xforms="http://www.w3.org/2002/xforms" xmlns="http://www.w3.org/1999/xhtml" xmlns:xhtml="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xhtml xsd ev xforms">
<xsl:output method="xml" omit-xml-declaration="yes"  indent="yes" encoding="utf-8" version="1.0" doctype-system="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-public="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />
<xsl:strip-space elements="*" />
<xsl:param name="uri" />

<!-- main template -->
<xsl:template match="/">
<html>
<head>
<title>xslt2Xforms processor demos</title>
<link type="text/css" rel="StyleSheet" href="style/calculator.css" />
<link type="text/css" rel="StyleSheet" href="style/styles.css" />
</head>
<body>
<h1>xslt2Xforms demo</h1>
<a href="{$uri}">xforms source</a> | <a href="index.html">back to examples list</a>
<div id="main">
	<xsl:apply-templates />
</div>
</body>
</html>
</xsl:template>


<xsl:template name="id">
	<xsl:choose><xsl:when test="../@id"><xsl:value-of select="../@id" /></xsl:when>
	<xsl:otherwise><xsl:value-of select="generate-id(..)" /></xsl:otherwise></xsl:choose>
</xsl:template>


<xsl:template match="*" mode="namespace">
	xnamespaces = {<xsl:for-each select="namespace::*"><xsl:if test="not(../ancestor::*[namespace::*[name() = name(current()) and . = current()]][last()])"><xsl:choose><xsl:when test="name()">"<xsl:value-of select="name()" />"</xsl:when><xsl:otherwise>""</xsl:otherwise></xsl:choose>:"<xsl:value-of select="." />"</xsl:if><xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>}
</xsl:template>


<!-- This allows xhtml to be mixed with the xforms xml -->

<xsl:template match="xhtml:head" >
<xsl:copy>
<xsl:apply-templates />
<link type="text/css" rel="StyleSheet" href="css/xforms.css" />
<script src="js/ie7/ie7-standard.js" type="text/javascript">// dummy</script>
<script type="text/javascript" src="js/xform.js" >// dummy</script>
<script type="text/javascript">
<xsl:apply-templates select="/" mode="namespace" />
	function onLoadHandler(evt) {
	<xsl:call-template name="xforms" />
	}

	window.addEventListener('load',onLoadHandler,false);

</script>
</xsl:copy>
</xsl:template>

<xsl:template match="xhtml:*">	
	<xsl:copy><xsl:copy-of select="@*" /><xsl:apply-templates/></xsl:copy>	
</xsl:template>

<xsl:template match="xhtml:html">	
<html xmlns="http://www.w3.org/1999/xhtml">
		<xsl:apply-templates/>
</html>
</xsl:template>


<!-- end inserted -->

<!-- Form controls module -->
<!-- generic template -->
<xsl:template match="xforms:*" >
	<xsl:apply-templates select="xforms:*" />
</xsl:template>

<xsl:template match="xforms:model" >
	<xsl:apply-templates select="xforms:*" />
</xsl:template>

<xsl:template match="xforms:input" >
	<div class="control">
	<xsl:call-template name="label" />
	<input type="text" id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms">
	<xsl:copy-of select="@*" />
	<xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if>
	</input>
	</div>
</xsl:template>

<!-- secret input field -->
<xsl:template match="xforms:secret">
	<div class="control">
	<xsl:call-template name="label" />
	<input type="password" id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms">
	<xsl:copy-of select="@*" /></input>
	</div>
</xsl:template>

<!-- textarea field -->
<xsl:template match="xforms:textarea">
	<div class="control">
	<xsl:call-template name="label" />
	<textarea id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms"><xsl:text> </xsl:text><xsl:copy-of select="@*" /></textarea>
	</div>
</xsl:template>

<!-- radio buttons -->
<xsl:template match="xforms:select1">
	<div class="control">
	<xsl:call-template name="label" />
	<xsl:apply-templates select="*" /></div>
</xsl:template>


<xsl:template match="xforms:choices/xforms:item" >
	<xsl:choose>
	<xsl:when test="../../@appearance='full' or ../../@appearance=false()" >
		<xsl:choose>
			<xsl:when test="name(../..)='xforms:select'" >
				<input type="checkbox" id="{generate-id(.)}" name="{generate-id(../..)}" xmlns="http://www.w3.org/2002/xforms" value="{xforms:value/text()}" ><xsl:copy-of select="../../@*" /></input> <xsl:value-of select="xforms:label/text()" />
			</xsl:when>
			<xsl:when test="name(../..)='xforms:select1'" >
				<input type="radio"  id="{generate-id(.)}" name="{generate-id(../..)}" xmlns="http://www.w3.org/2002/xforms" value="{xforms:value/text()}" ><xsl:copy-of select="../../@*" /></input> <xsl:value-of select="xforms:label/text()" />
			</xsl:when>
		</xsl:choose>			
	</xsl:when>
	<xsl:otherwise>
		<option value="{xforms:value/text()}"><xsl:value-of select = "xforms:label/text()" /></option>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- item -->
<xsl:template match="xforms:item" >
	<xsl:choose>
	<xsl:when test="../@appearance='full' or ../@appearance=false()" >
		<xsl:choose>
			<xsl:when test="name(..)='xforms:select'" >
				<input type="checkbox"  id="{generate-id(.)}" name="{generate-id(..)}" xmlns="http://www.w3.org/2002/xforms" value="{xforms:value/text()}"><xsl:copy-of select="../@*" /></input> <xsl:value-of select="xforms:label/text()" />
			</xsl:when>
			<xsl:when test="name(..)='xforms:select1'" >
				<input type="radio"  id="{generate-id(.)}" name="{generate-id(..)}" xmlns="http://www.w3.org/2002/xforms" value="{xforms:value/text()}"><xsl:copy-of select="../@*" /></input> <xsl:value-of select="xforms:label/text()" />
			</xsl:when>
		</xsl:choose>			
	</xsl:when>
	<xsl:otherwise>
		<option value="{xforms:value/text()}"><xsl:value-of select = "xforms:label/text()" /></option>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- radio/checkbox/options group -->
<xsl:template match="xforms:choices">
	<xsl:choose>
	<xsl:when test="../@appearance='full' or ../@appearance=false()" >
		<fieldset>
		<xsl:if test="xforms:label"><legend><xsl:value-of select = "xforms:label/text()" /></legend></xsl:if>
			<xsl:apply-templates select="*" />
		</fieldset>
	</xsl:when>
	<xsl:otherwise>
		<optgroup label="{xforms:label/text()}">
			<xsl:apply-templates select="*" />
		</optgroup>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- checkboxes -->
<xsl:template match="xforms:select|xforms:select[@appearance='full']">
	<div class="control">
	<xsl:call-template name="label" />
	<xsl:apply-templates select="*" /></div>
</xsl:template>

<!-- multilines box-->
<xsl:template match="xforms:select[@appearance='compact']|xforms:select1[@appearance='compact']">
	<div class="control">
    <xsl:call-template name="label" />
    <select id="{generate-id(.)}" size="4" multiple="true" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" />
    <xsl:apply-templates select="*" />
    </select></div>
</xsl:template>

<xsl:template match="xforms:select[@appearance='minimal']">
	<div class="control">
    <xsl:call-template name="label" />
    <select id="{generate-id(.)}" size="{count(child::xforms:item)}" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" />
        <xsl:apply-templates select="*" />
    </select></div>
</xsl:template>

<!-- combo box -->
<xsl:template match="xforms:select1[@appearance='minimal']">
    <xsl:call-template name="label" />
    <div class="control"><select id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" />
        <xsl:apply-templates select="*" />
    </select></div>
</xsl:template>

<!-- file -->
<xsl:template match="xforms:upload">
	<xsl:call-template name="label" />
	<div class="control"><input type="file" id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms">
	<xsl:copy-of select="@*" /></input>
	</div>
</xsl:template>

<!-- range -->
<xsl:template match="xforms:range">
	<div class="control">
	<xsl:call-template name="label" />
	<div id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" /></div>
	</div>
	<xsl:apply-templates select="*" />	
</xsl:template>

<!-- fieldset -->
<xsl:template match="xforms:group">
	<fieldset id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms">
	<xsl:copy-of select="@*" />
	<xsl:apply-templates select="*" />
	</fieldset>
</xsl:template>

<xsl:template match="xforms:group/xforms:label">
	<legend id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms"><xsl:value-of select="." /></legend>
</xsl:template>


<!-- output -->
<xsl:template match="xforms:output">
	<div class="control">
	<xsl:call-template name="label" />
	<div id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms"  class="output"><xsl:copy-of select="@*"/><xsl:text> </xsl:text></div>
	</div>
</xsl:template>


<!-- trigger -->
<xsl:template match="xforms:trigger">
<button id="{generate-id(.)}"><xsl:copy-of select="@*" /><xsl:value-of select = "xforms:label/text()" /></button>
</xsl:template>

<!-- submit -->
<xsl:template match="xforms:submit">
<button id="{@submission}" ><xsl:copy-of select="@*" /><xsl:value-of select = "xforms:label/text()" /></button>
</xsl:template>

<!-- label -->
<xsl:template name="label">
<xsl:param name="id" select="generate-id(.)" />
<xsl:if test="xforms:label"><label for="{$id}" id="{$id}.label"><xsl:value-of select = "xforms:label/text()" /></label></xsl:if>
</xsl:template>	

<!-- values attributes-->
<xsl:template name="val_attributes" >{<xsl:for-each select="@*"><xsl:choose><xsl:when test="position()=last()" >"<xsl:value-of select="name()" />": "<xsl:value-of select="normalize-space(.)" />"</xsl:when><xsl:otherwise>"<xsl:value-of select="name()" />":"<xsl:value-of select="normalize-space(.)" />",</xsl:otherwise></xsl:choose></xsl:for-each>}</xsl:template>

<!-- values attributes-->
<xsl:template name="val_attributes_insert">{<xsl:for-each select="@*"><xsl:choose><xsl:when test="name()='ev:event'"><xsl:choose><xsl:when test="position()=last()" >"<xsl:value-of select="name()" />": "click"</xsl:when><xsl:otherwise>"<xsl:value-of select="name()" />": "click",</xsl:otherwise></xsl:choose></xsl:when><xsl:when test="position()=last()" >"<xsl:value-of select="name()" />": "<xsl:value-of select="normalize-space(.)" />"</xsl:when><xsl:otherwise>"<xsl:value-of select="name()" />":"<xsl:value-of select="normalize-space(.)" />",</xsl:otherwise></xsl:choose></xsl:for-each>}</xsl:template>


<!-- serialize and escaping XML datas -->
<xsl:template name="startTag">
<xsl:variable name="prefix" select="substring-before(name(),':')" /> 
<xsl:variable name="uri" select="namespace-uri(.)" />
<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
<xsl:value-of select="name()" />
<xsl:if test="$prefix"><xsl:value-of select="concat(' xmlns:',$prefix,'=&#34;',$uri,'&#34;')" /><xsl:call-template name="attributes" /></xsl:if>
<xsl:if test="not(*|text()|comment()|processing-instruction())"> /</xsl:if><xsl:text disable-output-escaping="yes">&gt;</xsl:text>
</xsl:template>

<xsl:template name="endTag">
<xsl:text disable-output-escaping="yes">&lt;/</xsl:text><xsl:value-of select="name()" /><xsl:text disable-output-escaping="yes">&gt;</xsl:text>
</xsl:template>

<xsl:template name="attributes">
<xsl:for-each select="@*">
<xsl:text> </xsl:text>
<xsl:value-of select="name()" />
<xsl:text>="</xsl:text>
<xsl:value-of select="." />
<xsl:text>"</xsl:text>
</xsl:for-each>
</xsl:template>



<xsl:template match="*" mode="escape-xml">
<xsl:call-template name="startTag" />
<xsl:apply-templates mode="escape-xml" />
<xsl:if test="*|text()|comment()|processing-instruction()"><xsl:call-template name="endTag" /></xsl:if>
</xsl:template>


<!-- Xforms actions -->


<xsl:template match="xforms:trigger" mode="actions" >
	// trigger<xsl:apply-templates mode="actions" />
</xsl:template>

<xsl:template match="comment()" mode="actions" />
<xsl:template match="text()" mode="actions" />
<xsl:template match="xforms:label" mode="actions" />

<!-- insert -->
<xsl:template match="xforms:insert" mode="actions" >
	<xsl:variable name="id" ><xsl:call-template name="id" /></xsl:variable>
	new Xinsert('<xsl:value-of select="$id" />',<xsl:call-template name="val_attributes_insert" />);
</xsl:template>

<!-- delete -->
<xsl:template match="xforms:delete" mode="actions" >
	<xsl:variable name="id" ><xsl:call-template name="id" /></xsl:variable>
	new Xdelete('<xsl:value-of select="$id" />',<xsl:call-template name="val_attributes_insert" />);
</xsl:template>

<!--
<xsl:template match="*" mode="actions" ><xsl:variable name="id" ><xsl:call-template name="id" /></xsl:variable>
	new Xevent('<xsl:value-of select="$id" />',<xsl:call-template name="val_attributes" />,Xactions.<xsl:value-of select="local-name()" />Handler);</xsl:template>
-->
<xsl:template match="xforms:action/*" mode="actions">
	Xactions._<xsl:value-of select="name()" />(<xsl:call-template name="val_attributes" /><xsl:if test="text()">,'<xsl:value-of select="text()" />'</xsl:if>);</xsl:template>

<xsl:template match="xforms:action" mode="actions">
	<xsl:variable name="id" ><xsl:call-template name="id" /></xsl:variable>
	new Xevent('<xsl:value-of select="$id" />',<xsl:call-template name="val_attributes" />,function (evt) {<xsl:apply-templates mode="actions" />
   	}
   	);
</xsl:template>

<xsl:template match="xforms:label" mode="actions" />

<!-- Core model init  -->
<xsl:template name="xforms" >
	//### init xforms engine ###
	xforms = new Xforms();
	
	<xsl:for-each select="//xforms:model">
	<xsl:apply-templates select="xforms:action" mode="actions" />
	<xsl:variable name="pos" select="position()-1"/>
	//### init model ###
	xmodel = new Xmodel(<xsl:call-template name="val_attributes"/>);
	//--- instance
	<xsl:apply-templates select="xforms:instance" mode="model" />
	//--- bind ---
	<xsl:call-template name="bind" />
	//--- submission<xsl:apply-templates select="xforms:submission" mode="model" />
	xmodel.resolveFunctions();
	xforms.appendModel(xmodel);
	//### end of model ###
	</xsl:for-each>
	
	//--repeat initialization --
	<xsl:apply-templates select="//xforms:repeat" mode="init" />
	<xsl:apply-templates select="//@repeat-nodeset/.." mode="init" />
	
	<xsl:apply-templates select="//xforms:trigger" mode="actions" />
	<xsl:apply-templates select="//xforms:range" mode="model" />	
	<xsl:apply-templates select="//xforms:hint" mode="model" />
	
	xforms.run();
	//### end of xforms engine ###
</xsl:template>

<xsl:template match="xforms:instance" mode="model" >
	<xsl:choose>
	<xsl:when test="@src=true()">
	var httpRequest = new XMLHttpRequest(); 
	httpRequest.open('GET','<xsl:value-of select="@src" />', false);
	httpRequest.send(null); 	
	xmodel.appendInstance(<xsl:call-template name="val_attributes"/>,httpRequest.responseXML);
	</xsl:when>
	<xsl:otherwise>
	var xmlDoc = XmlDocument.create();
	xmlDoc.loadXML('<xsl:apply-templates select="*" mode="escape-xml" />');
	xmodel.appendInstance(<xsl:call-template name="val_attributes"/>,xmlDoc);
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- Submission elements init  -->
<xsl:template match="xforms:submission" mode="model" >
	xmodel.appendSubmission(new Xsubmission(<xsl:call-template name="val_attributes" />));</xsl:template>

<!-- Bind init-->
<xsl:template name="bind">
	<xsl:param name = "parent" select="'xmodel'" />
	<xsl:param name = "level" select="0" />
	<xsl:for-each select="xforms:bind">
	<xsl:value-of select="concat('xbind',$level)" /> = new Xbind(<xsl:call-template name="val_attributes" />)
	<xsl:value-of select="$parent" />.appendBind(xbind<xsl:value-of select="$level" />);
	<xsl:call-template name="bind">
	<xsl:with-param name="parent" select="concat('xbind',$level)" />
	<xsl:with-param name="level" select="$level+1" />
	</xsl:call-template>
	</xsl:for-each>
</xsl:template>

<!-- Hint elements init -->
<xsl:template match="xforms:hint" mode="model" >
	new Hint(document.getElementById('<xsl:value-of select="generate-id(..)" />.label'),'<xsl:value-of select="text()" />');</xsl:template>

<!-- Range controls init -->
<xsl:template match="xforms:range" mode="model">
	new Slider('<xsl:value-of select="generate-id(.)" />',<xsl:call-template name="val_attributes" />);
</xsl:template>

<!-- Switch module -->
<xsl:template match="xforms:switch" mode="model">
</xsl:template>

<!-- Repeat init -->
<xsl:template match="xforms:repeat" mode="init">
	xforms.appendRepeat(new Xrepeat(<xsl:call-template name="val_attributes" />));
</xsl:template>

<xsl:template match="@repeat-nodeset" mode="init">
	xforms.appendRepeat(new Xrepeat(<xsl:call-template name="val_attributes" />));
</xsl:template>

<!-- XForms repeat module -->
<!-- Div-based repeats -->
<!--
<xsl:template match="xforms:repeat">

	<div id="{@id}" name="{@id}" class="repeat" xmlns="http://www.w3.org/2002/xforms">
		<xsl:if test="./@nodeset"><xsl:attribute name="xforms:repeat-nodeset"><xsl:value-of select="@nodeset" /></xsl:attribute></xsl:if>
		<xsl:if test="./@model"><xsl:attribute name="xforms:repeat-model"><xsl:value-of select="@model" /></xsl:attribute></xsl:if>
		<xsl:if test="./@startindex"><xsl:attribute name="xforms:repeat-startindex"><xsl:value-of select="@startindex" /></xsl:attribute></xsl:if>
		<xsl:if test="./@bind"><xsl:attribute name="xforms:repeat-bind"><xsl:value-of select="@bind" /></xsl:attribute></xsl:if>
		<xsl:if test="./@number"><xsl:attribute name="xforms:repeat-number"><xsl:value-of select="@number" /></xsl:attribute></xsl:if>
		
		<div id="{generate-id(.)}" name="{generate-id(.)}" class="repeatinstance" index="0" xmlns="http://www.w3.org/2002/xforms">
			<xsl:attribute name="repeat"><xsl:value-of select="./@id"/></xsl:attribute>
			<xsl:apply-templates />			
		</div>
	
	</div>

</xsl:template>
-->

<!-- not working Generic xhtml repeat structure -->
<!--
<xsl:template match="@repeat-nodeset">
	<xsl:variable name="id" ><xsl:call-template name="id" /></xsl:variable>
	<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>	
	<xsl:copy-of select="@*" />
	<xsl:attribute name="class">repeat</xsl:attribute>	
	<xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
	
</xsl:template>
-->

<!-- Xforms repeat module - formats the repeat in tabular form -->
<xsl:template match="xforms:repeat">

	<table id="{@id}" name="{@id}" class="repeat" xmlns="http://www.w3.org/2002/xforms">
		<xsl:if test="./@nodeset"><xsl:attribute name="xforms:repeat-nodeset"><xsl:value-of select="@nodeset" /></xsl:attribute></xsl:if>
		<xsl:if test="./@model"><xsl:attribute name="xforms:repeat-model"><xsl:value-of select="@model" /></xsl:attribute></xsl:if>
		<xsl:if test="./@startindex"><xsl:attribute name="xforms:repeat-startindex"><xsl:value-of select="@startindex" /></xsl:attribute></xsl:if>
		<xsl:if test="./@bind"><xsl:attribute name="xforms:repeat-bind"><xsl:value-of select="@bind" /></xsl:attribute></xsl:if>
		<xsl:if test="./@number"><xsl:attribute name="xforms:repeat-number"><xsl:value-of select="@number" /></xsl:attribute></xsl:if>
		<!-- need to copy all of the other attributes -->
		
		<thead>
		<tr>
			<xsl:for-each select="./xforms:*">
				<th><xsl:call-template name="label" /></th>
			</xsl:for-each>
		</tr>
		</thead>
		
		
		<tbody>
		<tr id="{generate-id(.)}" name="{generate-id(.)}" class="repeatinstance" index="0" xmlns="http://www.w3.org/2002/xforms">
			<xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
			<xsl:attribute name="repeat"><xsl:value-of select="./@id"/></xsl:attribute>
			
			<xsl:for-each select="./xforms:*">
				<td><xsl:apply-templates select="." mode="repeat" /></td>
			</xsl:for-each>
		</tr>
		</tbody>
	</table>

</xsl:template>


<xsl:template match="xforms:input" mode="repeat">	
	<input type="text" id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms">
	<xsl:copy-of select="@*" />
	<xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if>
	<xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
	</input>	
</xsl:template>

<!-- secret input field -->
<xsl:template match="xforms:secret" mode="repeat">
	<input type="password" id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms">	
	<xsl:copy-of select="@*" />
	<xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if>
	<xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
	</input>
</xsl:template>

<!-- textarea field -->
<xsl:template match="xforms:textarea" mode="repeat">
	<textarea id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms">&#160;<xsl:copy-of select="@*" />
	<xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if>
	<xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
	</textarea>	
</xsl:template>

<!-- radio buttons -->
<xsl:template match="xforms:select1" mode="repeat">
	<xsl:apply-templates select="*" />
</xsl:template>


<xsl:template match="xforms:choices/xforms:item" >
	<xsl:choose>
	<xsl:when test="../../@appearance='full' or ../../@appearance=false()" >
		<xsl:choose>
			<xsl:when test="name(../..)='xforms:select'" >
				<input class="checkbox" type="checkbox" id="{generate-id(.)}" name="{generate-id(../..)}" xmlns="http://www.w3.org/2002/xforms" value="{xforms:value/text()}" >
				<xsl:copy-of select="../../@*" />
				<xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset">
				<xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute>
				</xsl:if>
				<xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
				</input> <xsl:value-of select="xforms:label/text()" />
			</xsl:when>
			<xsl:when test="name(../..)='xforms:select1'" >
				<input class="radio" type="radio"  id="{generate-id(.)}" name="{generate-id(../..)}" xmlns="http://www.w3.org/2002/xforms" value="{xforms:value/text()}" ><xsl:copy-of select="../../@*" /><xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if><xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute></input> <xsl:value-of select="xforms:label/text()" />
			</xsl:when>
		</xsl:choose>			
	</xsl:when>
	<xsl:otherwise>
		<option value="{xforms:value/text()}"><xsl:value-of select = "xforms:label/text()" /></option>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- item -->
<xsl:template match="xforms:item" >
	<xsl:choose>
	<xsl:when test="../@appearance='full' or ../@appearance=false()" >
		<xsl:choose>
			<xsl:when test="name(..)='xforms:select'" >
				<input class="checkbox" type="checkbox"  id="{generate-id(.)}" name="{generate-id(..)}" xmlns="http://www.w3.org/2002/xforms" value="{xforms:value/text()}"><xsl:copy-of select="../@*" /><xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if><xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute></input> <xsl:value-of select="xforms:label/text()" />
			</xsl:when>
			<xsl:when test="name(..)='xforms:select1'" >
				<input class="radio" type="radio"  id="{generate-id(.)}" name="{generate-id(..)}" xmlns="http://www.w3.org/2002/xforms" value="{xforms:value/text()}"><xsl:copy-of select="../@*" /><xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if><xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute></input> <xsl:value-of select="xforms:label/text()" />
			</xsl:when>
		</xsl:choose>			
	</xsl:when>
	<xsl:otherwise>
		<option value="{xforms:value/text()}"><xsl:value-of select = "xforms:label/text()" /></option>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- radio/checkbox/options group -->
<xsl:template match="xforms:choices" mode="repeat">
	<xsl:choose>
	<xsl:when test="../@appearance='full' or ../@appearance=false()" >
		<fieldset>
		<xsl:if test="xforms:label"><legend><xsl:value-of select = "xforms:label/text()" /></legend></xsl:if>
			<xsl:apply-templates select="*" />
		</fieldset>
	</xsl:when>
	<xsl:otherwise>
		<optgroup label="{xforms:label/text()}">
			<xsl:apply-templates select="*" />
		</optgroup>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- checkboxes -->
<xsl:template match="xforms:select|xforms:select[@appearance='full']" mode="repeat">
	<xsl:apply-templates select="*" />
</xsl:template>

<!-- multilines box-->
<xsl:template match="xforms:select[@appearance='compact']|xforms:select1[@appearance='compact']" mode="repeat">
    <select id="{generate-id(.)}" size="4" multiple="true" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" />
    <xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if>
    <xsl:apply-templates select="*" />
    <xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
    </select>
</xsl:template>

<xsl:template match="xforms:select[@appearance='minimal']" mode="repeat">
    <select id="{generate-id(.)}" size="{count(child::xforms:item)}" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" />
	<xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if>
       <xsl:apply-templates select="*" />
       <xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
    </select>
</xsl:template>

<!-- combo box -->
<xsl:template match="xforms:select1[@appearance='minimal']" mode="repeat">
    <select id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" />
	<xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if>
        <xsl:apply-templates select="*" />
	<xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
    </select>
</xsl:template>

<!-- file -->
<xsl:template match="xforms:upload" mode="repeat">
	<input type="file" id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms">
	<xsl:copy-of select="@*" />
	<xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if>
	<xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute>
	</input>
</xsl:template>

<!-- range -->
<xsl:template match="xforms:range" mode="repeat">
	<div class="control">
		<div id="{generate-id(.)}" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" /><xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute></div>
	</div>
	<xsl:apply-templates select="*" />	
</xsl:template>

<!-- fieldset -->
<xsl:template match="xforms:group" mode="repeat">
	<fieldset id="{generate-id(.)}">
	<xsl:apply-templates select="*" />
	</fieldset>
</xsl:template>

<xsl:template match="xforms:output" mode="repeat">
	<div id="{generate-id(.)}" class="control" xmlns="http://www.w3.org/2002/xforms"><xsl:copy-of select="@*" /><xsl:if test="ancestor::*/attribute::nodeset|ancestor::*/attribute::repeat-nodeset"><xsl:attribute name="repeat"><xsl:value-of select="ancestor::xforms:repeat/@id"/></xsl:attribute></xsl:if><xsl:attribute name="xmlns">http://www.w3.org/2002/xforms</xsl:attribute></div>
</xsl:template>


<!-- Xforms Switch module -->
<xsl:template match="xforms:switch" mode="model">
</xsl:template>


</xsl:stylesheet>