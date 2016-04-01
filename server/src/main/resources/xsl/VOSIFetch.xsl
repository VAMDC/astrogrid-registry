<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:param name="capURL"/>
	<xsl:param name="tableURL"/>
	<xsl:param name="ceaURL"/>

	<xsl:template match="capability">
	 	 <xsl:copy-of select="document($capURL)//capability"/>
	 	 <xsl:choose>
			<xsl:when test="not($tableURL)"> 
				<!-- parameter has not been supplied don't do anything -->
			</xsl:when>
			<xsl:otherwise> 
				<!--parameter has been supplied lets copy table now--> 
			    <xsl:copy-of select="document($tableURL)//table"/>
			</xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    
    <xsl:template match="content/contentLevel">
       <contentLevel><xsl:value-of select="." /></contentLevel>
	 	 <xsl:choose>
			<xsl:when test="not($ceaURL)"> 
				<!-- parameter has not been supplied don't do anything -->
			</xsl:when>
			<xsl:otherwise> 
				<!--parameter has been supplied lets copy table now--> 
				<xsl:if test="count(../relationship) = 0">
				   <relationship>
				    <relationshipType>service-for</relationshipType>
	   				<relatedResource><xsl:value-of select="../../identifier" />/ceaApplication</relatedResource>
				   </relationship> 
				 </xsl:if>
			</xsl:otherwise>
		</xsl:choose>       
       
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
