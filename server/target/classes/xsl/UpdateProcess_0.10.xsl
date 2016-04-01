<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:vr="http://www.ivoa.net/xml/VOResource/v0.10"
    xmlns:vg="http://www.ivoa.net/xml/VORegistry/v0.3" 
    xmlns:vs="http://www.ivoa.net/xml/VODataService/v0.5" 
	xmlns:sia="http://www.ivoa.net/xml/SIA/v0.7"
	xmlns:cea="http://www.ivoa.net/xml/CEAService/v0.2"
	xmlns:cs="http://www.ivoa.net/xml/ConeSearch/v0.3" 
	xmlns:sn="http://www.ivoa.net/xml/OpenSkyNode/v0.1"
	xmlns:tdb="urn:astrogrid:schema:vo-resource-types:TabularDB:v0.3"
	xmlns="http://www.ivoa.net/xml/VOResource/v0.10"
    xmlns:vor="http://www.ivoa.net/xml/RegistryInterface/v0.1">

	<xsl:output method="xml" />
	

    <xsl:template match="vr:Resource">
        <xsl:element name="vor:VOResources">
		<vor:Resource  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:vr="http://www.ivoa.net/xml/VOResource/v0.10"
    xmlns:vg="http://www.ivoa.net/xml/VORegistry/v0.3" 
    xmlns:vs="http://www.ivoa.net/xml/VODataService/v0.5" 
        xmlns:sia="http://www.ivoa.net/xml/SIA/v0.7"
        xmlns:cea="http://www.ivoa.net/xml/CEAService/v0.2"
        xmlns:cs="http://www.ivoa.net/xml/ConeSearch/v0.3" 
        xmlns:sn="http://www.ivoa.net/xml/OpenSkyNode/v0.1"
        xmlns:tdb="urn:astrogrid:schema:vo-resource-types:TabularDB:v0.3"
        xmlns="http://www.ivoa.net/xml/VOResource/v0.10"
    xmlns:vor="http://www.ivoa.net/xml/RegistryInterface/v0.1">
		        <xsl:call-template name="setResourceAttrs"/>
      			<xsl:apply-templates/>
			</vor:Resource>
        </xsl:element>
    </xsl:template>


    <xsl:template match="vor:Resource">
<vor:Resource  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:vr="http://www.ivoa.net/xml/VOResource/v0.10"
    xmlns:vg="http://www.ivoa.net/xml/VORegistry/v0.3" 
    xmlns:vs="http://www.ivoa.net/xml/VODataService/v0.5" 
        xmlns:sia="http://www.ivoa.net/xml/SIA/v0.7"
        xmlns:cea="http://www.ivoa.net/xml/CEAService/v0.2"
        xmlns:cs="http://www.ivoa.net/xml/ConeSearch/v0.3" 
        xmlns:sn="http://www.ivoa.net/xml/OpenSkyNode/v0.1"
        xmlns:tdb="urn:astrogrid:schema:vo-resource-types:TabularDB:v0.3"
        xmlns="http://www.ivoa.net/xml/VOResource/v0.10"
    xmlns:vor="http://www.ivoa.net/xml/RegistryInterface/v0.1">
		        <xsl:call-template name="setResourceAttrs"/>
      			<xsl:apply-templates/>
        </vor:Resource>
	</xsl:template>


	<xsl:template match="text()|processing-instruction()|comment()">
	  <xsl:value-of select="."/>
	</xsl:template>    

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

   <!--
     -  add record maintenance attributes to resource element 
  -->
   <xsl:template name="setResourceAttrs">
      <xsl:if test="@xsi:type">
         <xsl:attribute name="xsi:type">
           <xsl:choose>
	        <xsl:when test="contains(string(@xsi:type),':Service')">
				<xsl:text>vr:Service</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':SkyService')">
				<xsl:text>vs:SkyService</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':TabularSkyService')">
				<xsl:text>vs:TabularSkyService</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':DataCollection')">
				<xsl:text>vs:DataCollection</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':Registry')">
				<xsl:text>vg:Registry</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':Authority')">
				<xsl:text>vg:Authority</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':ConeSearch')">
				<xsl:text>cs:ConeSearch</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':SimpleImageAccess')">
				<xsl:text>sia:SimpleImageAccess</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':CeaService')">
				<xsl:text>cea:CeaServiceType</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':CeaApplicationType')">
				<xsl:text>cea:CeaApplicationType</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':CeaHttpApplicationType')">
				<xsl:text>cea:CeaHttpApplicationType</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':OpenSkyNode')">
				<xsl:text>sn:OpenSkyNode</xsl:text>
			</xsl:when>
	        <xsl:when test="contains(string(@xsi:type),':TabularDB')">
				<xsl:text>tdb:TabularDB</xsl:text>
			</xsl:when>
			<xsl:otherwise>
			  <xsl:value-of select="@xsi:type" />
			  </xsl:otherwise>
		   </xsl:choose>
         </xsl:attribute>
      </xsl:if>   
      <xsl:if test="@created">
         <xsl:attribute name="created">
			<xsl:choose>
              <xsl:when test="contains(@created,'T')">
	               <xsl:value-of select="substring(@created,1,10)" />
              </xsl:when>
              <xsl:otherwise>
                   <xsl:value-of select="@created"/>
              </xsl:otherwise>
            </xsl:choose>
         </xsl:attribute>
      </xsl:if>
      <xsl:if test="@updated">
         <xsl:attribute name="updated">
			 <xsl:choose>
              <xsl:when test="contains(@updated,'T')">
	               <xsl:value-of select="substring(@updated,1,10)" />
              </xsl:when>
              <xsl:otherwise>
                   <xsl:value-of select="@updated"/>
              </xsl:otherwise>
            </xsl:choose>     
         </xsl:attribute>
      </xsl:if>
      <xsl:if test="@status">
         <xsl:attribute name="status">
            <xsl:value-of select="@status"/>
         </xsl:attribute>
      </xsl:if>
   </xsl:template>

</xsl:stylesheet>