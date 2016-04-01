/**
 * xslt2xforms core class
 *
 */

var Xevents = new Array(); 
var Xmodels = new Array();
var Xrepeats = new Array();

var myDebug;



/**
 * xforms atom object
 */
Xforms.prototype = new Object();
Xforms.prototype.constructor = Xforms;
Xforms.superclass = Object.prototype;

/**
 * constructor
 */
function Xforms() {
		this.init();
}

/**
 * 
 */
Xforms.prototype.init = function() {

		myDebug = new Debug();
		
		this.InitializationEventsList = new Array(	'xforms-model-construct','xforms-model-construct-done', 'xforms-repeat-construct', 'xforms-repeat-construct-done', 
												'xforms-insert-construct', 'xforms-insert-construct-done', 'xforms-ready','xforms-model-destruct');
		this.InteractionEventsList = new Array(	'xforms-next','xforms-previous','xforms-focus','xforms-help',
												'xforms-hint','xforms-refresh','xforms-revalidate','xforms-recalculate',
												'xforms-rebuild','xforms-reset','xforms-submit');

    	this.NotificationEventsList = new Array('DOMActivate','xforms-activate','xforms-value-changed','xforms-select','xforms-deselect','xforms-scroll-first',
    											'xforms-scroll-last','xforms-insert','xforms-delete','xforms-setindex','xforms-valid','xforms-invalid',
        										'DOMFocusIn','DOMFocusOut','xforms-readonly','xforms-readwrite','xforms-required','xforms-optional',
        										'xforms-enabled','xforms-disabled','xforms-in-range','xforms-out-of-range','xforms-submit-done','xforms-submit-error');
    	
    	this.ErrorsEventsList = new Array('xforms-binding-exception','xforms-link-exception','xforms-link-error','xforms-compute-exception');

    	this.EventsList = new Array('change');
    
    	// register events	
    	this.registerEvents(this.InitializationEventsList);
    	this.registerEvents(this.InteractionEventsList);
    	this.registerEvents(this.NotificationEventsList);
    	this.registerEvents(this.ErrorsEventsList);
		this.registerEvents(this.EventsList);
	
		// register defaults handler
		this.addDefaultListeners();	
		
		this.childModels = new Array();	
		this.nChildModels= 0;

		document.dispatchEvent(Xevents['xforms-model-construct']);
}


/**
 * 
 */
Xforms.prototype.appendModel = function(oChild) {
	this.childModels[this.nChildModels] = oChild;
	this.childModels[this.nChildModels].run();
	if (oChild.id) this.childModels[oChild.id] = oChild;

	Xmodels[Xmodels.length] = oChild;
	if (oChild.id) Xmodels[oChild.id] = oChild;	
	
	this.nChildModels++
}


/**
 * 
 * scope: internal
 */
Xforms.prototype._traverseFormControls = function(node,contextNode) {
	var xPath = new Dom3Xpath();
	var _formcontrolBindings = new Array();
	

	for(var j=0; j < node.childNodes.length; j++) {
		switch (node.childNodes[j].nodeType) {
			case 1: 
				if (node.childNodes[j].getAttribute('xmlns')=='http://www.w3.org/2002/xforms' )	{
					try {
						var xPathString = node.childNodes[j].getAttribute('nodeset') || node.childNodes[j].getAttribute('ref');
						
						var reAbsolutePath = new RegExp("^/");						
						if(reAbsolutePath.test(xPathString)) xPathString = '/instances/instance' + xPathString;
						
						obj = xPath.evaluate(xPathString, contextNode, null, 9, null);
						setFormValue(node.childNodes[j], obj.stringValue);
						
						_formcontrolBindings[_formcontrolBindings.length] = {'el':node.childNodes[j],'nodeset': obj.singleNodeValue }; 					
						_formcontrolBindings.merge(this._traverseFormControls(node.childNodes[j],obj.singleNodeValue));
						
					} catch(err) {
						node.childNodes[j].dispatchEvent(Xevents['xforms-disabled']);
						node.childNodes[j].dispatchEvent(Xevents['xforms-binding-exception']);
					}
					
				} else {
					_formcontrolBindings.merge(this._traverseFormControls(node.childNodes[j],contextNode));
				}
				
				
				break;
		}
	}
	
		
	return _formcontrolBindings;
}		


