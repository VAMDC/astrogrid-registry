<collection xmlns="http://exist-db.org/collection-config/1.0">
   <index xmlns:xsi="http://www.w3c.org/2001/XMLSchema-instance">
       <fulltext default="all" attributes="true" alphanum="true">
       <create path="//@xsi:type" type="xs:string"></create>
       <create path="//@status" type="xs:string"></create> 
       <create path="//title" type="xs:string"></create>
       <create path="//shortName" type="xs:string"></create>
       <create path="//identifier" type="xs:string"></create>
       <create path="//curation/publisher" type="xs:string"></create>
       <create path="//curation/creator/name" type="xs:string"></create>
       <create path="//curation/contributor" type="xs:string"></create>
       <create path="//curation/contact/name" type="xs:string"></create>
       <create path="//content/subject" type="xs:string"></create>
       <create path="//content/description" type="xs:string"></create>
       <create path="//content/source" type="xs:string"></create>
       <create path="//content/referenceURL" type="xs:string"></create>
       <create path="//content/type" type="xs:string"></create>
       <create path="//content/contentLevel" type="xs:string"></create>
       </fulltext>
   </index>
</collection> 
