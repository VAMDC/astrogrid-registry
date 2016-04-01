<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
   version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:vr="http://www.ivoa.net/xml/VOResource/v0.10"
	xmlns="http://www.ivoa.net/xml/VOResource/v0.10"
    xmlns:vor="http://www.ivoa.net/xml/RegistryInterface/v0.1">

   <xsl:output method="xml" />

	<!--
    <xsl:template match="vr:Resource">
        <xsl:element name="vor:VOResources">
	        <xsl:element name="vor:Resource">
		        <xsl:call-template name="setResourceAttrs"/>
      			<xsl:apply-templates/>
		    </xsl:element>
        </xsl:element>
    </xsl:template>
	-->

	<xsl:template match="node()">    
   		<xsl:if test="count(descendant::text()[string-length(normalize-space(.))>0]|@*)">     
      		<xsl:copy>        
         		<xsl:apply-templates select="@*|node()" />      
      		</xsl:copy>    
   		</xsl:if>  
	</xsl:template>  

	<xsl:template match="@*">    
   		<xsl:if test="string-length(normalize-space()) > 0">
      		<xsl:copy />  
   		</xsl:if>
	</xsl:template>    

	<xsl:template match="text()">    
   		<xsl:value-of select="normalize-space(.)"/>  
 	</xsl:template>    
    
</xsl:stylesheet>