/**
 * 
 */
Xforms.prototype.getModel = function(id) {
	return  this.childModels[id] || this.childModels[0];
}

/**
 * 
 */
Xforms.prototype.appendRepeat = function (oRepeat) {	
	Xrepeats[Xrepeats.length]=oRepeat;
	if (oRepeat.id) Xrepeats[oRepeat.id]=oRepeat;
	
	oRepeat.parent = this;
	
	document.dispatchEvent(Xevents['xforms-repeat-construct-done']);
	return oRepeat;
}


/**
 * 
 */
Xforms.prototype.addDefaultListeners = function(oHTMLNode) {
	if (!oHTMLNode) oHTMLNode = document.documentElement;
	var a = oHTMLNode.getElementsByTagName('INPUT');

	for (var i=0;i < a.length; i++) {
		a[i].addEventListener('focus',focusHandler,false);	
		a[i].addEventListener('blur',blurHandler,false);
		a[i].addEventListener('keypress',keyHandler,false);
		a[i].addEventListener('change',changeHandler,false);
		
		a[i].addEventListener('xforms-disabled',disableHandler,false);
		a[i].addEventListener('xforms-enabled',enableHandler,false);

		a[i].addEventListener('xforms-readonly',readOnlyHandler,false);
		a[i].addEventListener('xforms-readwrite',readWriteHandler,false);
		
		a[i].addEventListener('xforms-required',requiredHandler,false);
		a[i].addEventListener('xforms-optional',optionalHandler,false);

		a[i].addEventListener('xforms-valid',validHandler,false);
		a[i].addEventListener('xforms-invalid',invalidHandler,false);	
	
		if (a[i].getAttribute('type')=='radio' || a[i].getAttribute('type')=='checkbox') a[i].addEventListener('click',clickHandlerRadioCheckbox,false);		
	}

	
	var a = oHTMLNode.getElementsByTagName('TEXTAREA');
	for (var i=0;i < a.length; i++) {
		a[i].addEventListener('focus',focusHandler,false);	
		a[i].addEventListener('blur',blurHandler,false);
		a[i].addEventListener('click',clickHandler,false);		
		
		a[i].addEventListener('xforms-disabled',disableHandler,false);
		a[i].addEventListener('xforms-enabled',enableHandler,false);
		
	}
	
	var a = oHTMLNode.getElementsByTagName('BUTTON');
	for (var i=0;i < a.length; i++) {
		a[i].addEventListener('focus',focusHandler,false);	
		a[i].addEventListener('blur',blurHandler,false);
		a[i].addEventListener('click',clickHandler,false);

		a[i].addEventListener('xforms-disabled',disableHandler,false);
		a[i].addEventListener('xforms-enabled',enableHandler,false);
		
	}

	var a = oHTMLNode.getElementsByTagName('FIELDSET');
	for (var i=0;i < a.length; i++) {
		a[i].addEventListener('xforms-disabled',disableHandler,false);
		a[i].addEventListener('xforms-enabled',enableHandler,false);
	}

	var a = _getElementsByClassName('repeatinstance', oHTMLNode);
	for (var i=0;i < a.length; i++) {
		a[i].addEventListener('xforms-setindex',setIndexHandler,false);
	}

	var a = this.ErrorsEventsList;
	for (var i=0;i < a.length; i++) {
		document.addEventListener(a[i],errorHandler,false);	
	}


Xforms.prototype.removeDefaultListeners = function(oHTMLNode) {
	if (!oHTMLNode) oHTMLNode = document.documentElement;
	var a = oHTMLNode.getElementsByTagName('INPUT');
	for (var i=0;i < a.length; i++) {
		a[i].removeEventListener('focus',focusHandler,false);	
		a[i].removeEventListener('blur',blurHandler,false);
		a[i].removeEventListener('keypress',keyHandler,false);
		a[i].removeEventListener('change',changeHandler,false);
		
		a[i].removeEventListener('xforms-disabled',disableHandler,false);
		a[i].removeEventListener('xforms-enabled',enableHandler,false);

		a[i].removeEventListener('xforms-readonly',readOnlyHandler,false);
		a[i].removeEventListener('xforms-readwrite',readWriteHandler,false);

		a[i].removeEventListener('xforms-required',requiredHandler,false);
		a[i].removeEventListener('xforms-optional',optionalHandler,false);

		a[i].removeEventListener('xforms-valid',validHandler,false);
		a[i].removeEventListener('xforms-invalid',invalidHandler,false);
			
		if (a[i].getAttribute('type')=='radio' || a[i].getAttribute('type')=='checkbox') a[i].removeEventListener('click',clickHandlerRadioCheckbox,false);		
	}

	var a = oHTMLNode.getElementsByTagName('TEXTAREA');
	for (var i=0;i < a.length; i++) {
		a[i].removeEventListener('focus',focusHandler,false);	
		a[i].removeEventListener('blur',blurHandler,false);
		a[i].removeEventListener('click',clickHandler,false);		
		
		a[i].removeEventListener('xforms-disabled',disableHandler,false);
		a[i].removeEventListener('xforms-enabled',enableHandler,false);
		
	}
	
	var a = oHTMLNode.getElementsByTagName('BUTTON');
	for (var i=0;i < a.length; i++) {
		a[i].removeEventListener('focus',focusHandler,false);	
		a[i].removeEventListener('blur',blurHandler,false);
		a[i].removeEventListener('click',clickHandler,false);

		a[i].removeEventListener('xforms-disabled',disableHandler,false);
		a[i].removeEventListener('xforms-enabled',enableHandler,false);
		
	}

	var a = oHTMLNode.getElementsByTagName('FIELDSET');
	for (var i=0;i < a.length; i++) {
		a[i].removeEventListener('xforms-disabled',disableHandler,false);
		a[i].removeEventListener('xforms-enabled',enableHandler,false);
	}
	
	var a = _getElementsByClassName('repeatinstance', oHTMLNode);
	for (var i=0;i < a.length; i++) {
		a[i].removeEventListener('xforms-setindex',setIndexHandler,false);
	}
	
	var a = this.ErrorsEventsList;
	for (var i=0;i < a.length; i++) {
		document.removeEventListener(a[i],errorHandler,false);	
	}
}
	
	function clickHandlerRadioCheckbox(evt) {
		var o= evt.currentTarget;
		o.dispatchEvent(Xevents['DOMActivate']);

		if (document.all) o.dispatchEvent(Xevents['change']); // correct IE refresh BUG on checkbox, radio formControls
		
		if (o.checked)
			document.dispatchEvent(Xevents['xforms-select']);
		else
			document.dispatchEvent(Xevents['xforms-deselect']);
			
		evt.stopPropagation();
	}
		
	function clickHandler(evt) {
		var o= evt.currentTarget;
		o.dispatchEvent(Xevents['DOMActivate']);
		o.dispatchEvent(Xevents['xforms-activate']);

		evt.stopPropagation();
	}
	
	function changeHandler(evt) {
		var o = evt.currentTarget;
		
		Xvalues = new Array();
		
				// loops through formControlsBindings and matches the current HTML target with the form control
		//  updates the xmodel data with the data entered in the form control
		
		// if element is a member of a repeat
		
		if (o.getAttribute('repeat')) {			
			repeatgroup = o.getAttribute('repeat');
			model = o.getAttribute('model');
			if (!model) model = 0;
			
			//alert('Repeat changeHandler: ' + 'repeatgroup: ' + repeatgroup + ' model: ' + xforms.childModels[model].id);
			//alert(xforms.childModels[model].repeatFormControlsBindings[repeatgroup][0][0].el);
			
			for (var i=0; i < xforms.childModels[model].repeatFormControlsBindings[repeatgroup].length; i++) {
				for (var j=0; j < xforms.childModels[model].repeatFormControlsBindings[repeatgroup][i].length; j++) {
					if (xforms.childModels[model].repeatFormControlsBindings[repeatgroup][i][j].el ==o)  {
						getFormValue(o, xforms.childModels[model].repeatFormControlsBindings[repeatgroup][i][j].nodeset);
						
					}
				}
			}
		}
		else {
			
			// loop through static
			// loop over each model
			for(var i=0; i < xforms.childModels.length; i++) {		
				for(var j=0; j <  xforms.childModels[i].formControlsBindings.length; j++) {
					if (xforms.childModels[i].formControlsBindings[j].el===o) {
						//alert(o.getAttribute('ref')+xforms.childModels[i].formControlsBindings[j].nodeset)
						getFormValue(o, xforms.childModels[i].formControlsBindings[j].nodeset);	
					}
				}
			}
		}
		
		o.dispatchEvent(Xevents['xforms-value-changed']);
		
		Xactions.recalculateHandler(evt);
		Xactions.revalidateHandler(evt);
		
		evt.stopPropagation();
		
	}

	// for setting the currenet index in a repeat
	function setIndex(oRepeatInstance) {
		repeatgroup = oRepeatInstance.getAttribute(repeat);
		siblingRepeatInstances = _getElementsByClassName('repeatinstance', document.getElementById(repeatgroup));
		index = siblingRepeatInstances.findElementIndex(oRepeatInstances);
		
		XRepeats[repeatgroup].currentIndex = index+1;
		alert('setIndex: ' + index+1 + ' oRepeat ' + repeatgroup);
		return index+1;
	}
	
	function setIndexHandler(evt) {
		setIndex(evt.target);
	}

	function focusHandler(evt) {
		var o = evt.currentTarget;
		
		document.dispatchEvent(Xevents['DOMFocusIn']);
		document.dispatchEvent(Xevents['xforms-focus']);

		switch (o.className) {
		 case 'Disabled':
		 case 'Readonly':
		 	evt.preventDefault();
		 	break;
		 default:
		 	o.className='Selected';
		 	break;
		}
		
		evt.stopPropagation();	
	}

	function blurHandler(evt) {
		var o = evt.currentTarget;
		document.dispatchEvent(Xevents['DOMFocusOut']);

		switch (o.className) {
		 case 'Disabled':
		 case 'Readonly':
		 	evt.preventDefault();
		 	break;
		 default:
			o.className = '';
		 	break;
		}

		evt.stopPropagation();	
	}

	function keyHandler(evt) {
		switch (evt.keyCode) {
		case 9: //tab
			if (evt.shiftKey) 
				document.dispatchEvent(Xevents['xforms-previous']);
			else
				document.dispatchEvent(Xevents['xforms-next']);
			break;
		case 8:
			if (evt.target.readOnly) {
				evt.preventDefault();
				evt.stopPropagation();
			}
			break;
		}
		var o = evt.currentTarget;
	}

	function errorHandler(evt) {
		//throw new Error(evt.type);	
	}
	

	function enableHandler(evt) {
		var o = evt.currentTarget;
		var a = o.getAttribute('nodeset') || o.getAttribute('ref');
		//alert('xforms-enabled Fired on ' + a + '(id:'+ o.id +')';

		if (o.nodeName=='FIELDSET') {
			_recursiveEnableDisable(o,false,'');
		} else {
			_recursiveEnableDisable(o.parentNode,false,'');
		}
		
		evt.preventDefault();
		evt.stopPropagation();
	}
		
	function disableHandler(evt) {
		var o = evt.currentTarget;
		var a = o.getAttribute('nodeset') || o.getAttribute('ref');
		//alert('xforms-disabled Fired on ' + a + '(id:'+ o.id +')');
			
		if (o.nodeName=='FIELDSET') {
			_recursiveEnableDisable(o,true,'Disabled');
		} else {
			_recursiveEnableDisable(o.parentNode,true,'Disabled');
		}
		
		evt.preventDefault();
		evt.stopPropagation();
	}
		
	function _recursiveEnableDisable(node,disabled,className) {
	
		switch (node.nodeName) {
		  case 'DIV':
			  break;
			default:
				node.readOnly=disabled;
				node.unselectable=disabled;
				node.className=className;
		}
		
		for (var i=0;i<node.childNodes.length;i++) {
			switch (node.childNodes[i].nodeType) {
			case 1:
			  switch (node.childNodes[i].nodeName) {
			  case 'DIV':
			  	break;
			  case 'INPUT':
			  	switch(node.childNodes[i].type) {
						case 'radio':
						case 'checkbox':
							node.childNodes[i].disabled = disabled;
			  	}
			  default:
					node.childNodes[i].readOnly=disabled;
					node.childNodes[i].unselectable=disabled;
				  node.childNodes[i].className=className;
				}
				_recursiveEnableDisable(node.childNodes[i],disabled,className);
			}
		}
	}
	
	function readOnlyHandler(evt) {
		var o = evt.currentTarget;
		
		switch (o.className) {
			case 'Disabled':
			  break;
			default:
			  o.readOnly=true;
				o.className='Readonly';
				_recursiveReadOnly(o,true,'Readonly');
				break;
		}
		
		evt.preventDefault();
		evt.stopPropagation();
	}

	function readWriteHandler(evt) {
		var o = evt.currentTarget;

		switch (o.className) {
			case 'Disabled':
			  break;
			default:
			  o.readOnly=false;
				o.className='';
				_recursiveReadOnly(o,true,'Readonly');
				_recursiveReadOnly(o,false,'');
				break;
		}
		
		evt.stopPropagation
		evt.preventDefault();
	}


	function _recursiveReadOnly(node,readonly,className) {
		for (var i=0;i<node.childNodes.length;i++) {
		if (node.childNodes[i].readOnly!=null) {
			node.childNodes[i].readOnly=readonly;
			node.childNodes[i].className=className;
		}
		_recursiveReadOnly(node.childNodes[i]);
		}
	}


	function requiredHandler(evt) {
		var o = evt.currentTarget;
		
		if (!o.requiredElement) {
		o.requiredElement=document.createElement('div');
		o.requiredElement.className='required';
		o.requiredElement.innerHTML='<span>*</span>';	
		
		o.parentNode.appendChild(o.requiredElement);	
		}
		
		evt.stopPropagation();
	}

	
	function optionalHandler(evt) {
		var o = evt.currentTarget;

		if (o.requiredElement) {
			o.parentNode.removeChild(o.requiredElement);
			o.requiredElement = null;
		}
		
		evt.stopPropagation();
	}


	function invalidHandler(evt) {
		var o = evt.currentTarget;
		
		if (!o.invalidElement) {
		o.invalidElement = document.createElement('div');
		o.invalidElement.className='invalid';
		o.invalidElement.innerHTML='<span>&#160;!&#160;</span>';
		new Hint(o.invalidElement,'Invalid field content, please correct it.');
		
		o.parentNode.appendChild(o.invalidElement);
		}
		
		evt.stopPropagation();
	}

	function validHandler(evt) {
		var o = evt.currentTarget;
		
		if (o.invalidElement) {
			o.parentNode.removeChild(o.invalidElement);
			o.invalidElement = null;
		}
		
		evt.stopPropagation();
	}
	
}

