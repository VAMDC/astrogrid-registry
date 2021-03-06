/*
**  Script written by Jason Johnston (jj@lojjic.net)
**
**  Including this file as the first script in your HTML document will make 
**  MSIE 5+ conform to most of the W3C DOM2 Events, Views, and Style modules.
**  For details see http://lojjic.net/script-library/IEtoW3C-doc.html
**
**  This work is licensed for use under the Creative Commons
**  Attribution-NonCommercial-ShareAlike license. In summary, you
**  may freely use, modify, and distribute this work as long as:
**    - You give me (Jason Johnston) credit,
**    - You do not use this work for commercial purposes, and
**    - Any redistribution of this or derivative works is made
**      available under a license identical to this one.
**  Before using this work please read the full license at 
**  http://creativecommons.org/licenses/by-nc-sa/1.0/legalcode
*/



//// Extend d.all and d.layers DOMs to understand d.getElementById
/* XXX - this doesn't really belong here, since it's become a 
         library for IE5+ only. */
if(!document.getElementById) {
	document.getElementById = function(id,contDoc) {
		var d=document; var x=null;
		if(d.layers) { //NS 4
			if(!contDoc) contDoc=d;
			if(contDoc[id]) x=contDoc[id];
			else {
				var lyrs=contDoc.layers;
				for(var i=0;!x && lyrs && i<lyrs.length;i++) x=d.getElementById(id,lyrs[i].document);
			}
			if(x) x.style = x;
		}
		else if(d.all) x=d.all[id]; //MSIE 4
		return x;
	}
} 


