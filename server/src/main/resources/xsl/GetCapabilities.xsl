<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
  xmlns:agr="urn:astrogrid:schema:RegistryStoreResource:v1">
  
  <!-- Set this to the URL that produces the capabilities document
       for the service whose registration is being transformed. -->
  <xsl:param name="vosi-uri"/>
  
  <xsl:param name="updated"/>
  
  <!-- Copy all the existing structure and add the capabilities at the
       end inside the document element. -->
  <xsl:template match="ri:Resource">
    <!-- <agr:AstrogridResource> -->
      <ri:Resource updated="{$updated}" xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
        xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.0"
        xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
        xmlns:va="http://www.ivoa.net/xml/VOApplication/v1.0rc1"
        xmlns:cap='http://www.ivoa.net/xml/VOSICapabilities/v1.0'>
        
        <!-- Copy the attributes (but not "updated" which is overridden above) -->
        <xsl:copy-of select="@status"/>
        <xsl:copy-of select="@xsi:type"/>
        <xsl:copy-of select="@created"/>

        <!-- Copy the Dublin Core -->
        <xsl:copy-of select="validationLevel"/>
        <xsl:copy-of select="title"/>
        <xsl:copy-of select="shortName"/>
        <xsl:copy-of select="identifier"/>
        <xsl:copy-of select="curation"/>
        <xsl:copy-of select="content"/>

        <!-- Copy the optional service-elements -->
        <xsl:copy-of select="facility"/>
        <xsl:copy-of select="rights"/>
        <xsl:copy-of select="coverage"/>

        <!-- Add the capabilities on the end -->
        <xsl:copy-of select="document($vosi-uri)//capability"/>
        
        <xsl:copy-of select="table"/>        
      </ri:Resource>
    <!--  </agr:AstrogridResource> -->
  </xsl:template>

</xsl:stylesheet>