/**
 * 
 */
Xforms.prototype.run = function() {

	document.dispatchEvent(Xevents['xforms-model-construct-done']);
	
	// fill formControls value
	for(var i=0;i < this.nChildModels; i++) {
		this.childModels[i].formControlsBindings=this._traverseFormControls(document.body,this.childModels[i].instanceData.documentElement.firstChild.firstChild);
		this.childModels[i]._refresh();
	}


	document.dispatchEvent(Xevents['xforms-ready']);
}


function myDebugHandler(evt) {
	myDebug.addEvent('',evt)
}

function myDebugHandler2(evt) {
	myDebug.addEvent('notice',evt)
}

/**
 * 
 */
Xforms.prototype.registerEvents = function(oList) {
	for(var i=0; i< oList.length; i++) {
		Xevents[oList[i]] = document.createEvent("Events");
		Xevents[oList[i]].initEvent(oList[i],true,true);
		
		if (myDebug.on) document.addEventListener(oList[i],myDebugHandler,false);
	}
	
}	

/**
 * xforms model object
 */
Xmodel.prototype = new Object();
Xmodel.prototype.constructor = Xmodel;
Xmodel.superclass = Object.prototype;
 
/**
 * constructor
 */
function Xmodel(oAttributes) {
	this.init(oAttributes);
}	