/*=== DOM2 Events: ===*/
if (!window.addEventListener && document.all /*(remove to enable partial (buggy) MacIE support:)*/ && window.attachEvent) {
	var IEtoW3C_evtAttrs = ["onmouseover","onmouseout","onmousemove","onclick","onchange","onfocus","onblur","onload","onkeypress"];
	function IEtoW3C_grabEventAttributes(elt) { //make on* attributes into DOM event listeners:
		if(!elt.attachEvent) return; //HACK: don't do this on Mac, since we have to preserve the onclick property later to listen for events.
		if(elt.nodeType != 1) return; //elements only
		for(var i=0; i<IEtoW3C_evtAttrs.length; i++) {
			var attr = (elt.getAttribute) ? elt.getAttribute(IEtoW3C_evtAttrs[i]) : null;
			if(attr) {
				elt["IEtoW3C_"+IEtoW3C_evtAttrs[i]] = elt[IEtoW3C_evtAttrs[i]]; //use later to test if "return false" should preventDefault().
				elt.addEventListener(IEtoW3C_evtAttrs[i].substring(2),IEtoW3C_execAttrEvent,false);
				elt[IEtoW3C_evtAttrs[i]] = null;
			}
		}
	}
	function IEtoW3C_execAttrEvent(evt) {
		var func = this["IEtoW3C_on"+evt.type].toString();
		var funcBody = func.substring(func.indexOf("{")+1, func.lastIndexOf("}"));
		funcBody = funcBody.replace(/([\W])this([\W])/,"$1event.currentTarget$2"); //fix "this" references
		funcBody = "var tmpFunc = function(event) {" + funcBody + "}; tmpFunc(evt);";
		var result = eval(funcBody);
		if(result == false) evt.preventDefault(); //"return false;" prevents default action
		func = funcBody = null;
	}
	function IEtoW3C_handleEvent(elt,evt) {
		var i, j, tmp;
		
		//fixup event object with DOM properties and methods:
		if(!evt) var evt = window.event;
		if(!evt.currentTarget) evt.currentTarget = elt;
		if(!evt.target) evt.target = evt.srcElement || elt;
		if(evt.bubbles == null) evt.bubbles = true;
		if(evt.cancelable == null) evt.cancelable = true;
		evt.cancelBubble = false;
		evt.returnValue = true;
		evt.stopPropagation = function() {evt.cancelBubble=true;};
		evt.preventDefault = function() {evt.returnValue=false;};
		if(!evt.relatedTarget) evt.relatedTarget = evt.fromElement || evt.toElement || null;
		evt.pageX = evt.clientX + document.body.scrollLeft;
		evt.pageY = evt.clientY + document.body.scrollTop;
		evt.timeStamp = new Date().valueOf(); //this is arbitrary by the spec?
		if(String.fromCharCode(evt.keyCode).match(/[\w\d]/)) { //this is incomplete list of keyChars.
			evt.charCode = evt.keyCode; evt.keyCode = 0;
		} else evt.charCode = 0;

		//get all ancestors:
		var ancestors = []; tmp = evt.target;
		while(tmp) {
			IEtoW3C_grabEventAttributes(tmp);
			ancestors[ancestors.length] = tmp;
			tmp = tmp.parentNode;
		}
		//add document and window at top of tree:
		tmp = ancestors[ancestors.length-1];
		if(tmp != window) {
			if(tmp != document) ancestors[ancestors.length] = document;
			ancestors[ancestors.length] = window;
		}

		//don't handle if anything higher up will also handle (prevents duplicate firing):
		for(i=0; i<ancestors.length; i++) {
			if(!ancestors[i].IEtoW3C_onevent) IEtoW3C_hookupDOMEventsOn(ancestors[i]);
			if(ancestors[i].IEtoW3C_onevent[evt.type]) {
				if(ancestors[i] != elt) ancestors = [];
				break;
			}
		}

		//fire each handler in correct order, passing the event object as sole parameter:
		//capturing:
		evt.eventPhase = 1;
		for(i=ancestors.length-1; i>=0 && (!evt.cancelable || !evt.cancelBubble); i--) {
			if(i==0) evt.eventPhase = 2;
			evt.currentTarget = ancestors[i];
			tmp = ancestors[i].IEtoW3C_onevent[evt.type];
			if(tmp) for(j=0; j<tmp.capture.length; j++) {
				elt.IEtoW3C_handler = tmp.capture[j]; //make "this" refer to element
				elt.IEtoW3C_handler(evt);
			}
		}
		//bubbling:
		for(i=0; i<ancestors.length && (!evt.cancelable || !evt.cancelBubble); i++) {
			if(ancestors[i]!=evt.target) evt.eventPhase = 3;
			evt.currentTarget = ancestors[i];
			tmp = ancestors[i].IEtoW3C_onevent[evt.type];
			if(tmp) for(j=0; j<tmp.bubble.length; j++) {
				elt.IEtoW3C_handler = tmp.bubble[j]; //make "this" refer to element
				elt.IEtoW3C_handler(evt);
			}
		}

		//replace keyCode so form fields will get keystroke
		if(evt.charCode > 0 && evt.keyCode == 0) evt.keyCode = evt.charCode;

		//clean up:
		i = j = tmp = elt = elt.IEtoW3C_handler = ancestors = evt = /*evt.currentTarget = evt.target = evt.stopPropagation = evt.preventDefault = evt.relatedTarget = evt.pageX = evt.pageY = evt.eventPhase = evt.timeStamp = evt.charCode =*/ null;
	}
	
	function IEtoW3C_hookupDOMEventsOn(elt) {
		if(elt.IEtoW3C_onevent) return;
		elt.IEtoW3C_onevent = {};

		elt.addEventListener = function(evtType,handler,capture) {
			var onevent = this.IEtoW3C_onevent[evtType];
			if(!onevent) {
				onevent = this.IEtoW3C_onevent[evtType] = {capture:[],bubble:[]};
				//set base listener to fire off custom event handling flow (it all starts here):
				if(this.attachEvent) this.attachEvent("on"+evtType, function() { IEtoW3C_handleEvent(elt); } );
				else this["on"+evtType] = function() { IEtoW3C_handleEvent(elt); } //IE Mac
			}
			var handlers = (capture) ? onevent.capture : onevent.bubble;
			for(var i=0; i<handlers.length; i++) {
				if(handlers[i] == handler) { //avoid duplicates; move duped handler to end:
					for(var j=i; j<handlers.length-1; j++) handlers[j] = handlers[j+1];
					handlers.length--;
				}
			}
			handlers[handlers.length] = handler;
			handlers = null;
		};
		elt.removeEventListener = function(evtType,handler,capture) {
			var onevent = this.IEtoW3C_onevent[evtType];
			if(!onevent) return;
			var handlers = (capture) ? onevent.capture : onevent.bubble;
			for(var i=0; i<handlers.length; i++) { //remove any instances of handler from list:
				if(handlers[i] == handler) {
					for(var j=i; j<handlers.length-1; j++) handlers[j] = handlers[j+1];
					handlers.length--;
				}
			}
		};
		elt.dispatchEvent = function(evt) {
			if(!evt.type) return;
			evt.target = evt.currentTarget = this;
			IEtoW3C_handleEvent(this,evt);
		};
		IEtoW3C_grabEventAttributes(elt);
	}
	function IEtoW3C_unhookDOMEventsFrom(elt) {
		elt.addEventListener = elt.removeEventListener = elt.dispatchEvent = /*elt.IEtoW3C_onevent =*/ null;
		for(var i=0; i<IEtoW3C_evtAttrs.length; i++) {
			elt["IEtoW3C_tmp"+IEtoW3C_evtAttrs[i]] = null;
		}
	}
	document.createEvent = function(evtFam) {
		var evt = {}; //new Event object
		if(evtFam=="UIEvents") evt.initUIEvent = function(type,bub,can,view,det) {
			this.type = type; this.bubbles = bub; this.cancelable = can;
			this.view = view; this.detail = det;
		}
		else if(evtFam=="MouseEvents") evt.initMouseEvent = function(type,bub,can,view,det,sX,sY,cX,cY,ctrl,alt,shft,meta,btn,relTgt) {
			this.type = type; this.bubbles = bub; this.cancelable = can;
			this.view = view; this.detail = det;
			this.screenX = sX; this.screenY = sY;
			this.clientX = cX; this.clientY = cY;
			this.ctrlKey = ctrl; this.altKey = alt;
			this.shiftKey = shft; this.metaKey = meta;
			this.button = btn; this.relatedTarget = relTgt;
		}
		else if(evtFam=="MutationEvents") evt.initMutationEvent = function(type,bub,can,relNode,prevVal,newVal,attrName,attrChange) {
			this.type = type; this.bubbles = bub; this.cancelable = can;
			this.relatedNode = relNode;
			this.prevValue = prevVal; this.newValue = newVal;
			this.attrName = attrName; this.attrChange = attrChange;
		}
		else evt.initEvent = function(type,bub,can) {
			this.type = type; this.bubbles = bub; this.cancelable = can;
		}
		return evt;
	}

	IEtoW3C_hookupDOMEventsOn(window);
	IEtoW3C_hookupDOMEventsOn(document);
	window.addEventListener("load",function() {
		var all = document.all;
		for(var i=0; i<all.length; i++) IEtoW3C_hookupDOMEventsOn(all[i]);
	},false);

	window.addEventListener("beforeunload",function() { //clean up:
		IEtoW3C_unhookDOMEventsFrom(window);
		IEtoW3C_unhookDOMEventsFrom(document);
		var all = document.all;
		for(var i=0; i<all.length; i++) IEtoW3C_unhookDOMEventsFrom(all[i]);
		document.createEvent = null;
	},false);
	
	//make it work for newly created elements:
	document.IEtoW3C_createElement = document.createElement;
	document.createElement = function(tagName) {
		var newElt = document.IEtoW3C_createElement(tagName);
		IEtoW3C_hookupDOMEventsOn(newElt);
		return newElt;
	}
}

