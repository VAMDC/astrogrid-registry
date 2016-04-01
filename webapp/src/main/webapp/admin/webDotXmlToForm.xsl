<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>
  
  <!-- This turns off copying of text and attribute nodes to the output. -->
  <xsl:template match="text()|@*"/>
  
  <!-- Start the table when matching the document node. 
       This ensures that the table encloses all the properties. -->
  <xsl:template match="/">
    <table cellpadding="3px">
      <tr>
        <th>Property name</th>
        <th>Type</th>
        <th>Value</th>
      </tr>
      <xsl:apply-templates />
    </table>
  </xsl:template>

  <!-- Turn an env-entry element into a row of the table. -->
  <xsl:template match="env-entry">
    <tr>
      <td><xsl:value-of select="env-entry-name"/></td>
      <td>(<xsl:value-of select="env-entry-type"/>)</td>
      <td>
        <input size="96">
          <xsl:attribute name="name">
            <xsl:value-of select="env-entry-name"/>
          </xsl:attribute>
          <xsl:attribute name="value">
            <xsl:value-of select="env-entry-value"/>
          </xsl:attribute>
        </input>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>