/**
 * 
 */
Xmodel.prototype.init = function(oAttributes) {
	this.attributes = oAttributes

	this.id = this.attributes['id']; 
	this.functions = this.attributes['functions']; 
	
	// create the default instance data
	this.instanceData = XmlDocument.create(); 
	this.instanceData.appendChild(this.instanceData.createElement('instances'));
	
	this.childSubmission = new Array();
	this.childBind = new Array();

	this.childInstance = new Array();	
	this.nChildInstance = 0;

	// static -> html element, xmodel node
	// repeat -> repeat instance -> html element, xmodel node
	
	this.formControlsBindings = new Array(); // for static elements (non repeat)
	this.nformControlsBindings = 0;
	
	this.repeatFormControlsBindings = new Array();
	this.nrepeatFormControlsBindings = 0;
	
	this.bindings = new Array();
			
}

/**
 * 
 */
Xmodel.prototype.resolveFunctions = function () {
	// functions parsing 
	if (this.functions) {
		var msg = '';
		var funList = this.functions.split(' ');
		for (var i=0;i< funList.length; i++) {
			var funName = funList[i].split(':');

			var prefix = funName[0];
			var uri = xnamespaces[prefix];
			var func = funName[1];

			// registers functions
			try {			
				funRes.addFunction(uri,func,eval(func));
				//alert('xmlns:'+prefix+'="'+uri+'" '+func + '\n' + eval(func));
			} catch(err) {
				document.dispatchEvent(Xevents['xforms-compute-exception']);						
			}
		}
		
	}	
}

