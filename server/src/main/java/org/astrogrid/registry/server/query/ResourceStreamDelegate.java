package org.astrogrid.registry.server.query;

import javax.xml.stream.*;
import javax.xml.stream.util.StreamReaderDelegate;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.codehaus.xfire.util.STAXUtils;
import java.io.StringReader;
import org.astrogrid.contracts.http.filters.ContractsFilter;
import org.xmldb.api.base.XMLDBException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class: RegistryXMLStreamDelegate
 * Description: Main Streaming Delegate that handles streaming results
 * of a query back to the client via XFire.  This delegate will take into
 * account an XML Wrapper around a ResourceSet then cursor through a ResourceSet
 * streaming out the XML from each Resource in the set.  Finally streeaming out the ending
 * wrapper elements.
 * This class is abstract a ResultStreamer class is defined for each contract.  The ResultStreamer
 * will handle anything special for each Resource content such as adding schemaLocations. 
 * @author kevinbenson
 *
 */
public abstract class ResourceStreamDelegate extends StreamReaderDelegate implements XMLStreamReader {

  protected List resSet = null;

  private XMLStreamReader resXMLStreamReader = null;
  //by default say we are on the wrapper part of the reader.
  private boolean currentReaderisWrapper = true;
  private XMLStreamReader wrapperStreamReader =  null;
  private boolean identOnly = false;
  
  private static final String ASTROGRID_SCHEMA_BASE = "http://software.astrogrid.org/schema/";
  protected static String schemaLocationBase;
  
  /**
   * Logging variable for writing information to the logs
   */
   private static final Log log = LogFactory.getLog(ResourceStreamDelegate.class);
  
  
  /**
   * Static to be used on the initiatian of this class for the config
   */   
  static {
        if(schemaLocationBase == null) {              
            schemaLocationBase = ContractsFilter.getContextURL() != null ? ContractsFilter.getContextURL() + "/schema/" :
                                 ASTROGRID_SCHEMA_BASE;
        }
  }  
  
  /**
   * Constructor
   * Description: Get the Set of Resources, and the xml string that is a wrapper around the whole Resource set.
   * @param resSet Set of XML Resource objects
   * @param xmlWrapper XML string used to wrap the ResourceSet hence it elements at the beginning and end.
   */
  public ResourceStreamDelegate(List resSet,String xmlWrapper) {
    super();
  	this.resSet = resSet;
  	//create a StreamReader for the xml string wrapper.  And since this is in the beginning set it as the current parent.
  	wrapperStreamReader = STAXUtils.createXMLStreamReader(new StringReader(xmlWrapper));
  	setParent(wrapperStreamReader);
  }
  
  /**
   * Constructor
   * Description: Get the Set of Resources, and the xml string that is a wrapper around the whole Resource set.
   * @param resSet Set of XML Resource objects
   * @param xmlWrapper XML string used to wrap the ResourceSet hence it elements at the beginning and end.
   * @param identOnly identifier only set to stream out only identifier elements not a whole Resource.
   */
  public ResourceStreamDelegate(List resSet,String xmlWrapper, boolean identOnly) {
	    super();
	  	this.resSet = resSet;
	  	this.identOnly = identOnly;
	  	//create a StreamReader for the xml string wrapper.  And since this is in the beginning set it as the current parent.	  	
	  	wrapperStreamReader = STAXUtils.createXMLStreamReader(new StringReader(xmlWrapper));
	  	setParent(wrapperStreamReader);
	  }  
  
  /**
   * Method: abstract getResourceContent
   * Description: Used by subclasses to analyze each single Resource from the Set and add
   * anything special to the Resource such as schemalocations finally returning the XML as a string which is
   * streamed out.
   * @param res Single XML Resource
   * @param identOnly boolean to determine if only the identifier element should be returned instead of the whole XML Resource.
   * @return XML string of normally the XML Resource or in other cases just the identifier.
   * @throws org.xmldb.api.base.XMLDBException thrown if a problem getting the XML Resource from the database.
   */
  public abstract String getResourceContent(Resource res, boolean identOnly) throws org.xmldb.api.base.XMLDBException;
  
