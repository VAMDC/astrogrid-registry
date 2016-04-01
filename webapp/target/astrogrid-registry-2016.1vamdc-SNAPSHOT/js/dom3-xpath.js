/***********************************************
 * Dom Level 3 Xpath Wrapper
 */

ANY_TYPE = 0;
NUMBER_TYPE = 1;
STRING_TYPE = 2;
BOOLEAN_TYPE = 3;
UNORDERED_NODE_ITERATOR_TYPE = 4;
ORDERED_NODE_ITERATOR_TYPE = 5;
UNORDERED_SNAPSHOT_TYPE = 6;
ORDERED_SNAPSHOT_TYPE = 7;
ANY_UNORDERED_NODE_TYPE = 8;
FIRST_ORDERED_NODE_TYPE = 9;

Dom3Xpath.prototype = new Object();
Dom3Xpath.prototype.constructor = Dom3Xpath;
Dom3Xpath.superclass = Object.prototype;

function Dom3Xpath() {
	this.init();
}

Dom3Xpath.prototype.init = function() {
	this.parser = new XPathParser();
	this.context = new XPathContext(null,nsRes,funRes);
}

Dom3Xpath.prototype.evaluate = function (expression,contextNode,nsResolver,resultType, other) {
  
	this.xpath =  this.parser.parse(expression);
	this.context.expressionContextNode = contextNode;
	this.result = this.xpath.evaluate(this.context);

	this.XPathResult = new Dom3XpathResult(this.result);
	return this.XPathResult;
	
}

Dom3Xpath.prototype.createNSResolver = function(contextNode) {
	return null;
}

Dom3NamespaceResolver.prototype = new NamespaceResolver();
Dom3NamespaceResolver.prototype.constructor = Dom3NamespaceResolver;
Dom3NamespaceResolver.superclass = NamespaceResolver.prototype;

function Dom3NamespaceResolver() {
}

Dom3NamespaceResolver.prototype.getNamespace = function(prefix, n) {
  if (prefix=='internal') return 'uri:internal';  
  if (xnamespaces[prefix]!=null)  return xnamespaces[prefix];
  return Dom3NamespaceResolver.superclass.getNamespace(prefix, n);
};

var nsRes = new Dom3NamespaceResolver();



Dom3XpathResult.prototype = new Object();
Dom3XpathResult.prototype.constructor = Dom3XpathResult;
Dom3XpathResult.superclass = Object.prototype;

function Dom3XpathResult(xresult) {
	this.init(xresult);
}

Dom3XpathResult.prototype.init = function(xresult) {
	this.result = xresult;
	
	this.stringValue = this.result.toString();
	
	switch (this.result.constructor) {
	case XNumber:
		this.resultType = 1;
		this.numberValue = this.result.numberValue();
		break;
	case XString:
		this.resultType = 2;
		this.stringValue = this.result.stringValue();
		break;
	case XBoolean:
		this.resultType = 3;
		this.booleanValue = this.result.booleanValue();
		break;
	case XNodeSet:
		this.resultType = 7;
		this.singleNodeValue = this.result.toArray()[0];
		this.nodeSet =  this.result.toArray();
			
		// added
		this.snapshotLength = this.nodeSet.length;
			
		//
		break;
	}
	
	
}

Dom3XpathResult.prototype.snapshotItem = function(index) {
	return this.nodeSet[index];
}