/**
 * 
 */
Xmodel.prototype.appendBind = function(oChild) {
	oChild.parent = this;
	oChild.model = this;
	this.childBind[this.childBind.length] = oChild;
	return oChild
}

Xmodel.prototype._rebuild = function() {
	for (var j=0; j< this.childBind.length; j++) { // xforms:bind elements
		this.childBind[j].rebuild(this.instanceData.documentElement.firstChild.firstChild);
	}
	document.dispatchEvent(Xevents['xforms-rebuild']);
}

Xmodel.prototype._recalculate = function() {
	for (var j=0; j< this.childBind.length; j++) { // xforms:bind elements
		this.childBind[j].recalculate(this.instanceData.documentElement.firstChild.firstChild);
	}
	document.dispatchEvent(Xevents['xforms-recalculate']);
}

Xmodel.prototype._revalidate = function() {
	for (var j=0; j< this.childBind.length; j++) { // xforms:bind elements
		this.childBind[j].revalidate(this.instanceData.documentElement.firstChild.firstChild);
	}
	
	/*
	for (var i=0; i < this.formControlsBindings.length; i++) {
		for (var j=0; j < this.bindings.length; j++) {
			if (this.formControlsBindings[i]===this.bindings[j]) alert(this.bindings[j].nodeName);
		}
	}
	*/

	document.dispatchEvent(Xevents['xforms-revalidate']);
}


