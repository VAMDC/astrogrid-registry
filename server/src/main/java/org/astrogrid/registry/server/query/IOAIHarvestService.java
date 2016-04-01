package org.astrogrid.registry.server.query;

import org.w3c.dom.Document;
import javax.xml.stream.*;

/**
 * Interface: ISearch
 * Description: Standard methods used for the Query Service which implements
 * this interface.
 * @author kevinbenson
 *
 */
public interface IOAIHarvestService {
	
	
	public XMLStreamReader Identify(Document update);
	
	public XMLStreamReader ListRecords(Document update);
	
	public XMLStreamReader ListMetadataFormats(Document update);
	
	public XMLStreamReader ListSets(Document update);
	
	public XMLStreamReader ResumeListSets(Document update);
	
	public XMLStreamReader ListIdentifiers(Document update);
	
	public XMLStreamReader GetRecord(Document update);
	
	
	
}