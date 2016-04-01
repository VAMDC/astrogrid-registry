<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:agr="urn:astrogrid:schema:RegistryStoreResource:v1"
  xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
  xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.0"
  xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0">
  
  <xsl:param name="xsiType"/>
  <xsl:param name="created"/>
  <xsl:param name="updated"/>
  <xsl:param name="identifier"/> 
  
  <xsl:template match="//ri:Resource">
   <!-- <agr:AstrogridResource> -->
      <ri:Resource 
        xsi:type="{$xsiType}"
        status="active"
        created="{$created}"
        updated="{$updated}"
        xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
        xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.0"
        xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
        xmlns:va="http://www.ivoa.net/xml/VOApplication/v1.0rc1"
        xmlns:cea="http://www.ivoa.net/xml/CEA/v1.0rc1">
        
        <!-- Impose the resource-type. -->
        <xsl:attribute name="xsi:type"><xsl:value-of select="$xsiType"/></xsl:attribute>
        
        <!-- Over-ride the identifier element; copy the rest unchanged. -->
        <xsl:for-each select="child::*">
          <xsl:choose>
            <xsl:when test="local-name(.)='identifier'">
              <identifier><xsl:value-of select="$identifier"/></identifier>
            </xsl:when>
            <xsl:otherwise>
              <xsl:copy-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </ri:Resource>
    <!-- </agr:AstrogridResource> -->
  </xsl:template>

</xsl:stylesheet>