/**
 * 
 */
Xmodel.prototype._refresh = function() {
	for (var j=0; j< this.childBind.length; j++) { // xforms:bind elements
		this.childBind[j].refresh(this.instanceData.documentElement.firstChild.firstChild);
	}
	document.dispatchEvent(Xevents['xforms-refresh']);
}


/**
 * 
 */
Xmodel.prototype.run = function() {
	this._rebuild();
	this._recalculate();
	this._revalidate();
}


/**
 * 
 */
Xmodel.prototype.appendSubmission = function(oChild) {
	oChild.parent = this;
	this.childSubmission[this.childSubmission.length] = oChild;
	return oChild
}

/**
 * 
 */
Xmodel.prototype.appendRepeat = function(repeatId) {
	// create an array for each repeat
	this.repeatFormControlsBindings[repeatId] = new Array(); 
	this.nrepeatformControlsBindings++;
}


/**
 * 
 */
Xmodel.prototype.appendInstance = function (oAttributes,oInstance) {

	var instanceNode = this.instanceData.firstChild.appendChild(this.instanceData.createElement('instance'));

	for (var i in oAttributes) {
		var attr = this.instanceData.createAttribute(i);
		attr.nodeValue = oAttributes[i];
		instanceNode.setAttributeNode(attr);
	}
		
	for (var i = 0; i < oInstance.childNodes.length; i++) {
		var importedNode=document.all?oInstance.childNodes[i]:this.instanceData.importNode(oInstance.childNodes[i], true);
		instanceNode.appendChild(importedNode);
	}
	
	this.childInstance[this.nChildInstance++] = instanceNode;
	if (oAttributes['id']) this.childInstance[oAttributes['id']] = instanceNode;
	
}

