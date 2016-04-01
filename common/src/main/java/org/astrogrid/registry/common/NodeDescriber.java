package org.astrogrid.registry.common;

public class NodeDescriber {
	
	private String namespace;
	private String localName;
	private String nodeName;
	private String prefix;
	
	
	public NodeDescriber(String namespace, String localName) {
		this(namespace, localName, null);
	}
	
	public NodeDescriber(String namespace, String localName, String prefix) {
		this.namespace = namespace;
		this.localName = localName;
		this.prefix = prefix;
		if(prefix != null) {
			this.nodeName = prefix + ":" + localName;
		}else {
			this.nodeName = localName;
		}
	}
	
	public String getNameSpace() { return this.namespace; }
	public String getLocalName() { return this.localName; }
	public String getPrefix() { return this.prefix; }
	public String getNodeName() { return this.nodeName; }
	

}