/*=== DOM2 Core: ===*/
if(!document.implementation) {
	document.implementation = {
		hasFeature : function(feature,version) {},
		createDocumentType : function(qName,pubId,sysId) {}
	}
}
/* Experimental: add createDocument() -- IE6 fails on this, because it doesn't allow custom properties to be added to its partially-implemented document.implementation. Grr.
if(!document.implementation.createDocument && window.ActiveXObject) {
	document.implementation.createDocument = function(namespace,rootNode,doctype) {
		var progIDs = ["Msxml2.DOMDocument.4.0", "Msxml2.DOMDocument.3.0", "MSXML2.DOMDocument", "MSXML.DOMDocument", "Microsoft.XmlDom"];
		var getProgID = function() {
			for(var i=0; i<progIDs.length; i++) {
				try {
					var tmp = new ActiveXObject(progIDs[i]);
					return progIDs[i];
				} catch(e) {}
			}
			throw "No MSXML found on system";
		}
		var newDoc = new ActiveXObject(getProgID());
		IEtoW3C_hookupDOMEventsOn(newDoc);
		newDoc.onreadystatechange = function() {
			if(this.readyState == 4) {
				if(this.onload) this.onload();
				var newEvt = document.createEvent("HTMLEvents");
				newEvt.initEvent("load",true,true);
				this.dispatchEvent(newEvt);
			}
		}
		return newDoc;
	}
} */

// Namespace methods (XML only):
var isXML = (document.documentElement && document.documentElement.namespaceURI) ? true : false;
if(isXML && !document.createElementNS && document.createElement) {
	document.createElementNS = function(ns,tagName) {
		var elt = document.createElement(tagName);
		elt.namespaceURI = ns;
		return elt;
	}
}
if(isXML && !document.getElementsByTagNameNS && document.getElementsByTagName) {
	function hookupNamespaceMethodsOn(elt) {
		elt.getElementsByTagNameNS = function(ns,tagName) {
			var allElts = this.getElementsByTagName(tagName);
			var nsElts = [];
			for(var i=0; i<allElts.length; i++) {
				if(allElts[i].namespaceURI == ns) nsElts[nsElts.length] = allElts[i];
			}
			return nsElts;
		}
	}
	window.addEventListener("load",function(evt) {
		var all = document.all;
		for(var i=0; i<all.length; i++) hookupNamespaceMethodsOn(all[i]);
		hookupNamespaceMethodsOn(document);
	},false);
}


/*=== DOM2 Views: ===*/
if(!document.defaultView) document.defaultView = window;

/*=== DOM2 Style: ===*/
//NOTE: these will not always return the same values as an actual implementation, but will usually be close.
if(!window.getComputedStyle) {
	window.getComputedStyle = function(elt,pseudo) { //pseudo is ignored
		return {
			getPropertyValue : function(prop) {
				//get actual values for width and height:
				var offsetTest = elt["offset" + prop.charAt(0).toUpperCase() + prop.substring(1)];
				if(offsetTest) return offsetTest + "px";

				//use currentStyle for IE:
				if(elt.currentStyle) {
					var jsProp = "";
					for(var i=0; i<prop.length; i++) {
						if(prop.charAt(i) == "-") jsProp += prop.charAt(++i).toUpperCase();
						else jsProp += prop.charAt(i);
					}
					return elt.currentStyle[jsProp];
				}
				
				//last chance:
				return elt.style[jsProp];
			}
		};
	}
}