/**
 * 
 */
Xmodel.prototype.getInstance = function () {
	var id = arguments[0];
	return arguments.length==1?this.childInstance[id]:this.childInstance[0];
}

/**
 * dump instance as url encoded form
 * @access private
 */
Xmodel.prototype._form= function (node) {
	var str = new String();
		for(var i=0;i<node.childNodes.length;i++) {
			switch (node.childNodes[i].nodeType) {
	 			case 1: // Element 
	 					if (node.childNodes[i].firstChild.nodeValue!=null) str+=node.childNodes[i].nodeName+'='+escape(node.childNodes[i].firstChild.nodeValue)+'&';
						str+=this._form(node.childNodes[i]);
					break;
				case 9: // Document
					str+=this._form(node.childNodes[i]);
					break;
			}
		}
	return str;
}

/**
 * 
 */
Xmodel.prototype.asXml = function () {
	var s = new XMLSerializer();
	return  s.serializeToString(this.instanceData.documentElement.firstChild.firstChild); 
}

/**
 * 
 */
Xmodel.prototype.asXform = function () {
	str = new String();
	str+=this._form(this.instanceData.documentElement.firstChild.firstChild);
	return str;
}

/**
 * xforms dom2 event object 
 */
Xevent.prototype = new Object();
Xevent.prototype.constructor = Xevent;
Xevent.superclass = Object.prototype;

/**
 * constructor
 */
function Xevent(oId,oAttributes,oFunction) {
	this.init(oId,oAttributes,oFunction);	
}

/**
 * initialization
 */
Xevent.prototype.init = function(oId,oAttributes,oFunction) {
	this.element = document.getElementById(oId);
	
	this.attributes = oAttributes;
	
	var evt= this.createEvent(this.attributes['ev:event']); 

	try {
		this.element.control = this;
		this.element.addEventListener(evt,oFunction,false);	
	} catch (err) {
		this.element = document.createElement('div');
		this.element.setAttribute('id',oId);

		document.body.appendChild(this.element);
		
		this.element.control = this;
		this.element.addEventListener(evt,oFunction,false);	
		
	}
	
}

/**
 * 
 */
Xevent.prototype.createEvent = function(evt) {
	if (evt && Xevents[evt]==undefined) {
		Xevents[evt] = document.createEvent("Events");
		Xevents[evt].initEvent(evt,true,true);
	}
	return evt;
}

function _getElementsByClassName(classname, o) {
	if (!o) o = document.documentElement;
	var elements = new Array();
	var allElements = o.getElementsByTagName('*');
	
	for (var i=0;i< allElements.length; i++) {
		if (allElements[i].className==classname) elements[elements.length]=allElements[i];
	}

return elements;
}
