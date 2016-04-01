<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:agr="urn:astrogrid:schema:RegistryStoreResource:v1"
  xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
  xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.0"
  xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
  xmlns:stc="http://www.ivoa.net/xml/STC/stc-v1.30.xsd">

  <xsl:param name="updated"/>

  <xsl:param name="wavebandRadio"/>
  <xsl:param name="wavebandMm"/>
  <xsl:param name="wavebandIR"/>
  <xsl:param name="wavebandOptical"/>
  <xsl:param name="wavebandUV"/>
  <xsl:param name="wavebandEUV"/>
  <xsl:param name="wavebandXRay"/>
  <xsl:param name="wavebandGammaRay"/>
  <xsl:param name="region"/>
  <xsl:param name="boxC1"/>
  <xsl:param name="boxC2"/>
  <xsl:param name="boxS1"/>
  <xsl:param name="boxS2"/>
  <xsl:param name="boxCoordSys"/>
  <xsl:param name="circleC1"/>
  <xsl:param name="circleC2"/>
  <xsl:param name="circleRadius"/>
  <xsl:param name="circleCoordSys"/>

  <!-- Transcribes all the standard elements except coverage.
       When the correct place for coverage is reached, writes
       the coverage element and calls subsidiary templates
       to fill it in. -->
  <xsl:template match="//ri:Resource">
<!--    <agr:AstrogridResource> -->
      <ri:Resource updated="{$updated}" xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
        xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.0"
        xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
        xmlns:vg="http://www.ivoa.net/xml/VORegistry/v1.0" 
        xmlns:cea="http://www.ivoa.net/xml/CEA/v1.0rc1" 
        xmlns:va="http://www.ivoa.net/xml/VOApplication/v1.0rc1">

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

        <!-- Copy the optional data-collection and service elements -->
        <xsl:copy-of select="facility"/>
        <xsl:copy-of select="rights"/>
        <xsl:copy-of select="format"/>

        <!-- Copy the service capabilities -->
        <xsl:copy-of select="capability"/>

        <!-- Write the coverage description -->
        <coverage>
          <xsl:call-template name="STC"/>
          <xsl:call-template name="wavebands"/>          
        </coverage>

        <!-- Copy the optional data-collection and service elements -->
        <xsl:copy-of select="table"/>
        

      </ri:Resource>
  <!--  </agr:AstrogridResource> -->
  </xsl:template>

  <!-- Writes a waveband element for each band present in the parameters -->
  <xsl:template name="wavebands">
    <xsl:if test="$wavebandRadio">
      <waveband>Radio</waveband>
    </xsl:if>
    <xsl:if test="$wavebandMm">
      <waveband>Millimeter</waveband>
    </xsl:if>
    <xsl:if test="$wavebandIR">
      <waveband>Infrared</waveband>
    </xsl:if>
    <xsl:if test="$wavebandOptical">
      <waveband>Optical</waveband>
    </xsl:if>
    <xsl:if test="$wavebandUV">
      <waveband>UV</waveband>
    </xsl:if>
    <xsl:if test="$wavebandEUV">
      <waveband>EUV</waveband>
    </xsl:if>
    <xsl:if test="$wavebandXRay">
      <waveband>X-ray</waveband>
    </xsl:if>
    <xsl:if test="$wavebandGammaRay">
      <waveband>Gamma-ray</waveband>
    </xsl:if>
  </xsl:template>

  <!-- Writes the STC encoding of the spatial coverage -->
  <xsl:template name="STC">
    <stc:STCResourceProfile>
      <stc:AstroCoordSystem id="whatever">
      <!-- 
        <stc:TimeFrame>
          <stc:TimeScale>TT</stc:TimeScale>
          <stc:TOPOCENTER/>
        </stc:TimeFrame>
 	-->   
 	<!--      
        <stc:SpaceFrame>
          <xsl:choose>
            <xsl:when test="$region='Box'">
              <xsl:element name="stc:{$boxCoordSys}"/>
            </xsl:when>
            <xsl:when test="$region='Circle'">
              <xsl:element name="stc:{$circleCoordSys}"/>
            </xsl:when>
            <xsl:otherwise>
              <stc:IRCS/>
            </xsl:otherwise>
          </xsl:choose>
          <stc:TOPOCENTER/>
          <stc:SPHERICAL coord_naxes="2"/>
        </stc:SpaceFrame>
         -->
        <!-- 
        <stc:SpectralFrame>
          <stc:TOPOCENTER/>
        </stc:SpectralFrame>
        <stc:RedshiftFrame>
          <stc:DopplerDefinition>RADIO</stc:DopplerDefinition>
          <stc:LSR/>
        </stc:RedshiftFrame>
         -->
      </stc:AstroCoordSystem>
      <xsl:choose>
        <xsl:when test="$region='Box'">
          <stc:AstroCoordArea coord_system_id="whatever">
            <stc:Box>
              <stc:Center>
                <stc:C1 pos_unit="deg"><xsl:value-of select="$boxC1"/></stc:C1>
                <stc:C2 pos_unit="deg"><xsl:value-of select="$boxC2"/></stc:C2>
              </stc:Center>
              <stc:Size>
                <stc:C1 pos_unit="deg"><xsl:value-of select="$boxS1"/></stc:C1>
                <stc:C2 pos_unit="deg"><xsl:value-of select="$boxS2"/></stc:C2>
              </stc:Size>
            </stc:Box>
          </stc:AstroCoordArea>
        </xsl:when>
        <xsl:when test="$region='Circle'">
          <stc:AstroCoordArea coord_system_id="whatever">
            <stc:Circle>
              <stc:Center>
                <stc:C1 pos_unit="deg"><xsl:value-of select="$circleC1"/></stc:C1>
                <stc:C2 pos_unit="deg"><xsl:value-of select="$circleC2"/></stc:C2>
              </stc:Center>
              <stc:Radius pos_unit="deg"><xsl:value-of select="$circleRadius"/></stc:Radius>
            </stc:Circle>
          </stc:AstroCoordArea>
        </xsl:when>
        <xsl:otherwise>
          <stc:AstroCoordArea coord_system_id="whatever">
            <stc:AllSky/>
          </stc:AstroCoordArea>
        </xsl:otherwise>
      </xsl:choose>
    </stc:STCResourceProfile>
  </xsl:template>

</xsl:stylesheet>
