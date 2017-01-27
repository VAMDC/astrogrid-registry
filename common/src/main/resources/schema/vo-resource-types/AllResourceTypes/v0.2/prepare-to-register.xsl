<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:agrt="urn:astrogrid:schema:vo-resource-types:AllResourceTypes:v0.2"
  xmlns:vor="http://www.ivoa.net/xml/VOResource/v0.10"
  xmlns:vods="http://www.ivoa.net/xml/VODataService/v0.5"
  xmlns:reg="http://www.ivoa.net/xml/VORegistry/v0.3"
  xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v0.1"
  xmlns:tdb="urn:astrogrid:schema:vo-resource-types:TabularDB:v0.3"
>

  <!-- Make it copy the XML, except where told to change it.
       The default, brain-dead behaviour is to output
       the value of each node; we override that behaviour here. -->
  <xsl:template match="*">
    <xsl:copy>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <!-- Wrap all the resources in one outer element.
       This wrapping works with AstroGrid's registry v0.8.0
       and may work with all IVOA-compliant registries. -->
  <xsl:template match="/">
    <ri:VOResources>
      <xsl:apply-templates/>
    </ri:VOResources>
  </xsl:template>
  
  
  <xsl:template match="agrt:authority">
    <ri:Resource xsi:type="reg:Authority"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>
  
  <xsl:template match="agrt:ceaApplication">
    <ri:Resource xsi:type="cea:CeaApplicationType"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>
  
  <xsl:template match="agrt:ceaService">
    <ri:Resource xsi:type="cea:CeaServiceType"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>
 
  <xsl:template match="agrt:dataCollection">
    <ri:Resource xsi:type="vods:DataCollection"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>
  
  <xsl:template match="agrt:organisation">
    <ri:Resource xsi:type="vor:Organisation"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>

  <xsl:template match="agrt:service">
    <ri:Resource xsi:type="vor:Service"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>

  <xsl:template match="agrt:skyService">
    <ri:Resource xsi:type="vods:SkyService"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>

  <xsl:template match="agrt:tabularDB">
    <ri:Resource xsi:type="tdb:TabularDB"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>
  
  <xsl:template match="agrt:tablularSkyService">
    <ri:Resource xsi:type="vods:TabularSkyService"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:copy-of select="child::*"/>
    </ri:Resource>
  </xsl:template>
  
</xsl:stylesheet>
