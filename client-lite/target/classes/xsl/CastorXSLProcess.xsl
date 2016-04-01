<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
	version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:vr="http://www.ivoa.net/xml/VOResource/v0.9"
    xmlns:vs="http://www.ivoa.net/xml/VODataService/v0.4"
    xmlns:vg="http://www.ivoa.net/xml/VORegistry/v0.2"
    xmlns:vc="http://www.ivoa.net/xml/VOCommunity/v0.2"
    xmlns:vt="http://www.ivoa.net/xml/VOTable/v0.1"
  	xmlns:cs="http://www.ivoa.net/xml/ConeSearch/v0.2"
  	xmlns:sia="http://www.ivoa.net/xml/SIA/v0.6">

	<xsl:output method="xml" />
	
    <xsl:template match="@xsi:type">
	    <xsl:choose>
	    	<xsl:when test=". = 'AuthorityType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.registry.AuthorityType</xsl:text>
			    </xsl:attribute>    		
	    	</xsl:when>	    	
	    	<xsl:when test=". = 'RegistryType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.registry.RegistryType</xsl:text>
			    </xsl:attribute>    		
	    	</xsl:when>	    	
	    	<xsl:when test=". = 'DataCollectionType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.dataservice.DataCollectionType</xsl:text>
			    </xsl:attribute>    		
	    	</xsl:when>
	    	<xsl:when test=". = 'ServiceType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.ServiceType</xsl:text>
			    </xsl:attribute>    		
	    	</xsl:when>
	    	<xsl:when test=". = 'ResourceType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.ResourceType</xsl:text>
			    </xsl:attribute>    		
	    	</xsl:when>	    	
	    	<xsl:when test=". = 'SkyServiceType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.dataservice.SkyServiceType</xsl:text>
			    </xsl:attribute>    		
	    	</xsl:when>	    	
	    	<xsl:when test=". = 'TabularSkyServiceType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.dataservice.TabularSkyServiceType</xsl:text>
			    </xsl:attribute>    		
	    	</xsl:when>	    	
	    	<xsl:when test=". = 'OrganisationType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.community.OrganisationType</xsl:text>
			    </xsl:attribute>
	    	</xsl:when>
	    	<xsl:when test=". = 'ConeSearchType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.conesearch.ConeSearchType</xsl:text>
			    </xsl:attribute>
	    	</xsl:when>
	    	<xsl:when test=". = 'SimpleImageAccessType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.sia.SimpleImageAccessType</xsl:text>
			    </xsl:attribute>
	    	</xsl:when>	    	
	    	<xsl:when test=". = 'ParamHTTPType'">
    			<xsl:attribute name="xsi:type">
	    			<xsl:text>java:org.astrogrid.registry.beans.resource.dataservice.ParamHTTPType</xsl:text>
			    </xsl:attribute>
	    	</xsl:when>	    	
	    	<xsl:otherwise>
				<xsl:apply-templates select="@*|node()"/>
	    	</xsl:otherwise>
	    </xsl:choose>
    	<!--
    	  <xsl:value-of select="."/>
		-->
    </xsl:template>
    
    <!-- Default, copy all and apply templates -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    

</xsl:stylesheet>