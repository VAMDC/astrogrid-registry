XMLSerializer.prototype = new Object();
XMLSerializer.prototype.constructor = XMLSerializer;
XMLSerializer.superclass = Object.prototype;

function XMLSerializer() {
	this.init();
}

XMLSerializer.prototype.init = function() {
	
}

XMLSerializer.prototype.createAttribute = function(a) {
	return " " + a.nodeName + '="' + a.nodeValue + '"';
}

XMLSerializer.prototype.serializeToText= function (node) {
	
	if (node == null) return "";
		
	var str = "";
	
	switch (node.nodeType) {
		case 1:	// Element

			var cs = node.childNodes;
			for (var i = 0; i < cs.length; i++)
				str += this.serializeToText(cs[i]);

			break;
	
		case 9:	// Document
			var cs = node.childNodes;
			for (var i = 0; i < cs.length; i++)
				str += this.serializeToText(cs[i]);
			break;
	
		case 3:	// Text
			if (!/^\s*$/.test(node.nodeValue))
				str += node.nodeValue;
			break;
	
		case 7:	// ProcessInstruction
			break;
	
		case 4:	// CDATA
			str = node.nodeValue;
			break;
			
		case 8:	// Comment
			break;
		
		case 10:
			break;
		default:
			//alert(node.nodeType + "\n" + node.nodeValue);
			inspect(node);
	}
	
	return str;
}
XMLSerializer.prototype.serializeToString= function (node) {
	
	if (node == null) return
		
	var str = "";
	
	switch (node.nodeType) {
		case 1:	// Element
			str += "<" + node.nodeName ;
			
			var attrs = node.attributes;
			for (var i = 0; i < attrs.length; i++)
				str += this.createAttribute(attrs[i]);
			
			if (!node.hasChildNodes())
				return str + "/>";
			
			str += ">";
			
			var cs = node.childNodes;
			for (var i = 0; i < cs.length; i++)
				str += this.serializeToString(cs[i]);
			
			str += "</" + node.nodeName + ">";
			break;
	
		case 9:	// Document
			var cs = node.childNodes;
			for (var i = 0; i < cs.length; i++)
				str += this.serializeToString(cs[i]);
			break;
	
		case 3:	// Text
			if (!/^\s*$/.test(node.nodeValue))
				str += node.nodeValue;
			break;
	
		case 7:	// ProcessInstruction
			str += "<?" + node.nodeName;
		
			var attrs = node.attributes;
			for (var i = 0; i < attrs.length; i++)
				str += this.createAttribute(attrs[i]);
			
			str+= "?>"
			break;
	
		case 4:	// CDATA
			str = "<![CDATA[" + node.nodeValue + "]]>";
			break;
			
		case 8:	// Comment
			str = "<!--" +  node.nodeValue + "-->";
			break;
		
		case 10:
				str = "<!DOCTYPE " + node.name;
				if (node.publicId) {
					str += " PUBLIC \"" + node.publicId + "\"";
					if (node.systemId) 
						str += " \"" + node.systemId + "\"";
				}
				else if (node.systemId) {
					str += " SYSTEM \"" + node.systemId + "\"";
				}
				str += ">";
				
				// TODO: Handle custom DOCTYPE declarations (ELEMENT, ATTRIBUTE, ENTITY)
				
				break;
		
		default:
			//alert(node.nodeType + "\n" + node.nodeValue);
			inspect(node);
	}
	
	return str;
	
}


XMLSerializer.prototype.serializeToHtml= function (node) {


	if (node == null) return "";
	var str = "";
	
	switch (node.nodeType) {
		case 1:	// Element
			str += "<div class='element'>&lt;<span class='elementname'>" + node.nodeName + "</span>";
			
			var attrs = node.attributes;
			for (var i = 0; i < attrs.length; i++)
				str += this.createAttribute(attrs[i]);
			
			if (!node.hasChildNodes())
				return str + "/&gt;</div>";
			
			str += "&gt;<br />";
			
			var cs = node.childNodes;
			for (var i = 0; i < cs.length; i++)
				str += this.serializeToHtml(cs[i]);
			
			
			str += "&lt;/<span class='elementname'>" + node.nodeName + "</span>&gt;</div>";
			break;
	
		case 9:	// Document
			var cs = node.childNodes;
			
			for (var i = 0; i < cs.length; i++)
				str += this.serializeToHtml(cs[i], indent);
			break;
			
			
	
		case 3:	// Text
			if (!/^\s*$/.test(node.nodeValue))
				str += "<span class='text'>" + node.nodeValue + "</span><br />";
			break;
	
		case 7:	// ProcessInstruction
			str += "&lt;?" + node.nodeName;
		
			var attrs = node.attributes;
			for (var i = 0; i < attrs.length; i++)
				str += this.createAttribute(attrs[i]);
			
			str+= "?&gt;<br />"
			break;
	
		case 4:	// CDATA
			str = "<div class='cdata'>&lt;![CDATA[<span class='cdata-content'>" + 
				node.nodeValue +
			"</span>]" + "]></div>";
			break;
			
		case 8:	// Comment
			str = "<div class='comment'>&lt;!--<span class='comment-content'>" + 
				node.nodeValue +
			"</span>--></div>";
			break;
		
		case 10:
				str = "<div class='doctype'>&lt;!DOCTYPE " + node.name;
				if (node.publicId) {
					str += " PUBLIC \"" + node.publicId + "\"";
					if (node.systemId) 
						str += " \"" + node.systemId + "\"";
				}
				else if (node.systemId) {
					str += " SYSTEM \"" + node.systemId + "\"";
				}
				str += "&gt;</div>";
				
				// TODO: Handle custom DOCTYPE declarations (ELEMENT, ATTRIBUTE, ENTITY)
				
				break;
		
		default:
			//alert(node.nodeType + "\n" + node.nodeValue);
			inspect(node);
	}

	
	return str;

}