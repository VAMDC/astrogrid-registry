/**
 * xslt2xforms bindings class
 *
 */

/**
 * xforms atom object
 */
Xbind.prototype = new Object();
Xbind.prototype.constructor = Xbind;
Xbind.superclass = Object.prototype;

/**
 * constructor
 */
function Xbind(oAttributes) {
	this.init(oAttributes);
}

/**
 * 
 */
Xbind.prototype.init = function(oAttributes) {
	this.id='bind';
	this.parent = null;
	this.attributes = oAttributes;
	this.regexp = regexps[this.attributes['type']];
	if(!this.regexp) this.regexp = regexps['xsd:string'];

	this.xPathString = this.attributes['nodeset'] || this.attributes['ref'];
	
	var reAbsolutePath = new RegExp("^/");						
	if(reAbsolutePath.test(this.xPathString)) this.xPathString = '/instances/instance' + this.xPathString;

	this.xPath = new Dom3Xpath();

	this.childNodes = new Array();
	this.nChildNodes = 0;	

	this.parent = null;
	this.model = null;
	
	this.bindResultCache = null;
	
	this.refreshCache = new Array();
	this.nRefreshCache = 0;
}

/**
 * 
 */
Xbind.prototype.appendBind = function(oChild) {
	oChild.parent = this;
	oChild.model = this.model;
	this.childNodes[this.nChildNodes++] = oChild;
}



/**
 * 
 */
Xbind.prototype.setDisabled = function(context) {
	if (!this.attributes['relevant']) return 'xforms-enabled';
	
	var s = new XMLSerializer();
	
	return this.xpath(this.attributes['relevant'],context)?'xforms-enabled':'xforms-disabled';
}

/**
 * 
 */
Xbind.prototype.setReadOnly = function(context) {
	if (!this.attributes['readonly']) return 'xforms-readwrite';
	return this.xpath(this.attributes['readonly'],context)?'xforms-readonly':'xforms-readwrite';
}

/**
 * 
 */
Xbind.prototype.setRequired = function(context) {
	if (!this.attributes['required']) return 'xforms-optional';
	return this.xpath(this.attributes['required'],context)?'xforms-required':'xforms-optional';
}

/**
 * 
 */
Xbind.prototype.validateType = function (context) { 
	//if (!this.attributes['type']) 
	var s = new XMLSerializer();
	return this.regexp.match(s.serializeToText(context))?'xforms-valid':'xforms-invalid';
}


/**
 * 
 */
Xbind.prototype.rebuild = function(context) {
	
	// add xpath binding result in cache
	this.bindResultCache = this.xPath.evaluate(this.xPathString, context, null, 9, null);
	
	for (var i=0; i<this.nChildNodes; i++) {
		this.childNodes[i].rebuild(this.bindResultCache.singleNodeValue);
	}

}

/**
 * 
 */
Xbind.prototype.refresh = function() {

	for (var j=0; j<this.bindResultCache.nodeSet.length; j++) {
		var c = this.bindResultCache.nodeSet[j];
		var r = this.refreshCache[j];

		for (var i=0; i < this.model.formControlsBindings.length; i++) {
			if (this.model.formControlsBindings[i].nodeset===c) {
				var el = this.model.formControlsBindings[i].el;
				el.dispatchEvent(Xevents[r.disabled]);
				el.dispatchEvent(Xevents[r.readOnly]);
				el.dispatchEvent(Xevents[r.required]);
				el.dispatchEvent(Xevents[r.validate]);
				var s = new XMLSerializer();
				setFormValue(el,s.serializeToText(c));		
			}
		}
	
	}

	for (var i=0; i<this.nChildNodes; i++) {
		this.childNodes[i].refresh();
	}	


}


/**
 * 
 */
Xbind.prototype.recalculate = function() {
		
	for (var j=0; j< this.bindResultCache.nodeSet.length; j++) {
		var c = this.bindResultCache.nodeSet[j];
		var r = this.refreshCache[j] = {'disabled' : this.setDisabled(c), 'readOnly': this.setReadOnly(c), 'required': this.setRequired(c), 'validate': this.validateType(c) }; 		

		if (this.attributes['calculate']) {
			if (c.nodeValue) {
				c.nodeValue = this.calculate(c);
			} else {
				c.appendChild(this.model.instanceData.createTextNode(this.calculate(c)));
			}
		}
	}	

	for (var i=0; i<this.nChildNodes; i++) {
		this.childNodes[i].recalculate();
	}	

}

/**
 * 
 */
Xbind.prototype.revalidate = function() {
	for (var j=0; j<this.bindResultCache.nodeSet.length; j++) {
		var c = this.bindResultCache.nodeSet[j];
		var r = this.refreshCache[j];
		
		for (var i=0; i < this.model.formControlsBindings.length; i++) {

			var el = this.model.formControlsBindings[i].el;
			
			if (this.model.formControlsBindings[i].nodeset===c) {
				el.dispatchEvent(Xevents[r.disabled]);
				el.dispatchEvent(Xevents[r.readOnly]);
				el.dispatchEvent(Xevents[r.required]);
				el.dispatchEvent(Xevents[r.validate]);
			}
		} 
	}
	
	
	for (var i=0; i<this.nChildNodes; i++) {
		this.childNodes[i].revalidate();
	}

}

/**
 * 
 */
Xbind.prototype.xpath = function(bindString,bindContext) {
	var bindResult = this.xPath.evaluate(bindString, bindContext, null, 3, null);
	return bindResult.booleanValue;
}

/**
 * 
 */
Xbind.prototype.calculate = function(calculateContext) {
	var calculateResult = this.xPath.evaluate( this.attributes['calculate'], calculateContext, nsRes, 3, null);		
	return calculateResult.stringValue;
}