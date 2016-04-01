<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
    xmlns:cea="http://www.ivoa.net/xml/CEA/v1.0rc1">
	
	<xsl:param name="ceaURL"/>
	
		<xsl:template match="ri:Resource/@xsi:type">
				   <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance"> 
						<xsl:text>cea:CeaApplication</xsl:text>
				   </xsl:attribute>
		</xsl:template>

	<xsl:template match="identifier">
	  <xsl:if test="count(../table) &gt;  0">
	  	<identifier><xsl:value-of select="." />/ceaApplication</identifier>
	  </xsl:if>
	  <xsl:if test="count(../table) = 0">
	  <identifier><xsl:value-of select="." /></identifier>
	  </xsl:if>
    </xsl:template>

    <xsl:template match="relationship">
    	    <relationship>
	   			<relationshipType>related-to</relationshipType>
   				<relatedResource><xsl:value-of select="../../identifier" /></relatedResource>
			</relationship> 
    </xsl:template>

    <xsl:template match="capability">

    </xsl:template>
    
     <xsl:template match="capability[not(following::capability)]">
       <xsl:copy-of select="document($ceaURL)//cost"/>
       <xsl:copy-of select="document($ceaURL)//licence"/>
       <xsl:copy-of select="document($ceaURL)//openSource"/>
       <xsl:copy-of select="document($ceaURL)//dataFormat"/>
       <xsl:copy-of select="document($ceaURL)//voStandard"/>
       <xsl:copy-of select="document($ceaURL)//sourceLanguage"/>
       <xsl:copy-of select="document($ceaURL)//sourceCodeURL"/>
       <xsl:copy-of select="document($ceaURL)//applicationDefinition"/>
    </xsl:template>
   
      <xsl:template match="content[not(following::capability)]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
       <xsl:copy-of select="document($ceaURL)//cost"/>
       <xsl:copy-of select="document($ceaURL)//licence"/>
       <xsl:copy-of select="document($ceaURL)//openSource"/>
       <xsl:copy-of select="document($ceaURL)//dataFormat"/>
       <xsl:copy-of select="document($ceaURL)//voStandard"/>
       <xsl:copy-of select="document($ceaURL)//sourceLanguage"/>
       <xsl:copy-of select="document($ceaURL)//sourceCodeURL"/>
       <xsl:copy-of select="document($ceaURL)//applicationDefinition"/>
    </xsl:template>

<xsl:template match="applicationDefinition"> 

</xsl:template>

    <xsl:template match="table">
    
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
