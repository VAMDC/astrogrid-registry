<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.ivoa.net/xml/ADQL/v1.0" 
                xmlns:ad="http://www.ivoa.net/xml/ADQL/v1.0" 
                xmlns:ri="http://www.ivoa.net/wsdl/RegistrySearch/v1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
   <xsl:output method="text" indent="no"/>
   
   <xsl:param name="resource_elem"/>
   <xsl:param name="declare_elems"/>
   
   
   <xsl:template match="/">
      <xsl:apply-templates select="*"/>
   </xsl:template>
   <xsl:template match="ad:Select | ad:selection | ad:Selection">      
      <xsl:apply-templates select="ad:Where"/>
   </xsl:template>
   <xsl:template match="ad:SelectionList">
      <xsl:variable name="list">
         <xsl:for-each select="ad:Item">
            <xsl:apply-templates select="."/>
            <xsl:text>, </xsl:text>
         </xsl:for-each>
      </xsl:variable>
      <xsl:value-of select="substring($list, 1, string-length($list)-2)"/>
      <xsl:text> </xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'aliasSelectionItemType'] | *[@xsi:type = 'aliasSelectionItemType']">
      <xsl:apply-templates select="ad:Expression"/>
      <xsl:if test="@As">
         <xsl:text> AS </xsl:text>
         <xsl:value-of select="@As"/>
      </xsl:if>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'allSelectionItemType'] | *[@xsi:type = 'allSelectionItemType']">
      <xsl:text>*</xsl:text>
   </xsl:template>
   <xsl:template match="ad:InTo">
      <xsl:text>INTO </xsl:text>
      <xsl:value-of select="ad:TableName"/>
      <xsl:text> </xsl:text>
   </xsl:template>
   <xsl:template match="ad:From">
      <xsl:variable name="list">
         <xsl:for-each select="ad:Table">
            <xsl:apply-templates select="."/>
            <xsl:text>, </xsl:text>
         </xsl:for-each>
      </xsl:variable>
      <xsl:text>FROM </xsl:text>
      <xsl:value-of select="substring($list, 1, string-length($list)-2)"/>
      <xsl:text> </xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'archiveTableType'] | *[@xsi:type = 'archiveTableType']">
      <xsl:value-of select="@Name"/>
      <xsl:if test="@Alias">
         <xsl:text> AS </xsl:text>
         <xsl:value-of select="@Alias"/>
      </xsl:if>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'tableType'] | *[@xsi:type = 'tableType']">
      <xsl:value-of select="@Name"/>
      <xsl:if test="@Alias">
         <xsl:text> AS </xsl:text>
         <xsl:value-of select="@Alias"/>
      </xsl:if>
   </xsl:template>
   <xsl:template match="ad:Where">
   <!--
   <xsl:value-of select="$declare_elems"/>
   -->
   <xsl:text>//</xsl:text><xsl:value-of select="$resource_elem"/><xsl:text>[(@status='active') and </xsl:text>
      <xsl:if test="ad:Condition">
    	  <xsl:text>(</xsl:text>
	      <xsl:apply-templates select="ad:Condition"/>
	      <xsl:text>) </xsl:text>
	  </xsl:if>
      <xsl:text>] </xsl:text>   
   </xsl:template>
      <xsl:template match="ri:Where">
   <xsl:value-of select="$declare_elems"/>
   <xsl:text>
      //</xsl:text><xsl:value-of select="$resource_elem"/><xsl:text>[(@status='active') and </xsl:text>
      <xsl:if test="ad:Condition">
    	  <xsl:text>(</xsl:text>
	      <xsl:apply-templates select="ad:Condition"/>
	      <xsl:text>)</xsl:text>
	  </xsl:if>
      <xsl:text>]</xsl:text>  
   </xsl:template>
   <!--
   Not used yet, but should be easy to implement.
   <xsl:template match="ad:OrderBy">
      <xsl:variable name="list">
         <xsl:for-each select="ad:Item">
            <xsl:apply-templates select="ad:Expression"/>
            <xsl:apply-templates select="ad:Order"/>
            <xsl:text>, </xsl:text>
         </xsl:for-each>
      </xsl:variable>
      <xsl:text>ORDER BY </xsl:text>
      <xsl:value-of select="substring($list, 1, string-length($list)-2)"/>
      <xsl:text> </xsl:text>
   </xsl:template>
   <xsl:template match="ad:Order">
      <xsl:text> </xsl:text>
      <xsl:value-of select="@Direction"/>
   </xsl:template>
   -->
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'intersectionSearchType'] | *[@xsi:type = 'intersectionSearchType']">
      <xsl:text> ( </xsl:text>
      <xsl:apply-templates select="ad:Condition[1]"/>
      <xsl:text> and </xsl:text>
      <xsl:apply-templates select="ad:Condition[2]"/>
      <xsl:text> ) </xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'unionSearchType'] | *[@xsi:type = 'unionSearchType']">
      <xsl:text> ( </xsl:text>
      <xsl:apply-templates select="ad:Condition[1]"/>
      <xsl:text> or </xsl:text>
      <xsl:apply-templates select="ad:Condition[2]"/>
      <xsl:text> ) </xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'likePredType'] | *[@xsi:type = 'likePredType']">
      <xsl:text> ( </xsl:text>
      <xsl:apply-templates select="ad:Arg"/>
                 <xsl:text> &amp;= </xsl:text>
      <xsl:apply-templates select="ad:Pattern"/>
      <xsl:text> ) </xsl:text>
   </xsl:template>
   <!--
   Need to think on this one again.
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'notLikePredType'] | *[@xsi:type = 'notLikePredType']">
      <xsl:apply-templates select="ad:Arg"/>
      <xsl:text> NOT LIKE </xsl:text>
      <xsl:apply-templates select="ad:Pattern"/>
   </xsl:template>
   -->
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'inclusiveSearchType'] | *[@xsi:type = 'inclusiveSearchType']">
      <xsl:apply-templates select="ad:Expression"/>
      <xsl:text> IN (</xsl:text>
      <xsl:apply-templates select="ad:Set"/>
      <xsl:text>)</xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'exclusiveSearchType'] | *[@xsi:type = 'exclusiveSearchType']">
      <xsl:apply-templates select="ad:Expression"/>
      <xsl:text> NOT IN (</xsl:text>
      <xsl:apply-templates select="ad:Set"/>
      <xsl:text>)</xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'subQuerySet'] | *[@xsi:type = 'subQuerySet']">
      <xsl:apply-templates select="ad:Selection"/>
      <xsl:apply-templates select="ad:selection"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'constantListSet'] | *[@xsi:type = 'constantListSet']">
      <xsl:variable name="list">
         <xsl:for-each select="ad:Item">
            <xsl:apply-templates select="."/>
            <xsl:text>, </xsl:text>
         </xsl:for-each>
      </xsl:variable>
      <xsl:value-of select="substring($list, 1, string-length($list)-2)"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'ConstantListSet'] | *[@xsi:type = 'ConstantListSet']">
      <xsl:variable name="list">
         <xsl:for-each select="ad:Item">
            <xsl:apply-templates select="."/>
            <xsl:text>, </xsl:text>
         </xsl:for-each>
      </xsl:variable>
      <xsl:value-of select="substring($list, 1, string-length($list)-2)"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'closedSearchType'] | *[@xsi:type = 'closedSearchType']">
      <xsl:text> (</xsl:text>
      <xsl:apply-templates select="ad:Condition"/>
      <xsl:text>) </xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'comparisonPredType'] | *[@xsi:type = 'comparisonPredType']">
      <xsl:apply-templates select="ad:Arg[1]"/>
      <xsl:value-of select="@Comparison"/>
      <xsl:apply-templates select="ad:Arg[2]"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'betweenPredType'] | *[@xsi:type = 'betweenPredType']">
      <xsl:apply-templates select="ad:Arg[1]"/>
      <xsl:text> BETWEEN </xsl:text>
      <xsl:apply-templates select="ad:Arg[2]"/>
      <xsl:text> AND </xsl:text>
      <xsl:apply-templates select="ad:Arg[3]"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'notBetweenPredType'] | *[@xsi:type = 'notBetweenPredType']">
      <xsl:apply-templates select="ad:Arg[1]"/>
      <xsl:text> NOT BETWEEN </xsl:text>
      <xsl:apply-templates select="ad:Arg[2]"/>
      <xsl:text> AND </xsl:text>
      <xsl:apply-templates select="ad:Arg[3]"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'inverseSearchType'] | *[@xsi:type = 'inverseSearchType']">
      <xsl:text>NOT </xsl:text>
      <xsl:apply-templates select="ad:Condition"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'closedExprType'] | *[@xsi:type = 'closedExprType']">
      <xsl:text> (</xsl:text>
      <xsl:apply-templates select="ad:Arg"/>
      <xsl:text>) </xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'binaryExprType'] | *[@xsi:type = 'binaryExprType']">
      <xsl:apply-templates select="ad:Arg[1]"/>
      <xsl:value-of select="@Oper"/>
      <xsl:apply-templates select="ad:Arg[2]"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'unaryExprType'] | *[@xsi:type = 'unaryExprType']">
      <xsl:value-of select="@Oper"/>
      <xsl:apply-templates select="ad:Arg"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'columnReferenceType'] | *[@xsi:type = 'columnReferenceType']">   
      <!--   
      <xsl:value-of select="@Table"/>
      <xsl:text>.</xsl:text>
      <xsl:value-of select="@Name"/>
      -->
      <xsl:choose>
         <xsl:when test="@xpathName != ''">
			<xsl:value-of select="@xpathName" />   
         </xsl:when>
         <xsl:otherwise>
			<xsl:value-of select="@Name" />   
         </xsl:otherwise>
      </xsl:choose>
      <!--
      Originally because clients were putting Resource as the start of the xpath,
      but that has now gone away.
      <xsl:choose>      
         <xsl:when test="starts-with($colName,'Resource')">
            <xsl:value-of select="substring(@Name, 10, string-length(@Name))"/>
         </xsl:when>
         <xsl:when test="starts-with($colName,'vr:Resource')">
            <xsl:value-of select="substring(@Name, 12, string-length(@Name))"/>
         </xsl:when>      
         <xsl:otherwise>
            <xsl:value-of select="$colName" />
         </xsl:otherwise>
      </xsl:choose>
      -->
      
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'atomType'] | *[@xsi:type = 'atomType']">
      <xsl:apply-templates select="ad:Literal"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'realType'] | *[@xsi:type = 'realType']">
      <xsl:value-of select="@Value"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'integerType'] | *[@xsi:type = 'integerType']">
      <xsl:value-of select="@Value"/>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'stringType'] | *[@xsi:type = 'stringType']">
                  <xsl:text>'</xsl:text>
                  <xsl:choose>
                     <xsl:when test="contains(@Value,'%')">
                        <xsl:value-of select="translate(@Value, '%', '*')" />
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="@Value"/>
                     </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>'</xsl:text>      
   </xsl:template>
   <xsl:template match="ad:Allow">
      <xsl:value-of select="@Option"/>
      <xsl:text> </xsl:text>
   </xsl:template>
   <xsl:template match="ad:Restrict">
      <xsl:text>TOP </xsl:text>
      <xsl:value-of select="@Top"/>
      <xsl:text> </xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'trigonometricFunctionType'] | *[@xsi:type = 'trigonometricFunctionType']">
      <xsl:value-of select="@Name"/>
      <xsl:text>(</xsl:text>
      <xsl:apply-templates select="ad:Allow"/>
      <xsl:apply-templates select="ad:Arg"/>
      <xsl:text>)</xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'mathFunctionType'] | *[@xsi:type = 'mathFunctionType']">
      <xsl:value-of select="@Name"/>
      <xsl:text>(</xsl:text>
      <xsl:apply-templates select="ad:Allow"/>
      <xsl:apply-templates select="ad:Arg"/>
      <xsl:text>)</xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'aggregateFunctionType'] | *[@xsi:type = 'aggregateFunctionType']">
      <xsl:value-of select="@Name"/>
      <xsl:text>(</xsl:text>
      <xsl:apply-templates select="ad:Allow"/>
      <xsl:apply-templates select="ad:Arg"/>
      <xsl:text>)</xsl:text>
   </xsl:template>
   <xsl:template match="*[substring-after(@xsi:type, ':') = 'userDefinedFunctionType'] | *[@xsi:type = 'userDefinedFunctionType']">
      <xsl:value-of select="ad:Name"/>
      <xsl:text>(</xsl:text>
      <xsl:if test="ad:Params">
         <xsl:variable name="list">
            <xsl:for-each select="ad:Params">
               <xsl:apply-templates select="."/>
               <xsl:text>, </xsl:text>
            </xsl:for-each>
         </xsl:variable>
         <xsl:value-of select="substring($list, 1, string-length($list)-2)"/>
      </xsl:if>
      <xsl:text>)</xsl:text>
   </xsl:template>
   <xsl:template match="text()"/>
</xsl:stylesheet>