  /**
   * Method: next
   * Description: Overwrites the next() method in the Xfire StreamReaderDelegate.
   * This method figures out if there is a next element and streams that element out to the 
   * client.  But does other logic such as determine if it is at the END of a Wrapper Element so it
   * can start going through the ResourceSet and streaming out the XML Resources whereby when done go
   * back to the wrap Streamer to finish off the end. 
   * 
   * @return next element type. Should constantly be called till END_DOCUMENT.
   * @throws XMLStreamException
   */
  public int next()  throws XMLStreamException {
   	
	int current;
	try {
		//Check if were processing a Resource in the ResourceSet
		//if so then simply call super.next().
    if(resXMLStreamReader != null) {
    	return super.next();
    }
    
    //okay resourceset size is 0 and  not on the wrapper(header/footer) reader.
    //so lets set it back to the wrapper reader and return an end_element.  This
    //should now put it is back on the end element for the footer area.
  	if(resSet.size() == 0 && !currentReaderisWrapper) {
  		setParent(wrapperStreamReader);
  		currentReaderisWrapper = true;
  		//we can return END_ELEMENT since that is where we left off for the footer area.
  		return wrapperStreamReader.END_ELEMENT;
  	}
  	current = super.next();
  	//We are at an end element and nothing has been checked for
  	//resourceSet yet.  So check if there are any and begin processing the
  	//first resource after that hasNext()
  	//will take over with the iteration.
  	if(current == wrapperStreamReader.END_ELEMENT && resSet.size() > 0) {
	   //log.info("resset size = " + resSet.getSize() + " here is the string in next() and resources still left = " + getResourceContent(resSet.getResource((resSet.getSize() - 1)),identOnly));
  	   resXMLStreamReader = STAXUtils.createXMLStreamReader
  	                           (new StringReader(getResourceContent((Resource)resSet.get((resSet.size() - 1)),identOnly)));
  		
  	   //okay set the delegate to this new reader.
  	   setParent(resXMLStreamReader);
  	   
  	   //no longer on the wrapper elements.
  	   currentReaderisWrapper = false;
  	   //okay we should have our content in the stream reader
  	   //remove the resource from the collection.
  	   resSet.remove((resSet.size() - 1));
  	   //log.info("just removed some kind of resource size = " + resSet.getSize());
  	   return super.nextTag();
  	}
  	
	}catch(XMLDBException xdbe) {
		log.error("xmldbexception + " + xdbe);
		log.error(xdbe);
		throw new XMLStreamException(xdbe.getMessage());
	}
  	
  	return current;
  }
  
  /**
   * Method: hasNext
   * Description: Overwrites the hasNext in the Xfire StreamerDelegate.
   * This method determines if were at the end of a Resource and step through
   * the ResourceSet to the next Resource whereby it will return true till
   * the last element in the last Resource of the Set has gone through.
   * 
   * @return true or false if there are more Nodes to process.
   * @throws XMLStreamException
   */
  public boolean hasNext()  throws XMLStreamException {
  	boolean current = super.hasNext();
  	//check if we are processing xml in the ResourceSet.
  	try {
    if(resXMLStreamReader != null) {
    	//okay were at the end of the document go to the next resource if there are any.
    	if(!current) {
    		if(resSet.size() > 0) {
    	    resXMLStreamReader.close();
    	    //log.info("hasNext() resset size = " + resSet.getSize() + " here is the string in hasNext() and resources still left = " + getResourceContent(resSet.getResource((resSet.getSize() - 1)), identOnly));
    	    //Make a StreamReader out of a Single Resource.
    	    //Calls getResourceContent in the ResultStreamer subclass in case there is 
    	    //anything special to be done to the Resource first.
    		resXMLStreamReader = STAXUtils.createXMLStreamReader
  	                           (new StringReader(getResourceContent((Resource)resSet.get((resSet.size()-1)), identOnly)));
    		
    	    
  	        //set a new parent reader.
  	        setParent(resXMLStreamReader);
  	   
	  	   //okay we should have our content in the stream reader
  		   //remove the resource from the collection.
  	   	   resSet.remove((resSet.size() - 1));
  	  	   //log.info("just removed some kind of resource size = " + resSet.getSize());  	   	   
  	   	   return true;
  	   	   }else {
  	   	     //okay we went through some kind of resourceset and now done.
  	   	     //lets set the reader to null.  Return true.  The 
  	   	     //next() method will set it back to the footer part of the
  	   	     //wrapperelements.
  	   		 //log.info("no more resources left time to set xmlstreamreader to null and return true in hasNext() because there is still the wrapper");
  	   	     resXMLStreamReader = null;
  	   	     return true;
  	   	   }
    	}else {
    	  //were not at the end of the document and in the Resource somewhere.
    	  return current;
    	}
    }//if
    
  	}catch(XMLDBException xdbe) {
		log.error("xmldbexception + " + xdbe);
		log.error(xdbe);
  		throw new XMLStreamException(xdbe.getMessage());
  	}
    //okay either  no resourceset or finished with it.  Must
    //be in the wrapper elements area.
    return current;
  }

}